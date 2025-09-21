package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * Scatha-Icon mit Googly Eyes (einfache, spaßige Animation).
 */
public class V2GooglyScatha extends V2Element {
    private final V2IconTexture base;
    private final V2IconTexture eyeOuter;
    private final V2IconTexture eyeInner;

    public V2GooglyScatha(int x, int y, float scale) {
        super(x, y, scale);
        base = new V2IconTexture(Identifier.of("scathapro","textures/overlay/scatha.png"), 0, 0, 1.0f, 512, 512);
        eyeOuter = new V2IconTexture(Identifier.of("scathapro","textures/overlay/googly_eye_outer.png"), 0, 0, 1.0f, 64, 64);
        eyeInner = new V2IconTexture(Identifier.of("scathapro","textures/overlay/googly_eye_inner.png"), 0, 0, 1.0f, 64, 64);
    }

    @Override
    public void draw(DrawContext ctx) {
        if (!visible) return;
        var m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(scale, scale, 1.0f);

        // Basis-Icon (skaliert auf 16x16 über V2IconTexture)
        base.draw(ctx);

        // Einfache Googly-Animation: Pupillen leicht versetzt per Zeitfunktion
        long t = System.currentTimeMillis();
        float px = (float)Math.sin(t * 0.006) * 1.5f;
        float py = (float)Math.cos(t * 0.004) * 1.2f;

        // Positionen relativ zu 16x16 Fläche
        int lx = 5, ly = 6; // linkes Auge
        int rx = 10, ry = 6; // rechtes Auge

        // Äußere Augen
        m.push(); m.translate(lx, ly, 0); eyeOuter.draw(ctx); m.pop();
        m.push(); m.translate(rx, ry, 0); eyeOuter.draw(ctx); m.pop();
        // Pupillen
        m.push(); m.translate(lx + px, ly + py, 0); eyeInner.draw(ctx); m.pop();
        m.push(); m.translate(rx + px, ry + py, 0); eyeInner.draw(ctx); m.pop();

        m.pop();
    }
}