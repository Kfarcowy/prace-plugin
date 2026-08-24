package pl.przyklad.prace.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.model.JobType;
import pl.przyklad.prace.model.PlayerJobData;
import pl.przyklad.prace.zone.Zone;

import java.util.Map;

public class PraceCommand implements CommandExecutor {

    private final PracePlugin plugin;

    public PraceCommand(PracePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Ta komenda jest tylko dla graczy.");
            return true;
        }

        if (args.length == 0) {
            PlayerJobData data = plugin.getJobDataManager().get(player.getUniqueId());
            player.openInventory(plugin.getGuiFactory().createMainGui(data));
            return true;
        }

        if (args[0].equalsIgnoreCase("zone")) {
            handleZone(player, args);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Uzycie: /prace  lub  /prace zone <create|remove|list> ...");
        return true;
    }

    private void handleZone(Player player, String[] args) {
        if (!player.hasPermission("prace.admin")) {
            player.sendMessage(ChatColor.RED + "Nie masz uprawnien.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Uzycie: /prace zone <create|remove|list> ...");
            return;
        }

        String sub = args[1].toLowerCase();
        switch (sub) {
            case "create": {
                if (args.length < 4) {
                    player.sendMessage(ChatColor.RED + "Uzycie: /prace zone create <gornik|drwal|farmer|rybak> <nazwa>");
                    return;
                }
                JobType job = JobType.fromString(args[2]);
                if (job == null) {
                    player.sendMessage(ChatColor.RED + "Nieznana praca. Uzyj: gornik, drwal, farmer, rybak");
                    return;
                }
                String name = args[3];
                if (plugin.getZoneManager().get(name) != null) {
                    player.sendMessage(ChatColor.RED + "Strefa o tej nazwie juz istnieje.");
                    return;
                }
                Location p1 = plugin.getPos1Map().get(player.getUniqueId());
                Location p2 = plugin.getPos2Map().get(player.getUniqueId());
                if (p1 == null || p2 == null) {
                    player.sendMessage(ChatColor.RED + "Musisz najpierw zaznaczyc obie pozycje wandem (/pracewand " + args[2] + ").");
                    return;
                }
                if (p1.getWorld() == null || p2.getWorld() == null || !p1.getWorld().equals(p2.getWorld())) {
                    player.sendMessage(ChatColor.RED + "Obie pozycje musza byc w tym samym swiecie.");
                    return;
                }
                // wysokosc strefy liczona automatycznie: 30 blokow w dol i 60 w gore
                // od poziomu zaznaczenia
                int groundY = Math.min(p1.getBlockY(), p2.getBlockY());
                int zoneY1 = groundY - 30;
                int zoneY2 = groundY + 60;
                Zone zone = plugin.getZoneManager().create(
                        name, job, p1.getWorld().getName(),
                        p1.getBlockX(), zoneY1, p1.getBlockZ(),
                        p2.getBlockX(), zoneY2, p2.getBlockZ()
                );
                player.sendMessage(ChatColor.GREEN + "Utworzono strefe '" + zone.getName()
                        + "' dla pracy " + job.getDisplayName() + ".");
                return;
            }
            case "remove": {
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Uzycie: /prace zone remove <nazwa>");
                    return;
                }
                boolean removed = plugin.getZoneManager().remove(args[2]);
                player.sendMessage(removed
                        ? ChatColor.GREEN + "Usunieto strefe."
                        : ChatColor.RED + "Nie znaleziono strefy.");
                return;
            }
            case "list": {
                Map<String, Zone> zones = plugin.getZoneManager().getAll();
                if (zones.isEmpty()) {
                    player.sendMessage(ChatColor.YELLOW + "Brak stref.");
                    return;
                }
                player.sendMessage(ChatColor.YELLOW + "Strefy (" + zones.size() + "):");
                for (Zone zone : zones.values()) {
                    player.sendMessage(ChatColor.GRAY + " - " + zone.getName()
                            + " [" + zone.getJobType().getDisplayName() + "]");
                }
                return;
            }
            default:
                player.sendMessage(ChatColor.RED + "Nieznana podkomenda. Uzyj: create, remove, list");
        }
    }
}
