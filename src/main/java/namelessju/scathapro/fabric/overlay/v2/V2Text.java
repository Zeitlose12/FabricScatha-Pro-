package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class V2Text extends V2Element {
    private Text text;
    int color;

    public V2Text(Text text, int color, int x, int y, float scale) {
        super(x, y, scale);
        this.text = text; this.color = color;
    }

    public void setText(Text t) { this.text = t; }
    public void setColor(int c) { this.color = c; }
    public int getColor() { return this.color; }
    public Text getRawText() { return this.text; }
    public int getTextWidth() {
        if (text == null) return 0;
        var tr = MinecraftClient.getInstance().textRenderer;
        return Math.round(tr.getWidth(text) * this.scale);
    }
    public int getTextHeight() {
        var tr = MinecraftClient.getInstance().textRenderer;
        return Math.round(tr.fontHeight * this.scale);
    }

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