package mc.obliviate.chatsupervisor.commands;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.listeners.ChatListener;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static mc.obliviate.chatsupervisor.utils.Utils.isOpOrConsole;

public class ChatLockCMD implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {

		if (isOpOrConsole(sender) || ChatSupervisor.getPermission().has(((Player) sender), "chatsupervisor.chatlock")) {
			ChatListener.chatLockEnabled = !ChatListener.chatLockEnabled;

			if (ChatListener.chatLockEnabled) {
				Bukkit.broadcastMessage(MessageUtils.parseColor(MessageUtils.getMessage("chat-lock.locked")));
			} else {
				Bukkit.broadcastMessage(MessageUtils.parseColor(MessageUtils.getMessage("chat-lock.unlocked")));
			}

		} else {
			MessageUtils.sendMessage(sender,"no-permission");
		}

		return false;
	}
}
