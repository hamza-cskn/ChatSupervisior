package mc.obliviate.chatsupervisor.handlers.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.handlers.HookHandler;
import mc.obliviate.chatsupervisor.listeners.packet.ChatPacketListener;
import org.bukkit.Bukkit;

public class ProtocolLibHandler implements HookHandler {

	private static boolean isDisabled;
	private ProtocolManager protocolManager;

	public static boolean isDisabled() {
		return isDisabled;
	}

	@Override
	public void init(ChatSupervisor plugin) {
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
			if (true) return;
			protocolManager = ProtocolLibrary.getProtocolManager();
			protocolManager.addPacketListener(new ChatPacketListener(plugin));
			plugin.getLogger().info("ProtocolLib plugin found. The API successfully hooked.");
			isDisabled = false;
			return;
		}
		isDisabled = true;
	}


}
