package namelessju.scathapro.fabric.achievements;

public enum FabricAchievementCategory {
    PROGRESS("Overall Achievement Progress"),
    KILL_AMOUNTS("Worm & Scatha Kills"),
    SCATHA_PET_DROPS("Scatha Pet Drops"),
    ALERT_MODES("Alert Modes"),
    HARD_STONE("Hard Stone Mined"),
    LOBBY_TIMER("Lobby Time"),
    WORM_LIFETIME("Worm Lifetime"),
    TIMEFRAME_KILLS("Timeframed Worm Kills"),
    SCATHA_FARMING_STREAK("Scatha Farming Streak"),
    SPAWN_STREAK("Spawn Streak"),
    SCATHA_SPAWNS("Specific Scatha Spawns"),
    WORM_HIT_KILL("Specific Worm Hits/Kills"),
    ABILITY("Pickaxe Ability"),
    MISCELLANEOUS("Miscellaneous");
    
    private final String name;
    
    FabricAchievementCategory(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public static String getName(FabricAchievementCategory category) {
        return (category != null ? category.name : "Uncategorized");
    }
}