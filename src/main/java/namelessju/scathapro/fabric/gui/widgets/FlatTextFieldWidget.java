package namelessju.scathapro.fabric.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class FlatTextFieldWidget extends TextFieldWidget {
    private final int bg = 0xFF1E1E1E;
    private final int border = 0xFF3B3B3B;

    public FlatTextFieldWidget(int x, int y, int width, int height, String placeholder) {
        super(MinecraftClient.getInstance().textRenderer, x, y, width, height, Text.literal(placeholder));
        this.setDrawsBackground(false);
    }

    @Override
    public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int x1=getX(), y1=getY(), x2=getX()+getWidth(), y2=getY()+getHeight();
        // Hintergrund minimal höher ziehen, damit optisch auf einer Zeile mit FlatButtons
        ctx.fill(x1, y1-1, x2, y2-1, bg);
        ctx.fill(x1, y1-1, x2, y1, border);
        ctx.fill(x1, y2-2, x2, y2-1, border);
        ctx.fill(x1, y1-1, x1+1, y2-1, border);
        ctx.fill(x2-1, y1-1, x2, y2-1, border);
        super.renderWidget(ctx, mouseX, mouseY, delta);
    }
}