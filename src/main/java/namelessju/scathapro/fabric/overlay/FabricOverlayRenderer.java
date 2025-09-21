package namelessju.scathapro.fabric.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import namelessju.scathapro.fabric.FabricScathaPro;

/**
 * Fabric Overlay Renderer
 * Registriert und behandelt HUD-Rendering-Events für das Overlay
 */
public class FabricOverlayRenderer
{
    private final FabricScathaPro scathaPro;
    private final MinecraftClient mc;
    
    // Timing für Updates
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 100; // Update alle 100ms
    
    public FabricOverlayRenderer(FabricScathaPro scathaPro)
    {
        this.scathaPro = scathaPro;
        this.mc = MinecraftClient.getInstance();
        
        registerHudRenderer();
    }
    
    /**
     * Registriert den HUD-Renderer bei Fabric
     */
    private void registerHudRenderer()
    {
        HudRenderCallback.EVENT.register(this::onHudRender);
        scathaPro.log("Overlay-Renderer registriert");
    }
    
    /**
     * HUD-Render-Callback - wird jeden Frame aufgerufen
     */
    private void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter)
    {
        if (!shouldRenderOverlay()) return;
        
        // Update-Logik (nicht jeden Frame)
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= UPDATE_INTERVAL)
        {
            scathaPro.getOverlay().update();
            lastUpdateTime = currentTime;
        }
        
        // Overlay rendern
        renderOverlay(drawContext);
    }
    
    /**
     * Prüft ob das Overlay gerendert werden soll
     */
    private boolean shouldRenderOverlay()
    {
        return mc.player != null && 
               !mc.getDebugHud().shouldShowDebugHud() && 
               mc.currentScreen == null &&
               scathaPro.isInCrystalHollows();
    }
    
    /**
     * Rendert das Overlay
     */
    private void renderOverlay(DrawContext drawContext)
    {
        try 
        {
            // Position berechnen (später konfigurierbar)
            int x = 10; // Standardposition
            int y = 10;
            
            // Matrix-Transformationen für Positionierung
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(x, y, 0);
            
            // Overlay zeichnen
            scathaPro.getOverlay().draw(drawContext);
            
            drawContext.getMatrices().pop();
        }
        catch (Exception e)
        {
            scathaPro.logError("Fehler beim Overlay-Rendering: " + e.getMessage());
        }
    }
    
    /**
     * Für Debug-Zwecke: Erzwinge Overlay-Update
     */
    public void forceUpdate()
    {
        scathaPro.getOverlay().update();
        lastUpdateTime = System.currentTimeMillis();
    }
}