package namelessju.scathapro.fabric.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class FabricUpdateChecker {
    private static final String MODRINTH_PROJECT_ID = "lPe25xOt";
    private static final String MODRINTH_API_VERSIONS_ENDPOINT = "https://api.modrinth.com/v2/project/"+MODRINTH_PROJECT_ID+"/version";
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    private FabricUpdateChecker() {}

    public static void check(boolean showAllResults) {
        var mc = MinecraftClient.getInstance();
        if (mc == null) return;
        new Thread(() -> {
            try {
                String gameVersion = "1.21.5";
                String loader = "fabric";
                String url = MODRINTH_API_VERSIONS_ENDPOINT + "?loaders=[\""+loader+"\"]&game_versions=[\""+gameVersion+"\"]";
                HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .header("User-Agent", "Scatha-Pro/" + namelessju.scathapro.fabric.FabricScathaPro.VERSION)
                        .header("Accept", "application/json")
                        .GET().build();
                HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
                if (res.statusCode() >= 200 && res.statusCode() < 300) {
                    JsonElement el = JsonParser.parseString(res.body());
                    if (el != null && el.isJsonArray()) {
                        String latestRelease = null;
                        JsonArray arr = el.getAsJsonArray();
                        for (JsonElement ve : arr) {
                            if (!ve.isJsonObject()) continue;
                            JsonObject vo = ve.getAsJsonObject();
                            String type = optString(vo, "version_type");
                            String status = optString(vo, "status");
                            if (!"release".equalsIgnoreCase(type)) continue;
                            if (!"listed".equalsIgnoreCase(status)) continue;
                            String ver = optString(vo, "version_number");
                            if (ver == null) continue;
                            if (latestRelease == null || compareVersions(latestRelease, ver) < 0) latestRelease = ver;
                        }
                        if (latestRelease != null) {
                            String current = namelessju.scathapro.fabric.FabricScathaPro.VERSION;
                            int cmp = compareVersions(current, latestRelease);
                            if (cmp < 0) {
                                send("§6A newer Scatha-Pro version (" + latestRelease + ") is available.");
                            } else if (showAllResults) {
                                if (cmp > 0) send("§bYour version is newer than the latest public release.");
                                else send("§aYou're using the newest version!");
                            }
                        } else if (showAllResults) send("§cNo versions found on API.");
                    } else if (showAllResults) send("§cUnexpected API response format.");
                } else if (showAllResults) send("§cHTTP Error: " + res.statusCode());
            } catch (Exception e) {
                if (showAllResults) send("§cError while checking for update: " + e.getMessage());
            }
        }, "SP-UpdateChecker").start();
    }

    private static int compareVersions(String a, String b) {
        if (a == null || b == null) return 0;
        String[] as = a.replaceAll("[,_\\-\\+]", ".").split("[.\\\\-]");
        String[] bs = b.replaceAll("[,_\\-\\+]", ".").split("[.\\\\-]");
        for (int i = 0; i < Math.max(as.length, bs.length); i++) {
            int ai = i < as.length ? parsePart(as[i]) : 0;
            int bi = i < bs.length ? parsePart(bs[i]) : 0;
            if (ai != bi) return Integer.compare(ai, bi);
        }
        return 0;
    }

    private static int parsePart(String p) {
        try { return Integer.parseInt(p); } catch (Exception ignored) { return 0; }
    }

    private static String optString(JsonObject o, String k) { return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : null; }

    private static void send(String msg) {
        var mc = MinecraftClient.getInstance();
        if (mc != null && mc.player != null) mc.execute(() -> mc.player.sendMessage(Text.literal(namelessju.scathapro.fabric.Constants.chatPrefix + msg), false));
    }
}
