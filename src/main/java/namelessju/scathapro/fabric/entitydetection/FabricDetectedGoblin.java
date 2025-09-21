package namelessju.scathapro.fabric.entitydetection;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;
import net.minecraft.entity.decoration.ArmorStandEntity;

/**
 * Erkannte Goblin-Entität (einfacher Lifecycle)
 */
public class FabricDetectedGoblin extends FabricDetectedEntity {

    public FabricDetectedGoblin(ArmorStandEntity entity) {
        super(entity);
    }

    @Override
    public long getMaxLifetime() {
        // einfache Heuristik: 60 Sekunden
        return 60000L;
    }

    @Override
    protected void onRegistration() {
        // Goblin Spawn Alert auslösen
        try {
            var sp = FabricScathaPro.getInstance();
            if (sp != null && sp.getAlertManager() != null) {
                ChatEvent evt = new ChatEvent(ChatEventType.GOBLIN_SPAWN, "Goblin detected", "Goblin detected");
                evt.withData("entityType", "goblin");
                sp.getAlertManager().triggerAlert(evt);
            }
        } catch (Exception ignored) {}
    }
}