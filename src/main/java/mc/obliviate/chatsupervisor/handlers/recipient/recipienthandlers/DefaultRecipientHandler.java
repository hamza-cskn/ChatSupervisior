package mc.obliviate.chatsupervisor.handlers.recipient.recipienthandlers;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.handlers.recipient.RecipientHandler;
import mc.obliviate.chatsupervisor.handlers.recipient.RecipientMode;
import mc.obliviate.chatsupervisor.handlers.recipient.RecipientResult;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

public class DefaultRecipientHandler extends RecipientHandler {


	public DefaultRecipientHandler(ChatSupervisor plugin) {
		super(plugin, RecipientMode.DEFAULT);
	}

	@Override
	public RecipientResult getRecipientResult(AsyncPlayerChatEvent event) {
		return new RecipientResult(new ArrayList<>(Bukkit.getOnlinePlayers()), event.getMessage(), "{format}");
	}

}
