package namelessju.scathapro.fabric.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.World;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.entitydetection.FabricDetectedEntity;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;

/**
 * Fabric Event-Manager 
 * Verwaltet die Registrierung und das Feuern von Fabric Events
 * Integriert das Custom Event-System mit Fabric's Event-System
 */
public class FabricEventManager
{
    private static FabricEventManager instance;
    private final FabricScathaPro scathaPro;
    
    // Event-Tracking-Variablen
    private boolean firstIngameTickPending = true;
    private boolean firstWorldTickPending = true;
    private boolean firstCrystalHollowsTickPending = true;
    private boolean gameStarted = false;
    private boolean registered = false;
    private World lastWorld = null;

    public boolean isRegistered() { return registered; }
    public boolean isGameStarted() { return gameStarted; }
    
    public FabricEventManager(FabricScathaPro scathaPro)
    {
        this.scathaPro = scathaPro;
        instance = this;
    }

    // ===== Chat-Kontext-Parser =====
    private namelessju.scathapro.fabric.chat.parsers.PlayerListParser playerListParser;
    private namelessju.scathapro.fabric.chat.parsers.ScoreboardParser scoreboardParser;
    private void ensureContextParsers() {
        if (playerListParser == null) playerListParser = new namelessju.scathapro.fabric.chat.parsers.PlayerListParser();
        if (scoreboardParser == null) scoreboardParser = new namelessju.scathapro.fabric.chat.parsers.ScoreboardParser();
    }
    
    public static FabricEventManager getInstance()
    {
        return instance;
    }
    
    /**
     * Registriert alle Fabric Event-Listener
     */
    public void registerEventListeners()
    {
        scathaPro.log("Event-Listener werden registriert...");
        
        // Client-Lifecycle Events
        ClientLifecycleEvents.CLIENT_STARTED.register(this::onClientStarted);
        ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStopping);
        
