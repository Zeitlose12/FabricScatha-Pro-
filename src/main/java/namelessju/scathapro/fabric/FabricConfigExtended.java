package namelessju.scathapro.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Erweiterte Fabric Config mit Key-System ähnlich dem Original
 * Ermöglicht einfache Verwaltung vieler Config-Optionen
 */
public class FabricConfigExtended {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "scathapro-extended.json";
    
    public enum ConfigKey {
        // Overlay Settings
        OVERLAY_ENABLED("overlay.enabled", true),
        OVERLAY_X("overlay.x", 6),
        OVERLAY_Y("overlay.y", 6),  
        OVERLAY_SCALE("overlay.scale", 1.0),
        OVERLAY_ALIGNMENT("overlay.alignment", ""),
        STATS_TYPE("overlay.statsType", ""),
        SCATHA_PERCENTAGE_ALTERNATIVE_POSITION("overlay.scathaPercentageAlternativePosition", false),
        SCATHA_PERCENTAGE_CYCLE_AMOUNT_DURATION("overlay.scathaPercentageCycleAmountDuration", 3),
        SCATHA_PERCENTAGE_CYCLE_PERCENTAGE_DURATION("overlay.scathaPercentageCyclePercentageDuration", 2),
        SCATHA_PERCENTAGE_DECIMAL_DIGITS("overlay.scathaPercentageDecimalPlaces", 2),
        OVERLAY_ELEMENT_STATES("overlay.overlayElementStates", ""),
        OVERLAY_BACKGROUND_ENABLED("overlay.backgroundEnabled", true),
        
        // Display Options
        OVERLAY_SHOW_SCATHA("overlay.showScatha", true),
        OVERLAY_SHOW_WORM("overlay.showWorm", true),
        OVERLAY_SHOW_TOTAL("overlay.showTotal", true),
        OVERLAY_SHOW_STREAK("overlay.showStreak", true),
        OVERLAY_SHOW_BAR("overlay.showBar", true),
        OVERLAY_SHOW_ICONS("overlay.showIcons", true),
        OVERLAY_SHOW_PET_DROPS("overlay.showPetDrops", true),
        OVERLAY_SHOW_SESSION("overlay.showSession", true),
        OVERLAY_SHOW_MAGIC_FIND("overlay.showMagicFind", true),
        OVERLAY_SHOW_COOLDOWN("overlay.showCooldown", true),
        OVERLAY_SHOW_ACHIEVEMENTS("overlay.showAchievements", false),
        
        // Sounds
        SOUNDS_VOLUME("sounds.volume", 1.0),
        MUTE_CRYSTAL_HOLLOWS_SOUNDS("sounds.muteCrystalHollowsSounds", false),
        KEEP_DRAGON_LAIR_SOUNDS("sounds.keepDragonLairSounds", false),
        
        // Alerts
        ALERT_MODE("alerts.mode", ""),
        CUSTOM_MODE_SUBMODE("alerts.customModeSubmode", ""),
        ALERT_TITLE_SCALE("alerts.title.scale", 1.0),
        ALERT_TITLE_POSITION_X("alerts.title.positionX", 0.5),
        ALERT_TITLE_POSITION_Y("alerts.title.positionY", 0.5),
        ALERT_TITLE_ALIGNMENT("alerts.title.alignment", ""),
        
        // Alert Types
        BEDROCK_WALL_ALERT("alerts.wall", true),
        BEDROCK_WALL_ALERT_TRIGGER_DISTANCE("alerts.wall.triggerDistance", 15),
        OLD_LOBBY_ALERT("alerts.oldLobby.enabled", false),
        OLD_LOBBY_ALERT_TRIGGER_DAY("alerts.oldLobby.triggerDay", 12),
        OLD_LOBBY_ALERT_TRIGGER_MODE("alerts.oldLobby.triggerMode", ""),
        WORM_SPAWN_COOLDOWN_END_ALERT("alerts.wormSpawnCooldownEnd", false),
        WORM_PRESPAWN_ALERT("alerts.wormsPre", true),
        REGULAR_WORM_SPAWN_ALERT("alerts.worms", true),
        SCATHA_SPAWN_ALERT("alerts.scathas", true),
        SCATHA_PET_DROP_ALERT("alerts.pet", true),
        HIGH_HEAT_ALERT("alerts.highHeat", false),
        HIGH_HEAT_ALERT_TRIGGER_VALUE("alerts.highHeat.triggerValue", 98),
        PICKAXE_ABILITY_READY_ALERT("alerts.pickaxeAbilityReadyAlert", true),
        GOBLIN_SPAWN_ALERT("alerts.goblinSpawn", true),
        JERRY_SPAWN_ALERT("alerts.jerrySpawn", true),
        ANTI_SLEEP_ALERT("alerts.antiSleep.enabled", false),
        ANTI_SLEEP_ALERT_INTERVAL_MIN("alerts.antiSleep.intervalMin", 3),
        ANTI_SLEEP_ALERT_INTERVAL_MAX("alerts.antiSleep.intervalMax", 10),
        
