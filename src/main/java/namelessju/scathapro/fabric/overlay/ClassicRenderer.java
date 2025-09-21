package namelessju.scathapro.fabric.overlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import namelessju.scathapro.fabric.state.ClientState;
import namelessju.scathapro.fabric.FabricConfig;

/**
 * Classic Overlay Renderer - nachbau des ursprünglichen Forge-Designs
 * Verwendet das gleiche Layout, die gleichen Farben und Schriftgrößen wie das Original
 */
public class ClassicRenderer {
    private final MinecraftClient mc;
    
    // Position und Skalierung
    private int baseX = 5;
    private int baseY = 5;
    private float baseScale = 1.0f;
    
    // Original Forge Farben
    private static final int COLOR_TITLE = 0xFFFFAA00; // Gold
    private static final int COLOR_HEADER_GREEN = 0xFF55FF55; // Grün
    private static final int COLOR_HEADER_YELLOW = 0xFFFFFF55; // Gelb  
    private static final int COLOR_HEADER_WHITE = 0xFFFFFFFF; // Weiß
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_GRAY = 0xFFAAAAAA;
    private static final int COLOR_PET_RARE = 0xFF5555FF; // Blau
    private static final int COLOR_PET_EPIC = 0xFFAA00AA; // Lila
    private static final int COLOR_PET_LEGENDARY = 0xFFFFAA00; // Orange/Gold
    
    // Icon-Skalierung (wie im Original)
    private static final float ICON_SCALE = 0.145f;
    private static final float LARGE_ICON_SCALE = 0.688f;
    
    public ClassicRenderer() {
        this.mc = MinecraftClient.getInstance();
    }
    
    public void syncConfig(FabricConfig cfg) {
        if (cfg != null) {
            this.baseX = Math.max(0, cfg.overlayX);
            this.baseY = Math.max(0, cfg.overlayY);
            this.baseScale = Math.max(0.1f, cfg.overlayScale);
        }
    }
    
    public void draw(DrawContext context) {
        if (mc.player == null) return;
        
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(baseX, baseY, 0);
        matrices.scale(baseScale, baseScale, 1.0f);
        
        drawClassicOverlay(context);
        
        matrices.pop();
    }
    
    private void drawClassicOverlay(DrawContext context) {
        var state = ClientState.get();
        
        // === EXAKTE NACHBILDUNG VON BILD 2 ===
        // Kompakter, horizontaler Stil wie im Original Forge-Overlay
        
        int x = 0;
        int y = 0;
        
        // Zeile 1: Scatha-Icon + Titel "Scatha Farming!"
        drawText(context, "Scatha Farming!", x, y, 1.0f, COLOR_TITLE); // Gold
        
        // Zeile 2: Pet-Header und Kill-Stats in einer Zeile  
        y += 12;
        drawText(context, "Pets", x, y, 0.9f, COLOR_HEADER_GREEN); // Grün
        drawText(context, "Worms", x + 60, y, 0.9f, COLOR_HEADER_YELLOW); // Gelb
        drawText(context, "Scathas", x + 120, y, 0.9f, COLOR_HEADER_YELLOW); // Gelb 
        drawText(context, "Total", x + 180, y, 0.9f, COLOR_HEADER_WHITE); // Weiß
        
        // Zeile 3: Pet-Icons und Zahlen horizontal
        y += 12;
        
        // Pet Drops mit kleinen Icons
        drawPetIconSmall(context, "rare", x, y);
        drawText(context, String.valueOf(state.getBluePetDrops()), x + 10, y + 1, 0.8f, COLOR_PET_RARE);
        
        drawPetIconSmall(context, "epic", x, y + 8);
        drawText(context, String.valueOf(state.getPurplePetDrops()), x + 10, y + 9, 0.8f, COLOR_PET_EPIC);
        
        drawPetIconSmall(context, "legendary", x, y + 16);
        drawText(context, String.valueOf(state.getOrangePetDrops()), x + 10, y + 17, 0.8f, COLOR_PET_LEGENDARY);
        
        // Worms (mittlere Spalte)
        drawText(context, String.valueOf(state.getWormKills()), x + 60, y + 1, 0.9f, COLOR_TEXT_WHITE);
        drawText(context, "(" + calculateWormRate() + "%)", x + 60, y + 10, 0.7f, COLOR_TEXT_GRAY);
        drawText(context, "No worms spawned yet", x + 60, y + 18, 0.7f, COLOR_TEXT_GRAY);
        
        // Scathas (rechte Spalte)
        drawText(context, String.valueOf(state.getScathaKills()), x + 120, y + 1, 0.9f, COLOR_TEXT_WHITE);
        drawText(context, "(" + calculateScathaRate() + "%)", x + 120, y + 10, 0.7f, COLOR_TEXT_GRAY);
        
        // Total (ganz rechts)
        drawText(context, String.valueOf(state.getTotalKills()), x + 180, y + 1, 0.9f, COLOR_TEXT_WHITE);
        drawText(context, "(100.0%)", x + 180, y + 10, 0.7f, COLOR_TEXT_GRAY);
        
        // Zeile 4: Zusätzliche Informationen  
        y += 30;
        long scathasSinceDrop = calculateScathasSinceLastPetDrop();
        drawText(context, "Scathas since last pet drop: " + (scathasSinceDrop >= 0 ? scathasSinceDrop : "??"), 
                x, y, 0.8f, COLOR_TEXT_WHITE);
        
        // Zeile 5: Day und Time
        y += 10;
        drawText(context, getDayTimeText(), x, y, 0.8f, COLOR_TEXT_WHITE);
        
        // Zeile 6: Koordinaten 
        y += 10;
        drawText(context, getCoordinatesText(), x, y, 0.7f, COLOR_TEXT_GRAY);
    }
    
