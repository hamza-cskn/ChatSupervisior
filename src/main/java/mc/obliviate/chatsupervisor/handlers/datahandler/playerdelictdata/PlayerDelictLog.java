package mc.obliviate.chatsupervisor.handlers.datahandler.playerdelictdata;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerDelictLog {

	private final long time;
	private final String staff;
	private final String message;
	private final int level;

	private PlayerDelictLog(final long time, final String staff, final String message, final int level) {
		this.time = time;
		this.staff = staff;
		this.message = message;
		this.level = level;
	}

	public static List<PlayerDelictLog> deserialize(final String serializedString) {
		final List<PlayerDelictLog> logs = new ArrayList<>();
		for (String serializedLog : serializedString.split(",")) {
			logs.add(deserializeLog(serializedLog));
		}
		return logs;

	}

	@NotNull
	private static PlayerDelictLog deserializeLog(final String serializedString) {
		if (serializedString.isEmpty()) return null;
		final String[] datas = serializedString.split(":");
		try {
			return new PlayerDelictLog(Long.parseLong(datas[0]), datas[1], datas[2], Integer.parseInt(datas[3]));
		} catch (Exception e) {
			Bukkit.getLogger().severe("[ChatSupervisor] Could not deserialized: 'PlayerDelictLog' object. [" + e.getClass().getName() + "]");
			Bukkit.getLogger().severe("[ChatSupervisor] String: " + serializedString);
			return new PlayerDelictLog(0, "CHECK CONSOLE", datas[2], 0);
		}
	}

	@NotNull
	public static PlayerDelictLog createLog(final long time, final String staff, final String message, final int level) {
		return new PlayerDelictLog(time, staff, message, level);
	}

	public int getLevel() {
		return level;
	}

	public long getTime() {
		return time;
	}

	public String getMessage() {
		return message;
	}

	public String getStaff() {
		return staff;
	}

	@NotNull
	public String serialize() {
		return time + ":" + staff + ":" + message + ":" + level;
	}


}
