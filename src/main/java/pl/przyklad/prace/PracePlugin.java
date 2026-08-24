package pl.przyklad.prace;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.przyklad.prace.commands.PraceCommand;
import pl.przyklad.prace.commands.PraceWandCommand;
import pl.przyklad.prace.data.JobDataManager;
import pl.przyklad.prace.gui.GuiFactory;
import pl.przyklad.prace.listeners.ChatListener;
import pl.przyklad.prace.listeners.FarmListener;
import pl.przyklad.prace.listeners.FishListener;
import pl.przyklad.prace.listeners.GuiListener;
import pl.przyklad.prace.listeners.MineListener;
import pl.przyklad.prace.listeners.WandListener;
import pl.przyklad.prace.listeners.WoodListener;
import pl.przyklad.prace.listeners.WoodZoneProtectListener;
import pl.przyklad.prace.placeholder.PraceExpansion;
import pl.przyklad.prace.util.BossBarManager;
import pl.przyklad.prace.zone.ZoneManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PracePlugin extends JavaPlugin {

    private Economy economy;
    private JobDataManager jobDataManager;
    private ZoneManager zoneManager;
    private GuiFactory guiFactory;
    private BossBarManager bossBarManager;

    private final Map<UUID, Location> pos1 = new HashMap<>();
    private final Map<UUID, Location> pos2 = new HashMap<>();

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Nie znaleziono Vault lub providera ekonomii! Wylaczam plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        jobDataManager = new JobDataManager(this);
        jobDataManager.load();

        zoneManager = new ZoneManager(this);
        zoneManager.load();

        guiFactory = new GuiFactory(this);
        bossBarManager = new BossBarManager(this);

        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new WandListener(this), this);
        getServer().getPluginManager().registerEvents(new MineListener(this), this);
        getServer().getPluginManager().registerEvents(new WoodListener(this), this);
        getServer().getPluginManager().registerEvents(new WoodZoneProtectListener(this), this);
        getServer().getPluginManager().registerEvents(new FarmListener(this), this);
        getServer().getPluginManager().registerEvents(new FishListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        getCommand("prace").setExecutor(new PraceCommand(this));
        getCommand("pracewand").setExecutor(new PraceWandCommand(this));

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PraceExpansion(this).register();
            getLogger().info("Zarejestrowano placeholder %your_job% w PlaceholderAPI.");
        }

        // odswiez tabliste dla juz polaczonych graczy (np. po /reload)
        ChatListener tabRefresher = new ChatListener(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            tabRefresher.updateTabName(player);
        }

        getLogger().info("PraceVault wlaczony pomyslnie.");
    }

    @Override
    public void onDisable() {
        if (jobDataManager != null) {
            jobDataManager.save();
        }
        if (zoneManager != null) {
            zoneManager.save();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public JobDataManager getJobDataManager() {
        return jobDataManager;
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public GuiFactory getGuiFactory() {
        return guiFactory;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public Map<UUID, Location> getPos1Map() {
        return pos1;
    }

    public Map<UUID, Location> getPos2Map() {
        return pos2;
    }
}
