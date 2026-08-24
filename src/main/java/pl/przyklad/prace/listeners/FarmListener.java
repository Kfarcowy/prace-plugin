package pl.przyklad.prace.listeners;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.model.JobType;
import pl.przyklad.prace.model.PlayerJobData;
import pl.przyklad.prace.zone.Zone;

import java.util.HashMap;
import java.util.Map;

public class FarmListener implements Listener {

    private static final int MILESTONE = 45;

    private static final Map<Material, Double> CROP_REWARDS = new HashMap<>();
    static {
        CROP_REWARDS.put(Material.WHEAT, 5.0);
        CROP_REWARDS.put(Material.CARROTS, 5.0);
        CROP_REWARDS.put(Material.POTATOES, 3.5);
        CROP_REWARDS.put(Material.BEETROOTS, 6.5);
    }

    private final PracePlugin plugin;

    public FarmListener(PracePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        if (!CROP_REWARDS.containsKey(type)) {
            return;
        }
        if (!isMature(block)) {
            return; // niedojrzala roslina - nie liczymy
        }

        Player player = event.getPlayer();
        if (player.hasPermission("prace.bypass")) {
            return;
        }

        PlayerJobData data = plugin.getJobDataManager().get(player.getUniqueId());
        Zone zone = plugin.getZoneManager().findZone(JobType.FARMER, block.getLocation());

        if (zone != null) {
            if (data.getJob() == JobType.FARMER) {
                event.setDropItems(false);
                handleMilestone(player, data, type);
            } else {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Tylko farmer moze zbierac plony w tej strefie.");
            }
            return;
        }

        if (data.getJob() != null) {
            event.setDropItems(false);
        }
    }

    private boolean isMature(Block block) {
        BlockData data = block.getBlockData();
        if (data instanceof Ageable ageable) {
            return ageable.getAge() >= ageable.getMaximumAge();
        }
        return false;
    }

    private void handleMilestone(Player player, PlayerJobData data, Material crop) {
        String key = crop.name();
        double reward = CROP_REWARDS.get(crop);
        int progress = data.getFarmerProgress(key) + 1;
        if (progress >= MILESTONE) {
            data.setFarmerProgress(key, 0);
            deposit(player, data, reward);
            player.sendMessage(ChatColor.GREEN + "+" + reward + "$ (45x " + cropName(crop) + "!)");
        } else {
            data.setFarmerProgress(key, progress);
        }
        plugin.getBossBarManager().updateProgress(player, ChatColor.YELLOW + "Farmer (" + cropName(crop) + ")",
                data.getFarmerProgress(key), MILESTONE, reward);
    }

    private String cropName(Material crop) {
        return switch (crop) {
            case WHEAT -> "pszenica";
            case CARROTS -> "marchewka";
            case POTATOES -> "ziemniak";
            case BEETROOTS -> "burak";
            default -> crop.name();
        };
    }

    private void deposit(Player player, PlayerJobData data, double amount) {
        EconomyResponse response = plugin.getEconomy().depositPlayer(player, amount);
        if (response.transactionSuccess()) {
            data.addEarnedMoney(amount);
            plugin.getJobDataManager().save();
        }
    }
}
