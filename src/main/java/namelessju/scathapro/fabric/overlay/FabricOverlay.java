package namelessju.scathapro.fabric.overlay;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.RenderLayer;
import namelessju.scathapro.fabric.FabricScathaPro;

public class FabricOverlay {
    private final FabricScathaPro scathaPro;
    
    private final OverlayText title;
    private final OverlayText scathaText;
    private final OverlayText wormText;
    private final OverlayText totalText;
    private final OverlayText streakText;
    private final OverlayProgressBar progress;
    private final OverlayText wormTimerText;
    private final OverlayText scathaTimerText;
    private final OverlayText petDropsText;
    private final OverlayText sessionStatsText;
    private final OverlayText magicFindText;
    private final OverlayText cooldownTimerText;
    private final OverlayText achievementProgressText;
    
    // Live-Scatha-Tracker (Phase 3B)
    private final FabricLiveScathaTracker liveScathaTracker;

    private boolean compact = false;
    private boolean showScatha = true, showWorm = true, showTotal = true, showStreak = true, showBar = true;
    private boolean showPetDrops = true, showSession = true, showMagicFind = true, showCooldown = true, showAchievements = false;
    private boolean showIcons = true;

    public FabricOverlay(FabricScathaPro scathaPro) {
        this.scathaPro = scathaPro;
        
        title = new OverlayText(Text.literal("Scatha-Pro"), 0xFFFFAA00, 0, 0, 1.2f);
        scathaText = new OverlayText(Text.literal(""), 0xFFFFFFFF, 16, 14, 1.0f);
        wormText = new OverlayText(Text.literal(""), 0xFFFFFFFF, 16, 26, 1.0f);
        totalText = new OverlayText(Text.literal(""), 0xFFAAAAAA, 16, 38, 1.0f);
        streakText = new OverlayText(Text.literal(""), 0xFF55FFFF, 16, 50, 1.0f);
        progress = new OverlayProgressBar(0, 64, 120, 8, 1.0f, 0xFF55FF55, 0x80555555);
        wormTimerText = new OverlayText(Text.literal(""), 0xFFBBBBBB, 16, 76, 0.9f);
        scathaTimerText = new OverlayText(Text.literal(""), 0xFFBBBBBB, 16, 86, 0.9f);
        petDropsText = new OverlayText(Text.literal(""), 0xFFFF55FF, 16, 96, 0.9f);
        sessionStatsText = new OverlayText(Text.literal(""), 0xFF55FF55, 16, 106, 0.9f);
        magicFindText = new OverlayText(Text.literal(""), 0xFF55FFAA, 16, 116, 0.9f);
        cooldownTimerText = new OverlayText(Text.literal(""), 0xFFFFAA55, 16, 126, 0.9f);
        achievementProgressText = new OverlayText(Text.literal(""), 0xFFAA55FF, 16, 136, 0.9f);
        
        // Live-Scatha-Tracker initialisieren
        liveScathaTracker = new FabricLiveScathaTracker(scathaPro);
    }

    public void update() {
        // Kill-Statistiken aus FabricScathaPro.variables
        int scathaKills = scathaPro.variables.scathaKills;
        int regularWormKills = scathaPro.variables.regularWormKills;
        int totalKills = (scathaKills >= 0 && regularWormKills >= 0) ? scathaKills + regularWormKills : -1;
        
        // Kill-Texte aktualisieren
        scathaText.setText(Text.literal("§6Scathas: §f" + formatNumber(scathaKills)));
        wormText.setText(Text.literal("§7Worms: §f" + formatNumber(regularWormKills)));
        totalText.setText(Text.literal("§7Total: §f" + formatNumber(totalKills)));
        
        // Scatha-Prozentsatz berechnen
        if (totalKills > 0 && scathaKills >= 0) {
            double percentage = (double) scathaKills / totalKills * 100.0;
            streakText.setText(Text.literal(String.format("§bScatha Rate: §f%.1f%%", percentage)));
        } else {
            streakText.setText(Text.literal("§bScatha Rate: §7Unknown"));
        }
        
        // Progress-Bar: Fortschritt zu nächstem 100er Milestone
        float progress = scathaKills >= 0 ? (scathaKills % 100) / 100.0f : 0.0f;
        this.progress.setProgress(progress);
        
        // Timer aktualisieren
        long now = System.currentTimeMillis();
        updateSpawnTimers(now);
        
        // Pet-Drops aktualisieren
        updatePetDropsDisplay();
        
        // Session-Stats aktualisieren
        updateSessionStats();
        
        // Advanced Features aktualisieren
        updateMagicFindDisplay();
        updateCooldownTimer(now);
        updateAchievementProgress();
        
        // Live-Scatha-Tracker aktualisieren
        liveScathaTracker.update();
    }

