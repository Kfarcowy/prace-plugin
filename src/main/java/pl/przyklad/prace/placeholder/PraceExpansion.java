package pl.przyklad.prace.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.model.JobType;
import pl.przyklad.prace.model.PlayerJobData;

public class PraceExpansion extends PlaceholderExpansion {

    private final PracePlugin plugin;

    public PraceExpansion(PracePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        // razem z placeholder() daje "%your_job%"
        return "your";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ty";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }
        if (!params.equalsIgnoreCase("job")) {
            return null;
        }
        PlayerJobData data = plugin.getJobDataManager().get(player.getUniqueId());
        JobType job = data.getJob();
        return job == null ? "Brak pracy" : job.getDisplayName();
    }
}
