package mc.obliviate.chatsupervisor.handlers.recipient.recipienthandlers;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.handlers.recipient.RecipientHandler;
import mc.obliviate.chatsupervisor.handlers.recipient.RecipientMode;
import mc.obliviate.chatsupervisor.handlers.recipient.RecipientResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class PerWorldGlobalRecipientHandler extends RecipientHandler {

	public PerWorldGlobalRecipientHandler(ChatSupervisor plugin) {
		super(plugin, RecipientMode.PER_WORLD_GLOBAL);
	}

	@Override
	public RecipientResult getRecipientResult(AsyncPlayerChatEvent event) {
		final String message = event.getMessage();

		if (message.startsWith(getPerWorldGlobalPrefix())) {
			return new RecipientResult(new ArrayList<>(Bukkit.getOnlinePlayers()), message.substring(getPerWorldGlobalPrefix().length()), getPerWorldGlobalChatMetaFormat());
		} else if (message.startsWith(getGlobalPrefix())) {
			return new RecipientResult(event.getPlayer().getWorld().getPlayers(), message.substring(getGlobalPrefix().length()), getGlobalChatMetaFormat());

		} else {
			return new RecipientResult(getRangedPlayers(getRange(), event.getPlayer()), message, "{format}");
		}

	}

	private List<Player> getRangedPlayers(int range, Player player) {
		final List<Player> result = new ArrayList<>();
		final Location loc = player.getLocation();
		for (final Player p : player.getWorld().getPlayers()) {
			if (loc.distance(p.getLocation()) < range) {
				result.add(p);
			}
		}
		return result;
	}



}
