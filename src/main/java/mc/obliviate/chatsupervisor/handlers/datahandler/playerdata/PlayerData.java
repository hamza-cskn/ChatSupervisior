package mc.obliviate.chatsupervisor.handlers.datahandler.playerdata;

import mc.obliviate.bloksqliteapi.sqlutils.SQLUpdateColumn;
import mc.obliviate.chatsupervisor.channel.ChatChannel;
import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

	private final UUID uuid;
	private final List<UUID> ignoredPlayers;
	private ChatChannel channel;
	//private final List<Component> messagePacketLog = new ArrayList<>();

	private PlayerData(final UUID uuid, final List<UUID> ignoredPlayers) {
		this.uuid = uuid;
		this.ignoredPlayers = ignoredPlayers;
	}

	/*
	public List<Component> getMessagePacketLog() {
		return messagePacketLog;
	}

	public void deleteMessageFromLog(int index) {
		messagePacketLog.set(index, Component.text().content(ChatColor.RED + "Bu mesaj silindi.").build());
	}

	public void addMessagePacketLog(Component string) {
		if (messagePacketLog.size() >= 100) {
			messagePacketLog.remove(0);
		}
		messagePacketLog.add(string);
	}

	 */

	@NotNull
	public static PlayerData createDefault(UUID uuid) {
		return new PlayerData(uuid, new ArrayList<>());
	}

	@Nullable
	public static PlayerData deserialize(ResultSet rs) throws SQLException {

		if (!rs.next()) {
			return null;
		}

		try {
			final UUID uuid = UUID.fromString(rs.getString("uuid"));
			final PlayerData result = new PlayerData(uuid, deserializeUUIDList(rs.getString("ignoredPlayers")));
			DataHandler.registerPlayerData(result);
			return result;
		} catch (IllegalArgumentException e) {
			Bukkit.getLogger().severe("[ChatSupervisor] PlayerData UUID could not deserialized from string: " + rs.getString("uuid"));
		} finally {
			while (rs.next()) ;
		}

		Bukkit.getLogger().severe("[ChatSupervisor] PlayerData could not deserialized from ResultSet");
		return null;
	}

	@NotNull
	private static List<UUID> deserializeUUIDList(String serializedString) {

		final List<UUID> result = new ArrayList<>();
		if (serializedString == null || serializedString.isEmpty()) return result;

		final String[] list = serializedString.split(",");
		try {
			for (String uuidString : list) {
				result.add(UUID.fromString(uuidString));
			}
		} catch (IllegalArgumentException e) {
			Bukkit.getLogger().severe("[ChatSupervisor] PlayerData UUID List could not deserialized from string: " + e.getMessage());
		}
		return result;
	}

	@NotNull
	private static String serializeUUIDList(List<UUID> list) {
		final StringBuilder sb = new StringBuilder();

		boolean first = false;
		for (UUID uuid : list) {
			if (first)
				sb.append(",");
			sb.append(uuid);
			first = true;
		}

		return sb.toString();
	}

	public UUID getUuid() {
		return uuid;
	}

	public List<UUID> getIgnoredPlayers() {
		return ignoredPlayers;
	}

	public SQLUpdateColumn serialize() {
		final SQLUpdateColumn update = DataHandler.getDelictDataTable().createUpdate(uuid.toString());

		update.putData("uuid", uuid.toString());
		update.putData("ignoredPlayers", serializeUUIDList(ignoredPlayers));

		return update;
	}

	public ChatChannel getChannel() {
		return channel;
	}

	public void setChannel(ChatChannel channel) {
		this.channel = channel;
	}

}
