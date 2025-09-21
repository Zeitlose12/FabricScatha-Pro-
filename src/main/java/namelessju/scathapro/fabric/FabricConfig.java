package namelessju.scathapro.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FabricConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "scathapro.json";

    // Overlay Settings
    public boolean overlayEnabled = true;
    public boolean overlayVisible = true;
    public boolean debugLogs = false;
    public boolean alertsEnabled = true;
    public boolean alertsDisplayEnabled = true;
    public String alertWormMessage = "Worm spawned!";
    public String alertScathaMessage = "Scatha spawned!";
    public float soundVolume = 1.0f;
    // Overlay v2 (experimentell)
    public boolean overlayV2Enabled = true; // V2 ist Standard und immer aktiv
    public String overlayV2ModelJson = null;
    public boolean overlayBackgroundEnabled = true;
    public boolean overlayColumnsEnabled = true;
    public boolean overlayGooglyEyesEnabled = true;
    public boolean overlaySpinEnabled = false;
    public boolean overlayTextShadow = true;
    public boolean overlaySnapEnabled = true;
    public int overlaySnapSize = 4;
    public int scathaKills = 0;
    public int wormKills = 0;
    public int streak = 0;
    public int overlayX = 6;
    public int overlayY = 6;
    public float overlayScale = 1.0f;
    public boolean overlayCompactMode = false;
    public boolean overlayShowScatha = true;
    public boolean overlayShowWorm = true;
    public boolean overlayShowTotal = true;
    public boolean overlayShowStreak = true;
    public boolean overlayShowBar = true;
    public boolean overlayShowIcons = true;
    public float overlayIconScale = 1.0f;
    public int overlayIconTexSize = 512;
    public String colorProfile = "default"; // default, dark, high
    // Advanced Statistics Display
    public boolean overlayShowPetDrops = true;
    public boolean overlayShowSession = true;
    public boolean overlayShowMagicFind = true;
    public boolean overlayShowCooldown = true;
    public boolean overlayShowAchievements = false;
    // Live-Scatha-Tracker
    public boolean overlayShowLiveTracker = true;
    public int overlayLiveTrackerMaxEntries = 5;
    // Title Icon
    public String overlayTitleIcon = "default"; // default, mode_anime, mode_custom, mode_custom_overlay, mode_meme, scatha_spin
    // Layout
    public int overlayPadding = 8;
    public int overlayColSpacing = 24;
    public int overlayRowSpacing = 12;
    public String overlayAlignment = ""; // left, center, right
    public String statsType = ""; // normal, compact, etc.
    public boolean scathaPercentageAlternativePosition = false;
    public int scathaPercentageCycleAmountDuration = 3;
    public int scathaPercentageCyclePercentageDuration = 2;
    public int scathaPercentageDecimalDigits = 2;
    public String overlayElementStates = "";
    
    // Sounds
    public boolean muteCrystalHollowsSounds = false;
    public boolean keepDragonLairSounds = false;
    
    // Alerts
    public String mode = "";
    public String customModeSubmode = "";
    public double alertTitleScale = 1.0;
    public double alertTitlePositionX = 0.5;
    public double alertTitlePositionY = 0.5;
    public String alertTitleAlignment = "";
    public boolean bedrockWallAlert = true;
    public int bedrockWallAlertTriggerDistance = 15;
    public boolean oldLobbyAlert = false;
    public int oldLobbyAlertTriggerDay = 12;
    public String oldLobbyAlertTriggerMode = "";
    public boolean wormSpawnCooldownEndAlert = false;
    public boolean wormPrespawnAlert = true;
    public boolean regularWormSpawnAlert = true;
    public boolean scathaSpawnAlert = true;
    public boolean scathaPetDropAlert = true;
    public boolean highHeatAlert = false;
    public int highHeatAlertTriggerValue = 98;
    public boolean pickaxeAbilityReadyAlert = true;
    public boolean goblinSpawnAlert = true;
    public boolean jerrySpawnAlert = true;
    public boolean antiSleepAlert = false;
    public int antiSleepAlertIntervalMin = 3;
    public int antiSleepAlertIntervalMax = 10;
    
    // Achievements
    public boolean achievementListPreOpenCategories = false;
    public boolean playAchievementAlerts = true;
    public boolean playRepeatAchievementAlerts = true;
    public boolean bonusAchievementsShown = false;
    public boolean hideUnlockedAchievements = false;
    public boolean repeatCountsShown = true;
    
    // Chat & Messages
    public boolean shortChatPrefix = false;
    public boolean hideWormSpawnMessage = false;
    public boolean dryStreakMessage = true;
    public boolean dailyScathaFarmingStreakMessage = true;
    public boolean chatCopy = false;
    public boolean wormSpawnTimer = false;
    
    // Player Rotation
    public boolean showRotationAngles = false;
    public boolean rotationAnglesYawOnly = false;
    public int rotationAnglesDecimalDigits = 2;
    public boolean rotationAnglesMinimalYaw = false;
    public double alternativeSensitivity = 0.0;
    
    // Automatic Features
    public boolean automaticBackups = true;
    public boolean automaticUpdateChecks = true;
    public boolean automaticWormStatsParsing = true;
    public boolean automaticPetDropScreenshot = false;
    
    // Drop Message Extension
    public String dropMessageRarityMode = "";
    public boolean dropMessageRarityColored = true;
    public boolean dropMessageRarityUppercase = false;
    public String dropMessageStatsMode = "";
    public boolean dropMessageCleanMagicFind = false;
    public boolean dropMessageStatAbbreviations = false;
    
    // April Fools
    public boolean aprilFoolsFakeDropEnabled = true;
    
    // Unlockables
    public boolean scappaMode = false;
    public boolean overlayIconGooglyEyes = false;
    
    // Accessibility
    public boolean highContrastColors = false;
    
    // Overlay Style Settings
    public String overlayStyle = "v2"; // "v2" oder "classic"
    
    // Dev Settings
    public boolean devMode = false;
    
    // Achievement System Settings (für Settings UI)
    public boolean achievementsEnabled = true;
    public boolean achievementSoundsEnabled = true;
    public boolean achievementProgressTracking = true;
    public boolean showBonusAchievements = false;

    private FabricConfig() {}

    public static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public static FabricConfig load() {
        Path path = getConfigPath();
        FabricConfig cfg = new FabricConfig();
        if (Files.exists(path)) {
            try {
                String s = Files.readString(path, StandardCharsets.UTF_8);
                JsonObject json = GSON.fromJson(s, JsonObject.class);
                if (json != null) {
                    if (json.has("overlayVisible")) cfg.overlayVisible = json.get("overlayVisible").getAsBoolean();
                    if (json.has("debugLogs")) cfg.debugLogs = json.get("debugLogs").getAsBoolean();
                    if (json.has("alertsEnabled")) cfg.alertsEnabled = json.get("alertsEnabled").getAsBoolean();
                    if (json.has("alertsDisplayEnabled")) cfg.alertsDisplayEnabled = json.get("alertsDisplayEnabled").getAsBoolean();
                    if (json.has("alertWormMessage")) cfg.alertWormMessage = json.get("alertWormMessage").getAsString();
                    if (json.has("alertScathaMessage")) cfg.alertScathaMessage = json.get("alertScathaMessage").getAsString();
                    if (json.has("soundVolume")) { try { cfg.soundVolume = Math.max(0f, Math.min(1f, json.get("soundVolume").getAsFloat())); } catch (Exception ignored) {} }
                    if (json.has("overlayV2Enabled")) cfg.overlayV2Enabled = json.get("overlayV2Enabled").getAsBoolean();
                    if (json.has("overlayV2ModelJson")) cfg.overlayV2ModelJson = json.get("overlayV2ModelJson").isJsonNull()? null : json.get("overlayV2ModelJson").getAsString();
                    if (json.has("overlayBackgroundEnabled")) cfg.overlayBackgroundEnabled = json.get("overlayBackgroundEnabled").getAsBoolean();
                    if (json.has("overlayColumnsEnabled")) cfg.overlayColumnsEnabled = json.get("overlayColumnsEnabled").getAsBoolean();
                    if (json.has("overlayGooglyEyesEnabled")) cfg.overlayGooglyEyesEnabled = json.get("overlayGooglyEyesEnabled").getAsBoolean();
                    if (json.has("overlaySpinEnabled")) cfg.overlaySpinEnabled = json.get("overlaySpinEnabled").getAsBoolean();
                    if (json.has("overlayTextShadow")) cfg.overlayTextShadow = json.get("overlayTextShadow").getAsBoolean();
                    if (json.has("scathaKills")) cfg.scathaKills = Math.max(0, json.get("scathaKills").getAsInt());
                    if (json.has("wormKills")) cfg.wormKills = Math.max(0, json.get("wormKills").getAsInt());
                    if (json.has("streak")) cfg.streak = Math.max(0, json.get("streak").getAsInt());
                    if (json.has("overlayX")) cfg.overlayX = Math.max(0, json.get("overlayX").getAsInt());
                    if (json.has("overlayY")) cfg.overlayY = Math.max(0, json.get("overlayY").getAsInt());
                    if (json.has("overlayScale")) {
                        try {
                            cfg.overlayScale = Math.max(0.1f, json.get("overlayScale").getAsFloat());
                        } catch (Exception ignored) {}
                    }
                    if (json.has("overlayCompactMode")) cfg.overlayCompactMode = json.get("overlayCompactMode").getAsBoolean();
                    if (json.has("overlayShowScatha")) cfg.overlayShowScatha = json.get("overlayShowScatha").getAsBoolean();
                    if (json.has("overlayShowWorm")) cfg.overlayShowWorm = json.get("overlayShowWorm").getAsBoolean();
                    if (json.has("overlayShowTotal")) cfg.overlayShowTotal = json.get("overlayShowTotal").getAsBoolean();
                    if (json.has("overlayShowStreak")) cfg.overlayShowStreak = json.get("overlayShowStreak").getAsBoolean();
                    if (json.has("overlayShowBar")) cfg.overlayShowBar = json.get("overlayShowBar").getAsBoolean();
                    if (json.has("overlayShowIcons")) cfg.overlayShowIcons = json.get("overlayShowIcons").getAsBoolean();
                    if (json.has("overlayIconScale")) {
                        try { cfg.overlayIconScale = Math.max(0.5f, json.get("overlayIconScale").getAsFloat()); } catch (Exception ignored) {}
                    }
                    if (json.has("overlayIconTexSize")) {
                        try { cfg.overlayIconTexSize = Math.max(16, json.get("overlayIconTexSize").getAsInt()); } catch (Exception ignored) {}
                    }
                    if (json.has("colorProfile")) cfg.colorProfile = json.get("colorProfile").getAsString();
                    if (json.has("overlayTitleIcon")) cfg.overlayTitleIcon = json.get("overlayTitleIcon").getAsString();
                    if (json.has("overlayPadding")) { try { cfg.overlayPadding = Math.max(0, json.get("overlayPadding").getAsInt()); } catch (Exception ignored) {} }
                    if (json.has("overlayColSpacing")) { try { cfg.overlayColSpacing = Math.max(0, json.get("overlayColSpacing").getAsInt()); } catch (Exception ignored) {} }
                    if (json.has("overlayRowSpacing")) { try { cfg.overlayRowSpacing = Math.max(0, json.get("overlayRowSpacing").getAsInt()); } catch (Exception ignored) {} }
                    if (json.has("overlaySnapEnabled")) cfg.overlaySnapEnabled = json.get("overlaySnapEnabled").getAsBoolean();
                    if (json.has("overlaySnapSize")) { try { cfg.overlaySnapSize = Math.max(1, json.get("overlaySnapSize").getAsInt()); } catch (Exception ignored) {} }
                    // Advanced Statistics Display
                    if (json.has("overlayShowPetDrops")) cfg.overlayShowPetDrops = json.get("overlayShowPetDrops").getAsBoolean();
                    if (json.has("overlayShowSession")) cfg.overlayShowSession = json.get("overlayShowSession").getAsBoolean();
                    if (json.has("overlayShowMagicFind")) cfg.overlayShowMagicFind = json.get("overlayShowMagicFind").getAsBoolean();
                    if (json.has("overlayShowCooldown")) cfg.overlayShowCooldown = json.get("overlayShowCooldown").getAsBoolean();
                    if (json.has("overlayShowAchievements")) cfg.overlayShowAchievements = json.get("overlayShowAchievements").getAsBoolean();
                    // Live-Scatha-Tracker
                    if (json.has("overlayShowLiveTracker")) cfg.overlayShowLiveTracker = json.get("overlayShowLiveTracker").getAsBoolean();
                    if (json.has("overlayLiveTrackerMaxEntries")) { try { cfg.overlayLiveTrackerMaxEntries = Math.max(1, Math.min(20, json.get("overlayLiveTrackerMaxEntries").getAsInt())); } catch (Exception ignored) {} }
                    // Overlay Style
                    if (json.has("overlayStyle")) cfg.overlayStyle = json.get("overlayStyle").getAsString();
                    // Achievement System Settings
                    if (json.has("achievementsEnabled")) cfg.achievementsEnabled = json.get("achievementsEnabled").getAsBoolean();
                    if (json.has("achievementSoundsEnabled")) cfg.achievementSoundsEnabled = json.get("achievementSoundsEnabled").getAsBoolean();
                    if (json.has("achievementProgressTracking")) cfg.achievementProgressTracking = json.get("achievementProgressTracking").getAsBoolean();
                    if (json.has("showBonusAchievements")) cfg.showBonusAchievements = json.get("showBonusAchievements").getAsBoolean();
                }
            } catch (Exception e) {
                ScathaProFabric.LOGGER.error("Konnte Konfig nicht laden: {}", e.getMessage());
            }
        }
        return cfg;
    }

    public void save() {
        Path path = getConfigPath();
        try {
            JsonObject json = new JsonObject();
            json.addProperty("overlayVisible", overlayVisible);
            json.addProperty("debugLogs", debugLogs);
            json.addProperty("alertsEnabled", alertsEnabled);
            json.addProperty("alertsDisplayEnabled", alertsDisplayEnabled);
            json.addProperty("alertWormMessage", alertWormMessage);
            json.addProperty("alertScathaMessage", alertScathaMessage);
            json.addProperty("soundVolume", soundVolume);
            json.addProperty("overlayV2Enabled", overlayV2Enabled);
            if (overlayV2ModelJson != null) json.addProperty("overlayV2ModelJson", overlayV2ModelJson); else json.add("overlayV2ModelJson", null);
            json.addProperty("overlayBackgroundEnabled", overlayBackgroundEnabled);
            json.addProperty("overlayColumnsEnabled", overlayColumnsEnabled);
            json.addProperty("overlayGooglyEyesEnabled", overlayGooglyEyesEnabled);
            json.addProperty("overlaySpinEnabled", overlaySpinEnabled);
            json.addProperty("overlayTextShadow", overlayTextShadow);
            json.addProperty("scathaKills", Math.max(0, scathaKills));
            json.addProperty("wormKills", Math.max(0, wormKills));
            json.addProperty("streak", Math.max(0, streak));
            json.addProperty("overlayX", Math.max(0, overlayX));
            json.addProperty("overlayY", Math.max(0, overlayY));
            json.addProperty("overlayScale", overlayScale);
            json.addProperty("overlayCompactMode", overlayCompactMode);
            json.addProperty("overlayShowScatha", overlayShowScatha);
            json.addProperty("overlayShowWorm", overlayShowWorm);
            json.addProperty("overlayShowTotal", overlayShowTotal);
            json.addProperty("overlayShowStreak", overlayShowStreak);
            json.addProperty("overlayShowBar", overlayShowBar);
            json.addProperty("overlayShowIcons", overlayShowIcons);
            json.addProperty("overlayIconScale", overlayIconScale);
            json.addProperty("overlayIconTexSize", overlayIconTexSize);
            json.addProperty("colorProfile", colorProfile);
            json.addProperty("overlayTitleIcon", overlayTitleIcon);
            json.addProperty("overlayPadding", overlayPadding);
            json.addProperty("overlayColSpacing", overlayColSpacing);
            json.addProperty("overlayRowSpacing", overlayRowSpacing);
            json.addProperty("overlaySnapEnabled", overlaySnapEnabled);
            json.addProperty("overlaySnapSize", overlaySnapSize);
            // Advanced Statistics Display
            json.addProperty("overlayShowPetDrops", overlayShowPetDrops);
            json.addProperty("overlayShowSession", overlayShowSession);
            json.addProperty("overlayShowMagicFind", overlayShowMagicFind);
            json.addProperty("overlayShowCooldown", overlayShowCooldown);
            json.addProperty("overlayShowAchievements", overlayShowAchievements);
            // Live-Scatha-Tracker
            json.addProperty("overlayShowLiveTracker", overlayShowLiveTracker);
            json.addProperty("overlayLiveTrackerMaxEntries", overlayLiveTrackerMaxEntries);
            // Overlay Style
            json.addProperty("overlayStyle", overlayStyle);
            // Achievement System Settings
            json.addProperty("achievementsEnabled", achievementsEnabled);
            json.addProperty("achievementSoundsEnabled", achievementSoundsEnabled);
            json.addProperty("achievementProgressTracking", achievementProgressTracking);
            json.addProperty("showBonusAchievements", showBonusAchievements);
            byte[] bytes = GSON.toJson(json).getBytes(StandardCharsets.UTF_8);
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
        } catch (IOException e) {
            ScathaProFabric.LOGGER.error("Konnte Konfig nicht speichern: {}", e.getMessage());
        }
    }
}
