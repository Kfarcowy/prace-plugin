package pl.przyklad.prace.listeners;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.model.JobType;
import pl.przyklad.prace.model.PlayerJobData;
import pl.przyklad.prace.zone.Zone;

import java.util.EnumSet;
import java.util.Set;

public class WoodListener implements Listener {

    private static final int MILESTONE = 15;
    private static final double MILESTONE_REWARD = 4.0;

    public static final Set<Material> LOGS = EnumSet.of(
            Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG,
            Material.ACACIA_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG, Material.CHERRY_LOG,
            Material.CRIMSON_STEM, Material.WARPED_STEM
    );

    private final PracePlugin plugin;

    public WoodListener(PracePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (!LOGS.contains(type)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasPermission("prace.bypass")) {
            return;
        }

        PlayerJobData data = plugin.getJobDataManager().get(player.getUniqueId());
        Zone zone = plugin.getZoneManager().findZone(JobType.DRWAL, event.getBlock().getLocation());

        if (zone != null) {
            if (data.getJob() == JobType.DRWAL) {
                event.setDropItems(false);
                handleMilestone(player, data);
            } else {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Tylko drwal moze rabac drewno w tej strefie.");
            }
            return;
        }

        // poza strefa drwala - normalne rabanie dla kazdego
        if (data.getJob() != null) {
            event.setDropItems(false); // ma jakas prace - zabrane, bez wyplaty (bo nie w strefie)
        }
    }

    private void handleMilestone(Player player, PlayerJobData data) {
        int progress = data.getDrwalProgress() + 1;
        if (progress >= MILESTONE) {
            data.setDrwalProgress(0);
            deposit(player, data, MILESTONE_REWARD);
            player.sendMessage(ChatColor.GREEN + "+" + MILESTONE_REWARD + "$ (15 drewna!)");
        } else {
            data.setDrwalProgress(progress);
        }
        plugin.getBossBarManager().updateProgress(player, ChatColor.GOLD + "Drwal",
                data.getDrwalProgress(), MILESTONE, MILESTONE_REWARD);
    }

    private void deposit(Player player, PlayerJobData data, double amount) {
        EconomyResponse response = plugin.getEconomy().depositPlayer(player, amount);
        if (response.transactionSuccess()) {
            data.addEarnedMoney(amount);
            plugin.getJobDataManager().save();
        }
    }
}
