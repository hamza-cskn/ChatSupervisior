package mc.obliviate.chatsupervisor.wordfilter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordFilter {

	private static final Pattern CAPS_REGEX = Pattern.compile("[A-Z]");
	private static final Pattern LINK_REGEX = Pattern.compile("(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})");
	private final List<Pattern> filterList = new ArrayList<>();

	public String checkRegex(String string) {
		string = "" + string;
		for (final Pattern filter : filterList) {
			final Matcher matcher = filter.matcher(string);
			if (matcher.find()) {
				return matcher.group();
			}
		}
		return "";
	}

	public boolean checkCaps(String string) {
		return CAPS_REGEX.matcher(string).group().length() > string.length() / 2;
	}

	public void addFilterList(String... regexes) {
		for (String regex : regexes) {
			filterList.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
		}
	}

	public String checkLink(String string) {
		final Matcher matcher = LINK_REGEX.matcher(string);
		if (matcher.find()) {
			return matcher.group();
		}
		return "";
	}
}
