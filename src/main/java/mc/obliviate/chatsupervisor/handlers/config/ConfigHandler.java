package mc.obliviate.chatsupervisor.handlers.config;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.format.ChatFormat;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandlerAbstract;
import mc.obliviate.chatsupervisor.handlers.recipient.recipienthandlers.DefaultRecipientHandler;
import mc.obliviate.chatsupervisor.handlers.recipient.recipienthandlers.LocalRecipientHandler;
import mc.obliviate.chatsupervisor.handlers.recipient.recipienthandlers.PerWorldGlobalRecipientHandler;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static mc.obliviate.chatsupervisor.utils.log.LogUtils.logFile;

public class ConfigHandler {

	public static final String[] buttonData = new String[2];
	private static boolean logChatData;

	public static YamlConfiguration config;
	private static SimpleDateFormat dateFormat;
	private final ChatSupervisor plugin;

	public ConfigHandler(ChatSupervisor plugin) {
		this.plugin = plugin;
	}

	public static boolean shouldChatLogToConsole() {
		return config.getBoolean("log.send-message-logs-to-console");
	}

	public static boolean islogFileEnabled() {
		return logChatData;
	}

	public static SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	private void setDateFormat(String format) {
		dateFormat = new SimpleDateFormat(format);
	}

	public void loadConfig() {
		loadFiles();

		registerFormats();
		registerRecipientsHandler();

		registerButtonData();
		setDateFormat(config.getString("date-format", "dd/MM/yyyy HH:mm"));

		logChatData = ConfigHandler.config.getBoolean("log.log-file.enabled");

		String logFileDir = config.getString("log.log-file.log-directory", "logs" + File.separator + "log-%M%.log");
		final String dateFormat = logFileDir.split("%")[1];
		logFileDir = logFileDir.replace("%" + dateFormat + "%", new SimpleDateFormat(dateFormat).format(new Date()));

		logFile = new File(plugin.getDataFolder().getPath() + File.separator + logFileDir);

		if (!logFile.getParentFile().exists()) {
			logFile.getParentFile().mkdirs();
		}
	}

	private void loadFiles() {
		final File configFile = new File(plugin.getDataFolder() + "/config.yml");
		final File messagesFile = new File(plugin.getDataFolder() + "/messages.yml");
		final File punishmentConfigFile = new File(plugin.getDataFolder() + "/punishment-config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
		YamlConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);

		if (!configFile.exists() || config.getKeys(true).size() == 0) {
			plugin.saveResource("config.yml", true);
			config = YamlConfiguration.loadConfiguration(configFile);
		}

		if (!messagesFile.exists() || messages.getKeys(true).size() == 0) {
			plugin.saveResource("messages.yml", true);
			messages = YamlConfiguration.loadConfiguration(messagesFile);
		}

		if (!punishmentConfigFile.exists()) {
			plugin.saveResource("punishment-config.yml", true);
		}

		MessageUtils.setMessageConfig(messages);


	}


	private void registerButtonData() {
		buttonData[0] = MessageUtils.parseColor(config.getString("message-based-punishment.staff-button.icon"));
		buttonData[1] = MessageUtils.parseColor(config.getString("message-based-punishment.staff-button.description"));
	}

	private void registerFormat(final String formatName, final String format, final int cooldown) {
		FormatHandlerAbstract.getFormats().put(formatName, new ChatFormat(MessageUtils.parseColor(format), cooldown));
	}

	private void registerFormats() {
		final ConfigurationSection section = config.getConfigurationSection("chat-groups");
		if (section == null) {
			plugin.getLogger().severe("No chat group found!");
			return;
		}

		if (!section.isSet("default.format")) {
			section.set("default.format", "&7{player}: &f{message}");
			section.set("default.chat-cooldown", 1000);
			plugin.getLogger().severe("Default group format could not found! Please do not delete default group format from config.yml.");
		}
		for (final String key : section.getKeys(false)) {
			final ConfigurationSection format = section.getConfigurationSection(key);
			if (format == null) continue;

			registerFormat(key, format.getString("format", ""), format.getInt("chat-cooldown", 0));
		}
	}


	private void registerRecipientsHandler() {
		if (config.getBoolean("global-chat.enabled")) {
			if (config.getBoolean("global-chat.global-chat-per-world.enabled")) {
				plugin.setRecipientHandler(new PerWorldGlobalRecipientHandler(plugin));
			} else {
				plugin.setRecipientHandler(new LocalRecipientHandler(plugin));
			}
		} else {
			plugin.setRecipientHandler(new DefaultRecipientHandler(plugin));
		}

		final int range = config.getInt("global-chat.local-chat-range", 0);
		if (range <= 0) {
			plugin.getLogger().severe("Local Chat Range is below or equal to zero. (" + range + ")");
		}
		plugin.getRecipientHandler().setRange(range);
		plugin.getRecipientHandler().setGlobalPrefix(config.getString("global-chat.prefix", ""));
		plugin.getRecipientHandler().setPerWorldGlobalPrefix(config.getString("global-chat.global-chat-per-world.prefix", ""));
		plugin.getRecipientHandler().setPerWorldGlobalPrefix(config.getString("global-chat.global-chat-per-world.prefix", ""));

		plugin.getRecipientHandler().setGlobalChatMetaFormat(MessageUtils.parseColor(config.getString("global-chat.meta-format", "")));
		plugin.getRecipientHandler().setPerWorldGlobalChatMetaFormat(MessageUtils.parseColor(config.getString("global-chat.global-chat-per-world.meta-format", "")));


	}
}
