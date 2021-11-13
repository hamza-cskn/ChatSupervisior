package mc.obliviate.chatsupervisor.handlers.recipient;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface IRecipientHandler {

	RecipientResult getRecipientResult(AsyncPlayerChatEvent event);

	RecipientMode getMode();

	String getGlobalPrefix();

	void setGlobalPrefix(String prefix);

	String getPerWorldGlobalPrefix();

	void setPerWorldGlobalPrefix(String prefix);

	ChatSupervisor getPlugin();

	int getRange();

	void setRange(int range);

	String getGlobalChatMetaFormat();

	void setGlobalChatMetaFormat(String metaFormat);

	String getPerWorldGlobalChatMetaFormat();

	void setPerWorldGlobalChatMetaFormat(String metaFormat);
}
