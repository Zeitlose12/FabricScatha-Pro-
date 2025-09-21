package namelessju.scathapro.fabric.webapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

public class HypixelWebApiClient {
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final WebApiCredentials creds;

    public HypixelWebApiClient(WebApiCredentials creds) { this.creds = creds; }

    public JsonObject getPlayer(UUID uuid) throws Exception {
        String url = "https://api.hypixel.net/v2/player?uuid=" + uuid.toString().replace("-", "");
        String body = get(url);
        return JsonParser.parseString(body).getAsJsonObject();
    }

    public JsonObject getSkyblockProfiles(UUID uuid) throws Exception {
        String url = "https://api.hypixel.net/v2/skyblock/profiles?uuid=" + uuid.toString().replace("-", "");
        String body = get(url);
        return JsonParser.parseString(body).getAsJsonObject();
    }

    private String get(String url) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10)).GET();
        String key = creds != null ? creds.getHypixelApiKey() : null;
        if (key != null && !key.isEmpty()) b.header("API-Key", key);
        HttpResponse<String> res = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 200 && res.statusCode() < 300) return res.body();
        throw new RuntimeException("HTTP " + res.statusCode() + " for " + url);
    }
}