package mc.obliviate.chatsupervisor.channel;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GlobalChatChannel implements ChatChannel {

    private final Set<Player> observers = new HashSet<>();

    @Override
    public void send(Player player, String message) {

    }

    @Override
    public void subscribe(Player player) {
        Preconditions.checkArgument(!observers.contains(player), "Player already subscribed to this channel.");
        observers.add(player);
    }

    @Override
    public void unsubscribe(Player player) {
        observers.remove(player);
    }

    @Override
    public String getChannelName() {
        return "GlobalChat";
    }

    public Set<Player> getSubscribers() {
        return Collections.unmodifiableSet(observers);
    }
}