    private void updateSpawnTimers(long now) {
        // Worm Spawn Timer
        if (scathaPro.variables.lastWormSpawnTime > 0) {
            double secs = (now - scathaPro.variables.lastWormSpawnTime) / 1000.0;
            wormTimerText.setText(Text.literal(String.format("§7Last Worm: §f%.1fs ago", secs)));
        } else {
            wormTimerText.setText(Text.literal("§7Last Worm: §8Never"));
        }
        
        // Scatha Spawn Timer (separate tracking nötig)
        if (scathaPro.variables.lastScathaKillTime > 0) {
            double secs = (now - scathaPro.variables.lastScathaKillTime) / 1000.0;
            scathaTimerText.setText(Text.literal(String.format("§6Last Scatha: §f%.1fs ago", secs)));
        } else {
            scathaTimerText.setText(Text.literal("§6Last Scatha: §8Never"));
        }
    }
    
    private void updatePetDropsDisplay() {
        int rare = scathaPro.variables.rarePetDrops;
        int epic = scathaPro.variables.epicPetDrops;
        int legendary = scathaPro.variables.legendaryPetDrops;
        int total = rare + epic + legendary;
        
        String petText = String.format("§dPets: §9%d§7/§5%d§7/§6%d §7(Total: %d)", 
                                       rare, epic, legendary, total);
        petDropsText.setText(Text.literal(petText));
    }
    
    private void updateSessionStats() {
        int sessionKills = scathaPro.variables.sessionScathaKills;
        String sessionText = sessionKills > 0 ? 
            String.format("§aSession: §f%d Scathas", sessionKills) :
            "§aSession: §7No kills yet";
        sessionStatsText.setText(Text.literal(sessionText));
    }
    
    private void updateMagicFindDisplay() {
        String magicFindStr = scathaPro.variables.getMagicFindString();
        String petLuckStr = scathaPro.variables.getPetLuckString();
        String totalMfStr = scathaPro.variables.getEffectiveMagicFindString();
        
        String magicFindDisplay = String.format("§b✯ MF: %s §7| §d♣ PL: %s §7| §bEMF: %s", 
                                               magicFindStr, petLuckStr, totalMfStr);
        magicFindText.setText(Text.literal(magicFindDisplay));
    }
    
    private void updateCooldownTimer(long now) {
        // Worm Spawn Cooldown Timer
        if (scathaPro.variables.wormSpawnCooldownStartTime > 0) {
            long elapsed = now - scathaPro.variables.wormSpawnCooldownStartTime;
            long remaining = namelessju.scathapro.fabric.Constants.wormSpawnCooldown - elapsed;
            
            if (remaining > 0) {
                double seconds = remaining / 1000.0;
                cooldownTimerText.setText(Text.literal(String.format("§cCooldown: §f%.1fs", seconds)));
            } else {
                cooldownTimerText.setText(Text.literal("§aWorms Ready to Spawn!"));
            }
        } else {
            cooldownTimerText.setText(Text.literal("§7Cooldown: §8Unknown"));
        }
    }
    
    private void updateAchievementProgress() {
        // Beispiel: Fortschritt zu nächstem Milestone (alle 100 Scatha-Kills)
        int scathaKills = scathaPro.variables.scathaKills;
        if (scathaKills >= 0) {
            int nextMilestone = ((scathaKills / 100) + 1) * 100;
            int remaining = nextMilestone - scathaKills;
            
            String achievementText = String.format("§dNext Milestone: §f%d §7(§f%d §7remaining)", 
                                                  nextMilestone, remaining);
            achievementProgressText.setText(Text.literal(achievementText));
        } else {
            achievementProgressText.setText(Text.literal("§dAchievements: §7Tracking..."));
        }
    }
    
    private String formatNumber(int number) {
        if (number < 0) return "§k?";
        if (number >= 1000000) return String.format("%.1fM", number / 1000000.0);
        if (number >= 1000) return String.format("%.1fK", number / 1000.0);
        return String.valueOf(number);
    }
    
    /**
     * Berechnet die aktuelle Höhe des Haupt-Overlays
     */
    private int getOverlayHeight() {
        int height = 0;
        
        // Title
        height += 12;
        
        // Haupt-Elemente
        if (showScatha) height += 12;
        if (showWorm) height += 12;
        if (showTotal) height += 12;
        if (showStreak) height += 12;
        if (showBar) height += 12;
        
        // Timer
        height += 12; // wormTimerText
        height += 12; // scathaTimerText
        
        // Advanced Features
        if (showPetDrops) height += 12;
        if (showSession) height += 12;
        if (showMagicFind) height += 12;
        if (showCooldown) height += 12;
        if (showAchievements) height += 12;
        
        return height;
    }

