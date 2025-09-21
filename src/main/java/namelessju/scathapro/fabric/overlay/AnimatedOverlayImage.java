package namelessju.scathapro.fabric.overlay;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * Ein animiertes Bild-Element. Unterstützt Frame-Listen mit fixem Frame-Intervall.
 */
public class AnimatedOverlayImage extends OverlayImage {
    private Identifier[] frames;
    private int current;
    private long frameDurationMs;
    private long lastSwitch;
    private boolean playing = true;

    public AnimatedOverlayImage(Identifier[] frames, int x, int y, float scale, int texW, int texH, long frameDurationMs) {
        super(frames != null && frames.length > 0 ? frames[0] : null, x, y, scale, texW, texH);
        this.frames = frames != null ? frames.clone() : new Identifier[0];
        this.frameDurationMs = Math.max(16, frameDurationMs);
        this.current = 0;
        this.lastSwitch = System.currentTimeMillis();
    }

    public void setPlaying(boolean p) { this.playing = p; }
    public void setFrameDuration(long ms) { this.frameDurationMs = Math.max(16, ms); }
    public void setFrames(Identifier[] f) { this.frames = f != null ? f.clone() : new Identifier[0]; this.current = 0; }

    @Override
    public void draw(DrawContext ctx) {
        if (!visible || frames == null || frames.length == 0) return;
        // Framewechsel
        if (playing) {
            long now = System.currentTimeMillis();
            if (now - lastSwitch >= frameDurationMs) {
                current = (current + 1) % frames.length;
                this.texture = frames[current];
                lastSwitch = now;
            }
        }
        super.draw(ctx);
    }
}