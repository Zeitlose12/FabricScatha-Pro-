package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;

public class V2Spacing extends V2Element {
    private int width; private int height;
    public V2Spacing(int x, int y, int width, int height) {
        super(x, y, 1.0f);
        this.width = width; this.height = height;
    }
    @Override
    public void draw(DrawContext ctx) { /* spacer: nichts zeichnen */ }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}