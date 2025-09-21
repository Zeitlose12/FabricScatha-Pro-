package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;

public class V2Container extends V2Element {
    private final List<V2Element> children = new ArrayList<>();
    public V2Container(int x, int y) { super(x, y, 1.0f); }
    public V2Container add(V2Element e){ children.add(e); return this; }
    public List<V2Element> getChildren(){ return children; }
    public void clearChildren(){ children.clear(); }
    @Override
    public void draw(DrawContext ctx) {
        if (!visible) return;
        var m = ctx.getMatrices(); m.push(); m.translate(x,y,0);
        for (var c: children) c.draw(ctx);
        m.pop();
    }
}