package mc.obliviate.chatsupervisor.handlers.datahandler;

import mc.obliviate.bloksqliteapi.SQLHandler;
import mc.obliviate.bloksqliteapi.sqlutils.DataType;
import mc.obliviate.bloksqliteapi.sqlutils.SQLTable;
import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.handlers.datahandler.playerdata.PlayerData;
import mc.obliviate.chatsupervisor.handlers.datahandler.playerdelictdata.PlayerDelictData;
import mc.obliviate.chatsupervisor.punishment.delict.DelictType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataHandler extends SQLHandler {


	private static final Map<UUID, PlayerDelictData> playerDelictDatas = new HashMap<>();
	private static final Map<UUID, PlayerData> playerDatas = new HashMap<>();
	private static SQLTable delictDataTable;
	private static SQLTable playerDataTable;
	private final ChatSupervisor plugin;

	public DataHandler(ChatSupervisor plugin) {
		super(plugin.getDataFolder().getPath());
		this.plugin = plugin;
	}


	public static void registerPlayerDelictData(PlayerDelictData playerDelictData) {
		playerDelictDatas.put(playerDelictData.getUuid(), playerDelictData);
	}

	public static void registerPlayerData(PlayerData playerData) {
		playerDatas.put(playerData.getUuid(), playerData);
	}

	@NotNull
	public static PlayerDelictData getDelictData(final UUID uuid) {

		PlayerDelictData delictData = playerDelictDatas.get(uuid);

		if (delictData != null) {
			return delictData;
		}

		try {
			delictData = PlayerDelictData.deserialize(delictDataTable.select(uuid.toString()));
		} catch (SQLException e) {
			Bukkit.getLogger().severe("[ChatSupervisor] PlayerDelictData could not deserialized: " + uuid);
			e.printStackTrace();
			delictData = null;
		}

		if (delictData != null) {
			return delictData;
		}

		return PlayerDelictData.createDefault(uuid);
	}

	@NotNull
	public static PlayerData getPlayerData(final UUID uuid) {

		PlayerData playerData = playerDatas.get(uuid);

		if (playerData != null) return playerData;

		if (playerDataTable == null) {
			Bukkit.getLogger().severe("[ChatSupervisor] Player Data Table could not found! ");
			return PlayerData.createDefault(uuid);
		}

		try {
			playerData = PlayerData.deserialize(playerDataTable.select(uuid.toString()));
		} catch (SQLException e) {
			Bukkit.getLogger().severe("[ChatSupervisor] PlayerData could not deserialized: " + uuid);
			e.printStackTrace();
		}

		if (playerData != null) {
			return playerData;
		}

		return PlayerData.createDefault(uuid);
	}

	public static void insertData(final PlayerDelictData playerDelictData) {
		delictDataTable.insertOrUpdate(playerDelictData.serialize());
	}

	public static void insertData(final PlayerData playerData) {
		playerDataTable.insertOrUpdate(playerData.serialize());
	}

	public static List<DelictType> getDelictTypes() {
		return DelictType.getDelictTypes();
	}

	public static SQLTable getDelictDataTable() {
		return delictDataTable;
	}

	public void init() {
		plugin.getLogger().info("Connecting to database...");
		connect("database");
	}

	@Override
	public void onConnect() {
		load();
		plugin.getLogger().info("Successfully connected to database...");

	}

	private void loadConfig() {
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder().getPath() + File.separator + "punishment-config.yml"));
		if (config.getKeys(true).isEmpty()) return;

		loadPunishmentConfig(config);

	}

	public void load() {

		loadConfig();

		delictDataTable = new SQLTable("delicts", "uuid").addField("uuid", DataType.TEXT);

		for (DelictType delictType : DelictType.getDelictTypes()) {
			Bukkit.getLogger().info(delictType.getName() + " delict loaded.");
			delictDataTable.addField(delictType.getName(), DataType.TEXT);
		}

		delictDataTable.create();

		playerDataTable = new SQLTable("playerdata", "uuid")
				.addField("uuid", DataType.TEXT)
				.addField("ignoredPlayers", DataType.TEXT)
				.create();

	}

	private void loadPunishmentConfig(YamlConfiguration config) {
		DelictType.getDelictTypes().clear();
		final ConfigurationSection section = config.getConfigurationSection("delicts");
		assert section != null;
		for (String key : section.getKeys(false)) {
			DelictType.deserializeConfig(plugin.getDelictNotifier(), section.getConfigurationSection(key));
		}
	}

	public static boolean isIgnoresPlayer(final Player playerThatIgnores, final Player ignored) {
		return DataHandler.getPlayerData(playerThatIgnores.getUniqueId()).getIgnoredPlayers().contains(ignored.getUniqueId());
	}
}
