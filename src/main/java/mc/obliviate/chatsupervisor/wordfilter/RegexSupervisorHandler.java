package mc.obliviate.chatsupervisor.wordfilter;

import mc.obliviate.chatsupervisor.wordfilter.regex.MatchedStringMeta;
import mc.obliviate.chatsupervisor.wordfilter.regex.RegexSupervisor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RegexSupervisorHandler {

	private final List<RegexSupervisor> regexSupervisorList = new LinkedList<>();

	public void init(final ConfigurationSection section) {
		if (!section.getBoolean("enabled")) return;
		loadAllRegexSupervisors(Objects.requireNonNull(section.getConfigurationSection("supervisors")));
	}

	private void loadAllRegexSupervisors(final ConfigurationSection section) {
		for (String regexSupervisorName : section.getKeys(false)) {
			loadRegexSupervisor(section.getConfigurationSection(regexSupervisorName));
		}
	}

	private void loadRegexSupervisor(final ConfigurationSection section) {
		final RegexSupervisor regexSupervisor = new RegexSupervisor(section);
		regexSupervisorList.add(regexSupervisor);
	}

	/**
	 * Checks with all regex supervisors
	 *
	 * @return meta of matched string
	 */
	@Nullable
	public MatchedStringMeta checkString(final @Nullable Player player, final @NotNull String message) {
		for (final RegexSupervisor rs : regexSupervisorList) {
			final MatchedStringMeta meta = rs.check(player, message);
			if (meta == null) continue;

			return meta;
		}
		return null;
	}

	/**
	 * Checks regexes with all regex supervisors and <b>executes</b>
	 * their commands when match.
	 *
	 * @return meta of matched string
	 */
	@Nullable
	public MatchedStringMeta checkStringAndExecute(final @NotNull Player player, final @NotNull String message) {
		for (final RegexSupervisor rs : regexSupervisorList) {
			final MatchedStringMeta meta = rs.checkAndExecute(player, message);
			if (meta == null) continue;
			return meta;
		}
		return null;
	}
}
