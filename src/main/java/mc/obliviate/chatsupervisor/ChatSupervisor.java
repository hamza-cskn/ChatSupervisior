package mc.obliviate.chatsupervisor;

import mc.obliviate.chatsupervisor.commands.*;
import mc.obliviate.chatsupervisor.handlers.config.ConfigHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.wordfilter.RegexSupervisorHandler;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandler;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandlerKyori;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandlerSpigot;
import mc.obliviate.chatsupervisor.handlers.placeholderapi.PlaceholderAPIHandler;
import mc.obliviate.chatsupervisor.handlers.protocollib.ProtocolLibHandler;
import mc.obliviate.chatsupervisor.handlers.recipient.IRecipientHandler;
import mc.obliviate.chatsupervisor.listeners.ChatListener;
import mc.obliviate.chatsupervisor.listeners.ConnectionListener;
import mc.obliviate.chatsupervisor.wordfilter.regex.MatchedStringMeta;
import mc.obliviate.inventory.InventoryAPI;
import mc.obliviate.util.versiondetection.ServerVersionController;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class ChatSupervisor extends JavaPlugin {

	private static Permission permission;
	private static String bukkitVersion;
	private final ConfigHandler configHandler = new ConfigHandler(this);
	private final RegexSupervisorHandler regexSupervisorHandler = new RegexSupervisorHandler();
	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private final DataHandler dataHandler = new DataHandler(this);
	private final ProtocolLibHandler protocolLibHandler = new ProtocolLibHandler();
	private final PlaceholderAPIHandler placeholderAPIHandler = new PlaceholderAPIHandler();
	private FormatHandler formatHandler;
	private IRecipientHandler recipientHandler;

	private PrivateMessageCMD privateMessageCMD;
	private ChatListener chatListener;

	public static Permission getPermission() {
		return permission;
	}

	public static String getBukkitVersion() {
		return bukkitVersion;
	}

	public static ChatSupervisor getInstance() {
		return JavaPlugin.getPlugin(ChatSupervisor.class);
	}

	@Override
	public void onEnable() {
		ServerVersionController.calculateServerVersion(Bukkit.getVersion());
		bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
		Bukkit.getLogger().info("ChatSupervisor v" + getDescription().getVersion() + " installing on " + bukkitVersion);
		configHandler.loadConfig();
		registerCommands();
		registerListeners();
		setupPermissions();
		initHandlers();

		lookForScanFile();

	}

	public void lookForScanFile() {
		if (new File(getDataFolder().getPath() + File.separator + "scan.log").exists()) {
			Bukkit.getLogger().info("Scan log file found. Scanning...");
			try {
				scan();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDisable() {
		try {
			if (DataHandler.getConnection() != null)
				DataHandler.getConnection().close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	private boolean setupPermissions() {
		final RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		assert rsp != null;
		permission = rsp.getProvider();
		return true;
	}

	private boolean isClassExist(String classPath) {
		try {
			Class.forName(classPath);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	private void initHandlers() {
		if (isClassExist("net.kyori.adventure.text.Component")) {
			formatHandler = new FormatHandlerKyori(this);
		} else {
			formatHandler = new FormatHandlerSpigot(this);
		}
		dataHandler.init();
		inventoryAPI.init();
		placeholderAPIHandler.init(this);
		protocolLibHandler.init(this);
		regexSupervisorHandler.init(Objects.requireNonNull(ConfigHandler.config.getConfigurationSection("regex-supervisor")));
	}

	public void registerCommands() {
		safeRegisterCommand("punish", new PunishCMD());
		safeRegisterCommand("delictlog", new DelictLogCMD());
		safeRegisterCommand("ignore", new IgnoreCMD());
		safeRegisterCommand("chatsupervisorreload", new ChatSupervisorCMD(this));
		if (ConfigHandler.config.getBoolean("private-message.enabled")) {
			privateMessageCMD = new PrivateMessageCMD(ConfigHandler.config.getString("private-message.format"), ConfigHandler.config.getString("private-message.replace-player-name-with"));
			safeRegisterCommand("pm", privateMessageCMD);
			safeRegisterCommand("r", privateMessageCMD);
		}
		safeRegisterCommand("chatlock", new ChatLockCMD());
		safeRegisterCommand("staffchat", new StaffChatCMD(this));
		safeRegisterCommand("clearchat", new ClearChatCMD());
	}

	private void safeRegisterCommand(String commandName, CommandExecutor cmd) {
		final PluginCommand command = getCommand(commandName);
		if (command == null) {
			getLogger().severe("Command could not found: " + commandName);
			return;
		}
		command.setExecutor(cmd);
	}

	public void registerListeners() {
		chatListener = new ChatListener(this);
		Bukkit.getPluginManager().registerEvents(chatListener, this);
		Bukkit.getPluginManager().registerEvents(new ConnectionListener(this), this);
	}

	public void scan() throws IOException {
		final long startedTime = System.currentTimeMillis();
		final BufferedReader br = new BufferedReader(new FileReader(getDataFolder().getPath() + File.separator + "scan.log"));
		String line;
		int lineCount = 0;
		int badwordCount = 0;
		while ((line = br.readLine()) != null) {
			final MatchedStringMeta meta = regexSupervisorHandler.checkString(null, line);
			if (meta != null) {
				String msg = line;
				msg = msg.replace(meta.getMatchedString(), "__" + ChatColor.RED + ChatColor.UNDERLINE + meta.getMessage() + ChatColor.WHITE + "__");
				Bukkit.getLogger().info(msg);
				badwordCount++;
			}
			lineCount++;
		}
		final long endedTime = System.currentTimeMillis();
		Bukkit.getLogger().info("SCAN FINISHED: " + lineCount + " line scanned and " + badwordCount + " badword found in " + ((int) (endedTime - startedTime) / 1000) + "ms.");
	}

	public ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public FormatHandler getFormatHandler() {
		return formatHandler;
	}


	public IRecipientHandler getRecipientHandler() {
		return recipientHandler;
	}

	public void setRecipientHandler(IRecipientHandler recipientHandler) {
		this.recipientHandler = recipientHandler;
	}

	public DataHandler getDataHandler() {
		return dataHandler;
	}


	public PrivateMessageCMD getPrivateMessageCMD() {
		return privateMessageCMD;
	}

	public ChatListener getChatListener() {
		return chatListener;
	}

	public RegexSupervisorHandler getRegexSupervisorHandler() {
		return regexSupervisorHandler;
	}


}
