package namelessju.scathapro.fabric.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import namelessju.scathapro.fabric.FabricScathaPro;

/**
 * Fabric implementation of AlertTitleOverlay for Minecraft 1.21.5
 * Compatible with the original Alert system from the Forge version
 */
public class FabricAlertTitleOverlay {
    
    private final FabricScathaPro scathaPro;
    private final MinecraftClient mc;
    
    // Alert state
    private String currentTitle = "";
    private String currentSubtitle = "";
    
    // Timing (using ticks instead of milliseconds for compatibility)
    private int fadeInTicks = 0;
    private int stayTicks = 0;
    private int fadeOutTicks = 0;
    private int animationTicksLeft = 0;
    
    // Position and scale settings
    private float positionX = 0.5f;
    private float positionY = 0.4f;
    private float scale = 1.0f;
    
    public FabricAlertTitleOverlay(FabricScathaPro scathaPro) {
        this.scathaPro = scathaPro;
        this.mc = MinecraftClient.getInstance();
        updateSettings();
    }
    
    /**
     * Compatible with AlertTitle.display() - main interface method
     */
    public void displayTitle(String titleText, String subtitleText, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        // Clear vanilla title if showing our custom one
        if (titleText != null || subtitleText != null) {
            mc.inGameHud.clearTitle();
        }
        
        this.currentTitle = titleText != null ? titleText : "";
        this.currentSubtitle = subtitleText != null ? subtitleText : "";
        
        this.fadeInTicks = fadeInTicks;
        this.stayTicks = stayTicks;
        this.fadeOutTicks = fadeOutTicks;
        this.animationTicksLeft = getTotalAnimationTicks();
    }
    
    /**
     * Clears the current title display
     */
    public void clearTitle() {
        this.animationTicksLeft = 0;
        this.currentTitle = "";
        this.currentSubtitle = "";
    }
    
    /**
     * Called every tick to update animation state
     */
    public void tick() {
        if (animationTicksLeft > 0) {
            animationTicksLeft--;
            if (animationTicksLeft <= 0) {
                clearTitle();
            }
        }
    }
    
    /**
     * Render method called from HUD rendering
     */
    public void render(DrawContext context, float partialTicks) {
        if (animationTicksLeft <= 0) return;
        
        if (mc.player == null) return;
        
        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();
        
        // Calculate opacity based on animation phase
        float opacity = calculateOpacity(partialTicks);
        if (opacity <= 0.03f) return; // Skip rendering if nearly invisible
        
        int alpha = (int)(opacity * 255) << 24;
        
        // Calculate positions
        int centerX = (int)(screenWidth * positionX);
        int centerY = (int)(screenHeight * positionY);
        
        // Render title
        if (!currentTitle.isEmpty()) {
            Text titleComponent = parseFormattedText(currentTitle);
            int titleWidth = mc.textRenderer.getWidth(titleComponent);
            int titleX = centerX - (int)(titleWidth * scale * 2.0f / 2);
            int titleY = centerY - (int)(20 * scale);
            
            context.getMatrices().push();
            context.getMatrices().translate(titleX, titleY, 0);
            context.getMatrices().scale(scale * 4.0f, scale * 4.0f, 1.0f);
            
            int titleColor = 0xFFFFFF | alpha;
            context.drawText(mc.textRenderer, titleComponent, 0, 0, titleColor, true);
            
            context.getMatrices().pop();
        }
        
        // Render subtitle
        if (!currentSubtitle.isEmpty()) {
            Text subtitleComponent = parseFormattedText(currentSubtitle);
            int subtitleWidth = mc.textRenderer.getWidth(subtitleComponent);
            int subtitleX = centerX - (int)(subtitleWidth * scale / 2);
            int subtitleY = centerY + (int)(10 * scale);
            
            context.getMatrices().push();
            context.getMatrices().translate(subtitleX, subtitleY, 0);
            context.getMatrices().scale(scale * 2.0f, scale * 2.0f, 1.0f);
            
            int subtitleColor = 0xFFFFFF | alpha;
            context.drawText(mc.textRenderer, subtitleComponent, 0, 0, subtitleColor, true);
            
            context.getMatrices().pop();
        }
    }
    
    /**
     * Legacy showAlert methods for compatibility with FabricAlertManager
     */
    public void showAlert(String title, String subtitle, AlertType alertType) {
        showAlert(title, subtitle, alertType, null, 3000);
    }
    
    public void showAlert(String title, String subtitle, AlertType alertType, String rarity) {
        showAlert(title, subtitle, alertType, rarity, 3000);
    }
    
    public void showAlert(String title, String subtitle, AlertType alertType, String rarity, long duration) {
        // Convert duration from milliseconds to ticks (20 ticks = 1 second)
        int durationTicks = (int)(duration / 50); // 50ms per tick
        
        // Default timing: 10 ticks fade in, stay for most duration, 10 ticks fade out
        int fadeInTicks = 10;
        int fadeOutTicks = 10;
        int stayTicks = Math.max(20, durationTicks - fadeInTicks - fadeOutTicks);
        
        displayTitle(title, subtitle, fadeInTicks, stayTicks, fadeOutTicks);
    }
    
