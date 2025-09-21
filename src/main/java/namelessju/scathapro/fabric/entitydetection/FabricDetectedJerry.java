package namelessju.scathapro.fabric.entitydetection;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;
import net.minecraft.entity.decoration.ArmorStandEntity;

/**
 * Erkannte Jerry-Entität (einfacher Lifecycle)
 */
public class FabricDetectedJerry extends FabricDetectedEntity {

    public FabricDetectedJerry(ArmorStandEntity entity) {
        super(entity);
    }

    @Override
    public long getMaxLifetime() {
        // einfache Heuristik: 60 Sekunden
        return 60000L;
    }

    @Override
    protected void onRegistration() {
        // Jerry Spawn Alert auslösen
        try {
            var sp = FabricScathaPro.getInstance();
            if (sp != null && sp.getAlertManager() != null) {
                ChatEvent evt = new ChatEvent(ChatEventType.JERRY_SPAWN, "Jerry detected", "Jerry detected");
                evt.withData("entityType", "jerry");
                sp.getAlertManager().triggerAlert(evt);
            }
        } catch (Exception ignored) {}
    }
}