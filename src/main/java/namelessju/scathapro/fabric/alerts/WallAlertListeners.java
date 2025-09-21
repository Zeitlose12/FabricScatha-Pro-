package namelessju.scathapro.fabric.alerts;

import namelessju.scathapro.fabric.Constants;
import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.ScathaProFabricClient;
import namelessju.scathapro.fabric.events.FabricEvent;
import namelessju.scathapro.fabric.events.FabricTickEvent;

/**
 * Listener für Bedrock-Wall-Alerts basierend auf Spielerabstand zu den Wänden der Crystal Hollows.
 */
public final class WallAlertListeners {
    private WallAlertListeners() {}

    private static boolean insideRangePrev = false;
    private static long lastTriggerMs = 0L;
    private static final long MIN_INTERVAL_MS = 10_000L; // 10s Anti-Spam

    public static void register(FabricScathaPro sp) {
        FabricEvent.register(FabricTickEvent.CrystalHollowsTickEvent.class, evt -> {
            var cfg = ScathaProFabricClient.CONFIG;
            if (cfg == null || !cfg.bedrockWallAlert) return;
            if (sp.getMinecraft() == null || sp.getMinecraft().player == null) return;

            var player = sp.getMinecraft().player;
            double x = player.getX();
            double z = player.getZ();

            int min = Constants.crystalHollowsBoundsMin;
            int max = Constants.crystalHollowsBoundsMax;

            double dx = Math.min(Math.abs(x - min), Math.abs(x - max));
            double dz = Math.min(Math.abs(z - min), Math.abs(z - max));
            double dist = Math.min(dx, dz);

            int triggerDist = Math.max(1, cfg.bedrockWallAlertTriggerDistance);
            boolean inside = dist <= triggerDist;

            long now = System.currentTimeMillis();
            boolean cooldownOk = (now - lastTriggerMs) >= MIN_INTERVAL_MS;

            if (inside && (!insideRangePrev || cooldownOk)) {
                lastTriggerMs = now;
                // Visual Alert
                var overlay = sp.getAlertTitleOverlay();
                if (overlay != null) {
                    String title = "§cBedrock Wall";
                    String subtitle = String.format("Distance: %.1f", dist);
                    overlay.showAlert(title, subtitle, AlertType.ACHIEVEMENT);
                }
                // Sound
                var sm = sp.getSoundManager();
                if (sm != null) sm.playAlertSound("warning");
                sp.logDebug(String.format("BedrockWallAlert ausgelöst (dist=%.2f, trigger=%d)", dist, triggerDist));
            }
            insideRangePrev = inside;
        });
    }
}