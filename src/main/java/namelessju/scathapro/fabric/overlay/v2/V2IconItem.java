package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class V2IconItem extends V2Element {
    private final ItemStack stack;
    public V2IconItem(ItemStack stack, int x, int y, float scale) {
        super(x, y, scale);
        this.stack = stack;
    }

    @Override
    public void draw(DrawContext ctx) {
        if (!visible || stack == null) return;
        var m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(scale, scale, 1.0f);
        ctx.drawItem(stack, 0, 0);
        m.pop();
    }
}