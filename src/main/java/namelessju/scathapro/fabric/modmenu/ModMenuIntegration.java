package namelessju.scathapro.fabric.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new SimpleConfigScreen(parent);
    }

    static class SimpleConfigScreen extends Screen {
        private final Screen parent;
        protected SimpleConfigScreen(Screen parent) {
            super(Text.literal("Scatha-Pro Einstellungen"));
            this.parent = parent;
        }
        @Override
        protected void init() {
            int y = 40;
            int x = this.width / 2 - 100;
            addDrawableChild(ButtonWidget.builder(Text.literal(toggleLabel("HUD", cfg().overlayVisible)), b -> {
                cfg().overlayVisible = !cfg().overlayVisible; cfg().save(); b.setMessage(Text.literal(toggleLabel("HUD", cfg().overlayVisible)));
            }).dimensions(x, y, 200, 20).build()); y += 24;

            addDrawableChild(ButtonWidget.builder(Text.literal(toggleLabel("Compact", cfg().overlayCompactMode)), b -> {
                cfg().overlayCompactMode = !cfg().overlayCompactMode; cfg().save(); b.setMessage(Text.literal(toggleLabel("Compact", cfg().overlayCompactMode))); 
            }).dimensions(x, y, 200, 20).build()); y += 24;

            addDrawableChild(ButtonWidget.builder(Text.literal(toggleLabel("Icons", cfg().overlayShowIcons)), b -> {
                cfg().overlayShowIcons = !cfg().overlayShowIcons; cfg().save(); b.setMessage(Text.literal(toggleLabel("Icons", cfg().overlayShowIcons))); 
            }).dimensions(x, y, 200, 20).build()); y += 24;

            addDrawableChild(ButtonWidget.builder(Text.literal(toggleLabel("Alerts", cfg().alertsEnabled)), b -> {
                cfg().alertsEnabled = !cfg().alertsEnabled; cfg().save(); b.setMessage(Text.literal(toggleLabel("Alerts", cfg().alertsEnabled))); 
            }).dimensions(x, y, 200, 20).build()); y += 24;

            addDrawableChild(ButtonWidget.builder(Text.literal(toggleLabel("DebugLogs", cfg().debugLogs)), b -> {
                cfg().debugLogs = !cfg().debugLogs; cfg().save(); b.setMessage(Text.literal(toggleLabel("DebugLogs", cfg().debugLogs))); 
            }).dimensions(x, y, 200, 20).build()); y += 24;

            addDrawableChild(ButtonWidget.builder(Text.literal("Farbprofil: " + cfg().colorProfile), b -> {
                String next = nextProfile(cfg().colorProfile);
                cfg().colorProfile = next; cfg().save(); b.setMessage(Text.literal("Farbprofil: " + next));
            }).dimensions(x, y, 200, 20).build()); y += 24;

            addDrawableChild(ButtonWidget.builder(Text.literal("Schließen"), b -> close()).dimensions(x, y + 8, 200, 20).build());
        }
        private namelessju.scathapro.fabric.FabricConfig cfg() {
            return namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
        }
        private static String toggleLabel(String name, boolean val) { return name + ": " + (val ? "ein" : "aus"); }
        private static String nextProfile(String p) {
            String v = p == null ? "default" : p.toLowerCase();
            return switch (v) { case "default" -> "dark"; case "dark" -> "high"; default -> "default"; };
        }
        @Override
        public void close() {
            MinecraftClient.getInstance().setScreen(parent);
        }
    }
}