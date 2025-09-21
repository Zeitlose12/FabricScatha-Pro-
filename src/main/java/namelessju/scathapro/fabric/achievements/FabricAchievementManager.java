package namelessju.scathapro.fabric.achievements;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.events.FabricAchievementUnlockedEvent;
import namelessju.scathapro.fabric.events.FabricEvent;
import namelessju.scathapro.fabric.util.FabricTimeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Fabric-Version des Achievement-Managers
 * Verwaltet freigeschaltete Achievements und Progress
 */
public class FabricAchievementManager {
    
    private final FabricScathaPro scathaPro;
    private final Map<String, FabricUnlockedAchievement> unlockedAchievements = new HashMap<>();
    
    public FabricAchievementManager(FabricScathaPro scathaPro) {
        this.scathaPro = scathaPro;
        updateBonusTypeVisibility();
        scathaPro.log("Achievement Manager initialisiert");
    }
    
    /**
     * Schaltet ein Achievement frei
     */
    public FabricUnlockedAchievement unlockAchievement(FabricAchievement achievement) {
        return unlockAchievement(achievement, -1);
    }
    
    /**
     * Schaltet ein Achievement mit spezifischem Goal-Count frei
     */
    public FabricUnlockedAchievement unlockAchievement(FabricAchievement achievement, int goalReachedCount) {
        FabricUnlockedAchievement unlockedAchievement = getUnlockedAchievement(achievement);
        
        if (goalReachedCount == 0) return unlockedAchievement;
        
        if (unlockedAchievement == null) {
            // Erstes Freischalten
            unlockedAchievement = new FabricUnlockedAchievement(achievement, FabricTimeUtil.now());
            unlockedAchievements.put(achievement.getID(), unlockedAchievement);
            
            scathaPro.log("Achievement freigeschaltet: " + achievement.achievementName);
            
        } else {
            // Achievement bereits freigeschaltet
            if (!achievement.isRepeatable) return unlockedAchievement;
            
            int repeatCount = unlockedAchievement.getRepeatCount();
            if (goalReachedCount > 0) {
                int newRepeatCount = goalReachedCount - 1;
                if (newRepeatCount <= repeatCount) return unlockedAchievement;
                unlockedAchievement.setRepeatCount(newRepeatCount);
            } else {
                unlockedAchievement.setRepeatCount(repeatCount + 1);
            }
            
            scathaPro.log("Achievement wiederholt: " + achievement.achievementName + " (" + unlockedAchievement.getRepeatCount() + "x)");
        }
        
        // Speichere Achievements
        savePersistentData();
        
        // Feuer Achievement-Event
        FabricEvent.post(new FabricAchievementUnlockedEvent(unlockedAchievement));
        
        return unlockedAchievement;
    }
    
    /**
     * Entzieht ein Achievement (für Debug/Admin-Zwecke)
     */
    public boolean revokeAchievement(FabricAchievement achievement) {
        if (unlockedAchievements.remove(achievement.getID()) != null) {
            achievement.setProgress(0f);
            savePersistentData();
            scathaPro.log("Achievement entzogen: " + achievement.achievementName);
            return true;
        }
        return false;
    }
    
    /**
     * Prüft ob ein Achievement freigeschaltet ist
     */
    public boolean isAchievementUnlocked(FabricAchievement achievement) {
        return getUnlockedAchievement(achievement) != null;
    }
    
    /**
     * Holt ein freigeschaltetes Achievement
     */
    public FabricUnlockedAchievement getUnlockedAchievement(FabricAchievement achievement) {
        return unlockedAchievements.get(achievement.getID());
    }
    
    /**
     * Löscht alle freigeschalteten Achievements (für Reset)
     */
    public void clearUnlockedAchievements() {
        unlockedAchievements.clear();
        scathaPro.log("Alle Achievements zurückgesetzt");
        savePersistentData();
    }
    
    /**
     * Fügt ein freigeschaltetes Achievement hinzu (für Laden von Daten)
     */
    public void addUnlockedAchievement(FabricUnlockedAchievement unlockedAchievement) {
        unlockedAchievements.put(unlockedAchievement.getAchievement().getID(), unlockedAchievement);
    }
    
