package namelessju.scathapro.fabric.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;
import java.util.function.Consumer;

public class FlatSliderWidget extends PressableWidget {
    private final double min;
    private final double max;
    private double value; // current value
    private final String label;
    private final Consumer<Double> onChange;
    private boolean dragging = false;

    // colors
    private final int trackBg = 0xFF2A2A2A;
    private final int trackFill = 0xFF3C3C3C;
    private final int border = 0xFF3B3B3B;
    private final int textColor = 0xFFEFEFEF;
    private final int thumbColor = 0xFF5A5A5A;

    public FlatSliderWidget(int x, int y, int width, int height, String label, double min, double max, double initial, Consumer<Double> onChange) {
        super(x, y, width, height, Text.literal(""));
        this.label = label;
        this.min = min;
        this.max = max;
        this.value = clamp(initial);
        this.onChange = onChange;
    }

    public double getValue() { return value; }
    public void setValue(double v) { this.value = clamp(v); if (onChange != null) onChange.accept(this.value); }

    private double clamp(double v) { return Math.max(min, Math.min(max, v)); }
    private double norm() { return (value - min) / (max - min); }

    @Override
    public void onPress() {
        this.dragging = true;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (!dragging) return;
        double nx = (mouseX - getX()) / (double) getWidth();
        nx = Math.max(0.0, Math.min(1.0, nx));
        double newVal = min + nx * (max - min);
        if (newVal != value) {
            value = newVal;
            if (onChange != null) onChange.accept(value);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        dragging = false;
    }

    @Override
    public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int x1=getX(), y1=getY(), x2=getX()+getWidth(), y2=getY()+getHeight();
        // track bg
        ctx.fill(x1, y1, x2, y2, trackBg);
        // border
        ctx.fill(x1, y1, x2, y1+1, border);
        ctx.fill(x1, y2-1, x2, y2, border);
        ctx.fill(x1, y1, x1+1, y2, border);
        ctx.fill(x2-1, y1, x2, y2, border);
        // fill portion
        int fillW = (int) Math.round(getWidth() * norm());
        if (fillW > 0) ctx.fill(x1+1, y1+1, x1+fillW-1, y2-1, trackFill);
        // thumb
        int thumbX = x1 + Math.max(1, Math.min(getWidth()-6, (int)Math.round(getWidth()*norm()) - 3));
        ctx.fill(thumbX, y1+2, thumbX+6, y2-2, thumbColor);
        // label text
        var tr = MinecraftClient.getInstance().textRenderer;
        String txt = String.format("%s: %.2f", label, value);
        int tw = tr.getWidth(txt);
        int tx = x1 + (getWidth()-tw)/2;
        int ty = y1 + (getHeight()-9)/2;
        ctx.drawText(tr, Text.literal(txt), tx, ty, textColor, false);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, Text.literal(label));
    }
}
