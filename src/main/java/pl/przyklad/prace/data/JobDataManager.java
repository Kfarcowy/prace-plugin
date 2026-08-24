package pl.przyklad.prace.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.model.JobType;
import pl.przyklad.prace.model.PlayerJobData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class JobDataManager {

    private final PracePlugin plugin;
    private final File file;
    private final Map<UUID, PlayerJobData> cache = new HashMap<>();

    public JobDataManager(PracePlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "playerdata.yml");
    }

    public void load() {
        cache.clear();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = cfg.getConfigurationSection("players");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection s = section.getConfigurationSection(key);
            if (s == null) continue;
            UUID uuid = UUID.fromString(key);
            PlayerJobData data = new PlayerJobData(uuid);
            String jobStr = s.getString("job", "none");
            data.setJob(jobStr.equalsIgnoreCase("none") ? null : JobType.fromString(jobStr));
            data.setEarnedMoney(s.getDouble("earnedMoney", 0.0));
            data.setMinerProgress(s.getInt("minerProgress", 0));
            data.setDrwalProgress(s.getInt("drwalProgress", 0));
            ConfigurationSection farmerSection = s.getConfigurationSection("farmerProgress");
            if (farmerSection != null) {
                for (String crop : farmerSection.getKeys(false)) {
                    data.setFarmerProgress(crop, farmerSection.getInt(crop, 0));
                }
            }
            cache.put(uuid, data);
        }
    }

    public void save() {
        YamlConfiguration cfg = new YamlConfiguration();
        for (PlayerJobData data : cache.values()) {
            String path = "players." + data.getUuid();
            cfg.set(path + ".job", data.getJob() == null ? "none" : data.getJob().name());
            cfg.set(path + ".earnedMoney", data.getEarnedMoney());
            cfg.set(path + ".minerProgress", data.getMinerProgress());
            cfg.set(path + ".drwalProgress", data.getDrwalProgress());
            for (Map.Entry<String, Integer> entry : data.getFarmerProgressMap().entrySet()) {
                cfg.set(path + ".farmerProgress." + entry.getKey(), entry.getValue());
            }
        }
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Nie udalo sie zapisac playerdata.yml", e);
        }
    }

    public PlayerJobData get(UUID uuid) {
        return cache.computeIfAbsent(uuid, PlayerJobData::new);
    }
}
