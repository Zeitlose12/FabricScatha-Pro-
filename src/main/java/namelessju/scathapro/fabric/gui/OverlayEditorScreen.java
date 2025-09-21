package namelessju.scathapro.fabric.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import namelessju.scathapro.fabric.gui.widgets.FlatButtonWidget;
import namelessju.scathapro.fabric.gui.widgets.FlatToggleWidget;
import namelessju.scathapro.fabric.gui.widgets.FlatSliderWidget;
import namelessju.scathapro.fabric.gui.widgets.FlatTextFieldWidget;

public class OverlayEditorScreen extends Screen {
    private final Screen parent;
    private String selected = "title";
    private int panelX, panelY, panelW, panelH;
    private int listScroll = 0;
    private final int itemHeight = 22;
    private String[] itemKeys = new String[]{};
    private FlatTextFieldWidget colorField;
    private FlatTextFieldWidget posXField;
    private FlatTextFieldWidget posYField;
    private FlatTextFieldWidget bgColorField;
    private FlatTextFieldWidget bgBorderField;
    private FlatTextFieldWidget barFgField;
    private FlatTextFieldWidget barBgField;
    private FlatSliderWidget scaleSlider;
    private FlatToggleWidget visibleToggle;

    public OverlayEditorScreen(Screen parent) {
        super(Text.literal("Scatha-Pro Overlay Editor"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        computePanel();
        buildUi();
    }

    private void computePanel() {
        panelW = Math.min(Math.max(720, this.width - 80), this.width - 40);
        panelH = Math.min(Math.max(360, this.height - 80), this.height - 40);
        panelX = (this.width - panelW) / 2; panelY = (this.height - panelH) / 2;
    }

    private void buildUi() {
        this.clearChildren();
        // Hintergrund
        // Sidebar links (Komponentenliste)
        int left = panelX + 16; int top = panelY + 40;
        addDrawableChild(new FlatButtonWidget(panelX + panelW - 120, panelY + panelH - 32, 100, 20, Text.literal("Close"), b -> close()));
        addDrawableChild(new FlatButtonWidget(panelX + panelW - 230, panelY + panelH - 32, 100, 20, Text.literal("Load"), b -> loadModel()));
        addDrawableChild(new FlatButtonWidget(panelX + panelW - 340, panelY + panelH - 32, 100, 20, Text.literal("Save"), b -> saveModel()));

        itemKeys = new String[]{
            "background","title","headerPets","headerWorms","headerScathas","headerTotal",
            "petsBlue","petsPurple","petsOrange","wormsCount","scathasCount","totalCount","totalPercent",
            "scathaText","wormText","totalText","streakText","wormTimerText","scathaTimerText","dayTimeText","coordsText","bar"
        };
        int listHeight = panelH - 100; // Platz bis unten
        int visible = Math.max(1, listHeight / itemHeight);
        listScroll = Math.max(0, Math.min(listScroll, Math.max(0, itemKeys.length - visible)));
        // Sidebar Hintergrund
        ctxFillPanel(left-8, top-12, 200, visible*itemHeight + 16);
        for (int i=0;i<visible;i++){
            int idx = listScroll + i; if (idx >= itemKeys.length) break;
            String key = itemKeys[idx];
            int y = top + i*itemHeight;
            addDrawableChild(new FlatButtonWidget(left, y, 180, 20, Text.literal(key + (key.equals(selected)?"  <":"")), btn -> { selected = key; buildUi(); }));
        }

        // Property Panel rechts
        int px = panelX + 220; int py = panelY + 40;
        var r = namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer();
        var m = r != null ? r.getModel() : null;
        drawPropertyPanel(px, py, m);
    }

    private void drawPropertyPanel(int x, int y, namelessju.scathapro.fabric.overlay.v2.V2Model m) {
        if (m == null) return;
        // Position (X/Y)
        var e = getElement(m, selected);
        int curX = e != null ? e.getX() : 0;
        int curY = e != null ? e.getY() : 0;
        posXField = new FlatTextFieldWidget(x, y, 80, 20, "X"); posXField.setText(Integer.toString(curX)); addDrawableChild(posXField);
        posYField = new FlatTextFieldWidget(x+88, y, 80, 20, "Y"); posYField.setText(Integer.toString(curY)); addDrawableChild(posYField);
        addDrawableChild(new FlatButtonWidget(x+176, y, 70, 20, Text.literal("Apply"), b -> {
            try { int nx = Integer.parseInt(posXField.getText()); int ny = Integer.parseInt(posYField.getText()); setPosition(m, selected, nx, ny); } catch (Exception ignored) {}
        })); y+=24;
        // Sichtbarkeit
        boolean visible = getVisible(m, selected);
        visibleToggle = new FlatToggleWidget(x, y, 160, 20, "Visible", visible, v -> { setVisible(m, selected, v); });
        addDrawableChild(visibleToggle); y+=24;
        // Scale (wo sinnvoll)
        float s = getScale(m, selected);
        scaleSlider = new FlatSliderWidget(x, y, 220, 20, "Scale", 0.5, 3.0, s, v -> { setScale(m, selected, v.floatValue()); });
        addDrawableChild(scaleSlider); y+=24;
        // Color (nur für Texte)
        if (getText(m, selected) != null) {
            int c = getColor(m, selected);
            colorField = new FlatTextFieldWidget(x, y, 220, 20, "Color ARGB (hex)");
            colorField.setText(String.format("%08X", c));
            addDrawableChild(colorField);
            addDrawableChild(new FlatButtonWidget(x+230, y, 70, 20, Text.literal("Apply"), b -> {
                try { int col = (int) Long.parseLong(colorField.getText(), 16); setColor(m, selected, col); } catch (Exception ignored) {}
            }));
            y+=24;
        }
        // Background Properties
        if ("background".equals(selected)) {
            bgColorField = new FlatTextFieldWidget(x, y, 220, 20, "BG ARGB (hex)"); bgColorField.setText(String.format("%08X", m.background.getColor())); addDrawableChild(bgColorField);
            addDrawableChild(new FlatButtonWidget(x+230, y, 70, 20, Text.literal("Apply"), b -> { try { int v = (int)Long.parseLong(bgColorField.getText(),16); m.background.setColor(v); } catch (Exception ignored) {} })); y+=24;
            Integer bc = m.background.getBorderColor();
            bgBorderField = new FlatTextFieldWidget(x, y, 220, 20, "Border ARGB (hex)"); bgBorderField.setText(bc==null?"":String.format("%08X", bc)); addDrawableChild(bgBorderField);
            addDrawableChild(new FlatButtonWidget(x+230, y, 70, 20, Text.literal("Apply"), b -> { try { String t = bgBorderField.getText(); m.background.setBorder(t==null||t.isEmpty()? null : (int)Long.parseLong(t,16)); } catch (Exception ignored) {} })); y+=24;
        }
        // Progress Bar Properties
        if ("bar".equals(selected)) {
            barFgField = new FlatTextFieldWidget(x, y, 220, 20, "Bar FG (hex)"); barFgField.setText(String.format("%08X", m.bar.getFgColor())); addDrawableChild(barFgField);
            addDrawableChild(new FlatButtonWidget(x+230, y, 70, 20, Text.literal("Apply"), b -> { try { int fg = (int)Long.parseLong(barFgField.getText(),16); m.bar.setColors(fg, m.bar.getBgColor()); } catch (Exception ignored) {} })); y+=24;
            barBgField = new FlatTextFieldWidget(x, y, 220, 20, "Bar BG (hex)"); barBgField.setText(String.format("%08X", m.bar.getBgColor())); addDrawableChild(barBgField);
            addDrawableChild(new FlatButtonWidget(x+230, y, 70, 20, Text.literal("Apply"), b -> { try { int bg = (int)Long.parseLong(barBgField.getText(),16); m.bar.setColors(m.bar.getFgColor(), bg); } catch (Exception ignored) {} })); y+=24;
        }
    }

    private boolean getVisible(namelessju.scathapro.fabric.overlay.v2.V2Model m, String key){
        var t = getText(m, key);
        if (key.equals("background")) return m.background.isVisible();
        if (key.equals("bar")) return m.bar.isVisible();
        return t!=null ? t.isVisible() : true;
    }
    private void setVisible(namelessju.scathapro.fabric.overlay.v2.V2Model m, String key, boolean v){
        var t = getText(m, key);
        if (key.equals("background")) m.background.setVisible(v); else if (key.equals("bar")) m.bar.setVisible(v); else if (t!=null) t.setVisible(v);
    }
    private float getScale(namelessju.scathapro.fabric.overlay.v2.V2Model m, String key){
        var t = getText(m, key);
        if (key.equals("background")) return 1.0f; if (key.equals("bar")) return m.bar.getScale(); return t!=null ? t.getScale() : 1.0f;
    }
    private void setScale(namelessju.scathapro.fabric.overlay.v2.V2Model m, String key, float s){
        var t = getText(m, key);
        if (key.equals("bar")) m.bar.setScale(s); else if (t!=null) t.setScale(s);
    }
    private int getColor(namelessju.scathapro.fabric.overlay.v2.V2Model m, String key){
        var t = getText(m, key); return t!=null ? t.getColor() : 0xFFFFFFFF;
    }
    private void setColor(namelessju.scathapro.fabric.overlay.v2.V2Model m, String key, int col){
        var t = getText(m, key); if (t!=null) t.setColor(col);
    }

    private namelessju.scathapro.fabric.overlay.v2.V2Element getElement(namelessju.scathapro.fabric.overlay.v2.V2Model m, String key){
        if ("background".equals(key)) return m.background;
        if ("bar".equals(key)) return m.bar;
        return getText(m, key);
    }

    private void setPosition(namelessju.scathapro.fabric.overlay.v2.V2Model m, String key, int x, int y){
        var e = getElement(m, key); if (e != null) e.setPosition(x, y);
    }

    private namelessju.scathapro.fabric.overlay.v2.V2Text getText(namelessju.scathapro.fabric.overlay.v2.V2Model m, String key){
        switch (key){
            case "title": return m.title;
            case "headerPets": return m.headerPets;
            case "headerWorms": return m.headerWorms;
            case "headerScathas": return m.headerScathas;
            case "headerTotal": return m.headerTotal;
            case "petsBlue": return m.petsBlue;
            case "petsPurple": return m.petsPurple;
            case "petsOrange": return m.petsOrange;
            case "wormsCount": return m.wormsCount;
            case "scathasCount": return m.scathasCount;
            case "totalCount": return m.totalCount;
            case "totalPercent": return m.totalPercent;
            case "scathaText": return m.scathaText;
            case "wormText": return m.wormText;
            case "totalText": return m.totalText;
            case "streakText": return m.streakText;
            case "wormTimerText": return m.wormTimerText;
            case "scathaTimerText": return m.scathaTimerText;
            case "dayTimeText": return m.dayTimeText;
            case "coordsText": return m.coordsText;
            default: return null;
        }
    }

    private void saveModel() {
        var r = namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer();
        if (r == null) return;
        var m = r.getModel();
        String json = m.toJson();
        try {
            java.nio.file.Path dir = namelessju.scathapro.fabric.FabricConfig.getConfigPath().getParent();
            java.nio.file.Path p = dir.resolve("overlay_v2_model.json");
            java.nio.file.Files.writeString(p, json);
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            if (cfg != null) { cfg.overlayV2ModelJson = p.toString(); cfg.save(); }
        } catch (Exception ignored) {}
    }

    private void loadModel() {
        try {
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            java.nio.file.Path dir = namelessju.scathapro.fabric.FabricConfig.getConfigPath().getParent();
            java.nio.file.Path p = java.nio.file.Paths.get(cfg != null && cfg.overlayV2ModelJson != null ? cfg.overlayV2ModelJson : dir.resolve("overlay_v2_model.json").toString());
            if (java.nio.file.Files.exists(p)) {
                String s = java.nio.file.Files.readString(p);
                var m = namelessju.scathapro.fabric.overlay.v2.V2Model.fromJson(s);
                var r = namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer();
                if (r != null) r.setModel(m);
            }
        } catch (Exception ignored) {}
        buildUi();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Kein Vanilla-Blur; dunkler Hintergrund für Klarheit
        ctx.fill(0, 0, this.width, this.height, 0xEE000000);
        // Hauptpanel
        ctx.fill(panelX, panelY, panelX+panelW, panelY+panelH, 0xD0151515);
        ctx.fill(panelX+1, panelY+1, panelX+panelW-1, panelY+panelH-1, 0xE01E1E1E);
        var tr = this.textRenderer;
        ctx.drawText(tr, Text.literal("Overlay Editor"), panelX + 16, panelY + 16, 0xFFFFFFFF, false);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public void close() { MinecraftClient.getInstance().setScreen(parent); }

    private void ctxFillPanel(int x, int y, int w, int h) {
        // Utility zum Zeichnen von Panels – wird in buildUi via render() nicht direkt verfügbar,
        // daher dient diese Methode nur der Semantik. Die eigentliche Füllung erfolgte im render().
        // Hier intentionally no-op (Layoutvorbereitung). In einem vollwertigen UI würde
        // man Drawable-Elemente hinzufügen; für jetzt genügt die klare Struktur.
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horiz, double vert) {
        int listHeight = panelH - 100; int visible = Math.max(1, listHeight / itemHeight);
        int maxScroll = Math.max(0, (itemKeys != null ? itemKeys.length : 0) - visible);
        if (maxScroll > 0 && Math.abs(vert) > 0) {
            listScroll = (int) Math.max(0, Math.min(maxScroll, listScroll - Math.signum(vert)));
            buildUi();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horiz, vert);
    }
}
