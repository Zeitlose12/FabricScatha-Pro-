package namelessju.scathapro.fabric.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import namelessju.scathapro.fabric.FabricScathaPro;

/**
 * Zentraler Input-Manager für Keybindings
 */
public class FabricInputManager {
    private final FabricScathaPro scathaPro;

    // Keybindings
    private KeyBinding openSettings;
    private KeyBinding moveOverlay;
    private KeyBinding nextAlertMode;

    public FabricInputManager(FabricScathaPro scathaPro) {
        this.scathaPro = scathaPro;
    }

    public void register() {
        openSettings = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.scathapro.opensettings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "key.categories.scathapro"));

        moveOverlay = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.scathapro.moveoverlay",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "key.categories.scathapro"));

        nextAlertMode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.scathapro.nextalertmode",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "key.categories.scathapro"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> handleClientTick(client));
        scathaPro.log("Input-Manager registriert");
    }

    private void handleClientTick(MinecraftClient client) {
        while (openSettings.wasPressed()) {
            if (client != null) {
                client.setScreen(new namelessju.scathapro.fabric.gui.SettingsScreen(client.currentScreen));
            }
        }
        while (moveOverlay.wasPressed()) {
            if (client != null) {
                client.setScreen(new namelessju.scathapro.fabric.gui.MoveOverlayScreen(client.currentScreen));
            }
        }
        while (nextAlertMode.wasPressed()) {
            if (scathaPro.getAlertManager() != null && scathaPro.getAlertModeManager() != null) {
                var mode = scathaPro.getAlertModeManager().next();
                if (client != null && client.player != null && mode != null) {
                    client.player.sendMessage(Text.literal("Alert Mode: " + mode.displayName()), false);
                }
                scathaPro.getAlertManager().setModeManager(scathaPro.getAlertModeManager());
            }
        }
    }
}