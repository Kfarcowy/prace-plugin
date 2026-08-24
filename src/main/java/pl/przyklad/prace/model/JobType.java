package pl.przyklad.prace.model;

import org.bukkit.Material;

public enum JobType {

    GORNIK("Górnik", "&b", Material.STONE_PICKAXE),
    DRWAL("Drwal", "&6", Material.WOODEN_AXE),
    FARMER("Farmer", "&e", Material.WHEAT),
    RYBAK("Rybak", "&a", Material.FISHING_ROD);

    private final String displayName;
    private final String colorCode; // kod koloru & uzywany w prefiksie czatu
    private final Material icon;

    JobType(String displayName, String colorCode, Material icon) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public Material getIcon() {
        return icon;
    }

    /**
     * Prefiks czatu / tablisty, np. "&bgórnik"
     */
    public String getPrefix() {
        return colorCode + displayName.toLowerCase();
    }

    public static JobType fromString(String s) {
        if (s == null) return null;
        for (JobType type : values()) {
            if (type.name().equalsIgnoreCase(s) || type.displayName.equalsIgnoreCase(s)) {
                return type;
            }
        }
        return null;
    }
}
