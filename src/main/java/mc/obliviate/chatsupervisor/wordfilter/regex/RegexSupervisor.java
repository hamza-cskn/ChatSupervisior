package mc.obliviate.chatsupervisor.wordfilter.regex;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Purpose of the class is,
 * storing regex checks, executable commands
 * and using them.
 */
public class RegexSupervisor {

	private final boolean cancelMessages;
	private final RegexCheckStorage regexCheck;
	private final ExecutableStorage executableStorage;
	private final String stringOverwriteFormat;

	public RegexSupervisor(boolean cancelMessages, RegexCheckStorage regexCheck, ExecutableStorage executableStorage, String stringOverwriteFormat) {
		this.cancelMessages = cancelMessages;
		this.regexCheck = regexCheck;
		this.executableStorage = executableStorage;
		this.stringOverwriteFormat = stringOverwriteFormat;
	}

	public RegexSupervisor(ConfigurationSection section) {
		assert section != null;
		this.regexCheck = new RegexCheckStorage().addRegex(section.getStringList("regex-checks"));
		this.executableStorage = new ExecutableStorage(section);
		this.cancelMessages = section.getBoolean("block-message", true);
		final String format = section.getString("when-match.overwrite-matched-string");
		if (format == null) {
			this.stringOverwriteFormat = "{matched-string}";
		} else {
			this.stringOverwriteFormat = ChatColor.translateAlternateColorCodes('&', format);
		}
	}

	@Nullable
	public MatchedStringMeta check(Player player, String message) {
		final String matchedString = regexCheck.getMatchedString(message);
		if (matchedString == null) return null;
		return new MatchedStringMeta(cancelMessages, this, message, matchedString, player);
	}

	@Nullable
	public MatchedStringMeta checkAndExecute(Player player, String message) {
		final MatchedStringMeta meta = check(player, message);
		if (meta == null) return null;
		execute(meta);
		return meta;
	}

	public void execute(MatchedStringMeta meta) {
		executableStorage.executeExecutables(meta);
	}

	protected String getStringOverwriteFormat() {
		return stringOverwriteFormat;
	}
}
