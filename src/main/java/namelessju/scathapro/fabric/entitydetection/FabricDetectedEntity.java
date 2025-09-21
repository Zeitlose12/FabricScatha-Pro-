package namelessju.scathapro.fabric.entitydetection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import namelessju.scathapro.fabric.Constants;
import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.events.FabricEvent;
import namelessju.scathapro.fabric.events.FabricScathaProEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

/**
 * Fabric-Version der DetectedEntity-Basis-Klasse
 * Verwaltet erkannte Entities und deren Lifecycle
 */
public abstract class FabricDetectedEntity
{
    private static final FabricEntityDetector[] ENTITY_DETECTORS = new FabricEntityDetector[] {
        new FabricWormDetector(),
        new FabricGoblinDetector(),
        new FabricJerryDetector()
    };
    
    /** Entities die erkannt wurden und gespeichert bleiben auch wenn unloaded */
    private static final HashMap<Integer, FabricDetectedEntity> registeredEntities = new HashMap<>();
    /** Entities die gerade in der Welt geladen sind */
    private static final List<FabricDetectedEntity> activeEntities = new ArrayList<>();
    
    /**
     * Löscht alle Entity-Listen (z.B. beim Welt-Wechsel)
     */
    public static void clearLists()
    {
        registeredEntities.clear();
        activeEntities.clear();
        FabricScathaPro.getInstance().logDebug("Entity-Listen geleert");
    }
    
    /**
     * Findet eine DetectedEntity anhand der Entity-ID
     */
    public static FabricDetectedEntity getById(int id)
    {
        for (FabricDetectedEntity entity : activeEntities)
        {
            if (entity.getEntity().getId() == id) return entity;
        }
        return null;
    }
    
    /**
     * Hauptupdate-Methode - wird regelmäßig aufgerufen
     */
    public static void update(ClientPlayerEntity player)
    {
        if (player == null || player.getWorld() == null) return;
        
        Box playerPositionBox = player.getBoundingBox();
        Box entityDetectionBox = playerPositionBox.expand(30f, 5f, 30f);
        Box killBox = playerPositionBox.expand(10f, 255f, 10f);
        
        // Entferne Entities die nicht mehr in der Welt sind
        removeInactiveEntities(player, killBox);
        
        // Entferne registrierte Entities wenn Lifetime abgelaufen
        removeExpiredEntities(player);
        
        // Erkenne neue Entities
        detectNewEntities(player, entityDetectionBox);
    }
    
    /**
     * Entfernt inaktive Entities aus den Listen
     */
    private static void removeInactiveEntities(ClientPlayerEntity player, Box killBox)
    {
        for (int i = activeEntities.size() - 1; i >= 0; i--)
        {
            FabricDetectedEntity detectedEntity = activeEntities.get(i);
            
            if (detectedEntity.getEntity() == null || detectedEntity.getEntity().isRemoved())
            {
                LeaveWorldReason leaveWorldReason = null;
                
                if (registeredEntities.containsKey(detectedEntity.entity.getId()))
                {
                    // Prüfe ob Lifetime beendet
                    if (detectedEntity.getMaxLifetime() >= 0 && 
                        detectedEntity.getCurrentLifetime() >= detectedEntity.getMaxLifetime() - Math.min(Constants.pingTreshold, detectedEntity.getMaxLifetime() * 0.2))
                    {
                        registeredEntities.remove(detectedEntity.entity.getId());
                        leaveWorldReason = LeaveWorldReason.LIFETIME_ENDED;
                    }
                    // Prüfe ob in Kill-Range
                    else if (killBox.contains(detectedEntity.entity.getPos()))
                    {
                        registeredEntities.remove(detectedEntity.entity.getId());
                        leaveWorldReason = LeaveWorldReason.KILLED;
                    }
                    else
                    {
                        leaveWorldReason = LeaveWorldReason.LEFT_SIMULATION_DISTANCE;
                    }
                }
                
                activeEntities.remove(i);
                detectedEntity.onLeaveWorld(leaveWorldReason);
                
                FabricScathaPro.getInstance().logDebug("Entity \"" + getEntityString(detectedEntity) + 
                    "\" entfernt (" + activeEntities.size() + " aktiv)");
            }
        }
    }
    
    /**
     * Entfernt abgelaufene registrierte Entities
     */
    private static void removeExpiredEntities(ClientPlayerEntity player)
    {
        List<Integer> expiredIds = new ArrayList<>();
        
        for (Integer entityId : registeredEntities.keySet())
        {
            FabricDetectedEntity detectedEntity = registeredEntities.get(entityId);
            
            if (!isInWorld(detectedEntity.entity, player.getWorld()) && 
                detectedEntity.getMaxLifetime() >= 0 && 
                detectedEntity.getCurrentLifetime() >= detectedEntity.getMaxLifetime())
            {
                expiredIds.add(entityId);
            }
        }
        
        for (Integer entityId : expiredIds)
        {
            registeredEntities.remove(entityId);
            FabricScathaPro.getInstance().logDebug("Abgelaufene Entity entfernt (" + registeredEntities.size() + " registriert)");
        }
    }
    
