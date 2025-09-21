package namelessju.scathapro.fabric.overlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.entitydetection.FabricDetectedEntity;
import namelessju.scathapro.fabric.entitydetection.FabricDetectedWorm;

import java.util.ArrayList;
import java.util.List;

/**
 * Live-Scatha-Tracker
 * Phase 3B: Zeigt aktive Scathas in der Nähe mit Entfernung und Status
 */
public class FabricLiveScathaTracker
{
    private final FabricScathaPro scathaPro;
    private final MinecraftClient mc;
    
    // Tracker-Einstellungen
    private boolean enabled = true;
    private double maxTrackingDistance = 100.0; // Maximale Entfernung für Tracking
    
    // Visual-Elemente
    private final List<TrackedScatha> trackedScathas = new ArrayList<>();
    
    public FabricLiveScathaTracker(FabricScathaPro scathaPro)
    {
        this.scathaPro = scathaPro;
        this.mc = MinecraftClient.getInstance();
    }
    
    /**
     * Aktualisiert die Liste der verfolgten Scathas
     */
    public void update()
    {
        if (!enabled || mc.player == null) return;
        
        trackedScathas.clear();
        Vec3d playerPos = mc.player.getPos();
        
        // Durchsuche alle aktiven DetectedWorms nach Scathas
        // TODO: Implementiere getActiveWorms() in FabricDetectedEntity
        /*
        for (FabricDetectedWorm worm : FabricDetectedEntity.getActiveScathas()) {
            if (worm.isScatha) {
                double distance = playerPos.distanceTo(worm.getEntity().getPos());
                
                if (distance <= maxTrackingDistance) {
                    TrackedScatha tracked = new TrackedScatha(worm, distance);
                    trackedScathas.add(tracked);
                }
            }
        }
        */
        
        // Temporäre Demo-Daten
        if (Math.random() < 0.1) { // 10% Chance auf Demo-Scatha
            TrackedScatha demoScatha = new TrackedScatha(null, Math.random() * 50 + 10);
            demoScatha.isDemo = true;
            trackedScathas.add(demoScatha);
        }
        
        // Sortiere nach Entfernung
        trackedScathas.sort((a, b) -> Double.compare(a.distance, b.distance));
    }
    
    /**
     * Rendert den Live-Scatha-Tracker
     */
    public void render(DrawContext drawContext, int x, int y)
    {
        if (!enabled || trackedScathas.isEmpty()) return;
        
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(x, y, 0);
        
        // Header
        String headerText = "§6Live Scathas §7(" + trackedScathas.size() + ")";
        drawContext.drawTextWithShadow(mc.textRenderer, Text.literal(headerText), 0, 0, 0xFFFFAA00);
        
        int currentY = 12;
        
        // Render jede verfolgte Scatha
        for (int i = 0; i < Math.min(trackedScathas.size(), 5); i++) // Max 5 anzeigen
        {
            TrackedScatha tracked = trackedScathas.get(i);
            renderTrackedScatha(drawContext, tracked, 0, currentY);
            currentY += 10;
        }
        
        drawContext.getMatrices().pop();
    }
    
    /**
     * Rendert eine einzelne verfolgte Scatha
     */
    private void renderTrackedScatha(DrawContext drawContext, TrackedScatha tracked, int x, int y)
    {
        // Icon (kleines Scatha-Icon)
        boolean drawnIcon = drawSmallScathaIcon(drawContext, x, y);
        if (!drawnIcon) {
            // Fallback: Kleiner Punkt
            drawContext.fill(x, y + 2, x + 6, y + 8, 0xFFFFAA00);
        }
        
        // Scatha-Info
        String status = tracked.getStatus();
        String distance = String.format("%.1fm", tracked.distance);
        String lifetime = tracked.getLifetimeString();
        
        String infoText = String.format("§6Scatha §7%s §8| §f%s §8| §7%s", 
                                       status, distance, lifetime);
        
        drawContext.drawTextWithShadow(mc.textRenderer, Text.literal(infoText), x + 10, y, 0xFFFFFFFF);
        
        // Entfernungs-Farb-Indikator
        int distanceColor = getDistanceColor(tracked.distance);
        drawContext.fill(x + 190, y + 2, x + 196, y + 8, distanceColor);
    }
    
    /**
     * Zeichnet ein kleines Scatha-Icon
     */
    private boolean drawSmallScathaIcon(DrawContext drawContext, int x, int y)
    {
        try {
            Identifier scathaIcon = Identifier.of("scathapro", "textures/overlay/scatha.png");
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(x, y, 0);
            drawContext.getMatrices().scale(0.125f, 0.125f, 1.0f); // Sehr klein
            drawContext.drawTexture(net.minecraft.client.render.RenderLayer::getGuiTextured, scathaIcon, 0, 0, 0f, 0f, 512, 512, 512, 512);
            drawContext.getMatrices().pop();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gibt die Farbe basierend auf der Entfernung zurück
     */
    private int getDistanceColor(double distance)
    {
        if (distance < 20) return 0xFF00FF00; // Grün - sehr nah
        if (distance < 40) return 0xFFFFFF00; // Gelb - mittel
        if (distance < 60) return 0xFFFF8800; // Orange - weit
        return 0xFFFF0000; // Rot - sehr weit
    }
    
    /**
     * Gibt die Höhe des Trackers zurück
     */
    public int getHeight()
    {
        if (!enabled || trackedScathas.isEmpty()) return 0;
        return 12 + Math.min(trackedScathas.size(), 5) * 10;
    }
    
    // ===== GETTERS & SETTERS =====
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public double getMaxTrackingDistance() { return maxTrackingDistance; }
    public void setMaxTrackingDistance(double maxTrackingDistance) { this.maxTrackingDistance = maxTrackingDistance; }
    
    public int getTrackedCount() { return trackedScathas.size(); }
    
    /**
     * Tracked-Scatha-Datenklasse
     */
    private static class TrackedScatha
    {
        public final FabricDetectedWorm worm;
        public final double distance;
        public final long trackingStartTime;
        public boolean isDemo = false;
        
        public TrackedScatha(FabricDetectedWorm worm, double distance)
        {
            this.worm = worm;
            this.distance = distance;
            this.trackingStartTime = System.currentTimeMillis();
        }
        
        public String getStatus()
        {
            if (isDemo) return "§aDEMO";
            if (worm == null) return "§8Unknown";
            
            // Status basierend auf Worm-Eigenschaften
            long lifetime = worm.getCurrentLifetime();
            if (lifetime < 5000) return "§aFresh"; // Frisch gespawnt
            if (lifetime < 15000) return "§eActive"; // Aktiv
            if (lifetime < 25000) return "§6Aging"; // Wird alt
            return "§cExpiring"; // Kurz vor Despawn
        }
        
        public String getLifetimeString()
        {
            if (isDemo) return "Demo";
            if (worm == null) return "?";
            
            long lifetime = worm.getCurrentLifetime() / 1000;
            return lifetime + "s";
        }
    }
}