        // Client-Tick Events
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        
        registered = true;
        scathaPro.log("Event-Listener erfolgreich registriert");
    }
    
    /**
     * Wird aufgerufen wenn der Client gestartet wird
     */
    private void onClientStarted(MinecraftClient client)
    {
        scathaPro.logDebug("Client gestartet - bereit für Events");
        gameStarted = true;
    }
    
    /**
     * Wird aufgerufen wenn der Client beendet wird
     */
    private void onClientStopping(MinecraftClient client)
    {
        scathaPro.logDebug("Client wird beendet");
        gameStarted = false;
        resetEventStates();
    }
    
    /**
     * Wird bei jedem Client-Tick aufgerufen
     */
    private void onClientTick(MinecraftClient client)
    {
        if (!gameStarted || client.player == null) return;
        
        ClientPlayerEntity player = client.player;
        World currentWorld = player.getWorld();
        
        // World-Change-Detection
        if (currentWorld != lastWorld && currentWorld != null)
        {
            // World-Join Event feuern
            long joinTime = System.currentTimeMillis();
            FabricEvent.post(new FabricWorldEvent.WorldJoinEvent(currentWorld, joinTime));
            scathaPro.variables.lastWorldJoinTime = joinTime;
            scathaPro.logDebug("World-Join Event gefeuert: " + currentWorld.toString());
            
            lastWorld = currentWorld;
            onWorldJoin();
        }
        
        // First Ingame Tick
        if (firstIngameTickPending)
        {
            firstIngameTickPending = false;
            FabricEvent.post(new FabricTickEvent.FirstIngameTickEvent());
            scathaPro.logDebug("First Ingame Tick Event gefeuert");
        }
        
        // First World Tick  
        if (firstWorldTickPending && player.getWorld() != null)
        {
            firstWorldTickPending = false;
            scathaPro.variables.firstWorldTickPending = false;
            FabricEvent.post(new FabricTickEvent.FirstWorldTickEvent(player));
            scathaPro.logDebug("First World Tick Event gefeuert");
        }
        
        // Crystal Hollows Tick-Events (TODO: Area-Detection implementieren)
        if (isInCrystalHollows())
        {
            // First Crystal Hollows Tick
            if (firstCrystalHollowsTickPending)
            {
                firstCrystalHollowsTickPending = false;
                scathaPro.variables.firstCrystalHollowsTickPending = false;
                FabricEvent.post(new FabricTickEvent.FirstCrystalHollowsTickEvent());
                scathaPro.logDebug("First Crystal Hollows Tick Event gefeuert");
            }
            
            // Regular Crystal Hollows Tick
            long now = System.currentTimeMillis();
            FabricEvent.post(new FabricTickEvent.CrystalHollowsTickEvent(now));
        }
        
        // Entity-Detection-Update
        FabricDetectedEntity.update(player);

        // Kontext-Parser (PlayerList/Scoreboard)
        try { ensureContextParsers(); playerListParser.update(scathaPro); scoreboardParser.update(scathaPro); } catch (Exception ignored) {}

        // Chest GUI Parsing
        try {
            var mc = net.minecraft.client.MinecraftClient.getInstance();
            if (mc != null && mc.currentScreen != null && scathaPro.getChestGuiParsingManager() != null) {
                scathaPro.getChestGuiParsingManager().update(mc.currentScreen);
            }
        } catch (Exception ignored) {}
        
        // Weitere Tick-basierte Events hier hinzufügen...
    }
    
    /**
     * Setzt Event-Zustand für neuen World-Join zurück
     */
    public void onWorldJoin()
    {
        firstWorldTickPending = true;
        firstCrystalHollowsTickPending = true;
        scathaPro.variables.resetForNewLobby();
        
        // Entity-Detection zurücksetzen
        FabricDetectedEntity.clearLists();
        
        scathaPro.logDebug("Event-Status und Entity-Listen für neue Welt zurückgesetzt");
    }
    
    /**
     * Setzt alle Event-Zustände zurück
     */
    private void resetEventStates()
    {
        firstIngameTickPending = true;
        firstWorldTickPending = true;
        firstCrystalHollowsTickPending = true;
        lastWorld = null;
    }
    
    /**
     * Prüft ob Spieler in Crystal Hollows ist
     * TODO: Implementieren wenn SkyblockArea-Detection portiert ist
     */
    private boolean isInCrystalHollows()
    {
        // TODO: Echte Area-Detection implementieren
        return scathaPro.isInCrystalHollows();
    }
    
    /**
     * Triggert ein Chat-Event
     */
    public void triggerChatEvent(ChatEvent event)
    {
        if (event == null) return;
        
        try {
            scathaPro.logDebug("Chat Event: " + event.getType() + " - " + event.getCleanMessage());
            
            // Verarbeite spezifische Event-Typen
            switch (event.getType()) {
                case PET_DROP_RARE:
                case PET_DROP_EPIC:
                case PET_DROP_LEGENDARY:
                    handlePetDropEvent(event);
                    break;
                    
                case WORM_SPAWN:
                    handleWormSpawnEvent(event);
                    break;
                    
                case SCATHA_SPAWN:
                    handleScathaSpawnEvent(event);
                    break;
                    
                case MAGIC_FIND_UPDATE:
                    handleMagicFindEvent(event);
                    break;
                    
                case HEAT_UPDATE:
                    handleHeatEvent(event);
                    break;
                    
                default:
                    // Unbekanntes Event - nur loggen
                    break;
            }
            
        } catch (Exception e) {
            scathaPro.logError("Error handling chat event: " + e.getMessage());
        }
    }
    
    private void handlePetDropEvent(ChatEvent event) {
        scathaPro.log("PET DROP DETECTED: " + event.getPetType() + " (" + event.getPetRarity() + ")");
        
        // Aktualisiere Pet Drop Statistiken
        String rarity = event.getPetRarity();
        if (rarity != null) {
            switch (rarity.toLowerCase()) {
                case "rare":
                case "blue":
                    scathaPro.variables.rarePetDrops++;
                    break;
                case "epic":
                case "purple":
                    scathaPro.variables.epicPetDrops++;
                    break;
                case "legendary":
                case "orange":
                    scathaPro.variables.legendaryPetDrops++;
                    break;
            }
        }
        
        // Triggere Pet Drop Alert
        if (scathaPro.getAlertManager() != null) {
            scathaPro.getAlertManager().triggerAlert(event);
        }
        // Achievement-Logic informieren
        namelessju.scathapro.fabric.events.FabricEvent.post(
            new namelessju.scathapro.fabric.events.FabricScathaProEvents.ScathaPetDropEvent(event)
        );
    }
    
    private void handleWormSpawnEvent(ChatEvent event) {
        scathaPro.log("WORM SPAWN DETECTED");
        scathaPro.variables.lastWormSpawnTime = event.getTimestamp();
        
        // Streak-Logik
        scathaPro.variables.consecutiveRegularWormSpawns = Math.max(0, scathaPro.variables.consecutiveRegularWormSpawns) + 1;
        scathaPro.variables.consecutiveScathaSpawns = 0;
        // Unlock Streak-Achievements
        try {
            var am = scathaPro.getAchievementManager();
            if (am != null) {
                if (scathaPro.variables.consecutiveRegularWormSpawns >= 7) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.regular_worm_streak_1);
                if (scathaPro.variables.consecutiveRegularWormSpawns >= 13) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.regular_worm_streak_2);
                if (scathaPro.variables.consecutiveRegularWormSpawns >= 20) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.regular_worm_streak_3);
            }
        } catch (Exception ignored) {}

        // Triggere Worm Spawn Alert
        if (scathaPro.getAlertManager() != null) {
            scathaPro.getAlertManager().triggerAlert(event);
        }
    }
    
    private void handleScathaSpawnEvent(ChatEvent event) {
        scathaPro.log("SCATHA SPAWN DETECTED");
        // TODO: Update Scatha spawn statistics
        
        // Streak-Logik
        scathaPro.variables.consecutiveScathaSpawns = Math.max(0, scathaPro.variables.consecutiveScathaSpawns) + 1;
        scathaPro.variables.consecutiveRegularWormSpawns = 0;
        try {
            var am = scathaPro.getAchievementManager();
            if (am != null) {
                if (scathaPro.variables.consecutiveScathaSpawns >= 2) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_streak_1);
                if (scathaPro.variables.consecutiveScathaSpawns >= 3) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_streak_2);
                if (scathaPro.variables.consecutiveScathaSpawns >= 4) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_streak_3);
                if (scathaPro.variables.consecutiveScathaSpawns >= 5) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_streak_4);
            }
        } catch (Exception ignored) {}

        // Standort-/Timing-Achievements
        try {
            var am = scathaPro.getAchievementManager();
            var mc = net.minecraft.client.MinecraftClient.getInstance();
            if (am != null && mc != null && mc.player != null) {
                double y = mc.player.getY();
                if (y <= namelessju.scathapro.fabric.Constants.crystalHollowsBottomY) {
                    am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_spawn_chbottom);
                }
                if (y >= namelessju.scathapro.fabric.Constants.crystalHollowsTopY) {
                    am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_spawn_chtop);
                }
                // Any%: innerhalb der ersten Minute nach Lobby-Join
                long t = event.getTimestamp();
                if (scathaPro.variables.lastWorldJoinTime > 0 && (t - scathaPro.variables.lastWorldJoinTime) <= 60_000) {
                    am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_spawn_time);
                }
                // <3s nach Cooldown-Ende
                long cdEnd = scathaPro.variables.wormSpawnCooldownStartTime + namelessju.scathapro.fabric.Constants.wormSpawnCooldown;
                if (scathaPro.variables.wormSpawnCooldownStartTime > 0 && (t - cdEnd) >= 0 && (t - cdEnd) <= 3_000) {
                    am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_spawn_time_cooldown_end);
                }
                // High heat
                if (scathaPro.variables.lastHeat >= 99) {
                    am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_spawn_heat_burning);
                }
            }
        } catch (Exception ignored) {}

        // Triggere Scatha Spawn Alert
        if (scathaPro.getAlertManager() != null) {
            scathaPro.getAlertManager().triggerAlert(event);
        }
    }
    
    private void handleMagicFindEvent(ChatEvent event) {
        Double magicFind = event.getMagicFind();
        if (magicFind != null) {
            scathaPro.variables.magicFind = magicFind.floatValue();
            scathaPro.logDebug("Magic Find updated: " + magicFind);
        }
    }
    
    private void handleHeatEvent(ChatEvent event) {
        Integer heat = event.getHeat();
        if (heat != null) {
            scathaPro.variables.lastHeat = heat;
            scathaPro.logDebug("Heat updated: " + heat);
        }
    }
}
