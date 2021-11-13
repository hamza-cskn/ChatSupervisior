package mc.obliviate.chatsupervisor.commands;

import mc.obliviate.chatsupervisor.punishment.gui.DelictLogGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelictLogCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

		if (!(sender instanceof Player)) return false;

		final Player player = (Player) sender;
		final String arg0;

		if (args.length == 0 || !player.isOp()) {
			arg0 = player.getName();
		} else {
			arg0 = args[0];
		}


		new DelictLogGUI(player, Bukkit.getOfflinePlayer(arg0)).open();

		return false;
	}
}
