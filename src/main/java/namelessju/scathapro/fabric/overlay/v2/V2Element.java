package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;

public abstract class V2Element {
    protected int x;
    protected int y;
    protected float scale;
    protected boolean visible = true;

    public V2Element(int x, int y, float scale) {
        this.x = x; this.y = y; this.scale = scale;
    }

    public abstract void draw(DrawContext ctx);

    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public void setScale(float s) { this.scale = s; }
    public float getScale() { return this.scale; }
    public void setVisible(boolean v) { this.visible = v; }
    public boolean isVisible() { return visible; }

    public int getX() { return x; }
    public int getY() { return y; }
}