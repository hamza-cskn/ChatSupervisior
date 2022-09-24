package mc.obliviate.chatsupervisor.punishment.gui;

import mc.obliviate.chatsupervisor.handlers.config.ConfigHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler;
import mc.obliviate.chatsupervisor.handlers.datahandler.playerdelictdata.PlayerDelictData;
import mc.obliviate.chatsupervisor.handlers.datahandler.playerdelictdata.PlayerDelictLog;
import mc.obliviate.chatsupervisor.punishment.delict.DelictType;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import mc.obliviate.chatsupervisor.utils.xmaterial.XMaterial;
import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Date;

public class DelictLogGUI extends Gui {

	private final OfflinePlayer target;

	public DelictLogGUI(Player player, OfflinePlayer target) {
		super(player, "delict-log-gui", "Ceza Kayıtları", 6);
		this.target = target;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);

		final PlayerDelictData playerDelictData = DataHandler.getDelictData(target.getUniqueId());

		int i = 9;
		//todo sort better
		for (final DelictType type : DelictType.getDelictTypes()) {
			for (final PlayerDelictLog log : playerDelictData.getLogs(type)) {
				final String formattedTime = ConfigHandler.getDateFormat().format(new Date(log.getTime()));

				final Icon hytem = new Icon(type.getDisplayIcon()).setName("§c" + type.getName()).appendLore("", "§fIhlal: §7" + log.getLevel() + ". kez", "§fZaman: §7" + formattedTime);
				if (player.isOp()) {
					hytem.appendLore("§fYetkili: §c" + log.getStaff());
				}
				hytem.appendLore("§fMesaj:").appendLore(MessageUtils.splitMessage(log.getMessage(), 20, "§f"));
				addItem(i++, hytem);
			}
		}
	}
}
