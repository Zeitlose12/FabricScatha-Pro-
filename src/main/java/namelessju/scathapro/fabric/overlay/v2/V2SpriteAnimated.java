package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

/**
 * Einfache Sprite-Sheet-Animation. Frames liegen horizontal nebeneinander.
 */
public class V2SpriteAnimated extends V2Element {
    private final Identifier texture;
    private final int texW, texH; // Gesamtgröße Sprite-Sheet
    private final int frameW, frameH; // Framegröße
    private final int frames; // Anzahl Frames
    private final int fps; // Frames pro Sekunde

    public V2SpriteAnimated(Identifier texture, int x, int y, float scale, int texW, int texH, int frameW, int frameH, int frames, int fps) {
        super(x, y, scale);
        this.texture = texture; this.texW=texW; this.texH=texH; this.frameW=frameW; this.frameH=frameH; this.frames=frames; this.fps=fps;
    }

    @Override
    public void draw(DrawContext ctx) {
        if (!visible) return;
        long t = System.currentTimeMillis();
        int frame = (int)((t / (1000 / Math.max(1,fps))) % Math.max(1, frames));
        int u = frame * frameW;
        var m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        // Normalisiere auf 16px Kante, dann skaliere
        float k = (16f / Math.max(frameW, frameH)) * scale;
        m.scale(k, k, 1.0f);
        ctx.drawTexture(RenderLayer::getGuiTextured, texture, 0, 0, (float)u, 0f, frameW, frameH, texW, texH);
        m.pop();
    }
}