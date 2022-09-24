package mc.obliviate.chatsupervisor.commands;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import mc.obliviate.chatsupervisor.utils.message.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static mc.obliviate.chatsupervisor.utils.Utils.isOpOrConsole;

public class ClearChatCMD implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {

		if (isOpOrConsole(sender) || ChatSupervisor.getPermission().has(((Player) sender), "chatsupervisor.clearchat")) {

			final String executorName = sender instanceof Player ? sender.getName() : MessageUtils.getMessage("console");

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!ChatSupervisor.getPermission().has(p, "chatsupervisor.clearchat")) {
					for (int i = 0; i < 100; i++) {
						p.sendMessage("");
					}
				}
				MessageUtils.sendMessage(p, "chat-cleared", new PlaceholderUtil().add("{executor}", executorName));
			}

		} else {
			MessageUtils.sendMessage(sender, "no-permission");
		}

		return false;


	}
}
