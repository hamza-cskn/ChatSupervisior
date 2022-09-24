package mc.obliviate.chatsupervisor.handlers.mention;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MentionTagHandler {

	private final boolean regexTrimEnabled;
	private final MentionTag playerTag;

	public MentionTagHandler(ConfigurationSection config) {
		playerTag = new MentionTag(config.getString("mention-tag.format"), config.getStringList("mention-tag.regex-checks"));
		regexTrimEnabled = config.getBoolean("mention-tag.trim-first-character");
	}

	public String formatString(String string) {
		return formatStringForPlayerTag(string);
	}

	public String formatStringForPlayerTag(String string) {
		final String mentionString = playerTag.getMentionString(string); //"@Mr_Obliviate"
		final Player player = findPlayerByMentionString(mentionString);

		if (player == null) return string;

		final String formattedMentionString = ChatColor.translateAlternateColorCodes('&', playerTag.getFormat().replace("{player}", player.getName())) + ChatColor.getLastColors(string);

		return string.replace(mentionString, formattedMentionString);

	}

	private Player findPlayerByMentionString(String mentionString) {
		if (mentionString == null || mentionString.length() == 1) return null;
		return Bukkit.getPlayerExact(trimFirstChar(mentionString));
	}

	private String trimFirstChar(String string) {
		return new StringBuilder(string).deleteCharAt(0).toString();
	}

}
