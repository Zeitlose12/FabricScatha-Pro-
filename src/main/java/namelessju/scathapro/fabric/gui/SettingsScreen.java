package namelessju.scathapro.fabric.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.RenderLayer;
import namelessju.scathapro.fabric.gui.widgets.FlatButtonWidget;
import namelessju.scathapro.fabric.gui.widgets.FlatToggleWidget;
import namelessju.scathapro.fabric.gui.widgets.FlatSliderWidget;
import namelessju.scathapro.fabric.gui.widgets.FlatTextFieldWidget;

public class SettingsScreen extends Screen {
    private final Screen parent;
    private int tab = 0; // 0=News,1=Overlay,2=Statistics,3=Alerts,4=Sound,5=Achievements
    // Layout
    private int panelX, panelY, panelW, panelH;
    private int lastLayoutW = -1, lastLayoutH = -1;
    // Scroll-Zustand für lange Tabs
    private int scrollY = 0;
    private int maxScroll = 0;
    // Referenzen auf die Tab-Widgets (für Render-Priorität über dem Body)
    private final java.util.List<net.minecraft.client.gui.widget.PressableWidget> headerTabWidgets = new java.util.ArrayList<>();
    private TextFieldWidget wormMsgField;
    private TextFieldWidget scathaMsgField;
    // Move GUI erfolgt über separaten Screen

