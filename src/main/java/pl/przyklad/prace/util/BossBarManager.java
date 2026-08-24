package pl.przyklad.prace.util;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pl.przyklad.prace.PracePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarManager {

    private final PracePlugin plugin;
    private final Map<UUID, BossBar> bars = new HashMap<>();
    private final Map<UUID, BukkitTask> hideTasks = new HashMap<>();

    public BossBarManager(PracePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Pokazuje/aktualizuje pasek postepu dla gracza. Chowa sie automatycznie po 4 sekundach bezczynnosci.
     */
    public void updateProgress(Player player, String title, int current, int max, double reward) {
        BossBar bar = bars.get(player.getUniqueId());
        if (bar == null) {
            bar = Bukkit.createBossBar(" ", BarColor.GREEN, BarStyle.SOLID);
            bar.addPlayer(player);
            bars.put(player.getUniqueId(), bar);
        }
        int remaining = Math.max(0, max - current);
        bar.setTitle(title + " - zostalo " + remaining + "/" + max + " (nagroda: " + String.format("%.2f", reward) + "$)");
        double progress = Math.min(1.0, (double) current / (double) max);
        bar.setProgress(progress);
        bar.setVisible(true);

        BukkitTask oldTask = hideTasks.remove(player.getUniqueId());
        if (oldTask != null) {
            oldTask.cancel();
        }
        BossBar finalBar = bar;
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> finalBar.setVisible(false), 80L); // 4 sekundy
        hideTasks.put(player.getUniqueId(), task);
    }

    public void remove(Player player) {
        BossBar bar = bars.remove(player.getUniqueId());
        if (bar != null) {
            bar.removeAll();
        }
        BukkitTask task = hideTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }
}