    public void draw(DrawContext ctx) {
        title.draw(ctx);
        // Icons Zeichnen (als Items), wenn konfiguriert
        var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
        boolean icons = cfg != null && cfg.overlayShowIcons;
        float iconScale = cfg != null ? Math.max(0.5f, cfg.overlayIconScale) : 1.0f;

        // Text links einrücken, wenn Icons aktiv sind
        int baseX = icons ? 16 : 0;
        scathaText.setPosition(baseX, scathaText.getY());
        wormText.setPosition(baseX, wormText.getY());
        totalText.setPosition(baseX, totalText.getY());
        streakText.setPosition(baseX, streakText.getY());
        wormTimerText.setPosition(baseX, wormTimerText.getY());
        scathaTimerText.setPosition(baseX, scathaTimerText.getY());

        if (icons) {
            int texSize = cfg != null ? Math.max(16, cfg.overlayIconTexSize) : 512;
            
            // Haupt-Icons mit Custom-Texturen
            boolean drawnScatha = drawTextureIcon(ctx, Identifier.of("scathapro", "textures/overlay/scatha.png"), 0, scathaText.getY(), iconScale, texSize, texSize);
            boolean drawnWorm = drawTextureIcon(ctx, Identifier.of("scathapro", "textures/overlay/worm.png"), 0, wormText.getY(), iconScale, texSize, texSize);
            
            // Fallback auf Items wenn Texturen nicht verfügbar
            if (!drawnScatha) drawItemIcon(ctx, new ItemStack(Items.AMETHYST_SHARD), 0, scathaText.getY(), iconScale);
            if (!drawnWorm) drawItemIcon(ctx, new ItemStack(Items.STRING), 0, wormText.getY(), iconScale);
            
            // Weitere Icons
            drawItemIcon(ctx, new ItemStack(Items.PAPER), 0, totalText.getY(), iconScale); // Total
            drawItemIcon(ctx, new ItemStack(Items.FIRE_CHARGE), 0, streakText.getY(), iconScale); // Streak/Rate
            
            // Timer-Icons
            drawItemIcon(ctx, new ItemStack(Items.CLOCK), 0, wormTimerText.getY(), iconScale);
            drawItemIcon(ctx, new ItemStack(Items.ENDER_EYE), 0, scathaTimerText.getY(), iconScale);
            
            // Pet-Drop-Icons - verwende die Custom-Pet-Texturen
            if (showPetDrops) {
                boolean drawnRarePet = drawTextureIcon(ctx, Identifier.of("scathapro", "textures/overlay/scatha_pet_rare.png"), -2, petDropsText.getY(), iconScale * 0.7f, 64, 64);
                if (!drawnRarePet) drawItemIcon(ctx, new ItemStack(Items.TROPICAL_FISH), 0, petDropsText.getY(), iconScale);
            }
            
            // Session-Icon
            if (showSession) {
                drawItemIcon(ctx, new ItemStack(Items.EXPERIENCE_BOTTLE), 0, sessionStatsText.getY(), iconScale);
            }
            
            // Magic Find-Icon
            if (showMagicFind) {
                drawItemIcon(ctx, new ItemStack(Items.ENCHANTED_BOOK), 0, magicFindText.getY(), iconScale);
            }
            
            // Cooldown-Icon
            if (showCooldown) {
                drawItemIcon(ctx, new ItemStack(Items.REDSTONE), 0, cooldownTimerText.getY(), iconScale);
            }
            
            // Achievement-Icon
            if (showAchievements) {
                drawItemIcon(ctx, new ItemStack(Items.NETHER_STAR), 0, achievementProgressText.getY(), iconScale);
            }
        }
        if (showScatha) scathaText.draw(ctx);
        if (showWorm) wormText.draw(ctx);
        if (showTotal) totalText.draw(ctx);
        if (showStreak) streakText.draw(ctx);
        if (showBar) progress.draw(ctx);
        wormTimerText.draw(ctx);
        scathaTimerText.draw(ctx);
        if (showPetDrops) petDropsText.draw(ctx);
        if (showSession) sessionStatsText.draw(ctx);
        if (showMagicFind) magicFindText.draw(ctx);
        if (showCooldown) cooldownTimerText.draw(ctx);
        if (showAchievements) achievementProgressText.draw(ctx);
        
        // Live-Scatha-Tracker renderen (unter dem Hauptoverlay), wenn aktiviert
        if (cfg != null && cfg.overlayShowLiveTracker && liveScathaTracker.isEnabled()) {
            int trackerY = getOverlayHeight() + 10;
            liveScathaTracker.render(ctx, 0, trackerY);
        }
    }

    private void drawItemIcon(DrawContext ctx, ItemStack stack, int x, int y, float scale) {
        var m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(scale, scale, 1.0f);
        ctx.drawItem(stack, 0, 0);
        m.pop();
    }

