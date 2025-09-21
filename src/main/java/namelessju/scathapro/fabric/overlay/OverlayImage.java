package namelessju.scathapro.fabric.overlay;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

/**
 * Einfaches Bild-Element für das Overlay. Unterstützt Skalierung und Transparenz.
 */
public class OverlayImage extends OverlayElement {
    protected Identifier texture;
    protected int texW;
    protected int texH;
    protected int srcW;
    protected int srcH;
    protected int tint = 0xFFFFFFFF; // ARGB

    public OverlayImage(Identifier texture, int x, int y, float scale, int texW, int texH) {
        super(x, y, scale);
        this.texture = texture;
        this.texW = texW;
        this.texH = texH;
        this.srcW = texW;
        this.srcH = texH;
    }

    public void setTexture(Identifier id, int texW, int texH) {
        this.texture = id; this.texW = texW; this.texH = texH; this.srcW = texW; this.srcH = texH;
    }

    public void setSourceSize(int w, int h) { this.srcW = w; this.srcH = h; }
    public void setTint(int argb) { this.tint = argb; }

    @Override
    public void draw(DrawContext ctx) {
        if (!visible || texture == null) return;
        var m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(scale, scale, 1.0f);
        try {
            // Zeichne die gesamte Textur (Region = volle Größe), Matrix übernimmt Skalierung
            ctx.drawTexture(RenderLayer::getGuiTextured, texture, 0, 0, 0f, 0f, srcW, srcH, texW, texH);
        } catch (Throwable ignored) {}
        m.pop();
    }
}