package pl.przyklad.prace.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.model.JobType;
import pl.przyklad.prace.model.PlayerJobData;

public class ChatListener implements Listener {

    // znaki z Private Use Area zmapowane w paczce tekstur (assets/minecraft/font/prace.json)
    // na gotowe odznaki (ikona + napis) dla kazdej pracy
    private static final Key PRACE_FONT = Key.key("minecraft", "prace");

    private final PracePlugin plugin;

    public ChatListener(PracePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component prefix = getPrefixComponent(player);

        Component fullMessage = Component.text()
                .append(prefix)
                .append(Component.text(" "))
                .append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                .append(Component.text(": ").color(NamedTextColor.GRAY))
                .append(event.message())
                .build();

        event.renderer((source, sourceDisplayName, message, viewer) -> fullMessage);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updateTabName(event.getPlayer());
    }

    public void updateTabName(Player player) {
        Component prefix = getPrefixComponent(player);
        Component listName = Component.text()
                .append(prefix)
                .append(Component.text(" " + player.getName()).color(NamedTextColor.WHITE))
                .build();
        player.playerListName(listName);
    }

    private Component getPrefixComponent(Player player) {
        PlayerJobData data = plugin.getJobDataManager().get(player.getUniqueId());
        JobType job = data.getJob();
        if (job == null) {
            return Component.text("brak pracy").color(NamedTextColor.GRAY);
        }

        String glyph = switch (job) {
            case GORNIK -> "\uE001";
            case DRWAL -> "\uE002";
            case FARMER -> "\uE003";
            case RYBAK -> "\uE004";
        };

        return Component.text(glyph).font(PRACE_FONT);
    }
}
