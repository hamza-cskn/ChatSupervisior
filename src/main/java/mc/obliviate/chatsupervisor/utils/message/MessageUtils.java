package mc.obliviate.chatsupervisor.utils.message;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {

	private static YamlConfiguration messageConfig;
	private static String prefix;

	public static void setMessageConfig(final YamlConfiguration messageConfig) {
		MessageUtils.messageConfig = messageConfig;
		MessageUtils.prefix = parseColor(messageConfig.getString("prefix"));
	}

	public static String getMessage(String node) {
		final String msg = messageConfig.getString(node);
		if (msg == null) {
			return "No message found: " + node;
		}
		return msg;
	}

	public static void sendMessage(final CommandSender sender, final String configNode) {
		sendWithType(sender,parseColor(getMessage(configNode)), true);
	}

	public static void sendMessage(final CommandSender sender, final String configNode, final PlaceholderUtil placeholderUtil) {
		sendWithType(sender, parsePlaceholders(getMessage(configNode),placeholderUtil), true);
	}

	public static void sendMessageList(final CommandSender sender, final String configNode) {
		sendList(sender, parseColor(messageConfig.getStringList(configNode)));
	}

	private static void sendList(final CommandSender sender, final List<String> list) {
		for (final String str : list) {
			sendWithType(sender, str, false);
		}
	}

	public static void sendMessageList(final CommandSender sender, final String configNode, final PlaceholderUtil placeholderUtil) {
		sendList(sender, parseListPlaceholders(parseColor(messageConfig.getStringList(configNode)), placeholderUtil));
	}

	private static void sendWithType(CommandSender sender, String text, boolean usePrefix) {
		if (usePrefix && prefix != null) {
			text = prefix + text;
		}
		sender.sendMessage(text);
	}

	public static String parseColor(String string) {
		if (string == null) return null;
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static List<String> parseColor(List<String> stringList) {
		if (stringList == null) return null;
		final List<String> result = new ArrayList<>();
		for (String str : stringList) {
			result.add(ChatColor.translateAlternateColorCodes('&', str));
		}
		return result;
	}

	public static List<String> listReplace(List<String> stringList, String search, String replace) {
		if (stringList == null) return null;
		final List<String> result = new ArrayList<>();
		for (String str : stringList) {
			result.add(str.replace(search, replace));
		}
		return result;
	}

	public static List<String> parseListPlaceholders(List<String> stringList, PlaceholderUtil placeholderUtil) {
		if (stringList == null) return null;
		final List<String> result = new ArrayList<>();
		for (String str : stringList) {
			result.add(parsePlaceholders(str, placeholderUtil));
		}
		return result;
	}

	public static String parsePlaceholders(String string, final PlaceholderUtil placeholderUtil) {
		string = parseColor(string);
		for (final InternalPlaceholder placeholder : placeholderUtil.getPlaceholders()) {
			string = string.replace(placeholder.getPlaceholder(), placeholder.getValue());
		}
		return string;
	}


	public static List<String> splitMessage(final String message) {
		return splitMessage(message, "");
	}

	public static List<String> splitMessage(final String message, final String prefix) {
		return splitMessage(message, 30, prefix);
	}

	public static List<String> splitMessage(final String message, final int splitRange, final String prefix) {
		final List<String> result = new ArrayList<>();
		int length = message.length();
		int i = 0;
		while (length > 0) {
			final String line = message.substring(i * splitRange, Math.min((i + 1) * splitRange, message.length()));
			result.add(prefix + line);
			length -= line.length();
			i++;
		}
		return result;
	}


}
