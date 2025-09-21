package namelessju.scathapro.fabric.overlay;

import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Container für OverlayElemente mit einfachem Layout (Vertical/Horizontal).
 */
public class DynamicOverlayContainer extends OverlayElement {
    public enum Direction { HORIZONTAL, VERTICAL }

    private final List<OverlayElement> children = new ArrayList<>();
    private Direction direction = Direction.VERTICAL;
    private int gap = 4;

    public DynamicOverlayContainer(int x, int y, float scale) {
        super(x, y, scale);
    }

    public DynamicOverlayContainer setDirection(Direction d) { this.direction = d; return this; }
    public DynamicOverlayContainer setGap(int g) { this.gap = Math.max(0, g); return this; }
    public DynamicOverlayContainer add(OverlayElement e) { if (e != null) children.add(e); return this; }
    public DynamicOverlayContainer clear() { children.clear(); return this; }

    @Override
    public void draw(DrawContext ctx) {
        if (!visible) return;
        var m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(scale, scale, 1.0f);
        int offX = 0, offY = 0;
        for (OverlayElement c : children) {
            if (c == null || !c.isVisible()) continue;
            // lokale Verschiebung
            var inner = ctx.getMatrices();
            inner.push();
            inner.translate(offX, offY, 0);
            c.draw(ctx);
            inner.pop();
            // einfachen Fluss-Layout anwenden
            if (direction == Direction.VERTICAL) offY += estimateHeight(c) + gap; else offX += estimateWidth(c) + gap;
        }
        m.pop();
    }

    private int estimateWidth(OverlayElement e) {
        if (e instanceof OverlayText t) return 100; // grobe Heuristik ohne TextRenderer-Zugriff
        if (e instanceof OverlayProgressBar p) return 120;
        if (e instanceof OverlayImage i) return i.srcW;
        return 16;
    }

    private int estimateHeight(OverlayElement e) {
        if (e instanceof OverlayText t) return 12;
        if (e instanceof OverlayProgressBar p) return 8;
        if (e instanceof OverlayImage i) return i.srcH;
        return 12;
    }
}