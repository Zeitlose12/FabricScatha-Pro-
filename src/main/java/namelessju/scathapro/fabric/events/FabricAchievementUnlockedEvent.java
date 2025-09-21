package namelessju.scathapro.fabric.events;

import namelessju.scathapro.fabric.achievements.FabricUnlockedAchievement;

/**
 * Event das gefeuert wird, wenn ein Achievement freigeschaltet wird
 */
public class FabricAchievementUnlockedEvent extends FabricEvent {
    
    private final FabricUnlockedAchievement unlockedAchievement;
    
    public FabricAchievementUnlockedEvent(FabricUnlockedAchievement unlockedAchievement) {
        this.unlockedAchievement = unlockedAchievement;
    }
    
    public FabricUnlockedAchievement getUnlockedAchievement() {
        return unlockedAchievement;
    }
    
    /**
     * Prüft ob es sich um eine Wiederholung handelt
     */
    public boolean isRepeat() {
        return unlockedAchievement.getRepeatCount() > 1;
    }
    
    @Override
    public String toString() {
        return "FabricAchievementUnlockedEvent{" +
                "achievement=" + unlockedAchievement.getAchievement().achievementName +
                ", repeatCount=" + unlockedAchievement.getRepeatCount() +
                '}';
    }
}