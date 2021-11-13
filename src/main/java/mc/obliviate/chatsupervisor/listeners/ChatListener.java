package mc.obliviate.chatsupervisor.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.format.ChatFormatMeta;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandler1_16;
import mc.obliviate.chatsupervisor.handlers.config.ConfigHandler;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandlerAbstract;
import mc.obliviate.chatsupervisor.handlers.recipient.IRecipientHandler;
import mc.obliviate.chatsupervisor.handlers.recipient.RecipientResult;
import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.format.ChatFormat;
import mc.obliviate.chatsupervisor.utils.log.LogUtils;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import mc.obliviate.chatsupervisor.utils.message.PlaceholderUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatListener implements Listener {

	public static boolean chatLockEnabled = false;
	private final ChatSupervisor plugin;
	private final IRecipientHandler recipientHandler;
	private final Cache<UUID, Long> playerTimeoutMap;

	public ChatListener(int cacheTimeOut, final ChatSupervisor plugin) {
		this.plugin = plugin;
		this.recipientHandler = plugin.getRecipientHandler();
		playerTimeoutMap = CacheBuilder.newBuilder().expireAfterAccess(cacheTimeOut, TimeUnit.SECONDS).build();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChat(final AsyncPlayerChatEvent event) {
		event.setCancelled(true);

		if (ConfigHandler.isLogChatEnabled()) {
			String log = event.getPlayer().getName() + ": " + event.getMessage();
			if (chatLockEnabled) {
				log = "[CHAT_LOCKED] " + log;
			}
			LogUtils.log(log);
		}

		if (chatLockEnabled && !ChatSupervisor.getPermission().has(event.getPlayer(), "chatsupervisor.bypass.chatlock")) {
			MessageUtils.sendMessage(event.getPlayer(), "chat-lock.you-can-not-use-chat");
			return;
		}

		final Player sender = event.getPlayer();
		final ChatFormat format = FormatHandlerAbstract.getChatFormat(ChatSupervisor.getPermission().getPrimaryGroup(sender));

		if (!checkCooldown(sender, format)) return;

		final RecipientResult recipientResult = recipientHandler.getRecipientResult(event);
		String message = recipientResult.getTrimmedMessage();

		final String badwordCatch = plugin.getWorldFilter().checkRegex(event.getMessage());
		if (!badwordCatch.isEmpty()) {
			final String msg = event.getMessage().replace(badwordCatch, MessageUtils.parseColor(MessageUtils.getMessage("blocked-message.blocked-part-of-message")));
			MessageUtils.sendMessage(sender, "blocked-message.message", new PlaceholderUtil().add("{message}", msg).add("{blocked-part}", badwordCatch));
			Bukkit.getScheduler().runTask(plugin,() -> {

				for (final String cmd : ConfigHandler.config.getStringList("auto-mute.mute-commands")) {
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd.replace("{player}", sender.getName()));
				}
			});

			return;
		}

		final String linkCatch = plugin.getWorldFilter().checkLink(event.getMessage());
		if (!linkCatch.isEmpty()) {
			message = message.replace(linkCatch, ChatColor.AQUA + linkCatch + ChatColor.WHITE);

		}

		//remove ignored players
		final List<Player> removeList = new ArrayList<>();
		for (Player p : recipientResult.getRecipients()) {
			if (DataHandler.isIgnoresPlayer(p, sender)) {
				removeList.add(p);
			}
		}
		recipientResult.getRecipients().removeAll(removeList);

		for (final Player p : recipientResult.getRecipients()) {
			plugin.getFormatHandler().send(p, new ChatFormatMeta(sender, p, message, recipientResult.getMetaFormat()), format);
			//DataHandler.getPlayerData(p.getUniqueId()).addMessagePacketLog(component);
		}

	}

	private boolean checkCooldown(final Player sender, final ChatFormat format) {
		final Long timeout = playerTimeoutMap.asMap().get(sender.getUniqueId());
		if (timeout != null && System.currentTimeMillis() < timeout) {
			MessageUtils.sendMessage(sender, "cooldown.chat");
			return false;
		}

		// the player is no longer timed out
		final int cooldown = format.getCooldown();
		if (cooldown > 0) {
			playerTimeoutMap.put(sender.getUniqueId(), System.currentTimeMillis() + cooldown);
		}
		return true;
	}

}
