package mc.obliviate.chatsupervisor.format;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;

public class ChatFormat {

	private final String format;
	private final int cooldown;

	public ChatFormat(final String format, int cooldown) {
		this.format = format;
		this.cooldown = cooldown;
	}

	public String convert(final ChatFormatMeta chatFormatMeta) {
		String message = chatFormatMeta.getMessage();
		if (ChatSupervisor.getPermission().has(chatFormatMeta.getSender(),"chatsupervisor.color")) message = MessageUtils.parseColor(message);
		return format
				.replace("{player}", chatFormatMeta.getSender().getName())
				.replace("{message}", message);
	}

	public int getCooldown() {
		return cooldown;
	}

	public String toString() {
		return format;
	}

}