        // Achievements
        ACHIEVEMENT_LIST_PRE_OPEN_CATEGORIES("achievements.listPreOpenCategories", false),
        PLAY_ACHIEVEMENT_ALERTS("achievements.playAchievementAlerts", true),
        PLAY_REPEAT_ACHIEVEMENT_ALERTS("achievements.playRepeatAchievementAlerts", true),
        BONUS_ACHIEVEMENTS_SHOWN("achievements.bonusAchievementsShown", false),
        HIDE_UNLOCKED_ACHIEVEMENTS("achievements.hideUnlockedAchievements", false),
        REPEAT_COUNTS_SHOWN("achievements.repeatCountsShown", true),
        
        // Chat & Messages
        SHORT_CHAT_PREFIX("other.shortChatPrefix", false),
        HIDE_WORM_SPAWN_MESSAGE("other.hideWormSpawnMessage", false),
        DRY_STREAK_MESSAGE("other.dryStreakMessage", true),
        DAILY_SCATHA_FARMING_STREAK_MESSAGE("other.dailyScathaFarmingStreakMessage", true),
        CHAT_COPY("other.chatCopy", false),
        WORM_SPAWN_TIMER("other.wormSpawnTimer", false),
        
        // Player Rotation
        SHOW_ROTATION_ANGLES("other.showRotationAngles", false),
        ROTATION_ANGLES_YAW_ONLY("other.rotationAnglesYawOnly", false),
        ROTATION_ANGLES_DECIMAL_DIGITS("other.rotationAnglesDecimalPlaces", 2),
        ROTATION_ANGLES_MINIMAL_YAW("other.rotationAnglesMinimalYaw", false),
        ALTERNATIVE_SENSITIVITY("other.alternativeSensitivity", 0.0),
        
        // Automatic Features
        AUTOMATIC_BACKUPS("other.automaticBackups", true),
        AUTOMATIC_UPDATE_CHECKS("other.automaticUpdateChecks", true),
        AUTOMATIC_WORM_STATS_PARSING("other.automaticStatsParsing", true),
        AUTOMATIC_PET_DROP_SCREENSHOT("other.automaticPetDropScreenshot", false),
        
        // Drop Message Extension
        DROP_MESSAGE_RARITY_MODE("other.dropMessageRarityMode", ""),
        DROP_MESSAGE_RARITY_COLORED("other.dropMessageRarityColored", true),
        DROP_MESSAGE_RARITY_UPPERCASE("other.dropMessageRarityUppercase", false),
        DROP_MESSAGE_STATS_MODE("other.dropMessageStatsMode", ""),
        DROP_MESSAGE_CLEAN_MAGIC_FIND("other.dropMessageCleanMagicFind", false),
        DROP_MESSAGE_STAT_ABBREVIATIONS("other.dropMessageStatAbbreviations", false),
        
        // Special Features
        APRIL_FOOLS_FAKE_DROP_ENABLED("other.aprilFoolsFakeDropEnabled", true),
        SCAPPA_MODE("other.scappaMode", false),
        OVERLAY_ICON_GOOGLY_EYES("other.overlayIconGooglyEyes", false),
        
        // Accessibility
        HIGH_CONTRAST_COLORS("accessibility.highContrastColors", false),
        
        // Development
        DEV_MODE("dev.devMode", false),
        DEBUG_LOGS("dev.debugLogs", false);
        
