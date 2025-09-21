package namelessju.scathapro.fabric.entitydetection;

import net.minecraft.entity.decoration.ArmorStandEntity;

/**
 * Detector für Goblins basierend auf dem Entity-Namen.
 */
public class FabricGoblinDetector extends FabricEntityDetector {
    @Override
    public FabricDetectedEntity detectEntity(ArmorStandEntity armorStand, String entityName) {
        if (entityName == null || entityName.isEmpty()) return null;
        // Einfache Muster: enthält "Goblin"
        if (entityName.toLowerCase().contains("goblin")) {
            return new FabricDetectedGoblin(armorStand);
        }
        return null;
    }
}