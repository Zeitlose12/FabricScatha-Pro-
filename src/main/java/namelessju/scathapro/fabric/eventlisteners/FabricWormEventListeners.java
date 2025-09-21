package namelessju.scathapro.fabric.eventlisteners;

import namelessju.scathapro.fabric.Constants;
import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.entitydetection.FabricDetectedWorm;
import namelessju.scathapro.fabric.events.FabricEvent;
import namelessju.scathapro.fabric.events.FabricWormEvent;

/**
 * Fabric Event-Listener für Worm-Events
 * Behandelt alle Worm-bezogenen Events (Spawn, Kill, Hit, Despawn)
 */
public class FabricWormEventListeners extends FabricScathaProListener
{
    public FabricWormEventListeners(FabricScathaPro scathaPro)
    {
        super(scathaPro);
        registerEventListeners();
    }
    
    /**
     * Registriert alle Worm-Event-Listener
     */
    private void registerEventListeners()
    {
        // Worm-Spawn Event
        FabricEvent.register(FabricWormEvent.WormSpawnEvent.class, this::onWormSpawn);
        
        // Worm-Hit Event
        FabricEvent.register(FabricWormEvent.WormHitEvent.class, this::onWormHit);
        
        // Worm-Kill Event
        FabricEvent.register(FabricWormEvent.WormKillEvent.class, this::onWormKill);
        
        // Worm-Despawn Event
        FabricEvent.register(FabricWormEvent.WormDespawnEvent.class, this::onWormDespawn);
        
        // Worm-PreSpawn Event (falls vorhanden)
        FabricEvent.register(FabricWormEvent.WormPreSpawnEvent.class, this::onWormPreSpawn);
        
        scathaPro.log("Worm Event-Listener registriert");
    }
    
    // ===== EVENT HANDLERS =====
    
    /**
     * Behandelt Worm-Spawn-Events
     */
    private void onWormSpawn(FabricWormEvent.WormSpawnEvent event)
    {
        FabricDetectedWorm worm = event.worm;
        String wormType = worm.isScatha ? "Scatha" : "Worm";
        
        scathaPro.log("🐛 " + wormType + " spawned! (ID: " + worm.getEntity().getId() + ")");
        
        // Spawn-Zeit aktualisieren
        scathaPro.variables.lastWormSpawnTime = System.currentTimeMillis();
        
        // Spawn-Cooldown starten
        scathaPro.variables.startWormSpawnCooldown(false);
        
        // Spawn-Zeit-Analyse
        if (event.timeSincePreviousSpawn > 0)
        {
            double secondsSince = event.timeSincePreviousSpawn / 1000.0;
            scathaPro.logDebug(String.format("Zeit seit letztem Spawn: %.1f Sekunden", secondsSince));
            
            // Cooldown-Warnung
            if (event.timeSincePreviousSpawn < Constants.wormSpawnCooldown)
            {
                scathaPro.logDebug("⚠️ Worm spawned vor Ende der Cooldown-Zeit!");
            }
        }
        
        // TODO: Alert-System implementieren
        // if (worm.isScatha)
        // {
        //     Alert.scathaSpawn.play();
        // }
        
        // TODO: Overlay aktualisieren
        // scathaPro.getOverlay().updateWormStats();
        
        // TODO: Achievement-Checks
        // scathaPro.getAchievementLogicManager().checkWormSpawnAchievements(worm);
        
        scathaPro.logDebug(wormType + " spawn verarbeitet");
    }
    
    /**
     * Behandelt Worm-Hit-Events
     */
    private void onWormHit(FabricWormEvent.WormHitEvent event)
    {
        FabricDetectedWorm worm = event.worm;
        String wormType = worm.isScatha ? "Scatha" : "Worm";
        
        scathaPro.logDebug("🎯 " + wormType + " hit! (Waffen: " + worm.getHitWeaponsCount() + ")");
        
        // TODO: Combat-Stats aktualisieren
        // scathaPro.variables.updateCombatStats();
        
        // TODO: Lootshare-Detection
        if (worm.lootsharePossible)
        {
            scathaPro.logDebug("Lootshare möglich für diesen " + wormType);
        }
        
        // TODO: Sound-Effects
        // if (worm.isScatha)
        // {
        //     worm.playScappaSound();
        // }
    }
    
