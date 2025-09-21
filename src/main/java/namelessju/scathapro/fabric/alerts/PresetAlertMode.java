package namelessju.scathapro.fabric.alerts;

import java.util.Locale;

/**
 * Vordefinierte Alert-Presets
 */
public enum PresetAlertMode implements AlertMode {
    VANILLA("vanilla", "Vanilla"),
    MEME("meme", "Meme"),
    ANIME("anime", "Anime"),
    CUSTOM("custom", "Custom");

    private final String id;
    private final String displayName;

    PresetAlertMode(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    @Override public String id() { return id; }
    @Override public String displayName() { return displayName; }

    public static PresetAlertMode byId(String id) {
        if (id == null) return VANILLA;
        String key = id.toLowerCase(Locale.ROOT);
        for (var m : values()) {
            if (m.id.equals(key)) return m;
        }
        return VANILLA;
    }
}