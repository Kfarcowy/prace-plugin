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

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class MineListener implements Listener {

    private static final int MILESTONE = 30;
    private static final double MILESTONE_REWARD = 5.0;

    private static final Set<Material> STONE_GROUP = EnumSet.of(
            Material.STONE, Material.DEEPSLATE, Material.GRANITE,
            Material.TUFF, Material.DIORITE, Material.ANDESITE
    );

    private static final Map<Material, Double> ORE_PRICES = new EnumMap<>(Material.class);
    static {
        ORE_PRICES.put(Material.EMERALD_ORE, 20.0);
        ORE_PRICES.put(Material.DEEPSLATE_EMERALD_ORE, 20.0);
        ORE_PRICES.put(Material.DIAMOND_ORE, 12.5);
        ORE_PRICES.put(Material.DEEPSLATE_DIAMOND_ORE, 12.5);
        ORE_PRICES.put(Material.COAL_ORE, 0.5);
        ORE_PRICES.put(Material.DEEPSLATE_COAL_ORE, 0.5);
        ORE_PRICES.put(Material.COPPER_ORE, 0.5);
        ORE_PRICES.put(Material.DEEPSLATE_COPPER_ORE, 0.5);
        ORE_PRICES.put(Material.IRON_ORE, 5.0);
        ORE_PRICES.put(Material.DEEPSLATE_IRON_ORE, 5.0);
        ORE_PRICES.put(Material.REDSTONE_ORE, 2.5);
        ORE_PRICES.put(Material.DEEPSLATE_REDSTONE_ORE, 2.5);
        ORE_PRICES.put(Material.LAPIS_ORE, 1.0);
        ORE_PRICES.put(Material.DEEPSLATE_LAPIS_ORE, 1.0);
    }

    private final PracePlugin plugin;

    public MineListener(PracePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        boolean isStoneGroup = STONE_GROUP.contains(type);
        boolean isOre = ORE_PRICES.containsKey(type);
        if (!isStoneGroup && !isOre) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasPermission("prace.bypass")) {
            return;
        }

        PlayerJobData data = plugin.getJobDataManager().get(player.getUniqueId());
        Zone zone = plugin.getZoneManager().findZone(JobType.GORNIK, event.getBlock().getLocation());

        if (zone != null) {
            if (data.getJob() == JobType.GORNIK) {
                event.setDropItems(false);
                if (isOre) {
                    payOre(player, data, ORE_PRICES.get(type));
                } else {
                    handleStoneMilestone(player, data);
                }
            }
            // inny/zaden zawod w kopalni -> normalne kopanie, zatrzymuje przedmiot
            return;
        }

        // poza strefa gornika
        if (data.getJob() == null) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Bez pracy mozesz kopac tylko w kopalni (strefie gornika).");
        } else {
            event.setDropItems(false); // ma jakas prace, ale nie w swojej strefie - zabrane, bez wyplaty
        }
    }

    private void payOre(Player player, PlayerJobData data, double amount) {
        deposit(player, data, amount);
        player.sendMessage(ChatColor.GREEN + "+" + amount + "$ (ruda)");
    }

    private void handleStoneMilestone(Player player, PlayerJobData data) {
        int progress = data.getMinerProgress() + 1;
        if (progress >= MILESTONE) {
            data.setMinerProgress(0);
            deposit(player, data, MILESTONE_REWARD);
            player.sendMessage(ChatColor.GREEN + "+" + MILESTONE_REWARD + "$ (30 blokow!)");
        } else {
            data.setMinerProgress(progress);
        }
        plugin.getBossBarManager().updateProgress(player, ChatColor.AQUA + "Gornik",
                data.getMinerProgress(), MILESTONE, MILESTONE_REWARD);
    }

    private void deposit(Player player, PlayerJobData data, double amount) {
        EconomyResponse response = plugin.getEconomy().depositPlayer(player, amount);
        if (response.transactionSuccess()) {
            data.addEarnedMoney(amount);
            plugin.getJobDataManager().save();
        }
    }
}
