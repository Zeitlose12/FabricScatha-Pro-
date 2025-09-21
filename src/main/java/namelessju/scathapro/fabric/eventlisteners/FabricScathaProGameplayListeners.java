package namelessju.scathapro.fabric.eventlisteners;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.Constants;
import namelessju.scathapro.fabric.entitydetection.FabricDetectedEntity;
import namelessju.scathapro.fabric.entitydetection.FabricDetectedWorm;
import namelessju.scathapro.fabric.events.FabricEvent;
import namelessju.scathapro.fabric.events.FabricScathaProEvents;
import namelessju.scathapro.fabric.events.FabricWorldEvent;
import namelessju.scathapro.fabric.events.FabricWormEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

/**
 * Fabric-Version der ScathaProGameplayListeners
 * Enthält die wichtigste Gameplay-Logik für Worm- und Pet-Detection
 * TODO: Viele Dependencies müssen noch portiert werden
 */
public class FabricScathaProGameplayListeners extends FabricScathaProListener
{
    public FabricScathaProGameplayListeners(FabricScathaPro scathaPro)
    {
        super(scathaPro);
        registerEventListeners();
    }
    
    /**
     * Registriert alle Event-Listener für Gameplay-Events
     */
    private void registerEventListeners()
    {
        // Worm Pre-Spawn Event
        FabricEvent.register(FabricWormEvent.WormPreSpawnEvent.class, this::onWormPreSpawn);
        
        // Worm Spawn Event
        FabricEvent.register(FabricWormEvent.WormSpawnEvent.class, this::onWormSpawn);
        
        // Worm Hit Event
        FabricEvent.register(FabricWormEvent.WormHitEvent.class, this::onWormHit);
        
        // Worm Kill Event
        FabricEvent.register(FabricWormEvent.WormKillEvent.class, this::onWormKill);
        
        // Worm Despawn Event
        FabricEvent.register(FabricWormEvent.WormDespawnEvent.class, this::onWormDespawn);
        
        // Scatha Pet Drop Event
        FabricEvent.register(FabricScathaProEvents.ScathaPetDropEvent.class, this::onScathaPetDrop);
        
        // Bedrock Wall Detected Event
        FabricEvent.register(FabricScathaProEvents.BedrockWallDetectedEvent.class, this::onBedrockWallDetected);
        
        // World Join Event
        FabricEvent.register(FabricWorldEvent.WorldJoinEvent.class, this::onWorldJoin);
        
        scathaPro.log("Gameplay Event-Listener registriert");
    }
    
    /**
     * Worm Pre-Spawn Event Handler
     */
    private void onWormPreSpawn(FabricWormEvent.WormPreSpawnEvent event)
    {
        scathaPro.logDebug("Worm Pre-Spawn Event empfangen");
        
        // Spawn-Cooldown starten
        scathaPro.variables.startWormSpawnCooldown(true);
        
        // TODO: Alert-System implementieren
        // Alert.wormPrespawn.play();
        
        // TODO: Achievement-System implementieren
        // scathaPro.getAchievementLogicManager().handleAnomalousDesireRecoverAchievement();
    }
    
    /**
     * Worm Spawn Event Handler
     */
    private void onWormSpawn(FabricWormEvent.WormSpawnEvent event)
    {
        scathaPro.logDebug("Worm Spawn Event empfangen - Zeit seit letztem Spawn: " + event.timeSincePreviousSpawn + "ms");
        
        long now = System.currentTimeMillis();
        ClientPlayerEntity player = mc.player;
        
        if (player == null) return;
        
        // TODO: Spawn-Detection-Logik implementieren wenn Entity-Detection portiert ist
        boolean spawnedBySelf = checkIfWormSpawnedBySelf(player, event);
        
        if (!spawnedBySelf) {
            scathaPro.logDebug("Worm von anderem Spieler gespawnt - ignoriert");
            return;
        }
        
        // Worm-Typ aus Event-Worm bestimmen
        boolean isScatha = event.worm.isScatha;
        
        if (isScatha)
        {
            handleScathaSpawn(event, now);
        }
        else
        {
            handleRegularWormSpawn(event, now);
        }
        
        // Spawn-Zeit aktualisieren
        scathaPro.variables.lastWormSpawnTime = now;
        scathaPro.variables.startWormSpawnCooldown(false);
        
        // TODO: Overlay-Updates implementieren
        // scathaPro.getOverlay().updateWormStreak();
        
        // TODO: Achievement-Updates implementieren
        // scathaPro.getAchievementLogicManager().updateSpawnAchievements(event);
    }
    
