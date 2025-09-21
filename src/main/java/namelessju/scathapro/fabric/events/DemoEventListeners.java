package namelessju.scathapro.fabric.events;

import namelessju.scathapro.fabric.FabricScathaPro;

/**
 * Demo Event-Listeners 
 * Zeigt wie das neue Fabric Event-System verwendet wird
 * Diese Klasse kann als Vorlage für weitere Event-Listener dienen
 */
public class DemoEventListeners
{
    /**
     * Registriert Demo Event-Listener
     */
    public static void registerDemoListeners()
    {
        FabricScathaPro scathaPro = FabricScathaPro.getInstance();
        if (scathaPro == null) return;
        
        // First Ingame Tick Event
        FabricEvent.register(FabricTickEvent.FirstIngameTickEvent.class, event -> {
            scathaPro.log("🎮 Erstes Ingame-Tick Event empfangen!");
        });
        
        // First World Tick Event  
        FabricEvent.register(FabricTickEvent.FirstWorldTickEvent.class, event -> {
            scathaPro.log("🌍 Erstes Welt-Tick Event empfangen! Spieler: " + 
                         (event.player != null ? event.player.getName().getString() : "null"));
        });
        
        // First Crystal Hollows Tick Event
        FabricEvent.register(FabricTickEvent.FirstCrystalHollowsTickEvent.class, event -> {
            scathaPro.log("💎 Erstes Crystal Hollows-Tick Event empfangen!");
        });
        
        // Crystal Hollows Tick Event (jeder Tick)
        FabricEvent.register(FabricTickEvent.CrystalHollowsTickEvent.class, event -> {
            // Nur alle 100 Ticks loggen um Spam zu vermeiden
            if (event.now % 5000 < 50) { // Ungefähr alle 5 Sekunden
                scathaPro.logDebug("💎 Crystal Hollows Tick: " + event.now);
            }
        });
        
        // Worm Spawn Event
        FabricEvent.register(FabricWormEvent.WormSpawnEvent.class, event -> {
            scathaPro.log("🪱 Worm Spawn Event! Zeit seit letztem Spawn: " + event.timeSincePreviousSpawn + "ms");
        });
        
        // Pet Drop Event
        FabricEvent.register(FabricScathaProEvents.ScathaPetDropEvent.class, event -> {
            scathaPro.log("🐾 Pet Drop Event empfangen!");
        });
        
        // Achievement Unlocked Event
        FabricEvent.register(FabricScathaProEvents.AchievementUnlockedEvent.class, event -> {
            scathaPro.log("🏆 Achievement freigeschaltet! Wiederholung: " + event.isRepeat);
        });
        
        // Bedrock Wall Detected Event  
        FabricEvent.register(FabricScathaProEvents.BedrockWallDetectedEvent.class, event -> {
            scathaPro.log("🧱 Bedrock-Wand erkannt! Entfernung: " + event.distance);
        });
        
        scathaPro.log("Demo Event-Listener registriert");
    }
}