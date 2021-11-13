package mc.obliviate.chatsupervisor.punishment.delict;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.playerdelictdata.PlayerDelictData;
import mc.obliviate.chatsupervisor.handlers.datahandler.playerdelictdata.PlayerDelictLog;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DelictType {

	private static final List<DelictType> DELICT_TYPE_TYPES = new ArrayList<>();
	private static int warnCooldown = 30 * 1000;
	private final Cache<UUID, Long> playerWarnCooldownMap = CacheBuilder.newBuilder().expireAfterAccess(300, TimeUnit.SECONDS).build();
	private final DelictNotifier delictNotifier;
	private final String name;
	private final String reason;
	private final Map<Integer, DelictLevel> delictLevels;
	private final Material displayIcon;

	private DelictType(DelictNotifier notifier, String name, String reason, Map<Integer, DelictLevel> delictLevels, Material displayIcon) {
		this.delictNotifier = notifier;
		this.name = name;
		this.reason = reason;
		this.delictLevels = delictLevels;
		this.displayIcon = displayIcon;
	}

	public static List<DelictType> getDelictTypes() {
		return DELICT_TYPE_TYPES;
	}

	public static DelictType deserializeConfig(DelictNotifier notifier, ConfigurationSection section) {
		if (section == null) return null;
		Material material = Material.getMaterial(section.getString("display-icon", "STONE"));
		if (material == null) {
			Bukkit.getLogger().severe("Material could not found: " + section.getString("display-icon"));
			material = Material.BEDROCK;
		}

		final Map<Integer, DelictLevel> delictLevels = new HashMap<>();
		final ConfigurationSection delictLevelsSection = section.getConfigurationSection("delict-levels");
		if (delictLevelsSection != null) {
			for (String delictKey : delictLevelsSection.getKeys(false)) {
				final DelictLevel dl = DelictLevel.deserializeConfig(delictLevelsSection.getConfigurationSection(delictKey));
				if (dl == null) continue;
				delictLevels.put(dl.getLevel(), dl);
			}
		}

		String name = section.getString("name");
		if (name == null) throw new IllegalStateException("Delict Name can not be null!");
		String reason = section.getString("reason", name);

		final DelictType result = new DelictType(notifier, name, reason, delictLevels, material);
		DELICT_TYPE_TYPES.add(result);
		return result;
	}

	public String getReason() {
		return reason;
	}

	public String getName() {
		return name;
	}

	public Material getDisplayIcon() {
		return displayIcon;
	}

	public Map<Integer, DelictLevel> getDelictLevels() {
		return delictLevels;
	}

	public void punish(final OfflinePlayer target, final Player staff, final String message) {

		if (!(checkCooldown(target.getUniqueId()))) {
			MessageUtils.sendMessage(staff,"cooldown.warning");
			return;
		}

		final PlayerDelictData playerDelictData = DataHandler.getDelictData(target.getUniqueId());

		int level = playerDelictData.getLogs(this).size() + 1;
		for (final String cmd : getCommands(Math.min(level, delictLevels.size() - 1))) {
			staff.performCommand(cmd.replace("{player}", target.getName()));
		}

		final PlayerDelictLog log = PlayerDelictLog.createLog(System.currentTimeMillis(), staff.getName(), message, level);
		playerDelictData.addLog(this, log);
		DataHandler.insertData(playerDelictData);

		delictNotifier.notify(target, this, log);
	}

	private List<String> getCommands(final int level) {
		final DelictLevel delictLevel = delictLevels.get(level);
		if (delictLevel == null) return new ArrayList<>();
		return delictLevel.getCommands();
	}

	private boolean checkCooldown(final UUID sender) {
		final Long timeout = playerWarnCooldownMap.asMap().get(sender);
		if (timeout != null && System.currentTimeMillis() < timeout) {
			return false;
		}

		if (warnCooldown > 0) {
			playerWarnCooldownMap.put(sender, System.currentTimeMillis() + warnCooldown);
		}
		return true;
	}

}
