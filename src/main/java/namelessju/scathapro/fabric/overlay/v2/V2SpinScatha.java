package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class V2SpinScatha extends V2Element {
    private final V2IconTexture base;
    public V2SpinScatha(int x, int y, float scale) {
        super(x, y, scale);
        base = new V2IconTexture(Identifier.of("scathapro","textures/overlay/scatha.png"), 0, 0, 1.0f, 512, 512);
    }
    @Override
    public void draw(DrawContext ctx) {
        if (!visible) return;
        var m = ctx.getMatrices();
        m.push();
        m.translate(x + 8, y + 8, 0);
        float angle = (System.currentTimeMillis() % 4000L) * 360f / 4000f;
        m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));
        m.translate(-8, -8, 0);
        m.scale(scale, scale, 1.0f);
        base.draw(ctx);
        m.pop();
    }
}