        public final String key;
        public final Object defaultValue;
        public final Class<?> type;
        
        ConfigKey(String key, Object defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.type = defaultValue.getClass();
        }
    }
    
    private final Map<String, Object> values = new HashMap<>();
    
    private FabricConfigExtended() {}
    
    public static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }
    
    public static FabricConfigExtended load() {
        Path path = getConfigPath();
        FabricConfigExtended config = new FabricConfigExtended();
        
        // Load defaults first
        for (ConfigKey key : ConfigKey.values()) {
            config.values.put(key.key, key.defaultValue);
        }
        
        // Load from file if exists
        if (Files.exists(path)) {
            try {
                String content = Files.readString(path, StandardCharsets.UTF_8);
                JsonObject json = GSON.fromJson(content, JsonObject.class);
                if (json != null) {
                    config.loadFromJson(json);
                }
            } catch (Exception e) {
                ScathaProFabric.LOGGER.error("Could not load extended config: {}", e.getMessage());
            }
        }
        
        return config;
    }
    
    private void loadFromJson(JsonObject json) {
        for (ConfigKey key : ConfigKey.values()) {
            if (json.has(key.key)) {
                try {
                    Object value = parseJsonValue(json.get(key.key), key.type);
                    if (value != null) {
                        values.put(key.key, value);
                    }
                } catch (Exception e) {
                    ScathaProFabric.LOGGER.warn("Could not parse config key {}: {}", key.key, e.getMessage());
                }
            }
        }
    }
    
    private Object parseJsonValue(com.google.gson.JsonElement element, Class<?> type) {
        if (element.isJsonNull()) return null;
        
        if (type == Boolean.class) {
            return element.getAsBoolean();
        } else if (type == Integer.class) {
            return element.getAsInt();
        } else if (type == Double.class) {
            return element.getAsDouble();
        } else if (type == String.class) {
            return element.getAsString();
        }
        return null;
    }
    
    public void save() {
        Path path = getConfigPath();
        try {
            JsonObject json = new JsonObject();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                if (value instanceof Boolean) {
                    json.addProperty(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    json.addProperty(key, (Integer) value);
                } else if (value instanceof Double) {
                    json.addProperty(key, (Double) value);
                } else if (value instanceof String) {
                    json.addProperty(key, (String) value);
                }
            }
            
            Files.createDirectories(path.getParent());
            Files.write(path, GSON.toJson(json).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            ScathaProFabric.LOGGER.error("Could not save extended config: {}", e.getMessage());
        }
    }
    
    // Getter methods
    public boolean getBoolean(ConfigKey key) {
        return (Boolean) values.getOrDefault(key.key, key.defaultValue);
    }
    
    public int getInt(ConfigKey key) {
        return (Integer) values.getOrDefault(key.key, key.defaultValue);
    }
    
    public double getDouble(ConfigKey key) {
        return (Double) values.getOrDefault(key.key, key.defaultValue);
    }
    
    public String getString(ConfigKey key) {
        return (String) values.getOrDefault(key.key, key.defaultValue);
    }
    
    // Setter methods
    public void set(ConfigKey key, boolean value) {
        values.put(key.key, value);
    }
    
    public void set(ConfigKey key, int value) {
        values.put(key.key, value);
    }
    
    public void set(ConfigKey key, double value) {
        values.put(key.key, value);
    }
    
    public void set(ConfigKey key, String value) {
        values.put(key.key, value);
    }
    
    // Convenience methods for compatibility
    public boolean isOverlayEnabled() { return getBoolean(ConfigKey.OVERLAY_ENABLED); }
    public boolean isDebugLogsEnabled() { return getBoolean(ConfigKey.DEBUG_LOGS); }
    public boolean isDevModeEnabled() { return getBoolean(ConfigKey.DEV_MODE); }
    public double getSoundsVolume() { return getDouble(ConfigKey.SOUNDS_VOLUME); }
    
    // Reset to defaults
    public void reset(ConfigKey key) {
        values.put(key.key, key.defaultValue);
    }
    
    public void resetAll() {
        values.clear();
        for (ConfigKey key : ConfigKey.values()) {
            values.put(key.key, key.defaultValue);
        }
    }
}