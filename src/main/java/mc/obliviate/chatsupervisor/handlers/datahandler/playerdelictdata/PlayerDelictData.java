package mc.obliviate.chatsupervisor.handlers.datahandler.playerdelictdata;

import mc.obliviate.bloksqliteapi.sqlutils.SQLUpdateColumn;
import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.punishment.delict.DelictType;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlayerDelictData {

	private final Map<PlayerDelictLog, DelictType> delictLogs;
	private final UUID uuid;

	private PlayerDelictData(final Map<PlayerDelictLog, DelictType> delictLogs, final UUID uuid) {
		this.delictLogs = delictLogs;
		this.uuid = uuid;
	}

	public UUID getUuid() {
		return uuid;
	}

	public Map<PlayerDelictLog, DelictType> getDelictLogs() {
		return delictLogs;
	}

	@NotNull
	public static PlayerDelictData createDefault(UUID uuid) {
		return new PlayerDelictData(new HashMap<>(), uuid);
	}

	@Nullable
	public static PlayerDelictData deserialize(ResultSet rs) throws SQLException {

		final Map<PlayerDelictLog, DelictType> logs = new HashMap<>();
		if (!rs.next()) {
			return null;
		}
		for (DelictType delictType : DelictType.getDelictTypes()) {
			final String serializedLog = rs.getString(delictType.getName());
			for (final PlayerDelictLog log : PlayerDelictLog.deserialize(serializedLog)) {
				logs.put(log, delictType);
			}
		}

		try {
			final UUID uuid = UUID.fromString(rs.getString("uuid"));
			final PlayerDelictData result = new PlayerDelictData(logs, uuid);
			DataHandler.registerPlayerDelictData(result);
			return result;
		} catch (IllegalArgumentException e) {
			Bukkit.getLogger().severe("[ChatSupervisor] UUID could not deserialized from string: " + rs.getString("uuid"));
		} finally {
			while (rs.next()) ;
		}


		Bukkit.getLogger().severe("[ChatSupervisor] PlayerDelictData could not deserialized from ResultSet");
		return null;
	}

	public SQLUpdateColumn serialize() {
		final SQLUpdateColumn update = DataHandler.getDelictDataTable().createUpdate(uuid.toString());

		update.putData("uuid", uuid.toString());


		for (final DelictType delictType : DelictType.getDelictTypes()) {
			final List<PlayerDelictLog> logs = getLogs(delictType);
			if (!logs.isEmpty()) {
				final StringBuilder sb = new StringBuilder();
				boolean first = false;
				for (PlayerDelictLog log : logs) {
					if (log == null) continue;
					if (first)
						sb.append(",");
					sb.append(log.serialize());
					first = true;
				}
				update.putData(delictType.getName(), sb.toString());
			} else {
				update.putData(delictType.getName(), "");
			}
		}
		return update;
	}

	public List<PlayerDelictLog> getLogs(DelictType type) {
		final List<PlayerDelictLog> result = new ArrayList<>();
		for (final PlayerDelictLog log : delictLogs.keySet()) {
			if (delictLogs.get(log).equals(type)) {
				result.add(log);
			}
		}
		return result;
	}

	public void addLog(DelictType type, PlayerDelictLog log) {
		delictLogs.put(log, type);
	}


}
