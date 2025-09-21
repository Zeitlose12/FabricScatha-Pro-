package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

/**
 * Zeichnet eine GUI-Textur quadratisch skaliert (niemals gestretched).
 * Die angegebene texW/texH sollte der nativen Texturgröße entsprechen.
 * Falls unbekannt, kann ein grober Wert (z.B. 512) genutzt werden; die
 * Skalierung orientiert sich immer an der größeren Kante.
 */
public class V2IconTexture extends V2Element {
    private final Identifier texture;
    private final int texW;
    private final int texH;

    public V2IconTexture(Identifier texture, int x, int y, float scale, int texW, int texH) {
        super(x, y, scale);
        this.texture = texture;
        this.texW = Math.max(1, texW);
        this.texH = Math.max(1, texH);
    }

    @Override
    public void draw(DrawContext ctx) {
        if (!visible || texture == null) return;
        var m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        float k = (16f / Math.max(texW, texH)) * scale; // auf 16x16 normieren
        m.scale(k, k, 1.0f);
        ctx.drawTexture(RenderLayer::getGuiTextured, texture, 0, 0, 0f, 0f, texW, texH, texW, texH);
        m.pop();
    }
}