    /**
     * Worm Hit Event Handler
     */
    private void onWormHit(FabricWormEvent.WormHitEvent event)
    {
        scathaPro.logDebug("Worm Hit Event empfangen");
        
        // TODO: NBTUtil und Achievement-System implementieren
        // String skyblockItemID = NBTUtil.getSkyblockItemID(event.weapon);
        // if (skyblockItemID != null && skyblockItemID.equals("DIRT") && event.worm.isScatha) 
        //     Achievement.scatha_hit_dirt.unlock();
    }
    
    /**
     * Worm Kill Event Handler
     */
    private void onWormKill(FabricWormEvent.WormKillEvent event)
    {
        scathaPro.logDebug("Worm Kill Event empfangen");
        
        // TODO: PlayerListParser implementieren
        // PlayerListParser.parseProfileStats();
        
        // Worm-Typ aus Event-Worm bestimmen
        boolean isScatha = event.worm.isScatha;
        
        if (isScatha)
        {
            handleScathaKill(event);
        }
        else
        {
            handleRegularWormKill(event);
        }
        
        // Kill-Zeit aktualisieren
        scathaPro.variables.lastKillTime = System.currentTimeMillis();
        
        // TODO: Data-Persistence implementieren
        // scathaPro.getPersistentData().saveWormKills();
        // scathaPro.getPersistentData().saveDailyStatsData();
    }
    
    /**
     * Worm Despawn Event Handler
     */
    private void onWormDespawn(FabricWormEvent.WormDespawnEvent event)
    {
        scathaPro.logDebug("Worm Despawn Event empfangen");
        
        // TODO: Achievement-System implementieren
        // Achievement.worm_despawn.unlock();
    }
    
    /**
     * Scatha Pet Drop Event Handler
     */
    private void onScathaPetDrop(FabricScathaProEvents.ScathaPetDropEvent event)
    {
        scathaPro.log("🐾 Scatha Pet Drop Event empfangen!");
        
        // TODO: PetDrop-Klasse und Rarity-System implementieren
        String rarityTitle = "UNKNOWN"; // Temporär
        
        // Pet-Drop-Counter aktualisieren
        // TODO: Rarity-basierte Logik implementieren
        scathaPro.variables.rarePetDrops++;
        
        // TODO: Alert-System implementieren
        // if (scathaPro.getConfig().getBoolean(Config.Key.scathaPetDropAlert))
        // {
        //     SoundUtil.playSound("random.chestopen", 1.5f, 0.95f);
        //     Alert.scathaPetDrop.play(rarityTitle);
        // }
        
        // Dry-Streak-Nachricht anzeigen
        handleDryStreakMessage();
        
        // Drop-Zeit aktualisieren
        scathaPro.variables.lastPetDropTime = System.currentTimeMillis();
        
        // TODO: Weitere Pet-Drop-Logik implementieren
        // scathaPro.getAchievementLogicManager().updatePetDropAchievements();
    }
    
    /**
     * Bedrock Wall Detected Event Handler
     */
    private void onBedrockWallDetected(FabricScathaProEvents.BedrockWallDetectedEvent event)
    {
        scathaPro.log("🧡 Bedrock-Wand erkannt! Entfernung: " + event.distance);
        
        // TODO: Alert-System implementieren
        // Alert.bedrockWall.play();
    }
    
