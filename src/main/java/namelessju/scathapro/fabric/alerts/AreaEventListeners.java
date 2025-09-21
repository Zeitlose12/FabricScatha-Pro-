package namelessju.scathapro.fabric.alerts;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.ScathaProFabricClient;
import namelessju.scathapro.fabric.areas.SkyblockArea;
import namelessju.scathapro.fabric.events.FabricEvent;
import namelessju.scathapro.fabric.events.FabricScathaProEvents;

/**
 * Listener für Area-Events, um CH-spezifische Systeme zu aktivieren.
 */
public final class AreaEventListeners {
    private AreaEventListeners() {}

    public static void register(FabricScathaPro sp) {
        FabricEvent.register(FabricScathaProEvents.SkyblockAreaDetectedEvent.class, evt -> {
            if (!(evt.area instanceof SkyblockArea area)) return;

            if (area.isCrystalHollows()) {
                // Bereite FirstCrystalHollowsTick vor
                sp.variables.firstCrystalHollowsTickPending = true;
                sp.log("Area: Crystal Hollows – CH-Events aktiviert");

                // Optionaler Enter-Hinweis
                var cfg = ScathaProFabricClient.CONFIG;
                if (cfg != null && cfg.alertsDisplayEnabled) {
                    var overlay = sp.getAlertTitleOverlay();
                    if (overlay != null) overlay.showAlert("§aCrystal Hollows", "Events aktiv", AlertType.ACHIEVEMENT);
                }
            } else {
                // Beim Verlassen vorbereiten, damit beim nächsten Eintritt wieder First-Event kommt
                sp.variables.firstCrystalHollowsTickPending = true;
                sp.log("Area gewechselt: " + area.getDisplayName());
            }
        });
    }
}