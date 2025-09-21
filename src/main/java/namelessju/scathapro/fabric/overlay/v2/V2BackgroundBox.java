package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;

public class V2BackgroundBox extends V2Element {
    private int width, height; private int color; private Integer borderColor = null;
    public V2BackgroundBox(int x, int y, int width, int height, int color) {
        super(x, y, 1.0f); this.width=width; this.height=height; this.color=color;
    }
    public V2BackgroundBox setBorder(Integer argb) { this.borderColor = argb; return this; }
    public Integer getBorderColor() { return borderColor; }
    public void setColor(int argb) { this.color = argb; }
    public int getColor() { return color; }
    public void setSize(int w, int h) { this.width = Math.max(1, w); this.height = Math.max(1, h); }
    @Override
    public void draw(DrawContext ctx) {
        if (!visible) return;
        var m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(scale, scale, 1.0f);
        ctx.fill(0,0,width,height,color);
        if (borderColor != null) {
            int c = borderColor;
            ctx.fill(0, 0, width, 1, c);
            ctx.fill(0, height-1, width, height, c);
            ctx.fill(0, 0, 1, height, c);
            ctx.fill(width-1, 0, width, height, c);
        }
        m.pop();
    }
}
