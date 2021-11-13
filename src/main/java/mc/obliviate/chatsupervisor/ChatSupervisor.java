package mc.obliviate.chatsupervisor;

import mc.obliviate.chatsupervisor.commands.*;
import mc.obliviate.chatsupervisor.format.ChatFormat;
import mc.obliviate.chatsupervisor.handlers.config.ConfigHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandler;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandler1_16;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandler1_8;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandlerAbstract;
import mc.obliviate.chatsupervisor.handlers.placeholderapi.PlaceholderAPIHandler;
import mc.obliviate.chatsupervisor.handlers.protocollib.ProtocolLibHandler;
import mc.obliviate.chatsupervisor.handlers.recipient.IRecipientHandler;
import mc.obliviate.chatsupervisor.listeners.ChatListener;
import mc.obliviate.chatsupervisor.listeners.ConnectionListener;
import mc.obliviate.chatsupervisor.punishment.delict.DelictNotifier;
import mc.obliviate.chatsupervisor.wordfilter.WordFilter;
import net.md_5.bungee.api.plugin.Plugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.efekurbann.inventory.InventoryAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

public class ChatSupervisor extends JavaPlugin {

	private static Permission permission;
	private static String bukkitVersion;
	private final ConfigHandler configHandler = new ConfigHandler(this);
	private final WordFilter worldFilter = new WordFilter();
	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private final DataHandler dataHandler = new DataHandler(this);
	private final DelictNotifier delictNotifier = new DelictNotifier(this);
	private final ProtocolLibHandler protocolLibHandler = new ProtocolLibHandler();
	private final PlaceholderAPIHandler placeholderAPIHandler = new PlaceholderAPIHandler();
	private FormatHandler formatHandler;
	private IRecipientHandler recipientHandler;

	public static Permission getPermission() {
		return permission;
	}

	@Override
	public void onEnable() {
		bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
		Bukkit.getLogger().info("ChatSupervisor v" + getDescription().getVersion() + " installing on " + bukkitVersion);
		configHandler.loadConfig();
		registerCommands();
		registerListeners();
		setupPermissions();
		initHandlers();

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

	private void initHandlers() {
		if (bukkitVersion.equalsIgnoreCase("1.8.8")) {
			formatHandler = new FormatHandler1_8(this);
		} else {
			formatHandler = new FormatHandler1_16(this);
		}
		dataHandler.init();
		inventoryAPI.init();
		placeholderAPIHandler.init(this);
		protocolLibHandler.init(this);


	}

	public void registerCommands() {
		getCommand("punish").setExecutor(new PunishCMD());
		getCommand("kontrol").setExecutor(new DelictLogCMD());
		getCommand("chatmute").setExecutor(new IgnoreCMD());
		getCommand("chatsupervisor-reload").setExecutor(new ChatSupervisorCMD(this));
		if (ConfigHandler.config.getBoolean("private-message.enabled")) {
			final PrivateMessageCMD pmcmd = new PrivateMessageCMD(ConfigHandler.config.getString("private-message.format"), ConfigHandler.config.getString("private-message.replace-player-name-with"));
			getCommand("pm").setExecutor(pmcmd);
			getCommand("r").setExecutor(pmcmd);
		}
		getCommand("chatlock").setExecutor(new ChatLockCMD());

	}

	public void registerListeners() {
		int maxCooldown = 0;
		for (ChatFormat format : FormatHandlerAbstract.getFormats().values()) {
			if (format.getCooldown() > maxCooldown) {
				maxCooldown = format.getCooldown();
			}
		}
		//cache time out time will be set to max cooldown of groups
		Bukkit.getPluginManager().registerEvents(new ChatListener(maxCooldown, this), this);
		Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
	}


	public void scan() throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(getDataFolder().getPath() + File.separator + "scan.log"));
		String line;
		int i = 0;
		int f = 0;
		while ((line = br.readLine()) != null) {
			final String[] splitted = line.split(":");
			line = line.replace(splitted[1], "");
			final String badwordCatch = getWorldFilter().checkRegex(line);
			if (!badwordCatch.isEmpty()) {
				String msg = line.replace(badwordCatch, "____" + ChatColor.RED + badwordCatch + ChatColor.WHITE + "____");
				Bukkit.getLogger().info(msg);
				f++;
			}
			i++;
		}
		Bukkit.getLogger().info("FINISHED: " + i + " line scanned and " + f + " badword found.");
	}

	public ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public FormatHandler getFormatHandler() {
		return formatHandler;
	}

	public DelictNotifier getDelictNotifier() {
		return delictNotifier;
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

	public WordFilter getWorldFilter() {
		return worldFilter;
	}

}
