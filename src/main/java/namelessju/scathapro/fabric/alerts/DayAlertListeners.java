package namelessju.scathapro.fabric.alerts;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.ScathaProFabricClient;
import namelessju.scathapro.fabric.events.FabricEvent;
import namelessju.scathapro.fabric.events.FabricScathaProEvents;

/**
 * Listener für Day-basierte Alerts (z. B. Old Lobby Alert)
 */
public final class DayAlertListeners {
    private DayAlertListeners() {}

    public static void register(FabricScathaPro sp) {
        FabricEvent.register(FabricScathaProEvents.CrystalHollowsDayStartedEvent.class, evt -> {
            var cfg = ScathaProFabricClient.CONFIG;
            if (cfg == null) return;
            if (!cfg.oldLobbyAlert) return;

            int triggerDay = Math.max(0, cfg.oldLobbyAlertTriggerDay);
            if (evt.day < triggerDay) return;

            // Optionaler Mode-Filter aus der Config
            String modeReq = cfg.oldLobbyAlertTriggerMode;
            if (modeReq != null && !modeReq.isEmpty()) {
                var mm = sp.getAlertModeManager();
                String cur = (mm != null && mm.getCurrent() != null) ? mm.getCurrent().id() : "";
                if (!"any".equalsIgnoreCase(modeReq) && !modeReq.equalsIgnoreCase(cur)) {
                    return;
                }
            }

            // Zeige Titel-Overlay
            var overlay = sp.getAlertTitleOverlay();
            if (overlay != null) {
                String title = "§eOld Lobby!";
                String subtitle = "Day " + evt.day + " reached";
                overlay.showAlert(title, subtitle, AlertType.ACHIEVEMENT);
            }

            // Spiele Benachrichtigungssound
            var sm = sp.getSoundManager();
            if (sm != null) {
                sm.playAlertSound("notification");
            }

            sp.log("Old Lobby Alert: Day " + evt.day);
        });
    }
}