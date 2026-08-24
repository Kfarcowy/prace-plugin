package pl.przyklad.prace.listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.model.JobType;
import pl.przyklad.prace.zone.Zone;

/**
 * W strefie drwala mozna niszczyc wylacznie drewno (logi). Wszystko inne jest
 * calkowicie zablokowane, chyba ze gracz jest OP lub ma uprawnienie prace.bypass.
 */
public class WoodZoneProtectListener implements Listener {

    private final PracePlugin plugin;

    public WoodZoneProtectListener(PracePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("prace.bypass")) {
            return;
        }

        Block block = event.getBlock();
        if (WoodListener.LOGS.contains(block.getType())) {
            return; // drewno obsluguje WoodListener
        }

        Zone zone = plugin.getZoneManager().findZone(JobType.DRWAL, block.getLocation());
        if (zone == null) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "W tej strefie mozna niszczyc tylko drewno.");
    }
}
