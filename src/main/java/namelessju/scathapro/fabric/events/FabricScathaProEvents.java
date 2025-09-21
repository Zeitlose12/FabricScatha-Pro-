package namelessju.scathapro.fabric.events;

import java.time.LocalDate;

/**
 * Sammlung wichtiger Scatha-Pro Events für Fabric
 * Portiert von den originalen Forge Events
 */
public class FabricScathaProEvents
{
    /**
     * Pet Drop Event
     */
    public static class ScathaPetDropEvent extends FabricEvent
    {
        // TODO: PetDrop-Klasse portieren, dann ersetzen
        public final Object petDrop; // Temporär als Object
        
        public ScathaPetDropEvent(Object petDrop)
        {
            this.petDrop = petDrop;
        }
    }
    
    /**
     * Achievement Unlocked Event
     */
    public static class AchievementUnlockedEvent extends FabricEvent
    {
        // TODO: Achievement-Klasse portieren
        public final Object achievement;
        public final boolean isRepeat;
        
        public AchievementUnlockedEvent(Object achievement, boolean isRepeat)
        {
            this.achievement = achievement;
            this.isRepeat = isRepeat;
        }
    }
    
    /**
     * Bedrock Wall Detected Event
     */
    public static class BedrockWallDetectedEvent extends FabricEvent
    {
        public final int distance;
        
        public BedrockWallDetectedEvent(int distance)
        {
            this.distance = distance;
        }
    }
    
    /**
     * Crystal Hollows Day Started Event
     */
    public static class CrystalHollowsDayStartedEvent extends FabricEvent
    {
        public final int day;
        
        public CrystalHollowsDayStartedEvent(int day)
        {
            this.day = day;
        }
    }
    
    /**
     * Daily Scatha Farming Streak Changed Event
     */
    public static class DailyScathaFarmingStreakChangedEvent extends FabricEvent
    {
        public final int previousStreak;
        public final int newStreak;
        public final LocalDate date;
        
        public DailyScathaFarmingStreakChangedEvent(int previousStreak, int newStreak, LocalDate date)
        {
            this.previousStreak = previousStreak;
            this.newStreak = newStreak;
            this.date = date;
        }
    }
    
    /**
     * Daily Stats Reset Event
     */
    public static class DailyStatsResetEvent extends FabricEvent
    {
        public final LocalDate date;
        
        public DailyStatsResetEvent(LocalDate date)
        {
            this.date = date;
        }
    }
    
    /**
     * Detected Entity Registered Event
     */
    public static class DetectedEntityRegisteredEvent extends FabricEvent
    {
        // TODO: DetectedEntity-Klasse portieren
        public final Object detectedEntity;
        
        public DetectedEntityRegisteredEvent(Object detectedEntity)
        {
            this.detectedEntity = detectedEntity;
        }
    }
    
    /**
     * Mod Update Event
     */
    public static class ModUpdateEvent extends FabricEvent
    {
        public final String oldVersion;
        public final String newVersion;
        
        public ModUpdateEvent(String oldVersion, String newVersion)
        {
            this.oldVersion = oldVersion;
            this.newVersion = newVersion;
        }
    }
    
    /**
     * Overlay Init Event
     */
    public static class OverlayInitEvent extends FabricEvent {}
    
    /**
     * Real Day Started Event  
     */
    public static class RealDayStartedEvent extends FabricEvent
    {
        public final LocalDate date;
        
        public RealDayStartedEvent(LocalDate date)
        {
            this.date = date;
        }
    }
    
    /**
     * Skyblock Area Detected Event
     */
    public static class SkyblockAreaDetectedEvent extends FabricEvent
    {
        // TODO: SkyblockArea enum portieren
        public final Object area; // Temporär
        
        public SkyblockAreaDetectedEvent(Object area)
        {
            this.area = area;
        }
    }
}