    /**
     * World Join Event Handler
     */
    private void onWorldJoin(FabricWorldEvent.WorldJoinEvent event)
    {
        scathaPro.logDebug("World-Join Event empfangen: " + 
                           (event.world != null ? event.world.toString() : "null"));
        
        // Variables für neuen Welt-Join zurücksetzen
        scathaPro.variables.resetForNewLobby();
        scathaPro.variables.lastWorldJoinTime = event.joinTime;
        
        // Entity-Listen zurücksetzen
        FabricDetectedEntity.clearLists();
        
        // TODO: Weitere Reset-Operationen implementieren
        // scathaPro.getChestGuiParsingManager().resetForNewLobby();
        // scathaPro.getAchievementLogicManager().resetForNewLobby();
        // scathaPro.getOverlay().updateOverlayFull();
        
        scathaPro.log("🌍 Neue Welt betreten - Mod-State zurückgesetzt");
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Prüft ob Worm vom lokalen Spieler gespawnt wurde
     * TODO: Echte Detection-Logik implementieren
     */
    private boolean checkIfWormSpawnedBySelf(ClientPlayerEntity player, FabricWormEvent.WormSpawnEvent event)
    {
        // TODO: Implementieren wenn Entity-Detection portiert ist
        // - Player-Position vs Worm-Position
        // - Nearby-Player-Detection
        // - Obstruction-Checks
        return true; // Temporär - alle Worms als "self-spawned" behandeln
    }
    
    /**
     * Behandelt Scatha-Spawn
     */
    private void handleScathaSpawn(FabricWormEvent.WormSpawnEvent event, long now)
    {
        scathaPro.log("🐲 Scatha gespawnt!");
        
        // TODO: Alert-System implementieren
        // Alert.scathaSpawn.play();
        
        // TODO: WormStatsType implementieren
        // WormStatsType.addScathaSpawn();
        
        // TODO: Achievement-System implementieren
        // scathaPro.getAchievementLogicManager().updateScathaSpawnAchievements(now, event.worm);
        
        // TODO: Scappa-Mode implementieren
        // if (scathaPro.isScappaModeActive())
        // {
        //     event.worm.playScappaSound();
        // }
    }
    
    /**
     * Behandelt regulären Worm-Spawn
     */
    private void handleRegularWormSpawn(FabricWormEvent.WormSpawnEvent event, long now)
    {
        scathaPro.logDebug("Regulärer Worm gespawnt");
        
        // TODO: Alert-System implementieren
        // Alert.regularWormSpawn.play();
        
        // TODO: WormStatsType implementieren
        // WormStatsType.addRegularWormSpawn();
    }
    
    /**
     * Behandelt Scatha-Kill
     */
    private void handleScathaKill(FabricWormEvent.WormKillEvent event)
    {
        scathaPro.log("🐲 Scatha getötet!");
        
        scathaPro.variables.addScathaKill();
        scathaPro.variables.lastScathaKillTime = System.currentTimeMillis();
        
        // TODO: Achievement-Logik implementieren
        // TODO: Overlay-Updates implementieren
        // TODO: Scappa-Mode-Unlock-Logik implementieren
    }
    
    /**
     * Behandelt regulären Worm-Kill
     */
    private void handleRegularWormKill(FabricWormEvent.WormKillEvent event)
    {
        scathaPro.logDebug("Regulärer Worm getötet");
        
        scathaPro.variables.addRegularWormKill();
        
        // TODO: Achievement-Logik implementieren
        // TODO: Overlay-Updates implementieren
    }
    
    /**
     * Behandelt Dry-Streak-Nachricht
     */
    private void handleDryStreakMessage()
    {
        // TODO: Config-System implementieren
        // if (!scathaPro.getConfig().getBoolean(Config.Key.dryStreakMessage)) return;
        
        if (scathaPro.variables.scathaKills >= 0)
        {
            boolean droppedPetBefore = scathaPro.variables.scathaKillsAtLastDrop >= 0;
            int dryStreak = scathaPro.variables.scathaKills - 1;
            if (droppedPetBefore) dryStreak -= scathaPro.variables.scathaKillsAtLastDrop;
            
            if (!scathaPro.variables.dropDryStreakInvalidated)
            {
                if (dryStreak > 0)
                {
                    scathaPro.log("Pet Drop nach " + dryStreak + " Scatha Kill" + (dryStreak != 1 ? "s" : "") + " Dry Streak");
                }
                else if (dryStreak == 0)
                {
                    if (droppedPetBefore)
                    {
                        scathaPro.log("🔥 BACK TO BACK Pet Drop!");
                    }
                    else
                    {
                        scathaPro.log("🎯 Pet Drop ON FIRST SCATHA KILL!");
                    }
                }
            }
        }
    }
}