    /**
     * Erkenne neue Entities in der Nähe
     */
    private static void detectNewEntities(ClientPlayerEntity player, Box entityDetectionBox)
    {
        // Entity-Detection mit ArmorStand-Entities
        try {
            java.util.List<ArmorStandEntity> nearbyEntities = player.getWorld().getEntitiesByClass(ArmorStandEntity.class, entityDetectionBox, entity -> entity != null);
            
            for (ArmorStandEntity armorStand : nearbyEntities)
            {
                if (FabricDetectedEntity.getById(armorStand.getId()) != null) continue;
                
                String entityName = armorStand.hasCustomName() ? 
                    armorStand.getCustomName().getString() : "";
                    
                for (FabricEntityDetector detector : ENTITY_DETECTORS)
                {
                    FabricDetectedEntity detectedEntity = detector.detectEntity(armorStand, entityName);
                    if (detectedEntity != null)
                    {
                        FabricDetectedEntity registeredEntity = registeredEntities.get(detectedEntity.entity.getId());
                        if (registeredEntity != null) 
                            update(registeredEntity, detectedEntity);
                        else 
                            register(detectedEntity);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            FabricScathaPro.getInstance().logError("Fehler bei Entity-Detection: " + e.getMessage());
        }
    }
    
    /**
     * Registriert eine neue erkannte Entity
     */
    private static void register(FabricDetectedEntity detectedEntity)
    {
        if (detectedEntity == null || detectedEntity.entity == null || activeEntities.contains(detectedEntity)) 
            return;
        
        activeEntities.add(detectedEntity);
        
        FabricScathaPro.getInstance().logDebug("Entity \"" + getEntityString(detectedEntity) + 
            "\" erkannt (" + activeEntities.size() + " total)");
        
        int entityId = detectedEntity.entity.getId();
        if (!registeredEntities.containsKey(entityId))
        {
            registeredEntities.put(entityId, detectedEntity);
            detectedEntity.onRegistration();
            
            FabricScathaPro.getInstance().logDebug("Entity \"" + getEntityString(detectedEntity) + 
                "\" registriert (" + registeredEntities.size() + " total)");
            
            FabricEvent.post(new FabricScathaProEvents.DetectedEntityRegisteredEvent(detectedEntity));
        }
    }
    
    /**
     * Aktualisiert eine bereits registrierte Entity
     */
    private static void update(FabricDetectedEntity oldEntity, FabricDetectedEntity newEntity)
    {
        oldEntity.entity = newEntity.entity;
        oldEntity.onChangedEntity();
        
        activeEntities.add(oldEntity);
        
        registeredEntities.remove(oldEntity.entity.getId());
        registeredEntities.put(newEntity.entity.getId(), oldEntity);
        
        FabricScathaPro.getInstance().logDebug("Entity \"" + getEntityString(oldEntity) + "\" aktualisiert");
    }
    
    /**
     * Prüft ob Entity in der Welt ist
     */
    private static boolean isInWorld(ArmorStandEntity entity, World world)
    {
        // TODO: Fabric-spezifische Implementation
        return entity != null && !entity.isRemoved() && entity.getWorld() == world;
    }
    
    /**
     * Erstellt Entity-String für Logging
     */
    private static String getEntityString(FabricDetectedEntity detectedEntity)
    {
        Text name = detectedEntity.entity.getDisplayName();
        return (name != null ? name.getString() : "Unknown") + " (" + detectedEntity.entity.getId() + ")";
    }
    
    // ===== INSTANCE MEMBERS =====
    
    public final long spawnTime;
    protected ArmorStandEntity entity;
    
    public FabricDetectedEntity(ArmorStandEntity entity)
    {
        this.entity = entity;
        this.spawnTime = System.currentTimeMillis();
    }
    
    /**
     * Gibt die maximale Lebensdauer der Entity zurück
     */
    public abstract long getMaxLifetime();
    
    /**
     * Gibt die aktuelle Lebensdauer zurück
     */
    public long getCurrentLifetime()
    {
        return System.currentTimeMillis() - spawnTime;
    }
    
    /**
     * Gibt die zugrunde liegende ArmorStand-Entity zurück
     */
    public ArmorStandEntity getEntity()
    {
        return entity;
    }
    
    /**
     * Wird aufgerufen wenn die Entity registriert wird
     */
    protected void onRegistration() {}
    
    /**
     * Wird aufgerufen wenn die Entity-Referenz geändert wird
     */
    protected void onChangedEntity() {}
    
    /**
     * Wird aufgerufen wenn die Entity die Welt verlässt
     */
    protected void onLeaveWorld(LeaveWorldReason leaveWorldReason) {}
    
    /**
     * Gründe warum eine Entity die Welt verlässt
     */
    public enum LeaveWorldReason
    {
        KILLED,
        LIFETIME_ENDED,
        LEFT_SIMULATION_DISTANCE
    }
}