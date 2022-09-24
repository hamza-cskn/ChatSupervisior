package mc.obliviate.chatsupervisor.wordfilter.regex;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Purpose of the class is,
 * storing regex checks and check them.
 */
public class RegexCheckStorage {

	private final List<Pattern> regexCheckList;

	protected RegexCheckStorage(List<Pattern> regexCheckList) {
		this.regexCheckList = regexCheckList;
	}

	protected RegexCheckStorage() {
		this(new ArrayList<>());
	}

	public RegexCheckStorage addRegex(String... regexes) {
		addRegex(Arrays.asList(regexes));
		return this;
	}

	public RegexCheckStorage addRegex(List<String> regexes) {
		for (String regex : regexes) {
			regexCheckList.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
		}
		return this;
	}

	@Nullable
	public String getMatchedString(String string) {
		if (regexCheckList.isEmpty()) return string;

		string = "" + string;
		for (final Pattern filter : regexCheckList) {
			final Matcher matcher = filter.matcher(string);
			if (matcher.find()) {
				Bukkit.getLogger().info(filter.pattern());
				Bukkit.getLogger().info(matcher.group() + "");
				return matcher.group();
			}
		}
		return null;
	}

	public List<Pattern> getRegexCheckList() {
		return regexCheckList;
	}
}