    private void drawText(DrawContext context, String text, int x, int y, float scale, int color) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawText(mc.textRenderer, Text.literal(text), 0, 0, color, false);
        context.getMatrices().pop();
    }
    
    private void drawScathaIcon(DrawContext context, int x, int y) {
        // Großes Scatha-Icon (wie im Original)
        try {
            Identifier iconId = Identifier.of("scathapro", "textures/overlay/scatha_icons/default.png");
            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0);
            context.getMatrices().scale(LARGE_ICON_SCALE, LARGE_ICON_SCALE, 1.0f);
            context.drawTexture(RenderLayer::getGuiTextured, iconId, 0, 0, 0f, 0f, 32, 32, 32, 32);
            context.getMatrices().pop();
        } catch (Exception e) {
            // Fallback: Zeichne einfachen Text
            drawText(context, "S", x, y, LARGE_ICON_SCALE * 2, COLOR_TITLE);
        }
    }
    
    private void drawScathaIconSmall(DrawContext context, int x, int y) {
        // Kleines Scatha-Icon für die Scatha-Spalte
        try {
            Identifier iconId = Identifier.of("scathapro", "textures/overlay/scatha.png");
            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0);
            context.getMatrices().scale(0.08f, 0.08f, 1.0f);
            context.drawTexture(RenderLayer::getGuiTextured, iconId, 0, 0, 0f, 0f, 512, 256, 512, 256);
            context.getMatrices().pop();
        } catch (Exception e) {
            // Fallback
        }
    }
    
    private void drawWormIcon(DrawContext context, int x, int y) {
        // Kleines Worm-Icon für die Worm-Spalte
        try {
            Identifier iconId = Identifier.of("scathapro", "textures/overlay/worm.png");
            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0);
            context.getMatrices().scale(0.08f, 0.08f, 1.0f);
            context.drawTexture(RenderLayer::getGuiTextured, iconId, 0, 0, 0f, 0f, 512, 256, 512, 256);
            context.getMatrices().pop();
        } catch (Exception e) {
            // Fallback
        }
    }
    
    private void drawPetIcon(DrawContext context, String rarity, int x, int y) {
        // Pet-Icons (wie im Original)
        try {
            Identifier iconId = Identifier.of("scathapro", "textures/overlay/scatha_pet_" + rarity + ".png");
            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0);
            context.getMatrices().scale(ICON_SCALE, ICON_SCALE, 1.0f);
            context.drawTexture(RenderLayer::getGuiTextured, iconId, 0, 0, 0f, 0f, 64, 64, 64, 64);
            context.getMatrices().pop();
        } catch (Exception e) {
            // Fallback: Zeige ersten Buchstaben der Rarity
            String letter = rarity.substring(0, 1).toUpperCase();
            drawText(context, letter, x, y, ICON_SCALE * 2, 
                rarity.equals("rare") ? COLOR_PET_RARE : 
                rarity.equals("epic") ? COLOR_PET_EPIC : COLOR_PET_LEGENDARY);
        }
    }
    
    private void drawPetIconSmall(DrawContext context, String rarity, int x, int y) {
        // Sehr kleine Pet-Icons für Classic Layout
        try {
            Identifier iconId = Identifier.of("scathapro", "textures/overlay/scatha_pet_" + rarity + ".png");
            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0);
            context.getMatrices().scale(0.07f, 0.07f, 1.0f); // Sehr kleine Icons
            context.drawTexture(RenderLayer::getGuiTextured, iconId, 0, 0, 0f, 0f, 64, 64, 64, 64);
            context.getMatrices().pop();
        } catch (Exception e) {
            // Fallback: Zeige ersten Buchstaben der Rarity
            String letter = rarity.substring(0, 1).toUpperCase();
            drawText(context, letter, x, y, 0.5f, 
                rarity.equals("rare") ? COLOR_PET_RARE : 
                rarity.equals("epic") ? COLOR_PET_EPIC : COLOR_PET_LEGENDARY);
        }
    }
    
    // === Berechnung der Stats (wie im Original) ===
    
    private String calculateWormRate() {
        var state = ClientState.get();
        int total = state.getTotalKills();
        int worms = state.getWormKills();
        return total > 0 ? String.format("%.1f", (worms * 100.0 / total)) : "0.0";
    }
    
    private String calculateScathaRate() {
        var state = ClientState.get();
        int total = state.getTotalKills();
        int scathas = state.getScathaKills();
        return total > 0 ? String.format("%.1f", (scathas * 100.0 / total)) : "0.0";
    }
    
    private String calculateTotalRate() {
        // Im Original: Verhältnis zu irgendeinem Zielwert
        return "100.0"; // Placeholder
    }
    
    private long calculateScathasSinceLastPetDrop() {
        // TODO: Implementierung für Pet-Drop-Tracking
        return -1; // -1 = unbekannt
    }
    
    private String getDayTimeText() {
        if (mc.world != null) {
            long timeOfDay = mc.world.getTimeOfDay() % 24000L;
            long day = mc.world.getTimeOfDay() / 24000L + 1;
            int hour = (int)((timeOfDay / 1000L + 6) % 24);
            int minute = (int)((timeOfDay % 1000L) * 60L / 1000L);
            return String.format("Day %d (%02d:%02d)", day, hour, minute);
        }
        return "Day - (--:--)";
    }
    
    private String getCoordinatesText() {
        if (mc.player != null) {
            int x = (int) Math.floor(mc.player.getX());
            int y = (int) Math.floor(mc.player.getY());
            int z = (int) Math.floor(mc.player.getZ());
            String facing = mc.player.getHorizontalFacing().asString();
            return String.format("%d %d %d (%s)", x, y, z, facing.toUpperCase());
        }
        return "- - - (-)";
    }
}