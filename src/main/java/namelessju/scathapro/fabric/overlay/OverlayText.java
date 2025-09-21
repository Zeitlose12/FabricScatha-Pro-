package namelessju.scathapro.fabric.overlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class OverlayText extends OverlayElement {
    private Text text;
    private int color;

    public OverlayText(Text text, int color, int x, int y, float scale) {
        super(x, y, scale);
        this.text = text;
        this.color = color;
    }

    public void setText(Text t) { this.text = t; }
    public void setColor(int c) { this.color = c; }

    @Override
    public void draw(DrawContext ctx) {
        if (!visible || text == null) return;
        var m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(scale, scale, 1.0f);
        var tr = MinecraftClient.getInstance().textRenderer;
        boolean dropShadow = true;
        try { var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG; if (cfg != null) dropShadow = cfg.overlayTextShadow; } catch (Exception ignored) {}
        ctx.drawText(tr, text, 0, 0, color, dropShadow);
        m.pop();
    }
}