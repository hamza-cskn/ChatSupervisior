package mc.obliviate.chatsupervisor.commands;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StaffChatCMD implements CommandExecutor {

	private final ChatSupervisor plugin;

	public StaffChatCMD(ChatSupervisor plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command only can be used from a player.");
			return false;
		}
		final Player player = ((Player) sender);

		if (ChatSupervisor.getPermission().has(player, "chatsupervisor.staffchat")) {
			if (plugin.getChatListener().getStaffChatPlayers().contains(player)) {
				plugin.getChatListener().getStaffChatPlayers().remove(player);
				MessageUtils.sendMessage(player, "staff-chat.disabled");
			} else {
				plugin.getChatListener().getStaffChatPlayers().add(player);
				MessageUtils.sendMessage(player, "staff-chat.enabled");
			}

		} else {
			MessageUtils.sendMessage(player, "no-permission");
		}

		return false;
	}
}
