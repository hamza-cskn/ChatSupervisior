package mc.obliviate.chatsupervisor.commands;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static mc.obliviate.chatsupervisor.utils.Utils.isOpOrConsole;

public class ChatLogCMD implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
		if (isOpOrConsole(sender) || ChatSupervisor.getPermission().has(((Player) sender), "chatsupervisor.chatlog")) {
			sender.sendMessage("this feature is under development");
		} else {
			MessageUtils.sendMessage(sender,"no-permission");
		}

		return false;
	}
}
