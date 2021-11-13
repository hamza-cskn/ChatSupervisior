package mc.obliviate.chatsupervisor.handlers.config;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.format.ChatFormat;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandlerAbstract;
import mc.obliviate.chatsupervisor.handlers.recipient.recipienthandlers.DefaultRecipientHandler;
import mc.obliviate.chatsupervisor.handlers.recipient.recipienthandlers.LocalRecipientHandler;
import mc.obliviate.chatsupervisor.handlers.recipient.recipienthandlers.PerWorldGlobalRecipientHandler;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static mc.obliviate.chatsupervisor.utils.log.LogUtils.logFile;

public class ConfigHandler {

	public static final String[] buttonData = new String[2];
	private static final boolean[] logChatData = new boolean[2];

	public static YamlConfiguration config;
	private static SimpleDateFormat dateFormat;
	private final ChatSupervisor plugin;

	public ConfigHandler(ChatSupervisor plugin) {
		this.plugin = plugin;
	}

	public static boolean isLogChatEnabled() {
		return logChatData[0];
	}

	public static boolean isLogPMEnabled() {
		return logChatData[1];
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
		registerWordFilter();
		registerButtonData();
		setDateFormat(config.getString("date-format", "dd/MM/yyyy HH:mm"));

		logChatData[0] = ConfigHandler.config.getBoolean("log-chat.enabled");
		logChatData[1] = ConfigHandler.config.getBoolean("log-chat.log-private-messages");

		String logFileDir = config.getString("log-chat.log-directory", "logs"+File.separator+"log-%M%.log");
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
		buttonData[0] = color(config.getString("interactive-staff-button.icon"));
		buttonData[1] = color(config.getString("interactive-staff-button.description"));
	}

	private void registerFormat(final String formatName, final String format, final int cooldown) {
		FormatHandlerAbstract.getFormats().put(formatName, new ChatFormat(color(format), cooldown));
	}

	private void registerFormats() {
		final ConfigurationSection section = config.getConfigurationSection("chat-groups");
		if (section == null) {
			plugin.getLogger().severe("No chat group found!");
			return;
		}
		for (final String key : section.getKeys(false)) {
			final ConfigurationSection format = section.getConfigurationSection(key);
			if (format == null) continue;

			registerFormat(key, format.getString("format", ""), format.getInt("chat-cooldown", 0));
		}
	}

	public void registerWordFilter() {
		for (final String regex : config.getStringList("auto-mute.regex-checks")) {
			plugin.getWorldFilter().addFilterList(regex);
		}
	}

	public String color(final String text) {
		if (text == null) return null;
		return ChatColor.translateAlternateColorCodes('&', text);
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

		plugin.getLogger().info("[!] Recipients Handler Mode: " + plugin.getRecipientHandler().getMode());

		final int range = config.getInt("global-chat.local-chat-range", 0);
		if (range <= 0) {
			plugin.getLogger().severe("[Warning] Local Chat Range is below or equal to zero. (" + range + ")");
		}
		plugin.getRecipientHandler().setRange(range);
		plugin.getRecipientHandler().setGlobalPrefix(config.getString("global-chat.prefix", ""));
		plugin.getRecipientHandler().setPerWorldGlobalPrefix(config.getString("global-chat.global-chat-per-world.prefix", ""));
		plugin.getRecipientHandler().setPerWorldGlobalPrefix(config.getString("global-chat.global-chat-per-world.prefix", ""));

		plugin.getRecipientHandler().setGlobalChatMetaFormat(color(config.getString("global-chat.meta-format", "")));
		plugin.getRecipientHandler().setPerWorldGlobalChatMetaFormat(color(config.getString("global-chat.global-chat-per-world.meta-format", "")));


	}
}
