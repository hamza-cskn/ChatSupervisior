package mc.obliviate.chatsupervisor.handlers.recipient;

import mc.obliviate.chatsupervisor.ChatSupervisor;

public abstract class RecipientHandler implements IRecipientHandler {

	private final ChatSupervisor plugin;
	private final RecipientMode mode;
	private String globalPrefix = null;
	private String perWorldGlobalPrefix = null;
	private String globalChatMetaFormat = null;
	private String perWorldGlobalChatMetaFormat = null;
	private int range;

	public RecipientHandler(final ChatSupervisor plugin, final RecipientMode mode) {
		this.plugin = plugin;
		this.mode = mode;
	}

	@Override
	public RecipientMode getMode() {
		return mode;
	}

	@Override
	public String getGlobalPrefix() {
		return globalPrefix;
	}

	@Override
	public void setGlobalPrefix(String globalPrefix) {
		this.globalPrefix = globalPrefix;
	}

	@Override
	public String getPerWorldGlobalPrefix() {
		return perWorldGlobalPrefix;
	}

	@Override
	public void setPerWorldGlobalPrefix(String perWorldGlobalPrefix) {
		this.perWorldGlobalPrefix = perWorldGlobalPrefix;
	}

	@Override
	public ChatSupervisor getPlugin() {
		return plugin;
	}

	@Override
	public int getRange() {
		return range;
	}

	@Override
	public void setRange(final int range) {
		this.range = range;
	}

	@Override
	public String getGlobalChatMetaFormat() {
		return globalChatMetaFormat;
	}

	@Override
	public void setGlobalChatMetaFormat(String globalChatMetaFormat) {
		this.globalChatMetaFormat = globalChatMetaFormat;
	}

	@Override
	public String getPerWorldGlobalChatMetaFormat() {
		return perWorldGlobalChatMetaFormat;
	}

	@Override
	public void setPerWorldGlobalChatMetaFormat(String perWorldGlobalChatMetaFormat) {
		this.perWorldGlobalChatMetaFormat = perWorldGlobalChatMetaFormat;
	}
}
