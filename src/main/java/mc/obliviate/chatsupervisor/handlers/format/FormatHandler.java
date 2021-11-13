package mc.obliviate.chatsupervisor.handlers.format;

import mc.obliviate.chatsupervisor.format.ChatFormat;
import mc.obliviate.chatsupervisor.format.ChatFormatMeta;
import org.bukkit.entity.Player;

public interface FormatHandler {

	void send(final Player receiver, final ChatFormatMeta chatFormatMeta, final ChatFormat format);

}
