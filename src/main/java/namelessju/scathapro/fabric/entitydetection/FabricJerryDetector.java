package namelessju.scathapro.fabric.entitydetection;

import net.minecraft.entity.decoration.ArmorStandEntity;

/**
 * Detector für Jerry basierend auf dem Entity-Namen.
 */
public class FabricJerryDetector extends FabricEntityDetector {
    @Override
    public FabricDetectedEntity detectEntity(ArmorStandEntity armorStand, String entityName) {
        if (entityName == null || entityName.isEmpty()) return null;
        // Einfache Muster: enthält "Jerry"
        if (entityName.toLowerCase().contains("jerry")) {
            return new FabricDetectedJerry(armorStand);
        }
        return null;
    }
}