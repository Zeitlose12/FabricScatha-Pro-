package namelessju.scathapro.fabric.eventlisteners;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.entitydetection.FabricDetectedEntity;
import namelessju.scathapro.fabric.events.FabricEvent;
import namelessju.scathapro.fabric.events.FabricTickEvent;
import net.minecraft.text.Text;

/**
 * Fabric-Version der ScathaProTickListeners
 * Behandelt alle Tick-basierten Events und regelmäßige Updates
 */
public class FabricScathaProTickListeners extends FabricScathaProListener
{
    private int heatCheckTickTimer = 0;
    
    public FabricScathaProTickListeners(FabricScathaPro scathaPro)
    {
        super(scathaPro);
        registerEventListeners();
    }
    
    /**
     * Registriert alle Tick-Event-Listener
     */
    private void registerEventListeners()
    {
        // First Ingame Tick Event
        FabricEvent.register(FabricTickEvent.FirstIngameTickEvent.class, this::onFirstIngameTick);
        
        // First World Tick Event
        FabricEvent.register(FabricTickEvent.FirstWorldTickEvent.class, this::onFirstWorldTick);
        
        // First Crystal Hollows Tick Event
        FabricEvent.register(FabricTickEvent.FirstCrystalHollowsTickEvent.class, this::onFirstCrystalHollowsTick);
        
        // Crystal Hollows Tick Event
        FabricEvent.register(FabricTickEvent.CrystalHollowsTickEvent.class, this::onCrystalHollowsTick);
        
        scathaPro.log("Tick Event-Listener registriert");
    }
    
    /**
     * First Ingame Tick Event Handler
     */
    private void onFirstIngameTick(FabricTickEvent.FirstIngameTickEvent event)
    {
        scathaPro.logDebug("First Ingame Tick Event empfangen");
        
        // TODO: Update-Checker implementieren
        // if (scathaPro.getConfig().getBoolean(Config.Key.automaticUpdateChecks))
        // {
        //     UpdateChecker.checkForUpdate(false);
        // }
        
        // TODO: Achievement-Logic-Manager implementieren
        // scathaPro.getAchievementLogicManager().updatePetDropAchievements();
        // scathaPro.getAchievementLogicManager().updateProgressAchievements();
        // scathaPro.getAchievementLogicManager().updateDailyScathaStreakAchievements();
    }
    
    /**
     * First World Tick Event Handler
     */
    private void onFirstWorldTick(FabricTickEvent.FirstWorldTickEvent event)
    {
        scathaPro.logDebug("First World Tick Event empfangen - Spieler: " + 
                           (event.player != null ? event.player.getName().getString() : "null"));
        
        // Cached Chat-Messages senden
        for (Text message : scathaPro.variables.cachedChatMessages)
        {
            // TODO: TextUtil implementieren
            // TextUtil.sendChatMessage(message);
            scathaPro.logDebug("Cached Message: " + message.getString());
        }
        scathaPro.variables.cachedChatMessages.clear();
    }
    
    /**
     * First Crystal Hollows Tick Event Handler
     */
    private void onFirstCrystalHollowsTick(FabricTickEvent.FirstCrystalHollowsTickEvent event)
    {
        scathaPro.logDebug("First Crystal Hollows Tick Event empfangen");
        
        heatCheckTickTimer = 0;
        
        // Worm-Stats-Parsing-Hinweis
        if (scathaPro.variables.regularWormKills == 0 && scathaPro.variables.scathaKills == 0)
        {
            // TODO: Config und TextUtil implementieren
            // if (scathaPro.getConfig().getBoolean(Config.Key.automaticWormStatsParsing))
            // {
            //     TextUtil.sendModChatMessage("Open the worm bestiary once to load previous worm kills into the overlay!");
            // }
            scathaPro.log("💡 Tipp: Öffne das Worm-Bestiarium um vorherige Kills zu laden!");
        }
        
        // Cached Crystal Hollows-Messages senden
        for (Text message : scathaPro.variables.cachedCrystalHollowsMessages)
        {
            // TODO: TextUtil implementieren
            // TextUtil.sendChatMessage(message);
            scathaPro.logDebug("Cached CH Message: " + message.getString());
        }
        scathaPro.variables.cachedCrystalHollowsMessages.clear();
    }
    
    /**
     * Crystal Hollows Tick Event Handler
     */
    private void onCrystalHollowsTick(FabricTickEvent.CrystalHollowsTickEvent event)
    {
        if (mc.player == null) return;
        
        // Entity-Detection System
        FabricDetectedEntity.update(mc.player);
        
        // Sneak-Start Tracking
        handleSneakTracking(event);
        
        // Achievement-Updates
        handleAchievementUpdates(event);
        
        // Heat-Check
        handleHeatCheck(event);
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Behandelt Sneak-Tracking für Achievements
     */
    private void handleSneakTracking(FabricTickEvent.CrystalHollowsTickEvent event)
    {
        boolean sneaking = mc.player.isSneaking();
        if (!scathaPro.variables.sneakingBefore && sneaking)
        {
            scathaPro.variables.lastSneakStartTime = event.now;
            scathaPro.logDebug("Spieler hat angefangen zu schleichen");
        }
        scathaPro.variables.sneakingBefore = sneaking;
    }
    
    /**
     * Behandelt Achievement-Updates basierend auf Zeit
     */
    private void handleAchievementUpdates(FabricTickEvent.CrystalHollowsTickEvent event)
    {
        float hours = (event.now - scathaPro.variables.lastWorldJoinTime) / (1000f * 60 * 60);
        
        // TODO: Achievement-System implementieren
        // Achievement.crystal_hollows_time_1.setProgress(hours);
        // Achievement.crystal_hollows_time_2.setProgress(hours);
        // Achievement.crystal_hollows_time_3.setProgress(hours);
        
        if (hours > 0.5f) { // Alle 30 Minuten loggen
            scathaPro.logDebug("Crystal Hollows Zeit: " + String.format("%.1f", hours) + " Stunden");
        }
    }
    
    /**
     * Behandelt Heat-Check für High-Heat-Alert
     */
    private void handleHeatCheck(FabricTickEvent.CrystalHollowsTickEvent event)
    {
        heatCheckTickTimer++;
        if (heatCheckTickTimer > 3 * 20) // Alle 3 Sekunden (60 Ticks)
        {
            heatCheckTickTimer = 0;
            
            // TODO: Config und ScoreboardParser implementieren
            // if (scathaPro.getConfig().getBoolean(Config.Key.highHeatAlert))
            // {
            //     int newHeat = ScoreboardParser.parseHeat();
            //     
            //     if (newHeat > 0)
            //     {
            //         int triggerValue = scathaPro.getConfig().getInt(Config.Key.highHeatAlertTriggerValue);
            //         if (newHeat >= triggerValue && scathaPro.variables.lastHeat >= 0 && scathaPro.variables.lastHeat < triggerValue)
            //         {
            //             Alert.highHeat.play();
            //         }
            //     }
            //     
            //     scathaPro.variables.lastHeat = newHeat;
            // }
            // else 
            //     scathaPro.variables.lastHeat = -1;
            
            // Realer Heat-Check erfolgt ereignis-basiert über Chat-Parser (HeatParser)
            // Optional: Scoreboard-Parsing für Heat kann hier später ergänzt werden.
        }
    }
}