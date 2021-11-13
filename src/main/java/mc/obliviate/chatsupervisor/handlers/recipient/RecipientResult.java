package mc.obliviate.chatsupervisor.handlers.recipient;

import org.bukkit.entity.Player;

import java.util.List;

public class RecipientResult {

	private final List<Player> recipients;
	private final String trimmedMessage;
	private final String metaFormat;

	public RecipientResult(final List<Player> recipients, String trimmedMessage, final String metaFormat) {
		this.recipients = recipients;
		this.trimmedMessage = trimmedMessage;
		this.metaFormat = metaFormat;
	}

	/**
	 * @return players that should see message.
	 */
	public List<Player> getRecipients() {
		return recipients;
	}

	/**
	 * @return meta format of format.
	 */
	public String getMetaFormat() {
		return metaFormat;
	}

	/**
	 * @return player's message without channel prefix.
	 */
	public String getTrimmedMessage() {
		return trimmedMessage;
	}
}
