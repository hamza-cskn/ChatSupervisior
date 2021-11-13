package mc.obliviate.chatsupervisor.utils.message;

public class InternalPlaceholder {

	private final String placeholder;
	private final String value;

	protected InternalPlaceholder(String placeholder, String value) {
		this.placeholder = placeholder;
		this.value = value;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public String getValue() {
		return value;
	}
}
