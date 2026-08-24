package pl.przyklad.prace.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.listeners.WandListener;
import pl.przyklad.prace.model.JobType;

public class PraceWandCommand implements CommandExecutor {

    private final PracePlugin plugin;

    public PraceWandCommand(PracePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Ta komenda jest tylko dla graczy.");
            return true;
        }
        if (!player.hasPermission("prace.admin")) {
            player.sendMessage(ChatColor.RED + "Nie masz uprawnien.");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Uzycie: /pracewand <gornik|drwal|farmer|rybak>");
            return true;
        }
        JobType job = JobType.fromString(args[0]);
        if (job == null) {
            player.sendMessage(ChatColor.RED + "Nieznana praca. Uzyj: gornik, drwal, farmer, rybak");
            return true;
        }
        player.getInventory().addItem(WandListener.createWand(job));
        player.sendMessage(ChatColor.GREEN + "Otrzymales wand dla pracy: " + job.getDisplayName());
        return true;
    }
}
