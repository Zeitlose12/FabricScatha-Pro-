package namelessju.scathapro.fabric.achievements;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Represents an unlocked achievement with timestamp and repeat count
 */
public class FabricUnlockedAchievement {
    
    private final FabricAchievement achievement;
    private final long unlockTimestamp;
    private int repeatCount;
    
    public FabricUnlockedAchievement(FabricAchievement achievement, long unlockTimestamp) {
        this.achievement = achievement;
        this.unlockTimestamp = unlockTimestamp;
        this.repeatCount = 1;
    }
    
    public FabricUnlockedAchievement(FabricAchievement achievement, long unlockTimestamp, int repeatCount) {
        this.achievement = achievement;
        this.unlockTimestamp = unlockTimestamp;
        this.repeatCount = repeatCount;
    }
    
    public FabricAchievement getAchievement() {
        return achievement;
    }
    
    public long getUnlockTimestamp() {
        return unlockTimestamp;
    }
    
    public int getRepeatCount() {
        return repeatCount;
    }
    
    public void incrementRepeatCount() {
        this.repeatCount++;
    }
    
    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }
    
    /**
     * Converts this unlocked achievement to JSON for saving
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("achievement", new JsonPrimitive(achievement.getID()));
        json.add("unlockTimestamp", new JsonPrimitive(unlockTimestamp));
        json.add("repeatCount", new JsonPrimitive(repeatCount));
        return json;
    }
    
    /**
     * Creates an unlocked achievement from JSON data
     */
    public static FabricUnlockedAchievement fromJson(JsonObject json) {
        if (!json.has("achievement") || !json.has("unlockTimestamp")) {
            return null;
        }
        
        String achievementId = json.get("achievement").getAsString();
        FabricAchievement achievement = FabricAchievement.getByID(achievementId);
        
        if (achievement == null) {
            return null;
        }
        
        long unlockTimestamp = json.get("unlockTimestamp").getAsLong();
        int repeatCount = json.has("repeatCount") ? json.get("repeatCount").getAsInt() : 1;
        
        return new FabricUnlockedAchievement(achievement, unlockTimestamp, repeatCount);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FabricUnlockedAchievement)) return false;
        
        FabricUnlockedAchievement other = (FabricUnlockedAchievement) obj;
        return achievement == other.achievement && unlockTimestamp == other.unlockTimestamp;
    }
    
    @Override
    public int hashCode() {
        return achievement.hashCode() * 31 + Long.hashCode(unlockTimestamp);
    }
    
    @Override
    public String toString() {
        return "FabricUnlockedAchievement{" +
                "achievement=" + achievement.getID() +
                ", unlockTimestamp=" + unlockTimestamp +
                ", repeatCount=" + repeatCount +
                '}';
    }
}