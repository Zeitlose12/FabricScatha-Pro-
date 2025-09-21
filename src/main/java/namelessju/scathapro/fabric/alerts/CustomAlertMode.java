package namelessju.scathapro.fabric.alerts;

import java.util.HashMap;
import java.util.Map;

public class CustomAlertMode implements AlertMode {
    private final String id;
    private final String name;
    private final Map<String,String> soundMap = new HashMap<>();
    private final Map<String,String> titleMap = new HashMap<>();
    private final Map<String,Float> volumeMap = new HashMap<>();

    public CustomAlertMode(String id, String name) { this.id = id; this.name = name != null ? name : id; }
    @Override public String id() { return id; }
    @Override public String displayName() { return name; }

    public void setSound(String defaultKey, String mappedKey) {
        if (defaultKey == null) return;
        if (mappedKey == null || mappedKey.isEmpty()) soundMap.remove(defaultKey); else soundMap.put(defaultKey, mappedKey);
    }

    public String mapSound(String defaultKey) {
        if (defaultKey == null) return null;
        return soundMap.getOrDefault(defaultKey, defaultKey);
    }

    public void setTitle(String key, String title) {
        if (key == null) return; if (title == null || title.isEmpty()) titleMap.remove(key); else titleMap.put(key, title);
    }
    public String getTitle(String key) { return key == null ? null : titleMap.get(key); }
    public Map<String,String> getTitles() { return new HashMap<>(titleMap); }

    public void setVolume(String key, Float vol) {
        if (key == null) return; if (vol == null) volumeMap.remove(key); else volumeMap.put(key, Math.max(0.0f, Math.min(1.0f, vol)));
    }
    public float getVolume(String key, float def) { if (key==null) return def; return volumeMap.getOrDefault(key, def); }
    public Map<String,Float> getVolumes() { return new HashMap<>(volumeMap); }

    public Map<String,String> getSounds() { return new HashMap<>(soundMap); }
}
