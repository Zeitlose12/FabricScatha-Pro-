package namelessju.scathapro.fabric.entitydetection;

import namelessju.scathapro.fabric.FabricScathaPro;
import net.minecraft.entity.decoration.ArmorStandEntity;

/**
 * Fabric-Worm-Detector
 * Erkennt Worms und Scathas basierend auf Entity-Namen
 */
public class FabricWormDetector extends FabricEntityDetector
{
    @Override
    public FabricDetectedEntity detectEntity(ArmorStandEntity armorStand, String entityName)
    {
        if (entityName == null || entityName.isEmpty()) return null;
        
        // Scatha-Detection
        if (entityName.contains("Scatha"))
        {
            FabricScathaPro.getInstance().logDebug("Scatha erkannt: " + entityName);
            return new FabricDetectedWorm(armorStand, true);
        }
        
        // Regulärer Worm-Detection
        if (entityName.contains("Worm") || 
            entityName.contains("worm") ||
            isWormPattern(entityName))
        {
            FabricScathaPro.getInstance().logDebug("Worm erkannt: " + entityName);
            return new FabricDetectedWorm(armorStand, false);
        }
        
        return null;
    }
    
    /**
     * Prüft auf Worm-spezifische Namens-Pattern
     */
    private boolean isWormPattern(String entityName)
    {
        // Hypixel Skyblock Worm-Namen-Patterns
        return entityName.matches(".*Worm.*") ||
               entityName.matches(".*worm.*") ||
               entityName.matches(".*\\d+❤.*"); // Health-Display Pattern
    }
}