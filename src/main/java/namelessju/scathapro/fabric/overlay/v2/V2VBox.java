package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;

/**
 * Einfache vertikale Box: zeichnet Kinder von oben nach unten mit spacing.
 */
public class V2VBox extends V2Container {
    private int spacing = 4;
    public V2VBox(int x, int y, int spacing) { super(x, y); this.spacing = spacing; }

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
        int y = 0;
        for (var c : getChildren()) {
            c.setPosition(0, y);
            c.draw(ctx);
            y += preferredHeight(c) + spacing;
        }
        m.pop();
    }
}