    /**
     * Behandelt Worm-Kill-Events
     */
    private void onWormKill(FabricWormEvent.WormKillEvent event)
    {
        FabricDetectedWorm worm = event.worm;
        String wormType = worm.isScatha ? "Scatha" : "Worm";
        
        scathaPro.log("💀 " + wormType + " killed!");
        
        // Kill-Zeit aktualisieren
        long now = System.currentTimeMillis();
        scathaPro.variables.lastKillTime = now;
        
        // Kill-Zähler aktualisieren
        if (worm.isScatha)
        {
            scathaPro.variables.addScathaKill();
            scathaPro.variables.sessionScathaKills++;
            scathaPro.variables.lastScathaKillTime = now;
            
            scathaPro.log("🏆 Scatha Kill #" + scathaPro.variables.scathaKills + 
                         " (Session: " + scathaPro.variables.sessionScathaKills + ")");
        }
        else
        {
            scathaPro.variables.addRegularWormKill();
            scathaPro.logDebug("Regular Worm Kill #" + scathaPro.variables.regularWormKills);
        }
        
        // Kampf-Analyse
        analyzeKill(worm);
        
        // Achievement-Checks (zusätzlich zu LogicManager):
        try {
            var am = scathaPro.getAchievementManager();
            if (am != null) {
                // Schnellster Kill nach Spawn (<1s)
                long life = worm.getCurrentLifetime();
                if (!worm.isScatha && life >= 0 && life < 1000) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.worm_kill_time_1);
                // Kill kurz vor Despawn (<3s vor Ende)
                long max = worm.getMaxLifetime();
                if (!worm.isScatha && max > 0 && (max - life) > 0 && (max - life) <= 3000) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.worm_kill_time_2);
                
                // Waffensammlung (verschiedene "Waffen" genutzt)
                int used = worm.getHitWeaponsCount();
                if (!worm.isScatha && used >= 5) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.kill_weapons_regular_worm);
                if (worm.isScatha && used >= 10) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.kill_weapons_scatha);
                
                // Spezifische Waffen/Methoden
                if (worm.isScatha) {
                    // Dirt ins Auge
                    if (containsWeapon(worm, "DIRT")) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_hit_dirt);
                    // Juju Shortbow
                    if (containsWeapon(worm, "JUJU")) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_kill_juju);
                    // Terminator
                    if (containsWeapon(worm, "TERMINATOR")) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_kill_terminator);
                    // Gemstone basierter Kill
                    if (containsWeapon(worm, "GEMSTONE")) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_kill_gemstone);
                    
                    // Highground (Spieler deutlich höher als Entity)
                    try {
                        var mc = net.minecraft.client.MinecraftClient.getInstance();
                        if (mc != null && mc.player != null) {
                            double dy = mc.player.getY() - worm.getEntity().getY();
                            if (dy >= 5.0) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_kill_highground);
                        }
                    } catch (Exception ignored) {}
                    
                    // April Fools
                    if (namelessju.scathapro.fabric.util.TimeUtil.isAprilFools()) {
                        am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.april_fools);
                    }
                }
                
                // Perfect Gemstone Gauntlet
                if (worm.wasHitWithPerfectGemstoneGauntlet()) {
                    am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.kill_perfect_gemstone_gauntlet);
                }
            }
        } catch (Exception ignored) {}
        
        // Overlay-Updates optional
        // scathaPro.getOverlay().updateWormStats();
        // scathaPro.getOverlay().updateKillStats();
        
        scathaPro.logDebug(wormType + " kill verarbeitet");
    }

    private boolean containsWeapon(FabricDetectedWorm worm, String needle) {
        try {
            String[] arr = worm.getHitWeapons();
            if (arr == null) return false;
            String n = needle.toUpperCase();
            for (String s : arr) { if (s != null && s.toUpperCase().contains(n)) return true; }
        } catch (Exception ignored) {}
        return false;
    }
    
    /**
     * Behandelt Worm-Despawn-Events
     */
    private void onWormDespawn(FabricWormEvent.WormDespawnEvent event)
    {
        FabricDetectedWorm worm = event.worm;
        String wormType = worm.isScatha ? "Scatha" : "Worm";
        
        scathaPro.logDebug("👻 " + wormType + " despawned (natürlich)");
        
        try {
            if (!worm.isScatha) {
                var am = scathaPro.getAchievementManager();
                if (am != null) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.worm_despawn);
            }
        } catch (Exception ignored) {}
        
        // TODO: Miss-Counter für Achievements
        // if (worm.isScatha)
        // {
        //     scathaPro.getAchievementLogicManager().checkScathaMissAchievements();
        // }
    }
    
    /**
     * Behandelt Worm-PreSpawn-Events
     */
    private void onWormPreSpawn(FabricWormEvent.WormPreSpawnEvent event)
    {
        FabricDetectedWorm worm = event.worm;
        String wormType = worm.isScatha ? "Scatha" : "Worm";
        
        scathaPro.logDebug("🔮 " + wormType + " pre-spawn detected");
        
        // TODO: Pre-Spawn-Alerts
        // if (worm.isScatha)
        // {
        //     Alert.scathaPreSpawn.play();
        // }
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Analysiert einen Kill für Statistiken und Achievements
     */
    private void analyzeKill(FabricDetectedWorm worm)
    {
        // Kampfzeit berechnen
        long spawnToKillTime = worm.getCurrentLifetime();
        scathaPro.logDebug("Kampfzeit: " + (spawnToKillTime / 1000.0) + " Sekunden");
        
        // Waffen-Analyse
        String[] hitWeapons = worm.getHitWeapons();
        if (hitWeapons.length > 0)
        {
            scathaPro.logDebug("Verwendete Waffen: " + String.join(", ", hitWeapons));
            
            // Perfect Gemstone Gauntlet Check
            if (worm.wasHitWithPerfectGemstoneGauntlet())
            {
                scathaPro.logDebug("🟢 Perfect Gemstone Gauntlet verwendet!");
                // TODO: Achievement-Check für Perfect Gauntlet
            }
        }
        
        // Fire-Aspect-Analyse
        if (worm.isFireAspectActive())
        {
            scathaPro.logDebug("🔥 Fire Aspect war aktiv beim Kill");
        }
        
        // Timing-Analyse
        long lastAttackTime = worm.getLastAttackTime();
        if (lastAttackTime > 0)
        {
            long timeSinceLastAttack = System.currentTimeMillis() - lastAttackTime;
            scathaPro.logDebug("Zeit seit letztem Angriff: " + timeSinceLastAttack + "ms");
        }
    }
}