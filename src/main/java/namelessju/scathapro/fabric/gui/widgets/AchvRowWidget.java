package namelessju.scathapro.fabric.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

/**
 * Achievement-Listenzeile mit Titel, Beschreibung, Datum und Fortschrittsbalken.
 */
public class AchvRowWidget extends PressableWidget {
    private final String title;
    private final String description;
    private final String dateRight; // kann leer sein
    private final int current;
    private final int goal;
    private final boolean completed;

    public AchvRowWidget(int x, int y, int width, int height,
                         String title, String description, String dateRight,
                         int current, int goal, boolean completed) {
        super(x, y, width, height, Text.literal(""));
        this.title = title;
        this.description = description;
        this.dateRight = dateRight == null ? "" : dateRight;
        this.current = Math.max(0, current);
        this.goal = Math.max(1, goal);
        this.completed = completed;
        this.active = false; // reine Anzeige
    }

    @Override
    public void onPress() { /* no-op */ }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int x1 = getX(), y1 = getY(), w = getWidth(), h = getHeight();
        int x2 = x1 + w, y2 = y1 + h;
        // Hintergrund & Rahmen (kompakter Stil)
        int bg = 0xFF141414;
        int border = 0xFF303030;
        ctx.fill(x1, y1, x2, y2, bg);
        ctx.fill(x1, y1, x2, y1+1, border); // top
        ctx.fill(x1, y2-1, x2, y2, border); // bottom

        var tr = MinecraftClient.getInstance().textRenderer;
        // Titel links, kräftige Farbe
        int titleColor = completed ? 0xFF32FF6A : 0xFFB8FFA6;
        ctx.drawText(tr, Text.literal(title), x1 + 8, y1 + 4, titleColor, false);
        // Datum rechts
        if (!dateRight.isEmpty()) {
            int tw = tr.getWidth(dateRight);
            ctx.drawText(tr, Text.literal(dateRight), x2 - 8 - tw, y1 + 4, 0xFFB0B0B0, false);
        }
        // Beschreibung unter Titel
        if (description != null && !description.isEmpty()) {
            ctx.drawText(tr, Text.literal(description), x1 + 8, y1 + 16, 0xFFB0B0B0, false);
        }
        // Fortschritt rechts als "c/g"
        String right = current + "/" + goal;
        int rightColor = completed ? 0xFF32FF6A : 0xFFFFE35C;
        int rw = tr.getWidth(right);
        ctx.drawText(tr, Text.literal(right), x2 - 8 - rw, y1 + 18, rightColor, false);

        // Fortschrittsbalken (kompakter, 3px hoch)
        float ratio = Math.min(1f, (float) current / (float) goal);
        int barBg = 0xFF2A2A2A;
        int barFill = completed ? 0xFF33CC66 : 0xFFFFCC33;
        int bx1 = x1 + 8, bx2 = x2 - 8, by1 = y1 + h - 7, by2 = by1 + 3;
        ctx.fill(bx1, by1, bx2, by2, barBg);
        int filled = bx1 + Math.round((bx2 - bx1) * ratio);
        ctx.fill(bx1, by1, filled, by2, barFill);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, Text.literal(title));
    }
}
