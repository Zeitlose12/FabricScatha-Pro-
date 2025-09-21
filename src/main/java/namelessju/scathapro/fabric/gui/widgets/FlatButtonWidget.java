package namelessju.scathapro.fabric.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

public class FlatButtonWidget extends PressableWidget {
    @FunctionalInterface
    public interface OnPress { void onPress(FlatButtonWidget button); }

    private final OnPress onPress;
    private int bgColor = 0xFF222222;
    private int hoverColor = 0xFF2E2E2E;
    private int disabledColor = 0xFF151515;
    private int borderColor = 0xFF3B3B3B;
    private int textColor = 0xFFEFEFEF;

    public FlatButtonWidget(int x, int y, int width, int height, Text message, OnPress onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    public void setText(Text text) { this.setMessage(text); }

    public FlatButtonWidget colors(int bg, int hover, int border, int text) {
        this.bgColor = bg; this.hoverColor = hover; this.borderColor = border; this.textColor = text; return this;
    }

    @Override
    public void onPress() { if (onPress != null) onPress.onPress(this); }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int c = !this.active ? disabledColor : (this.isHovered() ? hoverColor : bgColor);
        // Hintergrund
        ctx.fill(getX(), getY(), getX()+getWidth(), getY()+getHeight(), c);
        // Rahmen (1px)
        int x1=getX(), y1=getY(), x2=getX()+getWidth(), y2=getY()+getHeight();
        ctx.fill(x1, y1, x2, y1+1, borderColor);
        ctx.fill(x1, y2-1, x2, y2, borderColor);
        ctx.fill(x1, y1, x1+1, y2, borderColor);
        ctx.fill(x2-1, y1, x2, y2, borderColor);
        // Text mittig
        var tr = MinecraftClient.getInstance().textRenderer;
        int tw = tr.getWidth(this.getMessage());
        int tx = getX() + (getWidth()-tw)/2;
        int ty = getY() + (getHeight()-9)/2; // 9px Fonthöhe
        ctx.drawText(tr, this.getMessage(), tx, ty, this.active? textColor : 0xFF9E9E9E, false);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }
}