    /**
     * Static render method for previewing alerts
     */
    public void drawStatic(DrawContext context, String titleText, String subtitleText) {
        this.animationTicksLeft = 0;
        this.currentTitle = titleText != null ? titleText : "";
        this.currentSubtitle = subtitleText != null ? subtitleText : "";
        
        // Render with full opacity
        if (mc.player == null) return;
        
        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();
        
        int centerX = (int)(screenWidth * positionX);
        int centerY = (int)(screenHeight * positionY);
        
        // Render title
        if (!currentTitle.isEmpty()) {
            Text titleComponent = parseFormattedText(currentTitle);
            int titleWidth = mc.textRenderer.getWidth(titleComponent);
            int titleX = centerX - (int)(titleWidth * scale * 2.0f / 2);
            int titleY = centerY - (int)(20 * scale);
            
            context.getMatrices().push();
            context.getMatrices().translate(titleX, titleY, 0);
            context.getMatrices().scale(scale * 4.0f, scale * 4.0f, 1.0f);
            
            context.drawText(mc.textRenderer, titleComponent, 0, 0, 0xFFFFFFFF, true);
            
            context.getMatrices().pop();
        }
        
        // Render subtitle
        if (!currentSubtitle.isEmpty()) {
            Text subtitleComponent = parseFormattedText(currentSubtitle);
            int subtitleWidth = mc.textRenderer.getWidth(subtitleComponent);
            int subtitleX = centerX - (int)(subtitleWidth * scale / 2);
            int subtitleY = centerY + (int)(10 * scale);
            
            context.getMatrices().push();
            context.getMatrices().translate(subtitleX, subtitleY, 0);
            context.getMatrices().scale(scale * 2.0f, scale * 2.0f, 1.0f);
            
            context.drawText(mc.textRenderer, subtitleComponent, 0, 0, 0xFFFFFFFF, true);
            
            context.getMatrices().pop();
        }
    }
    
    private float calculateOpacity(float partialTicks) {
        int totalTicks = getTotalAnimationTicks();
        float ticksLeft = animationTicksLeft - partialTicks;
        
        if (animationTicksLeft > fadeOutTicks + stayTicks) {
            // Fade in phase
            return fadeInTicks > 0 ? (totalTicks - ticksLeft) / fadeInTicks : 0f;
        } else if (animationTicksLeft <= fadeOutTicks) {
            // Fade out phase
            return fadeOutTicks > 0 ? ticksLeft / fadeOutTicks : 0f;
        } else {
            // Stay phase
            return 1f;
        }
    }
    
    private Text parseFormattedText(String text) {
        if (text == null || text.isEmpty()) {
            return Text.empty();
        }
        
        // Convert legacy formatting codes to modern Text components
        return Text.literal(convertFormattingCodes(text));
    }
    
    private String convertFormattingCodes(String text) {
        // Convert section symbol formatting codes to Fabric Formatting
        text = text.replace(Formatting.BLACK.toString(), Formatting.BLACK.toString());
        text = text.replace(Formatting.DARK_BLUE.toString(), Formatting.DARK_BLUE.toString());
        text = text.replace(Formatting.DARK_GREEN.toString(), Formatting.DARK_GREEN.toString());
        text = text.replace(Formatting.DARK_AQUA.toString(), Formatting.DARK_AQUA.toString());
        text = text.replace(Formatting.DARK_RED.toString(), Formatting.DARK_RED.toString());
        text = text.replace(Formatting.DARK_PURPLE.toString(), Formatting.DARK_PURPLE.toString());
        text = text.replace(Formatting.GOLD.toString(), Formatting.GOLD.toString());
        text = text.replace(Formatting.GRAY.toString(), Formatting.GRAY.toString());
        text = text.replace(Formatting.DARK_GRAY.toString(), Formatting.DARK_GRAY.toString());
        text = text.replace(Formatting.BLUE.toString(), Formatting.BLUE.toString());
        text = text.replace(Formatting.GREEN.toString(), Formatting.GREEN.toString());
        text = text.replace(Formatting.AQUA.toString(), Formatting.AQUA.toString());
        text = text.replace(Formatting.RED.toString(), Formatting.RED.toString());
        text = text.replace(Formatting.LIGHT_PURPLE.toString(), Formatting.LIGHT_PURPLE.toString());
        text = text.replace(Formatting.YELLOW.toString(), Formatting.YELLOW.toString());
        text = text.replace(Formatting.WHITE.toString(), Formatting.WHITE.toString());
        text = text.replace(Formatting.OBFUSCATED.toString(), Formatting.OBFUSCATED.toString());
        text = text.replace(Formatting.BOLD.toString(), Formatting.BOLD.toString());
        text = text.replace(Formatting.STRIKETHROUGH.toString(), Formatting.STRIKETHROUGH.toString());
        text = text.replace(Formatting.UNDERLINE.toString(), Formatting.UNDERLINE.toString());
        text = text.replace(Formatting.ITALIC.toString(), Formatting.ITALIC.toString());
        text = text.replace(Formatting.RESET.toString(), Formatting.RESET.toString());
        
        return text;
    }
    
    public void updateSettings() {
        // TODO: Implement config reading when Config system is ported
        // For now use default values
        this.positionX = 0.5f;
        this.positionY = 0.4f;
        this.scale = 1.0f;
    }
    
    private int getTotalAnimationTicks() {
        return this.fadeInTicks + this.stayTicks + this.fadeOutTicks;
    }
}
