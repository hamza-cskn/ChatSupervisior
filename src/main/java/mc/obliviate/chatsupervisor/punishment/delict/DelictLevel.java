package mc.obliviate.chatsupervisor.punishment.delict;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class DelictLevel {

	private final int level;
	private final List<String> commands;

	private DelictLevel(int level, List<String> commands) {
		this.level = level;
		this.commands = commands;
	}

	public static DelictLevel deserializeConfig(ConfigurationSection section) {
		if (section == null) return null;
		return new DelictLevel(section.getInt("level"), section.getStringList("commands"));
	}

	public int getLevel() {
		return level;
	}

	public List<String> getCommands() {
		return commands;
	}


}
