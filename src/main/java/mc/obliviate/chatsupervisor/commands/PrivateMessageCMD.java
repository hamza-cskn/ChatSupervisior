package mc.obliviate.chatsupervisor.commands;

import mc.obliviate.chatsupervisor.handlers.config.ConfigHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.utils.log.LogUtils;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import mc.obliviate.chatsupervisor.utils.message.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrivateMessageCMD implements CommandExecutor {

	private static final Map<UUID, UUID> playerLastReceiverMap = new HashMap<>();
	private final String format;
	private final String replacePlayerName;

	public PrivateMessageCMD(String format, String replacePlayerName) {
		this.format = format;
		this.replacePlayerName = replacePlayerName;
	}

	public static void clearLastReceiverCache(UUID uuid) {
		playerLastReceiverMap.remove(uuid);
	}

	@Override
	public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command cmd, final @NotNull String s, final @NotNull String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command only can be used from a player.");
			return false;
		}

		if (cmd.getName().equalsIgnoreCase("pm")) {
			if (args.length < 2) {
				MessageUtils.sendMessage(sender, "private-message.usage", new PlaceholderUtil().add("{command}", cmd.getName()));
				return false;
			}

			final Player p = (Player) sender;
			final Player target = Bukkit.getPlayer(args[0]);

			if (target == null || !target.isOnline()) {
				MessageUtils.sendMessage(sender, "private-message.target-is-offline", new PlaceholderUtil().add("{target}", args[0]));
				return false;
			}

			if (DataHandler.isIgnoresPlayer(target, p)) {
				MessageUtils.sendMessage(sender, "private-message.target-ignoring-you", new PlaceholderUtil().add("{target}", target.getName()));
				return false;
			}

			playerLastReceiverMap.put(p.getUniqueId(), target.getUniqueId());
			playerLastReceiverMap.put(target.getUniqueId(), p.getUniqueId());
			sendPM(String.join(" ", Arrays.asList(args).subList(1, args.length)), p, target);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("r")) {
			if (args.length == 0) {
				MessageUtils.sendMessage(sender, "private-message.usage", new PlaceholderUtil().add("{command}", cmd.getName()));
				return false;
			}

			final Player p = (Player) sender;
			final UUID targetUniqueId = playerLastReceiverMap.get(p.getUniqueId());
			final Player target = Bukkit.getPlayer(targetUniqueId);

			if (target == null || !target.isOnline()) {
				MessageUtils.sendMessage(sender, "private-message.target-is-offline", new PlaceholderUtil().add("{target}", Bukkit.getOfflinePlayer(targetUniqueId).getName()));
				return false;
			}

			sendPM(String.join(" ", Arrays.asList(args)), p, target);


			return true;
		}

		return false;
	}


	private void sendPM(final String message, final Player sender, final Player receiver) {


		if (ConfigHandler.isLogPMEnabled()) {
			LogUtils.log("[PM] " + sender.getName() + " > " + receiver.getName() + ": " + message);
		}

		String fromFormatted;
		String toFormatted;
		if (!replacePlayerName.equalsIgnoreCase("disabled")) {
			fromFormatted = format.replace("{from}", replacePlayerName).replace("{to}", receiver.getName());
			toFormatted = format.replace("{to}", replacePlayerName).replace("{from}", sender.getName());
		} else {
			fromFormatted = format.replace("{from}", sender.getName()).replace("{to}", receiver.getName());
			toFormatted = format.replace("{from}", sender.getName()).replace("{to}", receiver.getName());
		}

		toFormatted = MessageUtils.parseColor(toFormatted.replace("{message}", message));
		fromFormatted = MessageUtils.parseColor(fromFormatted.replace("{message}", message));


		sender.sendMessage(fromFormatted);
		receiver.sendMessage(toFormatted);
	}


}
