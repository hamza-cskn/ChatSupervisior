package mc.obliviate.chatsupervisor.handlers.format;

import com.google.common.base.Preconditions;
import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.format.ChatFormat;
import mc.obliviate.chatsupervisor.format.ChatFormatMeta;
import mc.obliviate.chatsupervisor.handlers.config.ConfigHandler;
import mc.obliviate.chatsupervisor.handlers.placeholderapi.PlaceholderAPIHandler;
import mc.obliviate.inventory.configurable.util.XMaterial;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


//Uses modern kyori text library.
public class FormatHandlerKyori extends FormatHandlerAbstract implements FormatHandler {

    private static final Pattern HEX_PATTERN = Pattern.compile("<#[a-fA-F\\d]{6}>");

    public FormatHandlerKyori(ChatSupervisor plugin) {
        super(plugin);
    }

    private TextComponent applyFormatMeta(final ChatFormatMeta chatFormatMeta, final ChatFormat format) {

        final String formattedString = format.convert(chatFormatMeta);

        String metaFormattedString = chatFormatMeta.getMetaFormat();

        metaFormattedString = PlaceholderAPIHandler.parsePlaceholders(chatFormatMeta.getSender(), metaFormattedString);
        metaFormattedString = metaFormattedString.replace("{world}", chatFormatMeta.getSender().getWorld().getName()).replace("{format}", formattedString);
        metaFormattedString = formatMentionTag(metaFormattedString);

        TextComponent.Builder builder = Component.text().content(metaFormattedString);

        if (ChatSupervisor.getPermission().has(chatFormatMeta.getReceiver(), "chatsupervisor.punish")) {
            builder = parseColor(builder);
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

    private static TextComponent.Builder parseColor(TextComponent.Builder builder) {
        Preconditions.checkNotNull(builder);
        Matcher matcher = HEX_PATTERN.matcher(builder.content());
        while (matcher.find()) {
            final String hex = matcher.group().substring(1, matcher.group().length() - 1);
            final String before = builder.content().substring(0, matcher.start());
            final String after = builder.content().substring(matcher.end());
            builder = Component.text().append(Component.text(before)).color(TextColor.fromHexString(hex)).append(Component.text(after));
            matcher = HEX_PATTERN.matcher(builder.content());
        }
        return builder;
    }

    @Override
    public void send(Player receiver, ChatFormatMeta chatFormatMeta, ChatFormat format) {
        receiver.sendMessage(applyFormatMeta(chatFormatMeta, format));
    }
}
