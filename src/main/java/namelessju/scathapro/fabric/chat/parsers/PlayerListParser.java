package namelessju.scathapro.fabric.chat.parsers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import namelessju.scathapro.fabric.FabricScathaPro;

import java.util.Collection;

/**
 * Parser/Reader für die Tab-Playerliste (Client-seitig).
 * Dient als Kontextquelle für weitere Features.
 */
public class PlayerListParser {
    public void update(FabricScathaPro sp) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc == null || mc.getNetworkHandler() == null) return;
            Collection<PlayerListEntry> list = mc.getNetworkHandler().getPlayerList();
            int online = list != null ? list.size() : 0;
            // Für Debug-Zwecke loggen wir nur die Anzahl; spätere Logik kann hier ansetzen
            if (sp != null) sp.logDebug("PlayerList: " + online + " Einträge");
        } catch (Exception ignored) {}
    }
}