    /**
     * Gibt alle freigeschalteten Achievements zurück
     */
    public FabricUnlockedAchievement[] getAllUnlockedAchievements() {
        return unlockedAchievements.values().toArray(new FabricUnlockedAchievement[0]);
    }
    
    /**
     * Gibt alle verfügbaren Achievements zurück
     */
    public static FabricAchievement[] getAllAchievements() {
        return FabricAchievement.values();
    }
    
    /**
     * Aktualisiert die Sichtbarkeit von Bonus-Achievements
     */
    public void updateBonusTypeVisibility() {
        boolean showBonusAchievements = false;
        try {
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            if (cfg != null) showBonusAchievements = cfg.showBonusAchievements;
        } catch (Exception ignored) {}
        
        FabricAchievement.Type.BONUS.visibilityOverride = showBonusAchievements;
        scathaPro.logDebug("Bonus Achievement Sichtbarkeit: " + showBonusAchievements);
    }
    
    /**
     * Berechnet Achievement-Statistiken
     */
    public AchievementStats calculateStats() {
        int totalAchievements = 0;
        int unlockedCount = 0;
        int visibleAchievements = 0;
        int unlockedVisible = 0;
        
        for (FabricAchievement achievement : FabricAchievement.values()) {
            totalAchievements++;
            
            if (achievement.type.isVisible()) {
                visibleAchievements++;
                if (isAchievementUnlocked(achievement)) {
                    unlockedVisible++;
                }
            }
            
            if (isAchievementUnlocked(achievement)) {
                unlockedCount++;
            }
        }
        
        return new AchievementStats(totalAchievements, unlockedCount, visibleAchievements, unlockedVisible);
    }
    
    /**
     * Methoden für Settings-UI
     */
    public void testAchievement(String message) {
        scathaPro.log("Test-Achievement: " + message);
        
        // Test-Achievement mit Alert-System anzeigen
        try {
            namelessju.scathapro.fabric.util.FabricHudUtil.showOverlayMessage(message);
            
            // Zusätzlich Chat-Nachricht senden
            var mc = net.minecraft.client.MinecraftClient.getInstance();
            if (mc != null && mc.player != null) {
                mc.player.sendMessage(net.minecraft.text.Text.literal(
                    namelessju.scathapro.fabric.Constants.chatPrefix + "Test Achievement: " + message), false);
            }
        } catch (Exception e) {
            scathaPro.logError("Fehler beim Test-Achievement: " + e.getMessage());
        }
    }
    
    public void resetProgress() {
        clearUnlockedAchievements();
        scathaPro.log("Achievement-Progress zurückgesetzt");
    }
    
    public int getTotalAchievements() {
        return FabricAchievement.values().length;
    }
    
    public int getUnlockedAchievements() {
        return unlockedAchievements.size();
    }
    
    /**
     * Speichert Achievements in persistenten Daten
     */
    private void savePersistentData() {
        try {
            if (scathaPro != null && scathaPro.getPersistentData() != null) {
                scathaPro.getPersistentData().save(scathaPro);
            }
        } catch (Exception e) {
            scathaPro.logError("Fehler beim Speichern der Achievement-Daten: " + e.getMessage());
        }
        scathaPro.logDebug("Achievement-Daten gespeichert");
    }
    
    /**
     * Statistik-Klasse für Achievement-Übersichten
     */
    public static class AchievementStats {
        public final int totalAchievements;
        public final int unlockedCount;
        public final int visibleAchievements;
        public final int unlockedVisible;
        
        public AchievementStats(int totalAchievements, int unlockedCount, int visibleAchievements, int unlockedVisible) {
            this.totalAchievements = totalAchievements;
            this.unlockedCount = unlockedCount;
            this.visibleAchievements = visibleAchievements;
            this.unlockedVisible = unlockedVisible;
        }
        
        public double getProgressPercentage() {
            return visibleAchievements > 0 ? (double) unlockedVisible / visibleAchievements * 100.0 : 0.0;
        }
        
        public double getTotalProgressPercentage() {
            return totalAchievements > 0 ? (double) unlockedCount / totalAchievements * 100.0 : 0.0;
        }
    }
}