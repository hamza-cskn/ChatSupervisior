package mc.obliviate.chatsupervisor.wordfilter.executables.types;

import mc.obliviate.chatsupervisor.wordfilter.executables.IExecutable;
import mc.obliviate.chatsupervisor.wordfilter.regex.MatchedStringMeta;
import org.bukkit.ChatColor;

public class MessagePlayer implements IExecutable {

	public static final String prefix = "[MESSAGE_PLAYER]";
	private final String rawString;
	private final String configMessage;

	public MessagePlayer(String rawString) {
		this.rawString = rawString;
		this.configMessage = rawString.substring(prefix.length() + 1);
	}

	public void execute(MatchedStringMeta matchedStringMeta) {
		matchedStringMeta.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', matchedStringMeta.applyPlaceholders(configMessage)));
	}
}
