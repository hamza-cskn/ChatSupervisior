package mc.obliviate.chatsupervisor.wordfilter.executables;

import mc.obliviate.chatsupervisor.wordfilter.executables.types.ExecuteConsole;
import mc.obliviate.chatsupervisor.wordfilter.executables.types.ExecutePlayer;
import mc.obliviate.chatsupervisor.wordfilter.executables.types.MessagePlayer;

public class ExecutableParser {

	public static IExecutable parse(String rawString) {
		switch (rawString.split(" ")[0]) {
			case ExecutePlayer.prefix:
				return new ExecutePlayer(rawString);
			case MessagePlayer.prefix:
				return new MessagePlayer(rawString);
			case ExecuteConsole.prefix:
				return new ExecuteConsole(rawString);
		}
		throw new IllegalArgumentException("[ChatSupervisor] Executable string could not parsed: " + rawString);
	}

}
