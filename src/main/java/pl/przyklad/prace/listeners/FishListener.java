package pl.przyklad.prace.listeners;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.model.JobType;
import pl.przyklad.prace.model.PlayerJobData;
import pl.przyklad.prace.zone.Zone;

public class FishListener implements Listener {

    private static final double REWARD = 3.5;

    private final PracePlugin plugin;

    public FishListener(PracePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        Player player = event.getPlayer();
        PlayerJobData data = plugin.getJobDataManager().get(player.getUniqueId());
        if (data.getJob() != JobType.RYBAK) {
            return;
        }

        Zone zone = plugin.getZoneManager().findZone(JobType.RYBAK, player.getLocation());
        if (zone == null) {
            return; // rybak lowi tylko na specjalnej strefie - poza nia brak nagrody
        }

        EconomyResponse response = plugin.getEconomy().depositPlayer(player, REWARD);
        if (response.transactionSuccess()) {
            data.addEarnedMoney(REWARD);
            plugin.getJobDataManager().save();
            player.sendMessage(ChatColor.GREEN + "+" + REWARD + "$ (zlowiona ryba!)");
        }
    }
}
