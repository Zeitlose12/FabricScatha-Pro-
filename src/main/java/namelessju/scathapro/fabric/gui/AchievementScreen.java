package namelessju.scathapro.fabric.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.achievements.FabricAchievementManager;
import namelessju.scathapro.fabric.gui.widgets.FlatButtonWidget;
import namelessju.scathapro.fabric.gui.widgets.FlatToggleWidget;

/**
 * Achievement-Screen - zeigt alle Achievements und deren Fortschritt an
 */
public class AchievementScreen extends Screen {
    private final Screen parent;
    private int panelX, panelY, panelW, panelH;
    private int lastLayoutW, lastLayoutH;
    private int scrollY = 0;
    private int maxScroll = 0;
    private namelessju.scathapro.fabric.gui.widgets.FlatButtonWidget closeButton;
    private FabricAchievementManager achievementManager;
    // Ein-/Ausklapp-Zustand pro Kategorie
    private final java.util.EnumMap<namelessju.scathapro.fabric.achievements.FabricAchievementCategory, Boolean> collapsed =
            new java.util.EnumMap<>(namelessju.scathapro.fabric.achievements.FabricAchievementCategory.class);
    
    public AchievementScreen(Screen parent) {
        super(Text.literal("Scatha-Pro Achievements"));
        this.parent = parent;
        
        var scathaPro = FabricScathaPro.getInstance();
        if (scathaPro != null) {
            this.achievementManager = scathaPro.getAchievementManager();
        }
    }
    
    @Override
    protected void init() {
        rebuildAll();
    }
    
    private void rebuildAll() {
        this.clearChildren();
        computePanelBounds();
        addAchievementContent();
        lastLayoutW = panelW; 
        lastLayoutH = panelH;
    }
    
    private void addAchievementContent() {
        int left = panelX + 24;
        int top = panelY + 48;
        
        // Scroll-Offset anwenden
        top -= scrollY;
        
        // Kopfzeile: Statistiken
        var stats = achievementManager != null ? achievementManager.calculateStats() : null;
        String headerText;
        if (stats != null) {
            headerText = String.format("§aUnlocked: §6%d/%d §c(visible: %d/%d)§r", 
                stats.unlockedCount, stats.totalAchievements, stats.unlockedVisible, stats.visibleAchievements);
        } else {
            headerText = "§aUnlocked: §6-/ - (stats unavailable)";
        }
        addDrawableChild(new FlatButtonWidget(left, top, Math.max(300, panelW - 48), 20, Text.literal(headerText), btn -> {})).active = false;
        top += 25;
        
        // Kategorien als zwei Spalten rendern
        var categories = namelessju.scathapro.fabric.achievements.FabricAchievementCategory.values();
        var all = namelessju.scathapro.fabric.achievements.FabricAchievement.values();
        int gap = 16;
        int colW = (panelW - 48 - gap) / 2;
        int xL = left;
        int xR = left + colW + gap;
        int yL = top - scrollY; // zwecks Konsistenz mit Settings: top ist panelY+48, wir wollen scroll berücksichtigen
        int yR = top - scrollY;
        boolean toLeft = true;
        int catBottomGap = 18; // Mehr Abstand zwischen Kategorien

        for (var cat : categories) {
            int x = toLeft ? xL : xR;
            int y = toLeft ? yL : yR;
            // Collapse-State lesen
            boolean isCollapsed = Boolean.TRUE.equals(collapsed.get(cat));
            String caret = isCollapsed ? "▶ " : "▼ ";
            // Kategorie-Header (klickbar zum Ein-/Ausklappen)
            addDrawableChild(new FlatButtonWidget(x, y, colW, 20, Text.literal("§6§n" + caret + cat.getName() + " Achievements"), b -> {
                boolean nv = !Boolean.TRUE.equals(collapsed.get(cat));
                collapsed.put(cat, nv);
                rebuildAll();
            }));
            y += 28; // etwas mehr Abstand nach dem Header
            
            if (!isCollapsed) {
                // Rows kompakt (46px)
                for (var a : all) {
                    if (a.category != cat) continue;
                    boolean unlocked = achievementManager != null && achievementManager.isAchievementUnlocked(a);
                    boolean visible = a.type.isVisible() || unlocked;
                    if (!visible) continue;
                    String dateStr = "";
                    int cur = (int)a.getClampedProgress();
                    int goal = (int)a.goal;
                    if (unlocked && achievementManager != null) {
                        var ua = achievementManager.getUnlockedAchievement(a);
                        if (ua != null) {
                            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd.MM.yy HH:mm");
                            dateStr = df.format(new java.util.Date(ua.getUnlockTimestamp()));
                            cur = goal;
                        }
                    }
                    String name = a.achievementName;
                    String desc = a.description != null ? a.description : "";
                    addDrawableChild(new namelessju.scathapro.fabric.gui.widgets.AchvRowWidget(x, y, colW, 46, name, desc, dateStr, cur, goal, unlocked));
                    y += 48;
                }
            }
            y += catBottomGap; // zusätzlicher Abstand zwischen Kategorien
            // schreibe zurück in die passende Spalte
            if (toLeft) yL = y; else yR = y;
            toLeft = !toLeft;
        }
        // Scroll-Maximum anhand der tieferen Spalte
        int usedBottom = Math.max(yL, yR);
        int visibleBottom = panelY + panelH - 56;
        maxScroll = Math.max(0, (usedBottom + scrollY) - visibleBottom);

        // Schließen-Button referenzieren
        closeButton = new FlatButtonWidget(panelX + (panelW-100)/2, panelY + panelH - 36, 100, 20, Text.literal("Close"), btn -> close());
        addDrawableChild(closeButton);
    }
    
