package mc.obliviate.chatsupervisor.handlers.format;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.format.ChatFormat;
import mc.obliviate.chatsupervisor.handlers.config.ConfigHandler;
import mc.obliviate.chatsupervisor.handlers.mention.MentionTagHandler;

import java.util.HashMap;
import java.util.Map;

public abstract class FormatHandlerAbstract {

	private static final Map<String, ChatFormat> formats = new HashMap<>();
	protected final MentionTagHandler mentionTagHandler;
	private final ChatSupervisor plugin;

	public FormatHandlerAbstract(ChatSupervisor plugin) {
		this.mentionTagHandler = ConfigHandler.config.getBoolean("mention-tag.enabled") ? new MentionTagHandler(ConfigHandler.config) : null;
		this.plugin = plugin;
	}

	public static Map<String, ChatFormat> getFormats() {
		return formats;
	}

	public static ChatFormat getChatFormat(String group) {
		return formats.getOrDefault(group, formats.get("default"));
	}

	public String formatMentionTag(String string) {
		if (mentionTagHandler == null) return string;

		return mentionTagHandler.formatString(string);
	}


}
