package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;

/**
 * Einfache horizontale Box: zeichnet Kinder von links nach rechts mit spacing.
 * Breiten werden heuristisch ermittelt (Textbreite, 16px bei Icons, Barbreite).
 */
public class V2HBox extends V2Container {
    private int spacing = 8;
    public V2HBox(int x, int y, int spacing) { super(x, y); this.spacing = spacing; }

    private int preferredWidth(V2Element e){
        if (e instanceof V2Text t) return t.getTextWidth();
        if (e instanceof V2ProgressBar b) return b.getWidthPx();
        return (int)(16 * e.getScale());
    }
    private int preferredHeight(V2Element e){
        if (e instanceof V2Text t) return t.getTextHeight();
        if (e instanceof V2ProgressBar b) return b.getHeightPx();
        return (int)(16 * e.getScale());
    }

    @Override
    public void draw(DrawContext ctx) {
        if (!isVisible()) return;
        var m = ctx.getMatrices();
        m.push(); m.translate(getX(), getY(), 0);
        int x = 0;
        int baseH = 0;
        for (var c : getChildren()) {
            c.setPosition(x, 0);
            c.draw(ctx);
            x += preferredWidth(c) + spacing;
            baseH = Math.max(baseH, preferredHeight(c));
        }
        m.pop();
    }
}