package mc.obliviate.chatsupervisor.utils.message;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderUtil {

	private final List<InternalPlaceholder> placeholders = new ArrayList<>();

	public PlaceholderUtil add(final String key, final String value) {
		placeholders.add(new InternalPlaceholder(key, value));
		return this;
	}

	public List<InternalPlaceholder> getPlaceholders() {
		return placeholders;
	}
}
