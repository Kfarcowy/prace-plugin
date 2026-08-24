package pl.przyklad.prace.zone;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.model.JobType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ZoneManager {

    private final PracePlugin plugin;
    private final File file;
    private final Map<String, Zone> zones = new LinkedHashMap<>();

    public ZoneManager(PracePlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "zones.yml");
    }

    public void load() {
        zones.clear();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = cfg.getConfigurationSection("zones");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection s = section.getConfigurationSection(key);
            if (s == null) continue;
            JobType jobType = JobType.fromString(s.getString("job"));
            if (jobType == null) continue;
            Zone zone = new Zone(
                    key, jobType, s.getString("world"),
                    s.getInt("x1"), s.getInt("y1"), s.getInt("z1"),
                    s.getInt("x2"), s.getInt("y2"), s.getInt("z2")
            );
            zones.put(key.toLowerCase(), zone);
        }
    }

    public void save() {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Zone zone : zones.values()) {
            String path = "zones." + zone.getName();
            cfg.set(path + ".job", zone.getJobType().name());
            cfg.set(path + ".world", zone.getWorld());
            cfg.set(path + ".x1", zone.getX1());
            cfg.set(path + ".y1", zone.getY1());
            cfg.set(path + ".z1", zone.getZ1());
            cfg.set(path + ".x2", zone.getX2());
            cfg.set(path + ".y2", zone.getY2());
            cfg.set(path + ".z2", zone.getZ2());
        }
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Nie udalo sie zapisac zones.yml", e);
        }
    }

    public Zone create(String name, JobType jobType, String world, int x1, int y1, int z1, int x2, int y2, int z2) {
        Zone zone = new Zone(name, jobType, world, x1, y1, z1, x2, y2, z2);
        zones.put(name.toLowerCase(), zone);
        save();
        return zone;
    }

    public boolean remove(String name) {
        boolean removed = zones.remove(name.toLowerCase()) != null;
        if (removed) save();
        return removed;
    }

    public Zone get(String name) {
        return zones.get(name.toLowerCase());
    }

    public Map<String, Zone> getAll() {
        return zones;
    }

    /**
     * Zwraca pierwsza strefa danego typu pracy zawierajaca podana lokalizacje, lub null.
     */
    public Zone findZone(JobType jobType, Location loc) {
        for (Zone zone : zones.values()) {
            if (zone.getJobType() == jobType && zone.contains(loc)) {
                return zone;
            }
        }
        return null;
    }

    public List<Zone> getByJob(JobType jobType) {
        List<Zone> result = new ArrayList<>();
        for (Zone zone : zones.values()) {
            if (zone.getJobType() == jobType) result.add(zone);
        }
        return result;
    }
}
