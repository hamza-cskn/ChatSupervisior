package mc.obliviate.chatsupervisor.handlers.format;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.format.ChatFormat;
import mc.obliviate.chatsupervisor.format.ChatFormatMeta;
import mc.obliviate.chatsupervisor.handlers.config.ConfigHandler;
import mc.obliviate.chatsupervisor.handlers.placeholderapi.PlaceholderAPIHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;


//Uses modern kyori text library.
public class FormatHandler1_16 extends FormatHandlerAbstract implements FormatHandler {

	public FormatHandler1_16(ChatSupervisor plugin) {
		super(plugin);
	}

	private TextComponent applyFormatMeta(final ChatFormatMeta chatFormatMeta, final ChatFormat format) {

		final String formattedString = format.convert(chatFormatMeta);

		String metaFormattedString = chatFormatMeta.getMetaFormat();

		metaFormattedString = PlaceholderAPIHandler.parsePlaceholders(chatFormatMeta.getSender(), metaFormattedString);

		metaFormattedString = metaFormattedString.replace("{world}", chatFormatMeta.getSender().getWorld().getName()).replace("{format}", formattedString);

		final TextComponent.Builder builder = Component.text().content(metaFormattedString);

		//todo has permission
		if (chatFormatMeta.getReceiver().isOp()) {
			return builder.append(getStaffButton(chatFormatMeta.getSender().getName() + " " + chatFormatMeta.getMessage())).build();
		}

		return builder.build();

	}

	private TextComponent getStaffButton(final String cmd) {
		final TextComponent.Builder builder = Component.text().content(ConfigHandler.buttonData[0]);
		builder.hoverEvent(HoverEvent.showText(Component.text().content(ConfigHandler.buttonData[1]).build()));
		builder.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/punish " + cmd));
		return builder.build();
		/*
		 * 		button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punish " + cmd));
		 * 		button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(buttonData[1]).create()));
		 * 		return button;
		 */
	}


	@Override
	public void send(Player receiver, ChatFormatMeta chatFormatMeta, ChatFormat format) {
		receiver.sendMessage(applyFormatMeta(chatFormatMeta, format));
	}
}
