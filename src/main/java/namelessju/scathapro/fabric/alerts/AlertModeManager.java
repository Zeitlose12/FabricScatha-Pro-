package namelessju.scathapro.fabric.alerts;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.ScathaProFabricClient;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Verwalten von Alert-Modi (Preset & Custom)
 */
public class AlertModeManager {
    private final FabricScathaPro scathaPro;
    private final Map<String, AlertMode> modes = new LinkedHashMap<>();
    private AlertMode current;

    public AlertModeManager(FabricScathaPro scathaPro) {
        this.scathaPro = scathaPro;
        // Presets registrieren
        register(new ModeWrapper(PresetAlertMode.VANILLA));
        register(new ModeWrapper(PresetAlertMode.MEME));
        register(new ModeWrapper(PresetAlertMode.ANIME));
        register(new ModeWrapper(PresetAlertMode.CUSTOM));

        // aus Config laden
        String initial = (ScathaProFabricClient.CONFIG != null && ScathaProFabricClient.CONFIG.mode != null)
                ? ScathaProFabricClient.CONFIG.mode : PresetAlertMode.VANILLA.id();
        setById(initial);
    }

    public void register(AlertMode mode) {
        if (mode == null) return;
        modes.put(mode.id(), mode);
    }

    public AlertMode getCurrent() { return current; }

    public boolean setById(String id) {
        AlertMode next = modes.get(id);
        if (next == null) {
            next = modes.get(PresetAlertMode.VANILLA.id());
        }
        if (next == current) return false;
        if (current != null) current.onExit();
        current = next;
        if (current != null) current.onEnter();
        persist();
        scathaPro.log("AlertMode gesetzt: " + current.displayName());
        return true;
    }

    public AlertMode next() {
        if (modes.isEmpty()) return null;
        String[] keys = modes.keySet().toArray(new String[0]);
        int idx = 0;
        if (current != null) {
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equals(current.id())) { idx = i; break; }
            }
            idx = (idx + 1) % keys.length;
        }
        setById(keys[idx]);
        return current;
    }

    private void persist() {
        try {
            if (ScathaProFabricClient.CONFIG != null && current != null) {
                ScathaProFabricClient.CONFIG.mode = current.id();
                ScathaProFabricClient.CONFIG.save();
            }
        } catch (Exception ignored) {}
    }

    private static final class ModeWrapper implements AlertMode {
        private final PresetAlertMode preset;
        ModeWrapper(PresetAlertMode preset) { this.preset = preset; }
        @Override public String id() { return preset.id(); }
        @Override public String displayName() { return preset.displayName(); }
    }
}