    public SettingsScreen(Screen parent) {
        super(Text.literal("Scatha-Pro Einstellungen"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        rebuildAll();
    }

    private void rebuildAll() {
        this.clearChildren();
        computePanelBounds();
        // Erst Body aufbauen (Inhalte), danach Header-Tabs hinzufügen, damit Tabs immer oben liegen
        rebuildBody();
        addHeaderTabs();
        lastLayoutW = panelW; lastLayoutH = panelH;
    }

    private void addHeaderTabs() {
        // Tabs oben im Panel
        headerTabWidgets.clear();
        int y = panelY + 18;
        int x = panelX + (panelW - (6*78 + 5*10)) / 2; // 6 Tabs à 78 Breite, 10 Abstand
        var t1 = new FlatButtonWidget(x, y, 78, 22, Text.literal("News"), btn -> { tab=0; rebuildAll(); }); addDrawableChild(t1); headerTabWidgets.add(t1); x += 88;
        var t2 = new FlatButtonWidget(x, y, 78, 22, Text.literal("Overlay"), btn -> { tab=1; rebuildAll(); }); addDrawableChild(t2); headerTabWidgets.add(t2); x += 88;
        var t3 = new FlatButtonWidget(x, y, 78, 22, Text.literal("Stats"), btn -> { tab=2; rebuildAll(); }); addDrawableChild(t3); headerTabWidgets.add(t3); x += 88;
        var t4 = new FlatButtonWidget(x, y, 78, 22, Text.literal("Alerts"), btn -> { tab=3; rebuildAll(); }); addDrawableChild(t4); headerTabWidgets.add(t4); x += 88;
        var t5 = new FlatButtonWidget(x, y, 78, 22, Text.literal("Sound"), btn -> { tab=4; rebuildAll(); }); addDrawableChild(t5); headerTabWidgets.add(t5); x += 88;
        var t6 = new FlatButtonWidget(x, y, 78, 22, Text.literal("Achieve"), btn -> { tab=5; rebuildAll(); }); addDrawableChild(t6); headerTabWidgets.add(t6);
    }

    private void rebuildBody() {
        int left = panelX + 24;
        int top = panelY + 64;
        var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
        if (tab==0) {
            // NEWS TAB – short info text + credits with clickable links (EN)
            int y = top;
            int x = left;
            int w = panelW - 48;
            addDrawableChild(new FlatButtonWidget(x, y, w, 22, Text.literal("§6§nNews"), b -> {})).active = false; y += 28;
            addDrawableChild(new FlatButtonWidget(x, y, w, 18, Text.literal("This unofficial Fabric port to 1.21.5 was made by §bzeitlose / Jan§r."), b->{})).active = false; y+=22;
            addDrawableChild(new FlatButtonWidget(x, y, w, 18, Text.literal("Besides the port, custom features have been added and further expanded."), b->{})).active = false; y+=22;
            addDrawableChild(new FlatButtonWidget(x, y, w, 18, Text.literal("The source code will be published separately (GitHub) once the base is stable."), b->{})).active = false; y+=26;
            addDrawableChild(new FlatButtonWidget(x, y, w, 18, Text.literal("§7Credits: The original is by the Scatha-Pro developers (Forge 1.8.9)."), b->{})).active = false; y+=22;
            // Links
            addDrawableChild(new FlatButtonWidget(x, y, 260, 20, Text.literal("Modrinth (Original mod)"), b -> openLink("https://modrinth.com/mod/scatha-pro"))); y+=24;
            addDrawableChild(new FlatButtonWidget(x, y, 260, 20, Text.literal("GitHub (Original repo)"), b -> openLink("https://github.com/NamelessJu/Scatha-Pro"))); y+=24;
            addDrawableChild(new FlatButtonWidget(x, y, 260, 20, Text.literal("Discord (Scatha Farmers)"), b -> openLink("https://discord.gg/scatha-farmers-898827889145942056"))); y+=28;
            addDrawableChild(new FlatButtonWidget(panelX + (panelW-100)/2, panelY + panelH - 36, 100, 20, Text.literal("Close"), btn -> close()));
        } else if (tab==1) {
            // OVERLAY SETTINGS – Zweispalten-Layout + Scroll + Hard-Viewport
            var v2 = namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer();
            if (v2 != null) { v2.syncConfig(cfg); }

            int contentTop = panelY + 64 - scrollY; // Start unterhalb Tabs
            int sectionGap = 22;
            int controlGap = 24;

            int colHeaderW = 360;
            int colL = panelX + 24;
            int colR = panelX + panelW - 24 - colHeaderW; // rechte Spalte bündig rechts
            int yL = contentTop;
            int yR = contentTop;

            // Linke Spalte: Allgemein
            FlatButtonWidget secGeneral = new FlatButtonWidget(colL, yL, colHeaderW, 18, Text.literal("General"), b->{}); secGeneral.active = false; addDrawableChild(secGeneral); yL+=sectionGap;
            addDrawableChild(new FlatToggleWidget(colL, yL, 200, 20, "HUD enabled", cfg.overlayVisible, v->{cfg.overlayVisible=v; cfg.save(); syncOverlayConfig();})); yL+=controlGap;
            addDrawableChild(new FlatToggleWidget(colL, yL, 200, 20, "Compact mode", cfg.overlayCompactMode, v->{cfg.overlayCompactMode=v; cfg.save(); syncOverlayConfig();})); yL+=controlGap;
addDrawableChild(new FlatSliderWidget(colL, yL, 320, 20, "Overlay scale", 0.5, 3.0, cfg.overlayScale, v->{
                var c = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
                if (c != null) { c.overlayScale = (float) v.doubleValue(); c.save(); syncOverlayConfig(); }
            }));
            addDrawableChild(new FlatButtonWidget(colL, yL, 200, 20, Text.literal("Move HUD"), btn -> {
                var mc = MinecraftClient.getInstance();
                if (mc != null) mc.setScreen(new namelessju.scathapro.fabric.gui.MoveOverlayScreen(this));
            })); yL+=controlGap;

            // Rechte Spalte: Sichtbarkeit
            FlatButtonWidget secVis = new FlatButtonWidget(colR, yR, colHeaderW, 18, Text.literal("Visibility"), b->{}); secVis.active = false; addDrawableChild(secVis); yR+=sectionGap;
            addDrawableChild(new FlatToggleWidget(colR, yR, 170, 20, "Scatha kills", cfg.overlayShowScatha, v->{cfg.overlayShowScatha=v; cfg.save(); syncOverlayConfig();}));
            addDrawableChild(new FlatToggleWidget(colR+180, yR, 170, 20, "Worm kills", cfg.overlayShowWorm, v->{cfg.overlayShowWorm=v; cfg.save(); syncOverlayConfig();})); yR+=controlGap;
            addDrawableChild(new FlatToggleWidget(colR, yR, 170, 20, "Total kills", cfg.overlayShowTotal, v->{cfg.overlayShowTotal=v; cfg.save(); syncOverlayConfig();}));
            addDrawableChild(new FlatToggleWidget(colR+180, yR, 170, 20, "Streak / Rate", cfg.overlayShowStreak, v->{cfg.overlayShowStreak=v; cfg.save(); syncOverlayConfig();})); yR+=controlGap;
            addDrawableChild(new FlatToggleWidget(colR, yR, 170, 20, "Progress bar", cfg.overlayShowBar, v->{cfg.overlayShowBar=v; cfg.save(); syncOverlayConfig();}));
            addDrawableChild(new FlatToggleWidget(colR+180, yR, 170, 20, "Show icons", cfg.overlayShowIcons, v->{cfg.overlayShowIcons=v; cfg.save(); syncOverlayConfig();})); yR+=controlGap+4;

            // Linke Spalte: Layout & Stil
            FlatButtonWidget secLayout = new FlatButtonWidget(colL, yL, colHeaderW, 18, Text.literal("Layout & Style"), b->{}); secLayout.active = false; addDrawableChild(secLayout); yL+=sectionGap;
            addDrawableChild(new FlatToggleWidget(colL, yL, 200, 20, "Background", cfg.overlayBackgroundEnabled, v->{cfg.overlayBackgroundEnabled=v; cfg.save();})); yL+=controlGap;
            addDrawableChild(new FlatToggleWidget(colL, yL, 200, 20, "Columns layout", cfg.overlayColumnsEnabled, v->{cfg.overlayColumnsEnabled=v; cfg.save();})); yL+=controlGap;
            addDrawableChild(new FlatToggleWidget(colL, yL, 200, 20, "Text shadow", cfg.overlayTextShadow, v->{cfg.overlayTextShadow=v; cfg.save();})); yL+=controlGap;
            addDrawableChild(new FlatToggleWidget(colL, yL, 200, 20, "Snap to grid", cfg.overlaySnapEnabled, v->{cfg.overlaySnapEnabled=v; cfg.save();})); yL+=controlGap;
            FlatTextFieldWidget gridField = new FlatTextFieldWidget(colL, yL, 80, 20, "Grid px"); gridField.setText(Integer.toString(cfg.overlaySnapSize)); addDrawableChild(gridField);
            addDrawableChild(new FlatButtonWidget(colL+85, yL, 90, 20, Text.literal("Apply"), btn->{ try{ cfg.overlaySnapSize = Math.max(1, Integer.parseInt(gridField.getText())); cfg.save(); }catch(Exception ignored){} })); yL+=controlGap;
            // Style buttons
            String[] profiles = new String[]{"default","dark","high"};
            int idx = 0; for (int i=0;i<profiles.length;i++){ if (profiles[i].equalsIgnoreCase(cfg.colorProfile)) { idx=i; break; } }
            FlatButtonWidget colorBtn = new FlatButtonWidget(colL, yL, 200, 20, Text.literal("Color profile: "+profiles[idx]), btn -> {
                int i = 0; for (int j=0;j<profiles.length;j++){ if (("Color profile: "+profiles[j]).equals(btn.getMessage().getString())) { i=j; break; } }
                i = (i+1) % profiles.length;
                namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.colorProfile = profiles[i];
                namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.save();
                btn.setText(Text.literal("Color profile: "+profiles[i]));
            });
            addDrawableChild(colorBtn); yL+=controlGap;
            String[] titleIcons = new String[]{"default", "mode_anime", "mode_custom", "mode_custom_overlay", "mode_meme", "scatha_spin"};
            String[] titleIconNames = new String[]{"Default", "Anime", "Custom", "Custom Overlay", "Meme", "Scatha Spin"};
            int iconIdx = 0; for (int i=0;i<titleIcons.length;i++){ if (titleIcons[i].equalsIgnoreCase(cfg.overlayTitleIcon)) { iconIdx=i; break; } }
            FlatButtonWidget iconBtn = new FlatButtonWidget(colL, yL, 200, 20, Text.literal("Title Icon: "+titleIconNames[iconIdx]), btn -> {
                int i = 0; for (int j=0;j<titleIconNames.length;j++){ if (("Title Icon: "+titleIconNames[j]).equals(btn.getMessage().getString())) { i=j; break; } }
                i = (i+1) % titleIcons.length;
                namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.overlayTitleIcon = titleIcons[i];
                namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.save();
                btn.setText(Text.literal("Title Icon: "+titleIconNames[i]));
            });
            addDrawableChild(iconBtn); yL+=controlGap;
            String currentStyle = cfg.overlayStyle != null ? cfg.overlayStyle : "v2";
            String styleDisplayName = "classic".equals(currentStyle) ? "Classic (Forge-style)" : "V2 (Modern)";
            FlatButtonWidget styleBtn = new FlatButtonWidget(colL, yL, 200, 20, Text.literal("Overlay style: " + styleDisplayName), btn -> {
                String oldStyle = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.overlayStyle;
                String newStyle = "classic".equals(oldStyle) ? "v2" : "classic";
                namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.overlayStyle = newStyle;
                namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.save();
                String newDisplayName = "classic".equals(newStyle) ? "Classic (Forge-style)" : "V2 (Modern)";
                btn.setText(Text.literal("Overlay style: " + newDisplayName));
                var mc = net.minecraft.client.MinecraftClient.getInstance();
                if (mc != null && mc.player != null) {
                    mc.player.sendMessage(net.minecraft.text.Text.literal("Overlay style changed to: " + newDisplayName), false);
                }
            });
            addDrawableChild(styleBtn); yL+=controlGap;

            // Rechte Spalte: Farben & Icons + Abstände
            FlatButtonWidget secColors = new FlatButtonWidget(colR, yR, colHeaderW, 18, Text.literal("Colors & Icons"), b->{}); secColors.active = false; addDrawableChild(secColors); yR+=sectionGap;
            FlatTextFieldWidget bgCol = new FlatTextFieldWidget(colR, yR, 120, 20, "BG color"); bgCol.setText(String.format("%08X", cfg.overlayBackgroundEnabled? namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer().ensureModel().background.getColor() : 0x90000000)); addDrawableChild(bgCol);
            addDrawableChild(new FlatButtonWidget(colR+125, yR, 80, 20, Text.literal("Apply"), b->{ try{ int col=(int)Long.parseLong(bgCol.getText(),16); namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer().ensureModel().background.setColor(col);}catch(Exception ignored){} })); yR+=controlGap;
            FlatTextFieldWidget brCol = new FlatTextFieldWidget(colR, yR, 120, 20, "Border color"); Integer bc = namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer().ensureModel().background.getBorderColor(); brCol.setText(bc==null?"":String.format("%08X", bc)); addDrawableChild(brCol);
            addDrawableChild(new FlatButtonWidget(colR+125, yR, 80, 20, Text.literal("Apply"), b->{ try{ String t=brCol.getText(); var bg=namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer().ensureModel().background; bg.setBorder(t==null||t.isEmpty()? null : (int)Long.parseLong(t,16)); }catch(Exception ignored){} })); yR+=controlGap;
            FlatTextFieldWidget barFg = new FlatTextFieldWidget(colR, yR, 120, 20, "Bar FG"); barFg.setText(String.format("%08X", namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer().ensureModel().bar.getFgColor())); addDrawableChild(barFg);
            addDrawableChild(new FlatButtonWidget(colR+125, yR, 80, 20, Text.literal("Apply"), b->{ try{ int v=(int)Long.parseLong(barFg.getText(),16); var m=namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer().ensureModel(); m.bar.setColors(v, m.bar.getBgColor()); }catch(Exception ignored){} })); yR+=controlGap;
            FlatTextFieldWidget barBg = new FlatTextFieldWidget(colR, yR, 120, 20, "Bar BG"); barBg.setText(String.format("%08X", namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer().ensureModel().bar.getBgColor())); addDrawableChild(barBg);
            addDrawableChild(new FlatButtonWidget(colR+125, yR, 80, 20, Text.literal("Apply"), b->{ try{ int v=(int)Long.parseLong(barBg.getText(),16); var m=namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer().ensureModel(); m.bar.setColors(m.bar.getFgColor(), v); }catch(Exception ignored){} })); yR+=controlGap;
            addDrawableChild(new FlatSliderWidget(colR, yR, 320, 20, "Icon Scale", 0.5, 4.0, cfg.overlayIconScale, v->{
                var c = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
                if (c != null) { c.overlayIconScale = (float) v.doubleValue(); c.save(); }
            })); yR+=controlGap+4;

            FlatButtonWidget secSpacing = new FlatButtonWidget(colR, yR, colHeaderW, 18, Text.literal("Spacing"), b->{}); secSpacing.active = false; addDrawableChild(secSpacing); yR+=sectionGap;
            FlatTextFieldWidget padField = new FlatTextFieldWidget(colR, yR, 80, 20, "Padding"); padField.setText(Integer.toString(cfg.overlayPadding)); addDrawableChild(padField);
            FlatTextFieldWidget colField = new FlatTextFieldWidget(colR+90, yR, 80, 20, "Col gap"); colField.setText(Integer.toString(cfg.overlayColSpacing)); addDrawableChild(colField);
            FlatTextFieldWidget rowField = new FlatTextFieldWidget(colR+180, yR, 80, 20, "Row gap"); rowField.setText(Integer.toString(cfg.overlayRowSpacing)); addDrawableChild(rowField);
            addDrawableChild(new FlatButtonWidget(colR+265, yR, 60, 20, Text.literal("Apply"), b->{ try{ cfg.overlayPadding=Math.max(0,Integer.parseInt(padField.getText())); cfg.overlayColSpacing=Math.max(0,Integer.parseInt(colField.getText())); cfg.overlayRowSpacing=Math.max(0,Integer.parseInt(rowField.getText())); cfg.save(); }catch(Exception ignored){} })); yR+=controlGap;

            // Scroll-Maximum anhand der tieferen Spalte
            int usedBottom = Math.max(yL, yR);
            int visibleBottom = panelY + panelH - 56;
            maxScroll = Math.max(0, (usedBottom + scrollY) - visibleBottom);

            addDrawableChild(new FlatButtonWidget(panelX + (panelW-100)/2, panelY + panelH - 36, 100, 20, Text.literal("Schließen"), btn -> close()));
        }
        else if (tab==2) {
            // STATISTICS TAB - Extended overlay statistics
            int leftCol = left;
            int rightCol = left + 180;
            
            // Advanced toggles
            addDrawableChild(new FlatToggleWidget(leftCol, top, 170, 20, "Pet drops", cfg.overlayShowPetDrops, v->{cfg.overlayShowPetDrops=v; cfg.save(); syncOverlayConfig();}));
            addDrawableChild(new FlatToggleWidget(rightCol, top, 170, 20, "Session stats", cfg.overlayShowSession, v->{cfg.overlayShowSession=v; cfg.save(); syncOverlayConfig();})); top+=24;
            addDrawableChild(new FlatToggleWidget(leftCol, top, 170, 20, "Magic Find", cfg.overlayShowMagicFind, v->{cfg.overlayShowMagicFind=v; cfg.save(); syncOverlayConfig();}));
            addDrawableChild(new FlatToggleWidget(rightCol, top, 170, 20, "Cooldown timer", cfg.overlayShowCooldown, v->{cfg.overlayShowCooldown=v; cfg.save(); syncOverlayConfig();})); top+=24;
            addDrawableChild(new FlatToggleWidget(leftCol, top, 170, 20, "Achievements", cfg.overlayShowAchievements, v->{cfg.overlayShowAchievements=v; cfg.save(); syncOverlayConfig();}));
            addDrawableChild(new FlatToggleWidget(rightCol, top, 170, 20, "Live tracker", cfg.overlayShowLiveTracker, v->{cfg.overlayShowLiveTracker=v; cfg.save(); syncOverlayConfig();})); top+=28;
            
            // Live-Scatha-Tracker Einstellungen
            FlatTextFieldWidget maxEntriesField = new FlatTextFieldWidget(left, top, 120, 20, "Max tracker entries");
            maxEntriesField.setText(Integer.toString(cfg.overlayLiveTrackerMaxEntries));
            addDrawableChild(maxEntriesField);
            addDrawableChild(new FlatButtonWidget(left+125, top, 80, 20, Text.literal("Apply"), btn->{
                try{ cfg.overlayLiveTrackerMaxEntries = Math.max(1, Math.min(20, Integer.parseInt(maxEntriesField.getText()))); cfg.save(); }catch(Exception ignored){}
            })); top+=28;
            
            // Icon Scale Slider für Statistics
addDrawableChild(new FlatSliderWidget(left, top, 320, 20, "Icon scale", 0.5, 4.0, cfg.overlayIconScale, v->{
                var c = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
                if (c != null) { c.overlayIconScale = (float) v.doubleValue(); c.save(); syncOverlayConfig(); }
            }));
            
            // Fun Features
            addDrawableChild(new FlatToggleWidget(left, top, 200, 20, "Googly eyes", cfg.overlayGooglyEyesEnabled, v->{cfg.overlayGooglyEyesEnabled=v; cfg.save();})); top+=24;
            addDrawableChild(new FlatToggleWidget(left, top, 200, 20, "Spin animation", cfg.overlaySpinEnabled, v->{cfg.overlaySpinEnabled=v; cfg.save();})); top+=24;
            
            addDrawableChild(new FlatButtonWidget(panelX + (panelW-100)/2, panelY + panelH - 36, 100, 20, Text.literal("Close"), btn -> close()));
        } else if (tab==3) {
            // ALERTS TAB
            addDrawableChild(new FlatToggleWidget(left, top, 200, 20, "Display alerts", cfg.alertsDisplayEnabled, v->{cfg.alertsDisplayEnabled=v; cfg.save();})); top+=24;

            // Alert Mode Auswahl
            var sp = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
            var modeManager = sp != null ? sp.getAlertModeManager() : null;
            String modeName = modeManager != null && modeManager.getCurrent() != null ? modeManager.getCurrent().displayName() : "Vanilla";
            addDrawableChild(new FlatButtonWidget(left, top, 200, 20, Text.literal("Alert mode: " + modeName), btn -> {
                var mm = namelessju.scathapro.fabric.FabricScathaPro.getInstance() != null ? namelessju.scathapro.fabric.FabricScathaPro.getInstance().getAlertModeManager() : null;
                if (mm != null) { var cur = mm.next(); if (cur != null) btn.setText(Text.literal("Alert mode: " + cur.displayName())); }
            }));
            addDrawableChild(new FlatButtonWidget(left+210, top, 180, 20, Text.literal("Edit custom modes"), btn -> {
                var s = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
                if (s != null) {
                    var cm = s.getCustomAlertModeManager();
                    var mm = s.getAlertModeManager();
                    var mc = net.minecraft.client.MinecraftClient.getInstance();
                    if (mc != null) mc.setScreen(new namelessju.scathapro.fabric.gui.CustomAlertModeScreen(this, cm, mm));
                }
            }));
            top+=28;

            // High Heat Alert Toggle
            addDrawableChild(new FlatToggleWidget(left, top, 200, 20, "High Heat alert", cfg.highHeatAlert, v->{cfg.highHeatAlert=v; cfg.save();})); top+=24;
            // High Heat Trigger Value
            FlatTextFieldWidget heatField = new FlatTextFieldWidget(left, top, 120, 20, "Heat trigger"); heatField.setText(Integer.toString(cfg.highHeatAlertTriggerValue)); addDrawableChild(heatField);
            addDrawableChild(new FlatButtonWidget(left+125, top, 80, 20, Text.literal("Apply"), btn->{ try{ cfg.highHeatAlertTriggerValue = Math.max(90, Math.min(100, Integer.parseInt(heatField.getText()))); cfg.save(); }catch(Exception ignored){} })); top+=24;

            // Bedrock Wall Alert Trigger Distance
            addDrawableChild(new FlatToggleWidget(left, top, 200, 20, "Bedrock wall alert", cfg.bedrockWallAlert, v->{cfg.bedrockWallAlert=v; cfg.save();}));
            FlatTextFieldWidget wallField = new FlatTextFieldWidget(left+210, top, 90, 20, "Distance"); wallField.setText(Integer.toString(cfg.bedrockWallAlertTriggerDistance)); addDrawableChild(wallField);
            addDrawableChild(new FlatButtonWidget(left+305, top, 85, 20, Text.literal("Apply"), btn->{ try{ cfg.bedrockWallAlertTriggerDistance = Math.max(0, Math.min(50, Integer.parseInt(wallField.getText()))); cfg.save(); }catch(Exception ignored){} })); top+=28;

            // Per-Type Alert Toggles (2 Spalten)
            int colL = left;
            int colR = left + 220;
            addDrawableChild(new FlatToggleWidget(colL, top, 200, 20, "Worm prespawn", cfg.wormPrespawnAlert, v->{cfg.wormPrespawnAlert=v; cfg.save();}));
            addDrawableChild(new FlatToggleWidget(colR, top, 200, 20, "Worm spawn", cfg.regularWormSpawnAlert, v->{cfg.regularWormSpawnAlert=v; cfg.save();})); top+=24;
            addDrawableChild(new FlatToggleWidget(colL, top, 200, 20, "Scatha spawn", cfg.scathaSpawnAlert, v->{cfg.scathaSpawnAlert=v; cfg.save();}));
            addDrawableChild(new FlatToggleWidget(colR, top, 200, 20, "Pet drop", cfg.scathaPetDropAlert, v->{cfg.scathaPetDropAlert=v; cfg.save();})); top+=24;
            addDrawableChild(new FlatToggleWidget(colL, top, 200, 20, "Goblin spawn", cfg.goblinSpawnAlert, v->{cfg.goblinSpawnAlert=v; cfg.save();}));
            addDrawableChild(new FlatToggleWidget(colR, top, 200, 20, "Jerry spawn", cfg.jerrySpawnAlert, v->{cfg.jerrySpawnAlert=v; cfg.save();})); top+=24;
            addDrawableChild(new FlatToggleWidget(colL, top, 200, 20, "Cooldown ready", cfg.wormSpawnCooldownEndAlert, v->{cfg.wormSpawnCooldownEndAlert=v; cfg.save();}));
            addDrawableChild(new FlatToggleWidget(colR, top, 200, 20, "Pickaxe ready", cfg.pickaxeAbilityReadyAlert, v->{cfg.pickaxeAbilityReadyAlert=v; cfg.save();})); top+=24;
            addDrawableChild(new FlatToggleWidget(colL, top, 200, 20, "Bedrock Wall", cfg.bedrockWallAlert, v->{cfg.bedrockWallAlert=v; cfg.save();}));
            addDrawableChild(new FlatToggleWidget(colR, top, 200, 20, "Old Lobby", cfg.oldLobbyAlert, v->{cfg.oldLobbyAlert=v; cfg.save();})); top+=28;

            // Alert Test-Buttons
            var scathaPro = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
            addDrawableChild(new FlatButtonWidget(left, top, 140, 20, Text.literal("Test worm spawn"), btn->{ if (scathaPro!=null && scathaPro.getAlertManager()!=null) scathaPro.getAlertManager().triggerTestAlert(namelessju.scathapro.fabric.alerts.AlertType.WORM_SPAWN);}));
            addDrawableChild(new FlatButtonWidget(left+150, top, 140, 20, Text.literal("Test scatha spawn"), btn->{ if (scathaPro!=null && scathaPro.getAlertManager()!=null) scathaPro.getAlertManager().triggerTestAlert(namelessju.scathapro.fabric.alerts.AlertType.SCATHA_SPAWN);})); top+=24;
            addDrawableChild(new FlatButtonWidget(left, top, 140, 20, Text.literal("Test goblin"), btn->{ if (scathaPro!=null && scathaPro.getAlertManager()!=null) scathaPro.getAlertManager().triggerTestAlert(namelessju.scathapro.fabric.alerts.AlertType.GOBLIN_SPAWN);}));
            addDrawableChild(new FlatButtonWidget(left+150, top, 140, 20, Text.literal("Test jerry"), btn->{ if (scathaPro!=null && scathaPro.getAlertManager()!=null) scathaPro.getAlertManager().triggerTestAlert(namelessju.scathapro.fabric.alerts.AlertType.JERRY_SPAWN);})); top+=24;
            addDrawableChild(new FlatButtonWidget(left, top, 140, 20, Text.literal("Test high heat"), btn->{ if (scathaPro!=null && scathaPro.getAlertManager()!=null) scathaPro.getAlertManager().triggerTestAlert(namelessju.scathapro.fabric.alerts.AlertType.HIGH_HEAT);})); top+=28;

            // Worm & Scatha Messages (HUD Overlay Message Text)
            wormMsgField = new FlatTextFieldWidget(left, top, 320, 20, "Worm message");
            wormMsgField.setText(cfg.alertWormMessage); addDrawableChild(wormMsgField); top+=24;
            scathaMsgField = new FlatTextFieldWidget(left, top, 320, 20, "Scatha message");
            scathaMsgField.setText(cfg.alertScathaMessage); addDrawableChild(scathaMsgField); top+=24;
            addDrawableChild(new FlatButtonWidget(left, top, 120, 20, Text.literal("Save"), btn->{cfg.alertWormMessage = wormMsgField.getText(); cfg.alertScathaMessage = scathaMsgField.getText(); cfg.save();}));
            addDrawableChild(new FlatButtonWidget(left+130, top, 100, 20, Text.literal("Test worm msg"), btn->{namelessju.scathapro.fabric.util.FabricHudUtil.showOverlayMessage(cfg.alertWormMessage);}));
            addDrawableChild(new FlatButtonWidget(left+235, top, 100, 20, Text.literal("Test scatha msg"), btn->{namelessju.scathapro.fabric.util.FabricHudUtil.showOverlayMessage(cfg.alertScathaMessage);}));
            addDrawableChild(new FlatButtonWidget(panelX + (panelW-100)/2, panelY + panelH - 36, 100, 20, Text.literal("Close"), btn -> close()));
            // SOUND TAB
            addDrawableChild(new FlatToggleWidget(left, top, 200, 20, "Sounds", cfg.alertsEnabled, v->{cfg.alertsEnabled=v; cfg.save();})); top+=24;
            addDrawableChild(new FlatToggleWidget(left, top, 200, 20, "Mute in CH", cfg.muteCrystalHollowsSounds, v->{cfg.muteCrystalHollowsSounds=v; cfg.save();})); top+=24;
            FlatButtonWidget volLabel = new FlatButtonWidget(left+105, top, 110, 20, Text.literal(String.format("Vol: %.1f", cfg.soundVolume)), btn->{});
            volLabel.active = false; addDrawableChild(new FlatButtonWidget(left, top, 100, 20, Text.literal("Volume -"), btn->{cfg.soundVolume=Math.max(0f, cfg.soundVolume-0.1f); cfg.save(); volLabel.setText(Text.literal(String.format("Vol: %.1f", cfg.soundVolume)));}));
            addDrawableChild(volLabel);
            addDrawableChild(new FlatButtonWidget(left+220, top, 20, 20, Text.literal("+"), btn->{cfg.soundVolume=Math.min(1f, cfg.soundVolume+0.1f); cfg.save(); volLabel.setText(Text.literal(String.format("Vol: %.1f", cfg.soundVolume)));}));
            addDrawableChild(new FlatButtonWidget(panelX + (panelW-100)/2, panelY + panelH - 36, 100, 20, Text.literal("Close"), btn -> close()));
        } else if (tab==5) {
            // ACHIEVEMENTS TAB
            addDrawableChild(new FlatToggleWidget(left, top, 200, 20, "Achievement Benachrichtigungen", cfg.achievementsEnabled, v->{cfg.achievementsEnabled=v; cfg.save();})); top+=24;
            addDrawableChild(new FlatToggleWidget(left, top, 200, 20, "Achievement Sounds", cfg.achievementSoundsEnabled, v->{cfg.achievementSoundsEnabled=v; cfg.save();})); top+=24;
            addDrawableChild(new FlatToggleWidget(left, top, 200, 20, "Progress Tracking", cfg.achievementProgressTracking, v->{cfg.achievementProgressTracking=v; cfg.save();})); top+=28;
            
            // Achievement Liste anzeigen
            var scathaPro = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
            if (scathaPro != null && scathaPro.getAchievementManager() != null) {
                var achievementManager = scathaPro.getAchievementManager();
                
                // Test-Buttons für Achievement-System
                addDrawableChild(new FlatButtonWidget(left, top, 120, 20, Text.literal("Test Achievement"), btn -> {
                    // Test-Achievement auslsöen
                    achievementManager.testAchievement("Test Achievement erreicht!");
                })); 
                
                addDrawableChild(new FlatButtonWidget(left + 130, top, 120, 20, Text.literal("Reset Progress"), btn -> {
                    // Achievement-Progress zurücksetzen (falls implementiert)
                    achievementManager.resetProgress();
                })); top += 28;
                
                // Achievement-Statistiken anzeigen
                int totalAchievements = achievementManager.getTotalAchievements();
                int unlockedAchievements = achievementManager.getUnlockedAchievements();
                
                addDrawableChild(new FlatButtonWidget(left, top, 300, 20, 
                    Text.literal(String.format("Achievements: %d/%d freigeschaltet", unlockedAchievements, totalAchievements)), 
                    btn -> {})).active = false;
                top += 28;
                
                // Bonus Achievement Anzeige
                addDrawableChild(new FlatToggleWidget(left, top, 200, 20, "Bonus Achievements anzeigen", cfg.showBonusAchievements, v->{cfg.showBonusAchievements=v; cfg.save();})); top+=28;
                
                // Achievement-GUI öffnen Button
                addDrawableChild(new FlatButtonWidget(left, top, 200, 20, Text.literal("Achievement-GUI öffnen"), btn -> {
                    var mc = MinecraftClient.getInstance();
                    if (mc != null) {
                        mc.setScreen(new namelessju.scathapro.fabric.gui.AchievementScreen(this));
                    }
                })); top+=24;
            } else {
                // Fallback wenn Achievement-Manager nicht verfügbar
                addDrawableChild(new FlatButtonWidget(left, top, 300, 20, Text.literal("Achievement-System nicht verfügbar"), btn -> {})).active = false;
                top += 28;
            }
            
            addDrawableChild(new FlatButtonWidget(panelX + (panelW-100)/2, panelY + panelH - 36, 100, 20, Text.literal("Schließen"), btn -> close()));
        }
    }

    private static String t(String name, boolean v) { return name+": "+(v?"ein":"aus"); }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Eigene, nicht verschwommene Hintergrundabdunklung (kein World-Blur)
        ctx.fill(0, 0, this.width, this.height, 0xEE000000);
        // Panel-Bounds berechnen und zeichnen
        computePanelBounds();
        // Falls sich die Panelgröße seit letztem Layout geändert hat (z. B. beim ersten Frame), UI neu aufbauen
        if (panelW != lastLayoutW || panelH != lastLayoutH) {
            rebuildAll();
        }
        // Panel deutlich vom Hintergrund abheben (helleres Grau, klare Kanten)
        ctx.fill(panelX, panelY, panelX+panelW, panelY+panelH, 0xFF242424);
        ctx.fill(panelX+2, panelY+2, panelX+panelW-2, panelY+panelH-2, 0xFF2C2C2C);
        // dünne Highlight-Line oben
        ctx.fill(panelX+2, panelY+2, panelX+panelW-2, panelY+3, 0x40FFFFFF);
        // Titel
        var tr = this.textRenderer;
        ctx.drawText(tr, Text.literal("SCATHA-PRO"), panelX + 24, panelY + 20, 0xFFEFEFEF, false);

        // Hard-Viewport: nur Body innerhalb des Panels (unterhalb Tabs, oberhalb Close-Button)
        int clipLeft = panelX + 4;
        int clipTop = panelY + 46; // unter den Tabs
        int clipRight = panelX + panelW - 4;
        int clipBottom = panelY + panelH - 48; // oberhalb "Close"
        try { ctx.enableScissor(clipLeft, clipTop, clipRight, clipBottom); } catch (Throwable ignored) {}

        // Card-Hintergründe im News-Tab VOR den Widgets zeichnen
        if (this.tab == 0) {
            int x = panelX + 24;
            int y = panelY + 64;
            int w = panelW - 48;
            // Wir zeichnen Karten für Header + 4 Textzeilen
            int[] heights = new int[]{22, 18, 18, 18, 18};
            for (int i=0;i<heights.length;i++){
                int h = heights[i];
                // dezenter Schatten
                ctx.fill(x+2, y+2, x+w+2, y+h+2, 0x33000000);
                // Karte
                ctx.fill(x, y, x+w, y+h, 0xCC1E1E1E);
                // Border
                ctx.fill(x, y, x+w, y+1, 0x40FFFFFF);
                ctx.fill(x, y+h-1, x+w, y+h, 0x40000000);
                // nächster y
                y += (i==0 ? 28 : (i==3 ? 26 : 22));
            }
        }

        super.render(ctx, mouseX, mouseY, delta);

        // Dekoratives, großes Bild unten rechts im News-Tab (mit leichtem Rahmen/Glow)
        if (this.tab == 0) {
            try {
                Identifier newsImage = Identifier.of("scathapro", "textures/overlay/scatha_icons/mode_anime.png");
                // 32x32-Icon nur in ganzzahligen Faktoren skalieren (2x/3x/4x) für Schärfe
                int base = 32;
                int scale;
                if (panelW >= 900) scale = 4; else if (panelW >= 700) scale = 3; else scale = 2;
                int targetW = base * scale;
                int targetH = targetW;
                int drawX = panelX + panelW - 16 - targetW;
                int drawY = panelY + panelH - 56 - targetH;
                // Hintergrundplatte leicht transparent
                ctx.fill(drawX-6, drawY-6, drawX+targetW+6, drawY+targetH+6, 0x66000000);
                // feiner heller Rahmen
                ctx.fill(drawX-6, drawY-6, drawX+targetW+6, drawY-5, 0x30FFFFFF);
                ctx.fill(drawX-6, drawY+targetH+5, drawX+targetW+6, drawY+targetH+6, 0x30000000);
                ctx.fill(drawX-6, drawY-6, drawX-5, drawY+targetH+6, 0x30000000);
                ctx.fill(drawX+targetW+5, drawY-6, drawX+targetW+6, drawY+targetH+6, 0x30FFFFFF);
                var m = ctx.getMatrices();
                m.push();
                m.translate(drawX, drawY, 0);
                float s = (float) scale;
                m.scale(s, s, 1.0f);
                ctx.drawTexture(RenderLayer::getGuiTextured, newsImage, 0, 0, 0f, 0f, base, base, base, base);
                m.pop();
            } catch (Throwable ignored) {}
        }

        try { ctx.disableScissor(); } catch (Throwable ignored) {}

        // Tabs nach dem Body ohne Clipping rendern, damit sie immer oben liegen
        for (var w : headerTabWidgets) {
            if (w != null) w.render(ctx, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // Nur im Overlay-Tab scrollen
        if (tab == 1) {
            // Mausrad nach unten -> Inhalt nach unten (scrollY erhöht sich, list wandert nach oben)
            int step = 24;
            scrollY = Math.max(0, Math.min(maxScroll, scrollY - (int)(verticalAmount * step)));
            rebuildAll();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void refreshDragKeyLabel() {}

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    private void computePanelBounds() {
        panelW = Math.min(Math.max(320, this.width - 120), 1000);
        // etwas mehr Höhe erlauben, damit mehr Inhalte sichtbar sind
        panelH = Math.min(Math.max(240, this.height - 120), 620);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;
    }
    
    /**
     * Synchronisiert Config-Änderungen sofort mit dem Overlay für Live-Preview
     */
    private void syncOverlayConfig() {
        var renderer = namelessju.scathapro.fabric.client.ClientHooks.getV2Renderer();
        if (renderer != null) {
            renderer.syncConfig(namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG);
        }
    }

    // Öffnet einen Bestätigungsdialog und anschließend den Link im Browser
    private void openLink(String url) {
        var mc = MinecraftClient.getInstance();
        if (mc == null) return;
        mc.setScreen(new net.minecraft.client.gui.screen.ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                try { net.minecraft.util.Util.getOperatingSystem().open(url); } catch (Throwable ignored) {}
            }
            mc.setScreen(this);
        }, url, false));
    }

}
