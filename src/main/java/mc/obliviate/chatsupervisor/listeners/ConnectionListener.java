package mc.obliviate.chatsupervisor.listeners;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

	private final ChatSupervisor plugin;

	public ConnectionListener(ChatSupervisor plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		plugin.getPrivateMessageCMD().removeLastReceiverCache(e.getPlayer().getUniqueId());
		plugin.getChatListener().removeStaffList(e.getPlayer());

	}

}
