package namelessju.scathapro.fabric.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class FabricHudUtil {
    private FabricHudUtil() {}
    public static void showOverlayMessage(String message) {
        var mc = MinecraftClient.getInstance();
        if (mc == null || mc.inGameHud == null) return;
        mc.inGameHud.setOverlayMessage(Text.literal(message), false);
    }
}
