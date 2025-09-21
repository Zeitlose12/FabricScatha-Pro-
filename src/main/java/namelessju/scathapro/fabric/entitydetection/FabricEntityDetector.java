package namelessju.scathapro.fabric.entitydetection;

import net.minecraft.entity.decoration.ArmorStandEntity;

/**
 * Fabric-Basis-Klasse für Entity-Detectors
 * Erkennt spezifische Entities basierend auf Namen und anderen Kriterien
 */
public abstract class FabricEntityDetector
{
    /**
     * Versucht eine Entity zu erkennen basierend auf ArmorStand und Namen
     * @param armorStand Die ArmorStand-Entity
     * @param entityName Der Name der Entity (ohne Formatierung)
     * @return DetectedEntity wenn erkannt, null sonst
     */
    public abstract FabricDetectedEntity detectEntity(ArmorStandEntity armorStand, String entityName);
}