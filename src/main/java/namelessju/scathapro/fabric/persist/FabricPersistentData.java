package namelessju.scathapro.fabric.persist;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import namelessju.scathapro.fabric.FabricGlobalVariables;
import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.achievements.FabricAchievement;
import namelessju.scathapro.fabric.achievements.FabricAchievementManager;
import namelessju.scathapro.fabric.achievements.FabricUnlockedAchievement;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistente Daten für Scatha-Pro (Fabric)
 * Speichert Achievements, Statistiken und Versions-Infos als JSON.
 */
public class FabricPersistentData {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String DATA_DIR = "scathapro"; // unterhalb von configDir
    private static final String DATA_FILE = "persistent.json";

    public static Path getDataDir() {
        return FabricLoader.getInstance().getConfigDir().resolve(DATA_DIR);
    }

    public static Path getDataFile() {
        return getDataDir().resolve(DATA_FILE);
    }

    // In-Memory Repräsentation
    public static class DataModel {
        public String schemaVersion = "1";
        public String modVersion;           // letzte geladene Mod-Version
        public long lastUpdated;            // epoch millis

        public Stats stats = new Stats();
        public List<JsonObject> achievements = new ArrayList<>(); // list of FabricUnlockedAchievement JSONs
    }

    public static class Stats {
        public int regularWormKills = 0;
        public int scathaKills = 0;
        public int rarePetDrops = 0;
        public int epicPetDrops = 0;
        public int legendaryPetDrops = 0;
        public int scathaFarmingStreak = 0;
        public int scathaFarmingStreakHighscore = 0;
    }

    private DataModel model = new DataModel();

    public void load(FabricScathaPro sp) {
        try {
            Path file = getDataFile();
            if (Files.exists(file)) {
                String s = Files.readString(file, StandardCharsets.UTF_8);
                DataModel loaded = GSON.fromJson(s, DataModel.class);
                if (loaded != null) {
                    this.model = loaded;
                }
            }
            // wende Daten auf Runtime an
            applyToRuntime(sp);
            sp.log("PersistentData geladen");
        } catch (Exception e) {
            sp.logError("Fehler beim Laden der persistenten Daten: " + e.getMessage());
        }
    }

    public void save(FabricScathaPro sp) {
        try {
            captureFromRuntime(sp);
            model.modVersion = FabricScathaPro.VERSION;
            model.lastUpdated = Instant.now().toEpochMilli();
            Path dir = getDataDir();
            Files.createDirectories(dir);
            Files.writeString(getDataFile(), GSON.toJson(model), StandardCharsets.UTF_8);
            sp.logDebug("PersistentData gespeichert");
        } catch (IOException e) {
            sp.logError("Fehler beim Speichern der persistenten Daten: " + e.getMessage());
        }
    }

    public void applyToRuntime(FabricScathaPro sp) {
        // Stats -> Variables
        FabricGlobalVariables v = sp.variables;
        v.regularWormKills = Math.max(0, model.stats.regularWormKills);
        v.scathaKills = Math.max(0, model.stats.scathaKills);
        v.rarePetDrops = Math.max(0, model.stats.rarePetDrops);
        v.epicPetDrops = Math.max(0, model.stats.epicPetDrops);
        v.legendaryPetDrops = Math.max(0, model.stats.legendaryPetDrops);
        v.scathaFarmingStreak = Math.max(0, model.stats.scathaFarmingStreak);
        v.scathaFarmingStreakHighscore = Math.max(0, model.stats.scathaFarmingStreakHighscore);

        // Achievements -> Manager
        FabricAchievementManager am = sp.getAchievementManager();
        if (am != null && model.achievements != null) {
            for (JsonObject obj : model.achievements) {
                FabricUnlockedAchievement ua = FabricUnlockedAchievement.fromJson(obj);
                if (ua != null) am.addUnlockedAchievement(ua);
            }
        }
    }

    public void captureFromRuntime(FabricScathaPro sp) {
        // Variables -> Stats
        FabricGlobalVariables v = sp.variables;
        model.stats.regularWormKills = Math.max(0, v.regularWormKills);
        model.stats.scathaKills = Math.max(0, v.scathaKills);
        model.stats.rarePetDrops = Math.max(0, v.rarePetDrops);
        model.stats.epicPetDrops = Math.max(0, v.epicPetDrops);
        model.stats.legendaryPetDrops = Math.max(0, v.legendaryPetDrops);
        model.stats.scathaFarmingStreak = Math.max(0, v.scathaFarmingStreak);
        model.stats.scathaFarmingStreakHighscore = Math.max(0, v.scathaFarmingStreakHighscore);

        // Manager -> Achievements
        FabricAchievementManager am = sp.getAchievementManager();
        if (am != null) {
            model.achievements.clear();
            for (FabricUnlockedAchievement ua : am.getAllUnlockedAchievements()) {
                model.achievements.add(ua.toJson());
            }
        }
    }

    public DataModel getModel() { return model; }
}