package namelessju.scathapro.fabric.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import namelessju.scathapro.fabric.state.ClientState;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import namelessju.scathapro.fabric.chat.ChatMessageProcessor;
import org.lwjgl.glfw.GLFW;

public final class ClientHooks {
    public static namelessju.scathapro.fabric.overlay.v2.V2Renderer OVERLAY_V2;
    public static namelessju.scathapro.fabric.overlay.ClassicRenderer OVERLAY_CLASSIC;
    private ClientHooks() {}

    private static boolean showHud = true; // default, wird durch Config überschrieben
    public static KeyBinding toggleDragBinding;
    private static volatile boolean openSettingsPending = false;
    private static String lastLoggedOverlayStyle = null; // Für Debug-Logging

    public static boolean isHudShown() {
        return showHud;
    }

public static void toggleHud(MinecraftClient client) {
        showHud = !showHud;
        if (client != null && client.player != null) {
            client.player.sendMessage(Text.literal("Scatha-Pro HUD " + (showHud ? "ein" : "aus")), false);
        }
        // in Config persistieren
        if (namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG != null) {
            namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.overlayVisible = showHud;
            namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.save();
        }
    }

public static void register() {
        // Chat-Message-Event-Listener registrieren (Ersatz für ChatHudMixin)
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            try {
                ChatMessageProcessor.processChatMessage(message);
            } catch (Exception e) {
                System.err.println("[Scatha-Pro] Error processing chat message: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // initialen Zustand aus Config beziehen
        showHud = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG != null
                ? namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.overlayVisible
                : true;
        // initiale Kills/Streak aus Config
        if (namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG != null) {
            namelessju.scathapro.fabric.state.ClientState.get().setScathaKills(
                Math.max(0, namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.scathaKills)
            );
            namelessju.scathapro.fabric.state.ClientState.get().setWormKills(
                Math.max(0, namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.wormKills)
            );
            namelessju.scathapro.fabric.state.ClientState.get().setStreak(
                Math.max(0, namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.streak)
            );
        }
        // HUD-Overlay: V2Renderer und ClassicRenderer System
        OVERLAY_V2 = new namelessju.scathapro.fabric.overlay.v2.V2Renderer();
        OVERLAY_CLASSIC = new namelessju.scathapro.fabric.overlay.ClassicRenderer();
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!showHud) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.textRenderer == null) return;
            if (mc.getDebugHud().shouldShowDebugHud()) return; // Kein Overlay bei Debug-HUD
            if (mc.currentScreen != null) return; // Kein Overlay bei geöffneten GUIs
            
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            
            // Wähle Renderer basierend auf Config
            String overlayStyle = cfg != null ? cfg.overlayStyle : "v2";
            
            // Debug: Gebe aktuellen Overlay-Stil aus (nur einmalig bei Änderung)
            if (!overlayStyle.equals(lastLoggedOverlayStyle)) {
                System.out.println("[Scatha-Pro] Overlay style changed to: " + overlayStyle + " (Config: " + 
                    (cfg != null ? cfg.overlayStyle : "null") + ")");
                lastLoggedOverlayStyle = overlayStyle;
            }
            
            if (cfg != null && "classic".equals(overlayStyle)) {
                // Classic Overlay (ursprüngliches Forge-Design)
                System.out.println("[Scatha-Pro] Drawing Classic Overlay!");
                OVERLAY_CLASSIC.syncConfig(cfg);
                OVERLAY_CLASSIC.draw(drawContext);
            } else {
                // V2Renderer verwenden (Standard)
                System.out.println("[Scatha-Pro] Drawing V2 Overlay (style: " + overlayStyle + ")");
                OVERLAY_V2.syncConfig(cfg);
                OVERLAY_V2.update();
                OVERLAY_V2.draw(drawContext);
            }
        });

        // Keybind zum Umschalten des HUD
        KeyBinding toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scathapro.togglehud",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.scathapro"
        ));

        // Zusätzliche Keybinds: Erhöhe Kills (H) und Worm-Kills (J)
        KeyBinding incKills = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scathapro.increasekills",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.scathapro"
        ));
        KeyBinding incWormKills = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scathapro.increasewormkills",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "key.categories.scathapro"
        ));

        // Detection Manager
        final namelessju.scathapro.fabric.detect.DetectorManager detector = new namelessju.scathapro.fabric.detect.DetectorManager();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            detector.tick();
            
            // Area Detection aktualisieren
            var scathaPro = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
            if (scathaPro != null && scathaPro.getAreaDetector() != null) {
                scathaPro.getAreaDetector().update();
            }
            
            if (openSettingsPending) {
                openSettingsPending = false;
                client.setScreen(new namelessju.scathapro.fabric.gui.SettingsScreen(client.currentScreen));
            }
            while (toggleHud.wasPressed()) {
                showHud = !showHud;
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Scatha-Pro HUD " + (showHud ? "ein" : "aus")), false);
                }
                // persistieren
                if (namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG != null) {
                    namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.overlayVisible = showHud;
                    namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.save();
                }
            }
            while (incKills.wasPressed()) {
                ClientState.get().addScathaKills(1);
                if (namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG != null) {
                    namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.scathaKills = ClientState.get().getScathaKills();
                    namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.save();
                }
            }
            while (incWormKills.wasPressed()) {
                ClientState.get().addWormKills(1);
                if (namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG != null) {
                    namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.wormKills = ClientState.get().getWormKills();
                    namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.save();
                }
            }
        });
        // Markiere als registriert
        REGISTERED = true;
    }

    private static volatile boolean REGISTERED = false;
    public static boolean isRegistered() { return REGISTERED; }

    private static int[] getScaledMouse(MinecraftClient client) {
        double rawX = client.mouse.getX();
        double rawY = client.mouse.getY();
        var window = client.getWindow();
        int scaledX = (int) Math.round(rawX * window.getScaledWidth() / (double) window.getWidth());
        int scaledY = (int) Math.round(rawY * window.getScaledHeight() / (double) window.getHeight());
        return new int[]{scaledX, scaledY};
    }


    public static namelessju.scathapro.fabric.overlay.v2.V2Renderer getV2Renderer() { return OVERLAY_V2; }

    public static void requestOpenSettings() {
        openSettingsPending = true;
    }
}
