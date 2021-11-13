package mc.obliviate.chatsupervisor.punishment.delict;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.handlers.config.ConfigHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.playerdelictdata.PlayerDelictLog;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import mc.obliviate.chatsupervisor.utils.message.PlaceholderUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class DelictNotifier {

	private final ChatSupervisor plugin;

	public DelictNotifier(ChatSupervisor plugin) {
		this.plugin = plugin;
	}

	public void notify(OfflinePlayer target, DelictType delict, PlayerDelictLog log) {
		if (target.isOnline()) {
			final Player targetOnline = (Player) target;
			MessageUtils.sendMessageList(targetOnline, "warn-message", new PlaceholderUtil()
					.add("{reason}", delict.getReason())
					.add("{level}", log.getLevel() + "")
					.add("{message}", log.getMessage())
					.add("{staff}", log.getStaff())
					.add("{time}", ConfigHandler.getDateFormat().format(log.getTime()))
					.add("{player}", targetOnline.getName()));

		}
	}
}
