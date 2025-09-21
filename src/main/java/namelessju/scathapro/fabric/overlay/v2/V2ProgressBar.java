package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;

public class V2ProgressBar extends V2Element {
    private int width; private int height; private int fg; private int bg; private float progress;
    public V2ProgressBar(int x, int y, int width, int height, float scale, int fg, int bg) {
        super(x, y, scale); this.width=width; this.height=height; this.fg=fg; this.bg=bg; this.progress=0f;
    }
    public void setProgress(float p){ this.progress=Math.max(0f, Math.min(1f,p)); }
    public void setColors(int fg, int bg){ this.fg=fg; this.bg=bg; }
    public int getFgColor(){ return fg; }
    public int getBgColor(){ return bg; }
    public void setSize(int w, int h){ this.width = w; this.height = h; }
    public int getWidthPx() { return Math.round(width * scale); }
    public int getHeightPx() { return Math.round(height * scale); }
    @Override
    public void draw(DrawContext ctx) {
        if (!visible) return;
        var m = ctx.getMatrices(); m.push(); m.translate(x, y, 0); m.scale(scale, scale, 1.0f);
        if (bg>=0) ctx.fill(0,0,width,height,bg);
        if (fg>=0) ctx.fill(0,0,Math.round(width*progress),height,fg);
        m.pop();
    }
}