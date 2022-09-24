package mc.obliviate.chatsupervisor.wordfilter.regex;

import org.bukkit.entity.Player;

/**
 * Purpose of the class is,
 * storing datas about a message that is
 * matched with regex checks of regex supervisor.
 * <p>
 * following placeholders targeted:
 * {player} {message} {matched-string} {raw-message}
 */
public class MatchedStringMeta {

	private final boolean shouldMessageCancelled;
	private final RegexSupervisor regexSupervisor;
	private final String rawMessage;
	private final String matchedString;
	private final Player player;

	protected MatchedStringMeta(boolean shouldMessageCancelled, RegexSupervisor regexSupervisor, String rawMessage, String matchedString, Player player) {
		this.shouldMessageCancelled = shouldMessageCancelled;
		this.regexSupervisor = regexSupervisor;
		this.rawMessage = rawMessage;
		this.matchedString = matchedString;
		this.player = player;
	}

	public String applyPlaceholders(String string) {
		return string
				.replace("{message}", getMessage())
				.replace("{player}", player.getName())
				.replace("{matched-string}", matchedString)
				.replace("{raw-message}", rawMessage);
	}

	public String getMessage() {
		return rawMessage.replace(matchedString, regexSupervisor.getStringOverwriteFormat().replace("{matched-string}", matchedString));
	}

	public String getMatchedString() {
		return matchedString;
	}

	public String getRawMessage() {
		return rawMessage;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean shouldMessageCancelled() {
		return shouldMessageCancelled;
	}
}
