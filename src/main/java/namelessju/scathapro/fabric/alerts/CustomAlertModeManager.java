package namelessju.scathapro.fabric.alerts;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CustomAlertModeManager {
    private final Map<String, CustomAlertMode> modes = new LinkedHashMap<>();

    public Path getFile() { return FabricLoader.getInstance().getConfigDir().resolve("scathapro").resolve("custom_modes.json"); }

    public void load() {
        try {
            Path f = getFile();
            if (!Files.exists(f)) return;
            String s = Files.readString(f, StandardCharsets.UTF_8);
            JsonArray arr = JsonParser.parseString(s).getAsJsonArray();
            modes.clear();
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                JsonObject o = el.getAsJsonObject();
                String id = o.has("id") ? o.get("id").getAsString() : null;
                String name = o.has("name") ? o.get("name").getAsString() : id;
                if (id == null) continue;
                CustomAlertMode m = new CustomAlertMode(id, name);
                if (o.has("sounds") && o.get("sounds").isJsonObject()) {
                    JsonObject sm = o.get("sounds").getAsJsonObject();
                    for (Map.Entry<String, JsonElement> e : sm.entrySet()) {
                        if (e.getValue() != null && e.getValue().isJsonPrimitive()) {
                            m.setSound(e.getKey(), e.getValue().getAsString());
                        }
                    }
                }
                if (o.has("titles") && o.get("titles").isJsonObject()) {
                    JsonObject tm = o.get("titles").getAsJsonObject();
                    for (Map.Entry<String, JsonElement> e : tm.entrySet()) {
                        if (e.getValue() != null && e.getValue().isJsonPrimitive()) {
                            m.setTitle(e.getKey(), e.getValue().getAsString());
                        }
                    }
                }
                if (o.has("volumes") && o.get("volumes").isJsonObject()) {
                    JsonObject vm = o.get("volumes").getAsJsonObject();
                    for (Map.Entry<String, JsonElement> e : vm.entrySet()) {
                        try { m.setVolume(e.getKey(), e.getValue().getAsFloat()); } catch (Exception ignored) {}
                    }
                }
                modes.put(id, m);
            }
        } catch (Exception ignored) {}
    }

    public void save() {
        try {
            JsonArray arr = new JsonArray();
            for (CustomAlertMode m : modes.values()) {
                JsonObject o = new JsonObject();
                o.addProperty("id", m.id());
                o.addProperty("name", m.displayName());
                JsonObject sm = new JsonObject();
                for (Map.Entry<String,String> e : m.getSounds().entrySet()) {
                    sm.addProperty(e.getKey(), e.getValue());
                }
                o.add("sounds", sm);
                JsonObject tm = new JsonObject();
                for (Map.Entry<String,String> e : m.getTitles().entrySet()) {
                    tm.addProperty(e.getKey(), e.getValue());
                }
                o.add("titles", tm);
                JsonObject vm = new JsonObject();
                for (Map.Entry<String,Float> e : m.getVolumes().entrySet()) {
                    vm.addProperty(e.getKey(), e.getValue());
                }
                o.add("volumes", vm);
                arr.add(o);
            }
            Path f = getFile();
            Files.createDirectories(f.getParent());
            Files.writeString(f, new GsonBuilder().setPrettyPrinting().create().toJson(arr), StandardCharsets.UTF_8);
        } catch (IOException ignored) {}
    }

    public Collection<CustomAlertMode> getAll() { return modes.values(); }
    public CustomAlertMode get(String id) { return modes.get(id); }
    public void add(CustomAlertMode m) { if (m != null) modes.put(m.id(), m); }
}