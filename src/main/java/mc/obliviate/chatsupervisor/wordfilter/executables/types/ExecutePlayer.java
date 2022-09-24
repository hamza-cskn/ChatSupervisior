package mc.obliviate.chatsupervisor.wordfilter.executables.types;

import mc.obliviate.chatsupervisor.wordfilter.executables.IExecutable;
import mc.obliviate.chatsupervisor.wordfilter.regex.MatchedStringMeta;

public class ExecutePlayer implements IExecutable {

	public static final String prefix = "[EXECUTE_PLAYER]";
	private final String rawString;
	private final String command;

	public ExecutePlayer(String rawString) {
		this.rawString = rawString;
		this.command = rawString.substring(prefix.length()+1);
	}

	@Override
	public void execute(MatchedStringMeta matchedStringMeta) {
		if (matchedStringMeta.getPlayer() == null) return;
		matchedStringMeta.getPlayer().performCommand(command);
	}
}
