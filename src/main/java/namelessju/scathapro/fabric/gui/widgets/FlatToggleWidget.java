package namelessju.scathapro.fabric.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;
import java.util.function.Consumer;

public class FlatToggleWidget extends PressableWidget {
    private boolean value;
    private final String label;
    private final Consumer<Boolean> onChange;

    private int bgOff = 0xFF222222;
    private int bgOn = 0xFF2A2A2A;
    private int bgHover = 0xFF2E2E2E;
    private int border = 0xFF3B3B3B;
    private int textColor = 0xFFEFEFEF;

    public FlatToggleWidget(int x, int y, int width, int height, String label, boolean initial, Consumer<Boolean> onChange) {
        super(x, y, width, height, Text.literal(""));
        this.label = label;
        this.value = initial;
        this.onChange = onChange;
        updateMessage();
    }

    public boolean getValue() { return value; }
    public void setValue(boolean v) { this.value = v; updateMessage(); }

    private void updateMessage() { this.setMessage(Text.literal(label + ": " + (value ? "ON" : "OFF"))); }

    @Override
    public void onPress() {
        if (!this.active) return;
        this.value = !this.value;
        updateMessage();
        if (onChange != null) onChange.accept(this.value);
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int bg = value ? bgOn : bgOff;
        if (this.isHovered()) bg = bgHover;
        int x1=getX(), y1=getY(), x2=getX()+getWidth(), y2=getY()+getHeight();
        ctx.fill(x1, y1, x2, y2, bg);
        ctx.fill(x1, y1, x2, y1+1, border);
        ctx.fill(x1, y2-1, x2, y2, border);
        ctx.fill(x1, y1, x1+1, y2, border);
        ctx.fill(x2-1, y1, x2, y2, border);
        var tr = MinecraftClient.getInstance().textRenderer;
        int tw = tr.getWidth(this.getMessage());
        int tx = getX() + (getWidth()-tw)/2;
        int ty = getY() + (getHeight()-9)/2;
        ctx.drawText(tr, this.getMessage(), tx, ty, this.active? textColor : 0xFF9E9E9E, false);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }
}
