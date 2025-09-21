package namelessju.scathapro.fabric.overlay;

import net.minecraft.client.gui.DrawContext;

public class OverlayProgressBar extends OverlayElement {
    private int width;
    private int height;
    private int fgColor;
    private int bgColor;
    private float progress = 0f; // 0..1

    public OverlayProgressBar(int x, int y, int width, int height, float scale, int fgColor, int bgColor) {
        super(x, y, scale);
        this.width = width;
        this.height = height;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
    }

    public void setProgress(float p) { this.progress = Math.max(0f, Math.min(1f, p)); }
    public void setColors(int fg, int bg) { this.fgColor = fg; this.bgColor = bg; }

    @Override
    public void draw(DrawContext ctx) {
        if (!visible) return;
        var m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(scale, scale, 1.0f);
        if (bgColor >= 0) ctx.fill(0, 0, width, height, bgColor);
        if (fgColor >= 0) ctx.fill(0, 0, Math.round(width * progress), height, fgColor);
        m.pop();
    }
}