package mc.obliviate.chatsupervisor.commands;

import mc.obliviate.chatsupervisor.ignore.IgnoredPlayersGUI;
import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.playerdata.PlayerData;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import mc.obliviate.chatsupervisor.utils.message.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

		if (!(sender instanceof Player)) return false;

		final Player player = (Player) sender;

		if (args.length == 0) {
			new IgnoredPlayersGUI(player).open();
			return false;
		}

		if (args[0].equalsIgnoreCase(player.getName())) {
			MessageUtils.sendMessage(sender, "you-can-not-ignore-yourself");
			return false;
		}

		final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

		final PlayerData playerData = DataHandler.getPlayerData(player.getUniqueId());

		if (playerData.getIgnoredPlayers().contains(target.getUniqueId())) {
			playerData.getIgnoredPlayers().remove(target.getUniqueId());
			MessageUtils.sendMessage(sender, "player-unignored", new PlaceholderUtil().add("{ignored}", target.getName()));
		} else {
			playerData.getIgnoredPlayers().add(target.getUniqueId());
			MessageUtils.sendMessage(sender, "player-ignored", new PlaceholderUtil().add("{ignored}", target.getName()));
		}
		DataHandler.insertData(playerData);

		return false;
	}


}
