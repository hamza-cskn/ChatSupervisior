package mc.obliviate.chatsupervisor.handlers.placeholderapi;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.handlers.HookHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderAPIHandler implements HookHandler {

	private static boolean isDisabled;

	public static String parsePlaceholders(final Player player, final String text) {
		if (isDisabled()) return text;
		return PlaceholderAPI.setPlaceholders(player, text);
	}

	public static boolean isDisabled() {
		return isDisabled;
	}

	public void init(ChatSupervisor plugin) {
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			plugin.getLogger().info("PlaceholderAPI plugin found. The API successfully hooked.");
			isDisabled = false;
			return;
		}
		isDisabled = true;
	}
}
