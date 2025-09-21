package namelessju.scathapro.fabric.overlay.v2;

import net.minecraft.client.gui.DrawContext;
import namelessju.scathapro.fabric.state.ClientState;
import net.minecraft.text.Text;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public class V2Renderer {
    private V2Model model;
    private int baseX = 6, baseY = 6; 
    private float baseScale = 1.0f;
    
    public V2Model getModel(){ return model; }
    public void setModel(V2Model m){ this.model = m; }
    
    /**
     * Stellt sicher, dass ein Modell vorhanden ist und gibt es zurück.
     * Diese Methode ist UI-sicher (z.B. SettingsScreen), auch wenn der V2-Renderer
     * aktuell nicht als aktiver Overlay-Stil genutzt wird (classic aktiv).
     */
    public V2Model ensureModel() {
        if (model == null) {
            model = V2Model.defaultModel();
        }
        return model;
    }

    public void syncConfig(namelessju.scathapro.fabric.FabricConfig cfg) {
        if (cfg == null) return;
        baseX = Math.max(0, cfg.overlayX);
        baseY = Math.max(0, cfg.overlayY);
        baseScale = Math.max(0.1f, cfg.overlayScale);
        if (model == null) model = V2Model.defaultModel();
        
        // Erweiterte Farbprofile für alle Model-Elemente
        String profile = cfg.colorProfile != null ? cfg.colorProfile.toLowerCase() : "default";
        applyColorProfile(profile);
        
        // Sichtbarkeiten setzen (erweitert)
        model.background.setVisible(cfg.overlayBackgroundEnabled);
        
        // Classic stats visibility - wormText standardmäßig ausblenden
        model.scathaText.setVisible(cfg.overlayShowScatha);
        model.wormText.setVisible(false); // Standardmäßig ausblenden um Overlap zu vermeiden
        model.totalText.setVisible(cfg.overlayShowTotal);
        model.streakText.setVisible(cfg.overlayShowStreak);
        model.bar.setVisible(cfg.overlayShowBar);
        
        // Header und Tabellen-View immer sichtbar (kann erweitert werden)
        model.headerPets.setVisible(true);
        model.headerWorms.setVisible(true);
        model.headerScathas.setVisible(true);
        model.headerTotal.setVisible(true);
        
        // Pet drops basierend auf Config
        model.petsBlue.setVisible(cfg.overlayShowPetDrops);
        model.petsPurple.setVisible(cfg.overlayShowPetDrops);
        model.petsOrange.setVisible(cfg.overlayShowPetDrops);
        
        // Counts immer sichtbar
        model.wormsCount.setVisible(true);
        model.scathasCount.setVisible(true);
        model.totalCount.setVisible(true);
        model.totalPercent.setVisible(true);
        
        // Timer basierend auf Config
        model.wormTimerText.setVisible(cfg.overlayShowCooldown);
        model.scathaTimerText.setVisible(cfg.overlayShowCooldown);
        
        // World info basierend auf Session Stats Config (erweitert interpretiert)
        model.dayTimeText.setVisible(cfg.overlayShowSession);
        model.coordsText.setVisible(cfg.overlayShowSession);
        
        // Title-Icon basierend auf Config aktualisieren
        updateTitleIcon(cfg);
        
        // Offizielle Icons basierend auf Config (falls vorhanden)
        if (model.titleIcon != null) model.titleIcon.setVisible(cfg.overlayShowIcons);
        if (model.bluePetIcon != null) model.bluePetIcon.setVisible(cfg.overlayShowIcons && cfg.overlayShowPetDrops);
        if (model.purplePetIcon != null) model.purplePetIcon.setVisible(cfg.overlayShowIcons && cfg.overlayShowPetDrops);
        if (model.orangePetIcon != null) model.orangePetIcon.setVisible(cfg.overlayShowIcons && cfg.overlayShowPetDrops);
        if (model.scathaIcon != null) model.scathaIcon.setVisible(cfg.overlayShowIcons && cfg.overlayShowScatha);
        if (model.wormIcon != null) model.wormIcon.setVisible(cfg.overlayShowIcons && cfg.overlayShowWorm);
        
        // Fallback Item-Icons (falls Textures nicht verfügbar) - auch Config-abhängig
        if (model.scathaItemIcon != null) model.scathaItemIcon.setVisible(cfg.overlayShowIcons && cfg.overlayShowScatha && model.scathaIcon == null);
        if (model.wormItemIcon != null) model.wormItemIcon.setVisible(cfg.overlayShowIcons && cfg.overlayShowWorm && model.wormIcon == null);
    }
    
    private void applyColorProfile(String profile) {
        switch (profile) {
            case "dark":
                // Title
                model.title.setColor(0xFFFFD37A);
                // Headers
                model.headerPets.setColor(0xFF44DD44);
                model.headerWorms.setColor(0xFFDDDD88);
                model.headerScathas.setColor(0xFFDDDD44);
                model.headerTotal.setColor(0xFFDDDDDD);
                // Pet drops
                model.petsBlue.setColor(0xFF44AAEE);
                model.petsPurple.setColor(0xFF9944EE);
                model.petsOrange.setColor(0xFFEE9900);
                // Counts
                model.wormsCount.setColor(0xFFDDDDDD);
                model.scathasCount.setColor(0xFFDDDDDD);
                model.totalCount.setColor(0xFFDDDDDD);
                model.totalPercent.setColor(0xFF999999);
                // Classic texts
                model.scathaText.setColor(0xFFE6E6E6);
                model.wormText.setColor(0xFFE6E6E6);
                model.totalText.setColor(0xFFB0B0B0);
                model.streakText.setColor(0xFF7AE8FF);
                // Timers
                model.wormTimerText.setColor(0xFF999999);
                model.scathaTimerText.setColor(0xFF999999);
                // World info
                model.dayTimeText.setColor(0xFFCCCCCC);
                model.coordsText.setColor(0xFF999999);
                // Bar
                model.bar.setColors(0xFF44CC44, 0x80333333);
                break;
                
            case "high":
            case "high_contrast":
                // Title
                model.title.setColor(0xFFFFFF00);
                // Headers
                model.headerPets.setColor(0xFF00FF00);
                model.headerWorms.setColor(0xFFFFFF00);
                model.headerScathas.setColor(0xFFFFFF00);
                model.headerTotal.setColor(0xFFFFFFFF);
                // Pet drops
                model.petsBlue.setColor(0xFF00AAFF);
                model.petsPurple.setColor(0xFFAA00FF);
                model.petsOrange.setColor(0xFFFF8800);
                // Counts
                model.wormsCount.setColor(0xFFFFFFFF);
                model.scathasCount.setColor(0xFFFFFFFF);
                model.totalCount.setColor(0xFFFFFFFF);
                model.totalPercent.setColor(0xFFCCCCCC);
                // Classic texts
                model.scathaText.setColor(0xFFFFFFFF);
                model.wormText.setColor(0xFFFFFFFF);
                model.totalText.setColor(0xFFFFFFFF);
                model.streakText.setColor(0xFF00FFFF);
                // Timers
                model.wormTimerText.setColor(0xFFCCCCCC);
                model.scathaTimerText.setColor(0xFFCCCCCC);
                // World info
                model.dayTimeText.setColor(0xFFFFFFFF);
                model.coordsText.setColor(0xFFCCCCCC);
                // Bar
                model.bar.setColors(0xFFFFFF00, 0xFF000000);
                break;
                
            default: // "default"
                // Title
                model.title.setColor(0xFFFFAA00);
                // Headers (von V2Model)
                model.headerPets.setColor(0xFF55FF55);
                model.headerWorms.setColor(0xFFFFFFAA);
                model.headerScathas.setColor(0xFFFFFF55);
                model.headerTotal.setColor(0xFFFFFFFF);
                // Pet drops (von V2Model)
                model.petsBlue.setColor(0xFF55AAFF);
                model.petsPurple.setColor(0xFFAA55FF);
                model.petsOrange.setColor(0xFFFFAA00);
                // Counts
                model.wormsCount.setColor(0xFFFFFFFF);
                model.scathasCount.setColor(0xFFFFFFFF);
                model.totalCount.setColor(0xFFFFFFFF);
                model.totalPercent.setColor(0xFFB0B0B0);
                // Classic texts
                model.scathaText.setColor(0xFFFFFFFF);
                model.wormText.setColor(0xFFFFFFFF);
                model.totalText.setColor(0xFFAAAAAA);
                model.streakText.setColor(0xFF55FFFF);
                // Timers
                model.wormTimerText.setColor(0xFFBBBBBB);
                model.scathaTimerText.setColor(0xFFBBBBBB);
                // World info
                model.dayTimeText.setColor(0xFFE0E0E0);
                model.coordsText.setColor(0xFFB0B0B0);
                // Bar
                model.bar.setColors(0xFF55FF55, 0x80555555);
                break;
        }
    }

    public void update() {
        if (model == null) return;
        
        var state = ClientState.get();
        int scathaKills = state.getScathaKills();
        int wormKills = state.getWormKills();
        int totalKills = state.getTotalKills();
        int streak = state.getStreak();
        
        // Pet drops aktualisieren
        int bluePets = state.getBluePetDrops(); 
        int purplePets = state.getPurplePetDrops();
        int orangePets = state.getOrangePetDrops();
        
        // Titel bleibt "Scatha Farming!" (statisch im Model)

        // Animiertes Title-Icon (optional) – ähnlich Classic-Overlay
        try {
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            if (cfg != null && model != null && model.titleIcon != null) {
                if ("scatha_spin".equalsIgnoreCase(cfg.overlayTitleIcon)) {
                    float f = (float)(1.0 + 0.05 * Math.sin(System.currentTimeMillis() / 120.0));
                    model.titleIcon.setScale(f);
                } else {
                    // Default-Skala wiederherstellen (sofern sinnvoll)
                    model.titleIcon.setScale(1.1f);
                }
            }
        } catch (Throwable ignored) {}
        
        // Pet drops (erste Spalte) - echte Werte aus State
        model.petsBlue.setText(Text.literal(String.valueOf(bluePets)));
        model.petsPurple.setText(Text.literal(String.valueOf(purplePets)));
        model.petsOrange.setText(Text.literal(String.valueOf(orangePets)));
        
        // Erste Zeile: echte Kill-Werte
        model.wormsCount.setText(Text.literal(String.valueOf(wormKills)));
        model.scathasCount.setText(Text.literal(String.valueOf(scathaKills)));
        model.totalCount.setText(Text.literal(String.valueOf(totalKills)));
        
        // Info-Zeilen (wie im Referenzbild) - nur wenn sichtbar
        if (model.wormText.isVisible()) {
            long lastWorm = state.getLastWormSpawnMs();
            if (lastWorm > 0) {
                long elapsed = System.currentTimeMillis() - lastWorm;
                long minutes = elapsed / (60 * 1000);
                model.wormText.setText(Text.literal("Last worm: " + minutes + "m ago"));
            } else {
                model.wormText.setText(Text.literal("No worms yet"));
            }
        }
        
        // Scathas seit letztem Pet-Drop berechnen - nur wenn sichtbar
        if (model.scathaText.isVisible()) {
            // TODO: Diese Logik muss später implementiert werden wenn Pet-Drop-Tracking verfügbar ist
            model.scathaText.setText(Text.literal("Scathas since last pet drop: ??"));
        }
        
        // World info aktualisieren
        updateWorldInfo();
    }
    
    private void updateTimers() {
        long now = System.currentTimeMillis();
        long lastWorm = ClientState.get().getLastWormSpawnMs();
        long lastScatha = ClientState.get().getLastScathaSpawnMs();
        
        if (lastWorm > 0) {
            double secs = (now - lastWorm) / 1000.0;
            model.wormTimerText.setText(Text.literal(String.format("Worm seit: %.1fs", secs)));
        } else {
            model.wormTimerText.setText(Text.literal("Worm seit: --"));
        }
        
        if (lastScatha > 0) {
            double secs = (now - lastScatha) / 1000.0;
            model.scathaTimerText.setText(Text.literal(String.format("Scatha seit: %.1fs", secs)));
        } else {
            model.scathaTimerText.setText(Text.literal("Scatha seit: --"));
        }
    }
    
    private void updateWorldInfo() {
        var mc = MinecraftClient.getInstance();
        if (mc.world != null) {
            long timeOfDay = mc.world.getTimeOfDay() % 24000L;
            long day = mc.world.getTimeOfDay() / 24000L + 1;
            int hour = (int)((timeOfDay / 1000L + 6) % 24);
            int minute = (int)((timeOfDay % 1000L) * 60L / 1000L);
            model.dayTimeText.setText(Text.literal(String.format("Day %d (%02d:%02d) / 00:06:30", day, hour, minute)));
        } else {
            model.dayTimeText.setText(Text.literal("Day - (--:--) / --:--:--"));
        }
        
        if (mc.player != null) {
            int x = (int) Math.floor(mc.player.getX());
            int y = (int) Math.floor(mc.player.getY());
            int z = (int) Math.floor(mc.player.getZ());
            String facing = mc.player.getHorizontalFacing().asString();
            String dir = switch (facing) {
                case "north" -> "-Z";
                case "south" -> "+Z";
                case "west" -> "-X";
                case "east" -> "+X";
                default -> facing;
            };
            model.coordsText.setText(Text.literal(x + " " + y + " " + z + " " + dir));
        } else {
            model.coordsText.setText(Text.literal("- - - -"));
        }
    }

    public void draw(DrawContext ctx) {
        if (model == null) return;
        
        var matrices = ctx.getMatrices();
        matrices.push();
        matrices.translate(baseX, baseY, 0);
        matrices.scale(baseScale, baseScale, 1.0f);
        
        // Nutze die ursprünglich im V2Model definierten Positionen
        // Diese sind bereits optimal für ein tabellenbasiertes Layout ausgelegt
        
        // Verwende das Root-Container-System für koordinierte Darstellung
        if (model.root != null) {
            model.root.draw(ctx);
        } else {
            // Fallback: Direkte Zeichnung aller Elemente
            drawElementsDirect(ctx);
        }
        
        matrices.pop();
    }
    
    private void updateTitleIcon(namelessju.scathapro.fabric.FabricConfig cfg) {
        if (model == null || model.titleIcon == null || cfg == null) return;
        
        String iconName = cfg.overlayTitleIcon != null ? cfg.overlayTitleIcon : "default";
        
        // Sicherstellen, dass der Icon-Name gültig ist
        String[] validIcons = {"default", "mode_anime", "mode_custom", "mode_custom_overlay", "mode_meme", "scatha_spin"};
        boolean isValid = false;
        for (String valid : validIcons) {
            if (valid.equals(iconName)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) iconName = "default";
        
        // Neues Icon erstellen und ersetzen
        try {
            var newIcon = new V2IconTexture(
                Identifier.of("scathapro", "textures/overlay/scatha_icons/" + iconName + ".png"),
                2, 3, 1.1f, 512, 512
            );
            
            // Altes Icon aus Root entfernen, neues hinzufügen
            if (model.root != null) {
                model.root.getChildren().remove(model.titleIcon);
                model.titleIcon = newIcon;
                model.root.add(model.titleIcon);
            }
        } catch (Exception e) {
            // Fallback: behalte das aktuelle Icon
        }
    }
    
    private void drawElementsDirect(DrawContext ctx) {
        // Hintergrund zuerst
        if (model.background != null && model.background.isVisible()) {
            model.background.draw(ctx);
        }
        
        // Titel
        if (model.title != null && model.title.isVisible()) {
            model.title.draw(ctx);
        }
        
        // Tabellen-Header
        if (model.headerPets != null && model.headerPets.isVisible()) {
            model.headerPets.draw(ctx);
        }
        if (model.headerWorms != null && model.headerWorms.isVisible()) {
            model.headerWorms.draw(ctx);
        }
        if (model.headerScathas != null && model.headerScathas.isVisible()) {
            model.headerScathas.draw(ctx);
        }
        if (model.headerTotal != null && model.headerTotal.isVisible()) {
            model.headerTotal.draw(ctx);
        }
        
        // Pet Drop-Zahlen (linke Spalte)
        if (model.petsBlue != null && model.petsBlue.isVisible()) {
            model.petsBlue.draw(ctx);
        }
        if (model.petsPurple != null && model.petsPurple.isVisible()) {
            model.petsPurple.draw(ctx);
        }
        if (model.petsOrange != null && model.petsOrange.isVisible()) {
            model.petsOrange.draw(ctx);
        }
        
        // Kill-Zahlen (rechte Spalten)
        if (model.wormsCount != null && model.wormsCount.isVisible()) {
            model.wormsCount.draw(ctx);
        }
        if (model.scathasCount != null && model.scathasCount.isVisible()) {
            model.scathasCount.draw(ctx);
        }
        if (model.totalCount != null && model.totalCount.isVisible()) {
            model.totalCount.draw(ctx);
        }
        if (model.totalPercent != null && model.totalPercent.isVisible()) {
            model.totalPercent.draw(ctx);
        }
        
        // Classic stat texts (unter der Tabelle)
        if (model.scathaText != null && model.scathaText.isVisible()) {
            model.scathaText.draw(ctx);
        }
        if (model.wormText != null && model.wormText.isVisible()) {
            model.wormText.draw(ctx);
        }
        if (model.totalText != null && model.totalText.isVisible()) {
            model.totalText.draw(ctx);
        }
        if (model.streakText != null && model.streakText.isVisible()) {
            model.streakText.draw(ctx);
        }
        
        // Progress Bar
        if (model.bar != null && model.bar.isVisible()) {
            model.bar.draw(ctx);
        }
        
        // Timer texts (rechts)
        if (model.wormTimerText != null && model.wormTimerText.isVisible()) {
            model.wormTimerText.draw(ctx);
        }
        if (model.scathaTimerText != null && model.scathaTimerText.isVisible()) {
            model.scathaTimerText.draw(ctx);
        }
        
        // World info (unten)
        if (model.dayTimeText != null && model.dayTimeText.isVisible()) {
            model.dayTimeText.draw(ctx);
        }
        if (model.coordsText != null && model.coordsText.isVisible()) {
            model.coordsText.draw(ctx);
        }
        
        // Title Icon (falls verfügbar)
        if (model.titleIcon != null && model.titleIcon.isVisible()) {
            model.titleIcon.draw(ctx);
        }
        
        // Offizielle Texture-Icons (falls verfügbar)
        if (model.bluePetIcon != null && model.bluePetIcon.isVisible()) {
            model.bluePetIcon.draw(ctx);
        }
        if (model.purplePetIcon != null && model.purplePetIcon.isVisible()) {
            model.purplePetIcon.draw(ctx);
        }
        if (model.orangePetIcon != null && model.orangePetIcon.isVisible()) {
            model.orangePetIcon.draw(ctx);
        }
        if (model.scathaIcon != null && model.scathaIcon.isVisible()) {
            model.scathaIcon.draw(ctx);
        }
        if (model.wormIcon != null && model.wormIcon.isVisible()) {
            model.wormIcon.draw(ctx);
        }
        
        // Fallback Item-Icons (falls Texture-Icons nicht verfügbar)
        if (model.scathaItemIcon != null && model.scathaItemIcon.isVisible()) {
            model.scathaItemIcon.draw(ctx);
        }
        if (model.wormItemIcon != null && model.wormItemIcon.isVisible()) {
            model.wormItemIcon.draw(ctx);
        }
    }
}
