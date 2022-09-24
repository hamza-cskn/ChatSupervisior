package mc.obliviate.chatsupervisor.channel;

import org.bukkit.entity.Player;

public interface ChatChannel {

    void send(Player player, String message);

    void subscribe(Player player);

    void unsubscribe(Player player);

    String getChannelName();

}
