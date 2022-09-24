package mc.obliviate.chatsupervisor.handlers.format;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.format.ChatFormat;
import mc.obliviate.chatsupervisor.format.ChatFormatMeta;
import mc.obliviate.chatsupervisor.handlers.config.ConfigHandler;
import mc.obliviate.chatsupervisor.handlers.placeholderapi.PlaceholderAPIHandler;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

public class FormatHandlerSpigot extends FormatHandlerAbstract implements FormatHandler {

	public FormatHandlerSpigot(ChatSupervisor plugin) {
		super(plugin);
	}

	@Override
	public void send(Player receiver, ChatFormatMeta chatFormatMeta, ChatFormat format) {
		//receiver.sendMessage(chatFormatMeta.getSender() + ": " + chatFormatMeta.getMessage());
		receiver.sendMessage(applyFormatMeta(chatFormatMeta,format));
	}

	private BaseComponent applyFormatMeta(final ChatFormatMeta chatFormatMeta, final ChatFormat format) {

		final String formattedString = format.convert(chatFormatMeta);

		String metaFormattedString = chatFormatMeta.getMetaFormat();

		metaFormattedString = PlaceholderAPIHandler.parsePlaceholders(chatFormatMeta.getSender(), metaFormattedString);
		metaFormattedString = metaFormattedString.replace("{world}", chatFormatMeta.getSender().getWorld().getName()).replace("{format}", formattedString);
		metaFormattedString = formatMentionTag(metaFormattedString);

		final BaseComponent builder = new TextComponent(metaFormattedString);

		if (chatFormatMeta.getReceiver().isOp()) {
			builder.addExtra(getStaffButton(chatFormatMeta.getSender().getName() + " " + chatFormatMeta.getMessage()));
			return builder;
		}

		return builder;

	}

	private BaseComponent getStaffButton(final String cmd) {
		final TextComponent text = new TextComponent(ConfigHandler.buttonData[0]);
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ConfigHandler.buttonData[1]).create()));
		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punish " + cmd));
		return text;
		/*
		 * 		button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punish " + cmd));
		 * 		button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(buttonData[1]).create()));
		 * 		return button;
		 */
	}

}
