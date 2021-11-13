package mc.obliviate.chatsupervisor.commands;

import mc.obliviate.chatsupervisor.punishment.gui.PunishmentGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishCMD implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args) {

		if (!(sender instanceof Player)) return false;

		final Player player = (Player) sender;

		if (args.length > 1) {
			final String arg0 = args[0];
			args[0] = "";
			new PunishmentGUI(player, Bukkit.getOfflinePlayer(arg0), String.join(" ", args)).open();
		}
		/*
		final PlayerData playerData = DataHandler.getPlayerData(player.getUniqueId());

		playerData.deleteMessageFromLog(playerData.getMessagePacketLog().size());
		final List<Component> stringList = playerData.getMessagePacketLog();

		for (final Component str : stringList) {
			player.sendMessage(str);
		}
        */
		return false;
	}
}
