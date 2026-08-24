package pl.przyklad.prace.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.model.JobType;

import java.util.ArrayList;
import java.util.List;

public class WandListener implements Listener {

    private static final String WAND_PREFIX = ChatColor.GOLD + "" + ChatColor.BOLD + "Prace Wand: ";

    private final PracePlugin plugin;

    public WandListener(PracePlugin plugin) {
        this.plugin = plugin;
    }

    public static ItemStack createWand(JobType job) {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(WAND_PREFIX + job.getDisplayName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Lewy klik = pozycja 1");
            lore.add(ChatColor.GRAY + "Prawy klik = pozycja 2");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static JobType getJobFromWand(ItemStack item) {
        if (item == null || item.getType() != Material.BLAZE_ROD) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || !meta.getDisplayName().startsWith(WAND_PREFIX)) {
            return null;
        }
        String jobName = ChatColor.stripColor(meta.getDisplayName()).replace("Prace Wand: ", "");
        return JobType.fromString(jobName);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        JobType job = getJobFromWand(event.getItem());
        if (job == null) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }

        Player player = event.getPlayer();
        event.setCancelled(true);

        Location loc = event.getClickedBlock().getLocation();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            plugin.getPos1Map().put(player.getUniqueId(), loc);
            player.sendMessage(ChatColor.GREEN + "[" + job.getDisplayName() + "] Ustawiono pozycje 1: " + formatLoc(loc));
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            plugin.getPos2Map().put(player.getUniqueId(), loc);
            player.sendMessage(ChatColor.GREEN + "[" + job.getDisplayName() + "] Ustawiono pozycje 2: " + formatLoc(loc));
        }
    }

    private String formatLoc(Location loc) {
        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }
}
