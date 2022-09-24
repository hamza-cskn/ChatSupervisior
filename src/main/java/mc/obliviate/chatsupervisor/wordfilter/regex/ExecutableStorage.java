package mc.obliviate.chatsupervisor.wordfilter.regex;

import mc.obliviate.chatsupervisor.wordfilter.executables.ExecutableParser;
import mc.obliviate.chatsupervisor.wordfilter.executables.IExecutable;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose of the class,
 * storing executables and execute them.
 */
public class ExecutableStorage {

	private final List<IExecutable> executableList;

	protected ExecutableStorage(ConfigurationSection section) {
		this.executableList = parseExecutables(section.getStringList("when-match.execute"));
	}

	private static List<IExecutable> parseExecutables(List<String> list) {
		List<IExecutable> executables = new ArrayList<>();
		for (String rawString : list) {
			executables.add(ExecutableParser.parse(rawString));
		}
		return executables;
	}

	protected void executeExecutables(MatchedStringMeta matchedStringMeta) {
		for (IExecutable executable : executableList) {
			executable.execute(matchedStringMeta);
		}
	}

	public List<IExecutable> getExecutableList() {
		return executableList;
	}
}
