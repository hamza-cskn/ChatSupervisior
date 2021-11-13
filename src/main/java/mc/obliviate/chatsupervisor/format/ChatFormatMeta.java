package mc.obliviate.chatsupervisor.format;

import org.bukkit.entity.Player;

public class ChatFormatMeta {

	private final Player sender;
	private final Player receiver;
	private final String message;
	private final String metaFormat;

	public ChatFormatMeta(final Player sender, final Player receiver, final String message, final String metaFormat) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.metaFormat = metaFormat;
	}

	public Player getSender() {
		return sender;
	}

	public Player getReceiver() {
		return receiver;
	}

	public String getMessage() {
		return message;
	}

	public String getMetaFormat() {
		return metaFormat;
	}
}