    private void drawAchievement(int x, int y, namelessju.scathapro.fabric.achievements.FabricAchievement a,
                                  String name, String description, String date, String progress, boolean completed) {
        // Nicht mehr genutzt – ersetzt durch AchvRowWidget
    }
    
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Hintergrund und Panel – kein Vanilla-Blur, stattdessen eigene Abdunklung
        ctx.fill(0, 0, this.width, this.height, 0xEE000000);
        
        computePanelBounds();
        if (panelW != lastLayoutW || panelH != lastLayoutH) {
            rebuildAll();
        }
        
        // Panel zeichnen
        ctx.fill(panelX, panelY, panelX+panelW, panelY+panelH, 0xFF242424);
        ctx.fill(panelX+2, panelY+2, panelX+panelW-2, panelY+panelH-2, 0xFF2C2C2C);
        ctx.fill(panelX+2, panelY+2, panelX+panelW-2, panelY+3, 0x40FFFFFF);
        
        // Titel
        var tr = this.textRenderer;
        ctx.drawText(tr, Text.literal("SCATHA-PRO ACHIEVEMENTS"), panelX + 24, panelY + 20, 0xFFEFEFEF, false);
        
        // Body clppen (wie im SettingsScreen)
        int clipLeft = panelX + 4;
        int clipTop = panelY + 46;
        int clipRight = panelX + panelW - 4;
        int clipBottom = panelY + panelH - 48;
        try { ctx.enableScissor(clipLeft, clipTop, clipRight, clipBottom); } catch (Throwable ignored) {}
        super.render(ctx, mouseX, mouseY, delta);
        try { ctx.disableScissor(); } catch (Throwable ignored) {}
        // Close-Button nach dem Clipping obenauf zeichnen
        if (closeButton != null) closeButton.render(ctx, mouseX, mouseY, delta);
    }
    
    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int step = 24;
        scrollY = Math.max(0, Math.min(maxScroll, scrollY - (int)(verticalAmount * step)));
        rebuildAll();
        return true;
    }
    
    private void computePanelBounds() {
        panelW = Math.min(Math.max(640, this.width - 120), 1000);
        panelH = Math.min(Math.max(420, this.height - 120), 640);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;
    }
}
