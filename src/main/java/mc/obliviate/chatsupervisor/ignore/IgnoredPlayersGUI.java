package mc.obliviate.chatsupervisor.ignore;

import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.playerdata.PlayerData;
import mc.obliviate.chatsupervisor.utils.xmaterial.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import xyz.efekurbann.inventory.GUI;
import xyz.efekurbann.inventory.Hytem;

import java.util.UUID;

public class IgnoredPlayersGUI extends GUI {

	public IgnoredPlayersGUI(Player player) {
		super(player, "ignored-players-gui", "Susturulan Oyuncular", 6);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Hytem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);

		final PlayerData playerData = DataHandler.getPlayerData(player.getUniqueId());

		int i = 9;
		for (final UUID uuid : playerData.getIgnoredPlayers()) {
			final OfflinePlayer ignoredP = Bukkit.getOfflinePlayer(uuid);
			final Hytem hytem = new Hytem(XMaterial.PLAYER_HEAD.parseItem()).setName(ignoredP.getName()).appendLore("", ChatColor.RED + "Susturmasını kaldırmak için tıkla!");
			hytem.onClick(e -> {
				player.performCommand("chatmute " + ignoredP.getName());
				new IgnoredPlayersGUI(player).open();
			});
			addItem(i++, hytem);
		}

		if (i == 9) {
			addItem(31, new Hytem(XMaterial.BARRIER.parseItem()).setName(ChatColor.RED + "Susturulan kimse bulunamadı")
					.appendLore("", ChatColor.GRAY + "Hiç kimseyi susturmamışsın", ChatColor.GRAY + "bu harika!"));
		}
	}
}
