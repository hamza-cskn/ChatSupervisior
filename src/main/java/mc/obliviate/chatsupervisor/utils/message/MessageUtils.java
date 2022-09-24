package mc.obliviate.chatsupervisor.utils.message;

import mc.obliviate.util.string.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageUtils {

	private static YamlConfiguration messageConfig;
	private static String prefix;

	public static YamlConfiguration getMessageConfig() {
		return messageConfig;
	}

	public static void setMessageConfig(final YamlConfiguration messageConfig) {
		MessageUtils.messageConfig = messageConfig;
		MessageUtils.prefix = parseColor(messageConfig.getString("prefix"));
	}

	public static String getMessage(final String node) {
		String msg = messageConfig.getString(node);
		if (msg == null) {
			return "No Message Found: " + node;
		} else if (msg.equalsIgnoreCase("DISABLED")) {
			return null;
		}
		return msg;
	}

	public static List<String> getMessageList(final String node) {
		List<String> msg = messageConfig.getStringList(node);
		if (msg.size() == 0) {
			return Collections.singletonList("No Message List Found: " + node);
		} else if (msg.get(0).equalsIgnoreCase("DISABLED")) {
			return null;
		}
		return msg;
	}

	public static void sendMessage(final CommandSender player, final String configNode) {
		sendMessage(player, configNode, false);
	}

	public static void sendMessage(final CommandSender player, final String configNode, final PlaceholderUtil placeholderUtil) {
		sendMessage(player, configNode, placeholderUtil, false);
	}

	public static void sendMessage(final CommandSender player, final String configNode, final boolean forceDisablePrefix) {
		final String message = getMessage(configNode);
		if (message == null) return;
		send(player, parseColor(message), forceDisablePrefix);
	}

	public static void sendMessage(final CommandSender player, final String configNode, final PlaceholderUtil placeholderUtil, final boolean forceDisablePrefix) {
		final String message = getMessage(configNode);
		if (message == null) return;
		send(player, parse(message, placeholderUtil), forceDisablePrefix);
	}

	private static void send(final CommandSender player, String finalMessage, final boolean forceDisablePrefix) {
		if (!forceDisablePrefix) {
			if (finalMessage.startsWith("[DISABLE_PREFIX]")) {
				finalMessage = finalMessage.replace("[DISABLE_PREFIX]", "");
			} else {
				finalMessage = prefix + finalMessage;
			}
		}
		player.sendMessage(finalMessage);
	}

	public static String parse(String string, PlaceholderUtil placeholderUtil) {
		return parseColor(applyPlaceholders(string, placeholderUtil));
	}

	public static void sendMessageList(final CommandSender player, final String configNode) {
		sendMessageList(player, configNode, null);
	}

	public static void sendMessageList(final CommandSender player, final String configNode, PlaceholderUtil placeholderUtil) {
		final List<String> messages = parseColor(applyPlaceholders(getMessageList(configNode), placeholderUtil));
		for (final String str : messages) {
			send(player, str, true);
		}
	}

	public static String applyPlaceholders(String message, final PlaceholderUtil placeholderUtil) {
		if (message == null) return null;
		if (placeholderUtil == null) return message;
		for (final InternalPlaceholder placeholder : placeholderUtil.getPlaceholders()) {
			message = message.replace(placeholder.getPlaceholder(), placeholder.getValue());
		}
		return message;
	}

	public static List<String> applyPlaceholders(List<String> list, final PlaceholderUtil placeholderUtil) {
		if (placeholderUtil == null) return list;
		for (final InternalPlaceholder placeholder : placeholderUtil.getPlaceholders()) {
			list = replaceList(list, placeholder.getPlaceholder(), placeholder.getValue());
		}
		return list;
	}

	public static String parseColor(final String string) {
		if (string == null) return null;
		return StringUtil.parseColor(string);
		//return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static List<String> parseColor(final List<String> stringList) {
		if (stringList == null) return null;
		final List<String> result = new ArrayList<>();
		for (String str : stringList) {
			result.add(ChatColor.translateAlternateColorCodes('&', str));
		}
		return result;
	}

	public static List<String> replaceList(final List<String> stringList, final String search, final String replace) {
		if (stringList == null) return null;
		final List<String> result = new ArrayList<>();
		for (String str : stringList) {
			result.add(str.replace(search, replace));
		}
		return result;
	}

	public static List<String> splitMessage(final String message, final int splitRange, final String prefix) {
		if (true) return Arrays.asList(message);
		final List<String> result = new ArrayList<>();
		int length = message.length();
		int lineCount = 1;
		while (length > 0) {
			final String line = message.substring(lineCount * splitRange, Math.min((lineCount + 1) * splitRange, message.length()));
			result.add(prefix + line);
			length -= line.length();
			lineCount++;
		}
		return result;
	}


}
