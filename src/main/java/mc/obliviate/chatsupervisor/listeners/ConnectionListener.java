package mc.obliviate.chatsupervisor.listeners;

import mc.obliviate.chatsupervisor.commands.PrivateMessageCMD;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		PrivateMessageCMD.clearLastReceiverCache(e.getPlayer().getUniqueId());
	}

}