    private boolean drawTextureIcon(DrawContext ctx, Identifier id, int x, int y, float scale, int texW, int texH) {
        try {
            var m = ctx.getMatrices();
            m.push();
            m.translate(x, y, 0);
            // Skaliere proportional auf 16x16, immer quadratisch (kein Stretching)
            float k = (16f / Math.max(texW, texH)) * scale;
            m.scale(k, k, 1.0f);
            // Zeichne die gesamte Textur (Region = volle Größe), Matrix übernimmt Downsizing
            ctx.drawTexture(RenderLayer::getGuiTextured, id, 0, 0, 0f, 0f, texW, texH, texW, texH);
            m.pop();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public void syncConfig(namelessju.scathapro.fabric.FabricConfig cfg) {
        if (cfg == null) return;
        boolean newCompact = cfg.overlayCompactMode;
        boolean changedLayout = newCompact != this.compact;
        this.compact = newCompact;
        this.showScatha = cfg.overlayShowScatha;
        this.showWorm = cfg.overlayShowWorm;
        this.showTotal = cfg.overlayShowTotal;
        this.showStreak = cfg.overlayShowStreak;
        this.showBar = cfg.overlayShowBar;
        // Advanced Features - jetzt voll konfigurierbar!
        this.showPetDrops = cfg.overlayShowPetDrops;
        this.showSession = cfg.overlayShowSession;
        this.showMagicFind = cfg.overlayShowMagicFind;
        this.showCooldown = cfg.overlayShowCooldown;
        this.showAchievements = cfg.overlayShowAchievements;
        this.showIcons = cfg.overlayShowIcons;
        
        // Live-Scatha-Tracker konfigurieren
        liveScathaTracker.setEnabled(cfg.overlayShowLiveTracker);
        
        applyColors(cfg);
        if (changedLayout) applyLayout();
    }

    private void applyColors(namelessju.scathapro.fabric.FabricConfig cfg) {
        String profile = (cfg.colorProfile == null) ? "default" : cfg.colorProfile.toLowerCase();
        int titleCol, primary, secondary, streakCol, barFg, barBg;
        switch (profile) {
            case "dark":
                titleCol = 0xFFFFD37A; primary = 0xFFE6E6E6; secondary = 0xFFB0B0B0; streakCol = 0xFF7AE8FF; barFg = 0xFF44CC44; barBg = 0x80333333; break;
            case "high":
            case "high_contrast":
                titleCol = 0xFFFFFF00; primary = 0xFFFFFFFF; secondary = 0xFFFFFFFF; streakCol = 0xFF00FFFF; barFg = 0xFFFFFF00; barBg = 0xFF000000; break;
            default:
                titleCol = 0xFFFFAA00; primary = 0xFFFFFFFF; secondary = 0xFFAAAAAA; streakCol = 0xFF55FFFF; barFg = 0xFF55FF55; barBg = 0x80555555; break;
        }
        title.setColor(titleCol);
        scathaText.setColor(primary);
        wormText.setColor(primary);
        totalText.setColor(secondary);
        streakText.setColor(streakCol);
        progress.setColors(barFg, barBg);
    }

    private void applyLayout() {
        if (!compact) {
            // Standard-Layout
            title.setPosition(0, 0);
            scathaText.setPosition(0, 14);
            wormText.setPosition(0, 26);
            totalText.setPosition(0, 38);
            streakText.setPosition(0, 50);
            progress.setPosition(0, 64);
            wormTimerText.setPosition(0, 76);
            scathaTimerText.setPosition(0, 86);
            petDropsText.setPosition(0, 96);
            sessionStatsText.setPosition(0, 106);
            magicFindText.setPosition(0, 116);
            cooldownTimerText.setPosition(0, 126);
            achievementProgressText.setPosition(0, 136);
            scathaText.setScale(1.0f);
            wormText.setScale(1.0f);
            totalText.setScale(1.0f);
            streakText.setScale(1.0f);
            progress.setScale(1.0f);
        } else {
            // Kompakt: kleinere Abstände/Skalierung
            title.setPosition(0, 0);
            scathaText.setPosition(0, 11);
            wormText.setPosition(0, 20);
            totalText.setPosition(0, 29);
            streakText.setPosition(0, 38);
            progress.setPosition(0, 48);
            wormTimerText.setPosition(0, 58);
            scathaTimerText.setPosition(0, 68);
            petDropsText.setPosition(0, 76);
            sessionStatsText.setPosition(0, 84);
            magicFindText.setPosition(0, 92);
            cooldownTimerText.setPosition(0, 100);
            achievementProgressText.setPosition(0, 108);
            scathaText.setScale(0.9f);
            wormText.setScale(0.9f);
            totalText.setScale(0.9f);
            streakText.setScale(0.9f);
            progress.setScale(0.9f);
        }
    }
}
