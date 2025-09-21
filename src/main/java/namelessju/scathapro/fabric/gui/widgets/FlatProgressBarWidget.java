package namelessju.scathapro.fabric.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

/**
 * Einfache, nicht interaktive Fortschrittsleiste im Flat-Stil.
 */
public class FlatProgressBarWidget extends PressableWidget {
    private float value; // 0.0 - 1.0

    private final int bg = 0xFF2A2A2A;
    private final int border = 0xFF3B3B3B;
    private final int fill = 0xFF33CC66; // grün
    private final int textColor = 0xFFEFEFEF;

    private final boolean showPercentText;

    public FlatProgressBarWidget(int x, int y, int width, int height, float value) {
        this(x, y, width, height, value, false);
    }

    public FlatProgressBarWidget(int x, int y, int width, int height, float value, boolean showPercentText) {
        super(x, y, width, height, Text.literal(""));
        this.value = clamp(value);
        this.showPercentText = showPercentText;
        this.active = false; // nicht interaktiv
    }

    public void setValue(float v) { this.value = clamp(v); }
    public float getValue() { return value; }

    private float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }

    @Override
    public void onPress() { /* no-op */ }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int x1 = getX(), y1 = getY(), x2 = getX()+getWidth(), y2 = getY()+getHeight();
        // Hintergrund + Rahmen
        ctx.fill(x1, y1, x2, y2, bg);
        ctx.fill(x1, y1, x2, y1+1, border);
        ctx.fill(x1, y2-1, x2, y2, border);
        ctx.fill(x1, y1, x1+1, y2, border);
        ctx.fill(x2-1, y1, x2, y2, border);
        // Füllung
        int fillW = Math.max(0, Math.min(getWidth()-2, Math.round((getWidth()-2) * value)));
        if (fillW > 0) ctx.fill(x1+1, y1+1, x1+1+fillW, y2-1, fill);
        // Optional Prozenttext
        if (showPercentText) {
            var tr = MinecraftClient.getInstance().textRenderer;
            String t = String.format("%d%%", Math.round(value * 100f));
            int tw = tr.getWidth(t);
            int tx = x1 + (getWidth()-tw)/2;
            int ty = y1 + (getHeight()-9)/2;
            ctx.drawText(tr, Text.literal(t), tx, ty, textColor, false);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, Text.literal("Progress"));
    }
}
