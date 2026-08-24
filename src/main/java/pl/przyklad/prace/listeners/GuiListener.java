package pl.przyklad.prace.listeners;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.gui.ChangeJobsHolder;
import pl.przyklad.prace.gui.GuiFactory;
import pl.przyklad.prace.gui.MainJobsHolder;
import pl.przyklad.prace.model.JobType;
import pl.przyklad.prace.model.PlayerJobData;
import pl.przyklad.prace.util.SmallCaps;

public class GuiListener implements Listener {

    private static final double CHANGE_JOB_COST = 35.0;

    private final PracePlugin plugin;

    public GuiListener(PracePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getInventory().getHolder() instanceof MainJobsHolder) {
            event.setCancelled(true);
            handleMainGuiClick(player, event.getRawSlot());
        } else if (event.getInventory().getHolder() instanceof ChangeJobsHolder) {
            event.setCancelled(true);
            handleChangeGuiClick(player, event.getRawSlot());
        }
    }

    private void handleMainGuiClick(Player player, int slot) {
        if (slot == GuiFactory.PAPER_SLOT) {
            player.closeInventory();
            player.openInventory(plugin.getGuiFactory().createChangeGui());
            return;
        }
        if (slot == GuiFactory.BOOK_SLOT) {
            return; // tylko informacyjna, nic nie robi
        }
        if (slot == GuiFactory.RESIGN_SLOT) {
            handleResign(player);
            return;
        }

        JobType job = jobFromSlot(slot);
        if (job == null) {
            return;
        }

        PlayerJobData data = plugin.getJobDataManager().get(player.getUniqueId());
        if (data.hasJob()) {
            player.sendMessage(ChatColor.RED + "Juz masz prace! Uzyj papieru 'Zmien prace', aby ja zmienic.");
            return;
        }

        // pierwsza praca za darmo
        data.setJob(job);
        plugin.getJobDataManager().save();
        player.closeInventory();
        showJobTitle(player, job);
    }

    private void handleResign(Player player) {
        PlayerJobData data = plugin.getJobDataManager().get(player.getUniqueId());
        if (!data.hasJob()) {
            player.sendMessage(ChatColor.RED + "Nie masz zadnej pracy, z ktorej mozna zrezygnowac.");
            return;
        }
        data.setJob(null);
        data.setMinerProgress(0);
        data.setDrwalProgress(0);
        data.getFarmerProgressMap().clear();
        plugin.getJobDataManager().save();
        plugin.getBossBarManager().remove(player);
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Zrezygnowales z pracy. Kolejny wybor jest znowu za darmo.");
    }

    private void handleChangeGuiClick(Player player, int slot) {
        JobType job = jobFromSlot(slot);
        if (job == null) {
            return;
        }

        PlayerJobData data = plugin.getJobDataManager().get(player.getUniqueId());

        if (data.hasJob() && data.getJob() == job) {
            player.sendMessage(ChatColor.RED + "Juz wykonujesz te prace.");
            return;
        }

        double balance = plugin.getEconomy().getBalance(player);
        if (balance < CHANGE_JOB_COST) {
            player.sendMessage(ChatColor.RED + "Nie masz wystarczajaco pieniedzy (potrzebujesz "
                    + CHANGE_JOB_COST + "$).");
            return;
        }

        EconomyResponse response = plugin.getEconomy().withdrawPlayer(player, CHANGE_JOB_COST);
        if (!response.transactionSuccess()) {
            player.sendMessage(ChatColor.RED + "Transakcja nie powiodla sie: " + response.errorMessage);
            return;
        }

        data.setJob(job);
        plugin.getJobDataManager().save();
        player.closeInventory();
        showJobTitle(player, job);
    }

    private JobType jobFromSlot(int slot) {
        int[] slots = GuiFactory.JOB_SLOTS;
        JobType[] jobs = JobType.values();
        for (int i = 0; i < slots.length && i < jobs.length; i++) {
            if (slots[i] == slot) {
                return jobs[i];
            }
        }
        return null;
    }

    private void showJobTitle(Player player, JobType job) {
        String title = ChatColor.GREEN + SmallCaps.convert("Wybrales Prace " + job.getDisplayName());
        player.sendTitle(title, "", 10, 60, 10);
    }
}
