package namelessju.scathapro.fabric.webapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WebApiCredentials {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private String hypixelApiKey;

    public static Path getFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("scathapro").resolve("credentials.json");
    }

    public void load() {
        try {
            Path f = getFile();
            if (!Files.exists(f)) return;
            String s = Files.readString(f, StandardCharsets.UTF_8);
            JsonObject o = GSON.fromJson(s, JsonObject.class);
            if (o != null && o.has("hypixelApiKey")) this.hypixelApiKey = o.get("hypixelApiKey").getAsString();
        } catch (Exception ignored) {}
    }

    public void save() {
        try {
            JsonObject o = new JsonObject();
            if (hypixelApiKey != null && !hypixelApiKey.isEmpty()) o.addProperty("hypixelApiKey", hypixelApiKey);
            Path f = getFile();
            Files.createDirectories(f.getParent());
            Files.writeString(f, GSON.toJson(o), StandardCharsets.UTF_8);
        } catch (Exception ignored) {}
    }

    public String getHypixelApiKey() { return hypixelApiKey; }
    public void setHypixelApiKey(String key) { this.hypixelApiKey = key; }
    public void clear() { this.hypixelApiKey = null; }
}