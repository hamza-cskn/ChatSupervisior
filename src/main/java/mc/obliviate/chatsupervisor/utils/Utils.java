package mc.obliviate.chatsupervisor.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {

	public static boolean isOpOrConsole(CommandSender sender) {
		if (sender instanceof Player) return sender.isOp();
		return true;
	}


}
