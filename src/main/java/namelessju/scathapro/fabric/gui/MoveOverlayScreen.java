package namelessju.scathapro.fabric.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MoveOverlayScreen extends Screen {
    private final Screen parent;
    private boolean dragging = false;
    private int dragDx = 0, dragDy = 0;

    public MoveOverlayScreen(Screen parent) {
        super(Text.literal("Move GUI"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // nichts – rein zum Ziehen benutzen
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Verdunkelter Hintergrund (stärker)
        ctx.fill(0, 0, this.width, this.height, 0xAA000000);
        var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
        var tr = this.textRenderer;
        
        // Overlay-Vorschau explizit zeichnen (HUD-Hook rendert bei Screens nicht)
        try {
            if (cfg != null) {
                String style = cfg.overlayStyle != null ? cfg.overlayStyle : "v2";
                if ("classic".equalsIgnoreCase(style)) {
                    var r = namelessju.scathapro.fabric.client.ClientHooks.OVERLAY_CLASSIC;
                    if (r != null) { r.syncConfig(cfg); r.draw(ctx); }
                } else {
                    var r2 = namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer();
                    if (r2 != null) { r2.syncConfig(cfg); r2.update(); r2.draw(ctx); }
                }
            }
        } catch (Throwable ignored) {}
        
        // Hinweis oben mittig und Scale-Info links oben – nach allem anderen, damit es garantiert oben liegt
        String help = "ESC to save • Mouse wheel to change size • Left-click to move";
        int tw = tr.getWidth(help);
        ctx.drawText(tr, Text.literal(help), (this.width - tw) / 2, 8, 0xFFFFFFFF, true);
        String scaleInfo = String.format("Scale: %.2f", Math.max(0.1f, cfg.overlayScale));
        ctx.drawText(tr, Text.literal(scaleInfo), 12, 8, 0xFFE0E0E0, true);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            int x = Math.max(0, cfg.overlayX);
            int y = Math.max(0, cfg.overlayY);
            dragDx = (int)mouseX - x;
            dragDy = (int)mouseY - y;
            dragging = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (dragging && button == 0) {
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            int nx = Math.max(0, (int)mouseX - dragDx);
            int ny = Math.max(0, (int)mouseY - dragDy);
            if (cfg.overlaySnapEnabled) {
                int g = Math.max(1, cfg.overlaySnapSize);
                nx = (nx / g) * g;
                ny = (ny / g) * g;
            }
            cfg.overlayX = nx; cfg.overlayY = ny;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
        if (cfg != null) {
            float s = cfg.overlayScale;
            // Mausrad: feine Skalierung in Stufen (5% pro Notch)
            float step = verticalAmount > 0 ? 1.05f : 0.95f;
            s *= Math.pow(step, Math.abs(verticalAmount));
            s = Math.max(0.5f, Math.min(3.0f, s));
            cfg.overlayScale = s;
            // kein sofortiges Save; Speicherung beim ESC/close
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging) { dragging = false; return true; }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            if (cfg != null) cfg.save();
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}