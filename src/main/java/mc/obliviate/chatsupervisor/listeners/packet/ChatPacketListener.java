package mc.obliviate.chatsupervisor.listeners.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.playerdata.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;

public class ChatPacketListener extends PacketAdapter {

	public ChatPacketListener(ChatSupervisor plugin) {
		super(plugin, ListenerPriority.MONITOR, PacketType.Play.Server.CHAT);
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		if (event.isCancelled()) return;

		final StructureModifier<WrappedChatComponent> components = event.getPacket().getChatComponents();
		final WrappedChatComponent chat = components.read(0);
		if (chat == null) return;
		final Component message = GsonComponentSerializer.colorDownsamplingGson().deserialize(chat.getJson());

		final PlayerData data = DataHandler.getPlayerData(event.getPlayer().getUniqueId());

		//data.addMessagePacketLog(message);

	}

}
