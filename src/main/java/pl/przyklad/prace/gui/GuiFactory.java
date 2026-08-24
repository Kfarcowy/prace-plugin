package pl.przyklad.prace.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.przyklad.prace.PracePlugin;
import pl.przyklad.prace.model.JobType;
import pl.przyklad.prace.model.PlayerJobData;
import pl.przyklad.prace.util.SmallCaps;

import java.util.ArrayList;
import java.util.List;

public class GuiFactory {

    // sloty srodkowego rzedu, wysrodkowane, 1 slot odstepu miedzy kazda praca
    public static final int[] JOB_SLOTS = {10, 12, 14, 16};
    public static final int BOOK_SLOT = 18;
    public static final int RESIGN_SLOT = 22;
    public static final int PAPER_SLOT = 26;

    private final PracePlugin plugin;

    public GuiFactory(PracePlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory createMainGui(PlayerJobData data) {
        MainJobsHolder holder = new MainJobsHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, ChatColor.DARK_GRAY + SmallCaps.convert("Prace"));
        holder.setInventory(inv);

        for (int i = 0; i < JobType.values().length && i < JOB_SLOTS.length; i++) {
            inv.setItem(JOB_SLOTS[i], createJobItem(JobType.values()[i]));
        }

        inv.setItem(BOOK_SLOT, createInfoBook(data));
        inv.setItem(RESIGN_SLOT, createResignItem(data));
        inv.setItem(PAPER_SLOT, createChangeJobPaper());

        return inv;
    }

    public Inventory createChangeGui() {
        ChangeJobsHolder holder = new ChangeJobsHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, ChatColor.DARK_GRAY + SmallCaps.convert("Zmiana Pracy"));
        holder.setInventory(inv);

        for (int i = 0; i < JobType.values().length && i < JOB_SLOTS.length; i++) {
            inv.setItem(JOB_SLOTS[i], createJobItem(JobType.values()[i]));
        }

        return inv;
    }

    private ItemStack createJobItem(JobType job) {
        ItemStack item = new ItemStack(job.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&l" + job.getColorCode())
                    + SmallCaps.convert(job.getDisplayName()));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Kliknij, aby wybrac te prace.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createInfoBook(PlayerJobData data) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta meta = book.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + SmallCaps.convert("Informacje"));
            List<String> lore = new ArrayList<>();
            String jobName = data.hasJob() ? data.getJob().getDisplayName() : "brak";
            lore.add(ChatColor.GRAY + "Wybrana praca: " + ChatColor.WHITE + jobName);
            lore.add(ChatColor.GRAY + "Ilosc zarobionej kasy: " + ChatColor.GREEN + String.format("%.2f", data.getEarnedMoney()) + "$");
            lore.add(ChatColor.RED + "Uwaga: jesli klikniesz w zmiane pracy,");
            lore.add(ChatColor.RED + "pobierze ci 35 dolarow.");
            meta.setLore(lore);
            book.setItemMeta(meta);
        }
        return book;
    }

    private ItemStack createChangeJobPaper() {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + SmallCaps.convert("Zmien prace"));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Prace mozesz zmienic za " + ChatColor.GREEN + "35 dolarkow" + ChatColor.GRAY + ".");
            meta.setLore(lore);
            paper.setItemMeta(meta);
        }
        return paper;
    }

    private ItemStack createResignItem(PlayerJobData data) {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + SmallCaps.convert("Rezygnuj z pracy"));
            List<String> lore = new ArrayList<>();
            if (data.hasJob()) {
                lore.add(ChatColor.GRAY + "Aktualna praca: " + ChatColor.WHITE + data.getJob().getDisplayName());
                lore.add(ChatColor.GRAY + "Kliknij, aby zrezygnowac (za darmo).");
            } else {
                lore.add(ChatColor.GRAY + "Nie masz obecnie zadnej pracy.");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
