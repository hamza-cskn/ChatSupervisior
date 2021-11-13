package mc.obliviate.chatsupervisor.handlers.recipient.recipienthandlers;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.handlers.recipient.RecipientHandler;
import mc.obliviate.chatsupervisor.handlers.recipient.RecipientMode;
import mc.obliviate.chatsupervisor.handlers.recipient.RecipientResult;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PerWorldRecipientHandler extends RecipientHandler {

    public PerWorldRecipientHandler(ChatSupervisor plugin) {
        super(plugin, RecipientMode.PER_WORLD);
    }

    @Override
    public RecipientResult getRecipientResult(AsyncPlayerChatEvent event) {
        return new RecipientResult(event.getPlayer().getWorld().getPlayers(), event.getMessage(), "{format}");
    }

}
