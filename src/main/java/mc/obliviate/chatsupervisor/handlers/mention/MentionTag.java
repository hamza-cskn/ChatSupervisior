package mc.obliviate.chatsupervisor.handlers.mention;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionTag {

	private final String format;
	private final List<Pattern> regexChecks;

	public MentionTag(String format, List<String> regexes) {
		this.format = format;
		List<Pattern> regexChecks = new ArrayList<>();
		for (String regex : regexes) {
			regexChecks.add(Pattern.compile(regex));
		}
		this.regexChecks = regexChecks;
	}

	public List<Pattern> getRegexChecks() {
		return regexChecks;
	}

	public String getFormat() {
		return format;
	}

	/**
	 * @param string
	 * @return matched string. (nullable)
	 */
	public String getMentionString(String string) {
		for (final Pattern pattern : regexChecks) {
			final Matcher matcher = pattern.matcher(string);
			if (matcher.find()) {
				return matcher.group();
			}
		}
		return null;
	}
}
