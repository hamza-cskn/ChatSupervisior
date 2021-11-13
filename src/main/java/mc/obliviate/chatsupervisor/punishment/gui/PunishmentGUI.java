package mc.obliviate.chatsupervisor.punishment.gui;

import mc.obliviate.chatsupervisor.punishment.delict.DelictType;
import mc.obliviate.chatsupervisor.utils.message.MessageUtils;
import mc.obliviate.chatsupervisor.utils.xmaterial.XMaterial;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import xyz.efekurbann.inventory.GUI;
import xyz.efekurbann.inventory.Hytem;

import static mc.obliviate.chatsupervisor.handlers.datahandler.DataHandler.getDelictTypes;

public class PunishmentGUI extends GUI {

	private final OfflinePlayer target;
	private final String message;

	public PunishmentGUI(Player staff, OfflinePlayer target, String message) {
		super(staff, "punishment-gui", "Punish: " + target.getName(), 6);
		this.message = message;
		this.target = target;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillGui(new ItemStack(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()));

		addItem(4, new Hytem(XMaterial.PLAYER_HEAD.parseItem()).setName(target.getName()).appendLore(MessageUtils.splitMessage(message, "§f")));

		int i = 10;
		for (DelictType delictType : getDelictTypes()) {
			addItem(i, new Hytem(XMaterial.BEDROCK.parseItem()).setName("§c§l" + delictType.getName()).onClick(e -> {
				delictType.punish(target, player, message);
			}));
			i += 2;
		}
		player.closeInventory();
	}

}
