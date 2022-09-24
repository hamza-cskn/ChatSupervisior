package mc.obliviate.chatsupervisor.ignore;

import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.playerdata.PlayerData;
import mc.obliviate.chatsupervisor.utils.xmaterial.XMaterial;
import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.advancedslot.AdvancedSlotManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.UUID;

public class IgnoredPlayersGUI extends Gui {

	public IgnoredPlayersGUI(Player player) {
		super(player, "ignored-players-gui", "Susturulan Oyuncular", 6);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);

		final PlayerData playerData = DataHandler.getPlayerData(player.getUniqueId());

		AdvancedSlotManager advancedSlotManager = new AdvancedSlotManager(this);
		advancedSlotManager.addAdvancedIcon(29, new Icon(Material.AIR));

		int i = 9;
		for (final UUID uuid : playerData.getIgnoredPlayers()) {
			final OfflinePlayer ignoredP = Bukkit.getOfflinePlayer(uuid);
			final Icon hytem = new Icon(XMaterial.PLAYER_HEAD.parseItem()).setName(ignoredP.getName()).appendLore("", ChatColor.RED + "Susturmasını kaldırmak için tıkla!");
			hytem.onClick(e -> {
				player.performCommand("chatmute " + ignoredP.getName());
				new IgnoredPlayersGUI(player).open();
			});
			addItem(i++, hytem);
		}

		if (i == 9) {
			addItem(31, new Icon(XMaterial.BARRIER.parseItem()).setName(ChatColor.RED + "Susturulan kimse bulunamadı")
					.appendLore("", ChatColor.GRAY + "Hiç kimseyi susturmamışsın", ChatColor.GRAY + "bu harika!"));
		}
	}
}
