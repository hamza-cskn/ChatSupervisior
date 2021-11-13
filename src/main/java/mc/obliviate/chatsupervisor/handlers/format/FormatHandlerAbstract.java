package mc.obliviate.chatsupervisor.handlers.format;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.format.ChatFormat;

import java.util.HashMap;
import java.util.Map;

public abstract class FormatHandlerAbstract {

	private static final Map<String, ChatFormat> formats = new HashMap<>();
	private final ChatSupervisor plugin;

	public FormatHandlerAbstract(ChatSupervisor plugin) {
		this.plugin = plugin;
	}

	public static Map<String, ChatFormat> getFormats() {
		return formats;
	}

	public static ChatFormat getChatFormat(String group) {
		return formats.getOrDefault(group, formats.get("default"));
	}


}
