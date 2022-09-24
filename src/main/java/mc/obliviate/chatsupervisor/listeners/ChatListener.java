package mc.obliviate.chatsupervisor.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.format.ChatFormat;
import mc.obliviate.chatsupervisor.format.ChatFormatMeta;
import mc.obliviate.chatsupervisor.handlers.config.ConfigHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.handlers.format.FormatHandlerAbstract;
import mc.obliviate.chatsupervisor.handlers.recipient.IRecipientHandler;
import mc.obliviate.chatsupervisor.handlers.recipient.RecipientResult;
import mc.obliviate.chatsupervisor.utils.log.LogUtils;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import mc.obliviate.chatsupervisor.utils.message.PlaceholderUtil;
import mc.obliviate.chatsupervisor.wordfilter.regex.MatchedStringMeta;
import org.bukkit.Bukkit;
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
	private final boolean logToConsole;
	private final ChatSupervisor plugin;
	private final IRecipientHandler recipientHandler;
	private final Cache<UUID, Long> playerTimeoutMap;
	private final List<Player> staffChatPlayers = new ArrayList<>();

	public ChatListener(final ChatSupervisor plugin) {
		this.plugin = plugin;
		this.recipientHandler = plugin.getRecipientHandler();
		this.logToConsole = ConfigHandler.shouldChatLogToConsole();

		int maxCooldown = 0;
		for (ChatFormat format : FormatHandlerAbstract.getFormats().values()) {
			if (format.getCooldown() > maxCooldown) {
				maxCooldown = format.getCooldown();
			}
		}
		//cache time out time will be set to max cooldown of groups
		playerTimeoutMap = CacheBuilder.newBuilder().expireAfterAccess(maxCooldown, TimeUnit.SECONDS).build();
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onChat(final AsyncPlayerChatEvent event) {
		event.setCancelled(true);

		final boolean isInStaffMode = staffChatPlayers.contains(event.getPlayer());

		//log stuffs
		if (ConfigHandler.islogFileEnabled()) {
			String log = event.getPlayer().getName() + ": " + event.getMessage();
			if (chatLockEnabled) {
				log = "[CHAT_LOCKED] " + log;
			}
			if (isInStaffMode) {
				log = "[STAFF_CHAT] " + log;
			}
			LogUtils.log(log);
			if (logToConsole) {
				Bukkit.getLogger().info(log);
			}
		}

		if (isInStaffMode && ChatSupervisor.getPermission().has(event.getPlayer(), "chatsupervisor.staffchat")) {
			sendStaffChatMessage(event.getPlayer(), event.getMessage());
			return;
		}

		if (chatLockEnabled && !ChatSupervisor.getPermission().has(event.getPlayer(), "chatsupervisor.bypass.chatlock")) {
			MessageUtils.sendMessage(event.getPlayer(), "chat-lock.you-can-not-use-chat");
			return;
		}

		final Player sender = event.getPlayer();
		final ChatFormat format = FormatHandlerAbstract.getChatFormat(ChatSupervisor.getPermission().getPrimaryGroup(sender));

		if (!checkCooldown(sender, format)) return;

		final RecipientResult recipientResult = recipientHandler.getRecipientResult(event); //if you want to channel system code here
		String message = recipientResult.getTrimmedMessage();

		final MatchedStringMeta matchedStringMeta = plugin.getRegexSupervisorHandler().checkStringAndExecute(event.getPlayer(), message);
		if (matchedStringMeta != null && matchedStringMeta.shouldMessageCancelled()) {
			return;
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

	private void sendStaffChatMessage(Player p, String message) {
		final String format = MessageUtils.parse(ConfigHandler.config.getString("staff-chat.format"), new PlaceholderUtil().add("{message}", message).add("{sender}", p.getName()));
		for (Player player : staffChatPlayers) {
			player.sendMessage(format);
		}
	}

	public void removeStaffList(Player p) {
		staffChatPlayers.remove(p);
	}

	public List<Player> getStaffChatPlayers() {
		return staffChatPlayers;
	}
}
