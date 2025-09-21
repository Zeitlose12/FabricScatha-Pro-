package namelessju.scathapro.fabric.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class FabricSoundUtil {
    private FabricSoundUtil() {}

    public static void playModSound(String path) {
        playModSound(path, 1.0f);
    }

    public static void playModSound(String path, float volume) {
        if (!isAlertsEnabled()) return;
        try {
            Identifier id = Identifier.of("scathapro", path);
            SoundEvent evt = SoundEvent.of(id);
            var sm = MinecraftClient.getInstance().getSoundManager();
            float vol = volume * getVolume();
            sm.play(PositionedSoundInstance.master(evt, vol));
        } catch (Throwable ignored) {}
    }

    private static boolean isAlertsEnabled() {
        var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
        return cfg != null && cfg.alertsEnabled;
    }
    private static float getVolume() {
        var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
        return cfg != null ? Math.max(0f, Math.min(1f, cfg.soundVolume)) : 1.0f;
    }
}
