package namelessju.scathapro.fabric.areas;

/**
 * Enum für verschiedene Skyblock-Bereiche
 * Wichtig für die korrekte Feature-Aktivierung
 */
public enum SkyblockArea {
    UNKNOWN("Unknown", false),
    PRIVATE_ISLAND("Private Island", false),
    HUB("Hub", false),
    THE_PARK("The Park", false),
    SPIDERS_DEN("Spider's Den", false),
    THE_END("The End", false),
    CRIMSON_ISLE("Crimson Isle", false),
    THE_RIFT("The Rift", false),
    
    // Mining Areas
    GOLD_MINE("Gold Mine", true),
    DEEP_CAVERNS("Deep Caverns", true),
    DWARVEN_MINES("Dwarven Mines", true),
    CRYSTAL_HOLLOWS("Crystal Hollows", true),
    MINESHAFT("Mineshaft", true),
    GLACITE_MINES("Glacite Mines", true),
    
    // Dungeons
    CATACOMBS("Catacombs", false),
    MASTER_CATACOMBS("Master Catacombs", false),
    
    // Special Areas
    GARDEN("Garden", false),
    FARMING_ISLANDS("Farming Islands", false),
    
    // Events
    JERRY_WORKSHOP("Jerry's Workshop", false),
    WINTER_ISLAND("Winter Island", false);
    
    private final String displayName;
    private final boolean isMiningArea;
    
    SkyblockArea(String displayName, boolean isMiningArea) {
        this.displayName = displayName;
        this.isMiningArea = isMiningArea;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isMiningArea() {
        return isMiningArea;
    }
    
    public boolean isCrystalHollows() {
        return this == CRYSTAL_HOLLOWS;
    }
    
    public boolean isDwarvenMines() {
        return this == DWARVEN_MINES;
    }
    
    public boolean isGlaciteMines() {
        return this == GLACITE_MINES;
    }
    
    public boolean supportsWormDetection() {
        // Worms spawn only in Crystal Hollows
        return this == CRYSTAL_HOLLOWS;
    }
    
    public boolean supportsScathaDetection() {
        // Scathas spawn in Crystal Hollows
        return this == CRYSTAL_HOLLOWS;
    }
    
    /**
     * Versucht den Bereich anhand des Bereichsnamens zu ermitteln
     */
    public static SkyblockArea fromString(String areaName) {
        if (areaName == null || areaName.trim().isEmpty()) {
            return UNKNOWN;
        }
        
        String normalized = areaName.trim().toLowerCase();
        
        // Exakte Matches
        for (SkyblockArea area : values()) {
            if (area.displayName.toLowerCase().equals(normalized)) {
                return area;
            }
        }
        
        // Partial matches and common variations
        if (normalized.contains("crystal") && normalized.contains("hollow")) {
            return CRYSTAL_HOLLOWS;
        }
        if (normalized.contains("dwarven") && normalized.contains("mine")) {
            return DWARVEN_MINES;
        }
        if (normalized.contains("deep") && normalized.contains("cavern")) {
            return DEEP_CAVERNS;
        }
        if (normalized.contains("gold") && normalized.contains("mine")) {
            return GOLD_MINE;
        }
        if (normalized.contains("glacite") && normalized.contains("mine")) {
            return GLACITE_MINES;
        }
        if (normalized.contains("spider") && normalized.contains("den")) {
            return SPIDERS_DEN;
        }
        if (normalized.contains("private") && normalized.contains("island")) {
            return PRIVATE_ISLAND;
        }
        if (normalized.contains("crimson") && normalized.contains("isle")) {
            return CRIMSON_ISLE;
        }
        if (normalized.contains("hub")) {
            return HUB;
        }
        if (normalized.contains("park")) {
            return THE_PARK;
        }
        if (normalized.contains("end")) {
            return THE_END;
        }
        if (normalized.contains("rift")) {
            return THE_RIFT;
        }
        if (normalized.contains("garden")) {
            return GARDEN;
        }
        if (normalized.contains("catacombs")) {
            if (normalized.contains("master")) {
                return MASTER_CATACOMBS;
            }
            return CATACOMBS;
        }
        if (normalized.contains("jerry") && normalized.contains("workshop")) {
            return JERRY_WORKSHOP;
        }
        if (normalized.contains("winter")) {
            return WINTER_ISLAND;
        }
        if (normalized.contains("mineshaft")) {
            return MINESHAFT;
        }
        if (normalized.contains("farming")) {
            return FARMING_ISLANDS;
        }
        
        return UNKNOWN;
    }
}