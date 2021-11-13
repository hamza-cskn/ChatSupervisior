package mc.obliviate.chatsupervisor.commands;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static mc.obliviate.chatsupervisor.utils.Utils.isOpOrConsole;

public class ChatSupervisorCMD implements CommandExecutor {

	private final ChatSupervisor plugin;

	public ChatSupervisorCMD(ChatSupervisor plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {

		if (isOpOrConsole(sender)) {
			sender.sendMessage(ChatColor.YELLOW + "ChatSupervisor yapılandırma dosyaları yeniden yükleniyor...");
			plugin.getConfigHandler().loadConfig();
			plugin.registerCommands();
			plugin.registerListeners();
			sender.sendMessage(ChatColor.YELLOW + "ChatSupervisor yapılandırma dosyaları yeniden yüklendi!");
		}
		return false;
	}
}
