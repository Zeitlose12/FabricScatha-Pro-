package namelessju.scathapro.fabric.areas;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;
import namelessju.scathapro.fabric.FabricScathaPro;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * System zur Erkennung des aktuellen Skyblock-Bereichs
 * Verwendet Tab-Liste und Scoreboard-Informationen
 */
public class AreaDetector {
    
    private static final Pattern AREA_PATTERN = Pattern.compile("Area: (.+)");
    private static final Pattern LOCATION_PATTERN = Pattern.compile("♪ (.+)");
    
    private SkyblockArea currentArea = SkyblockArea.UNKNOWN;
    private String lastKnownLocation = "";
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 2000; // Update alle 2 Sekunden
    
    public SkyblockArea getCurrentArea() {
        return currentArea;
    }
    
    public String getLastKnownLocation() {
        return lastKnownLocation;
    }
    
    /**
     * Aktualisiert die Bereichserkennung
     * Sollte regelmäßig aufgerufen werden (z.B. jeden Tick)
     */
    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastUpdateTime < UPDATE_INTERVAL) {
            return; // Noch zu früh für Update
        }
        lastUpdateTime = now;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) {
            if (currentArea != SkyblockArea.UNKNOWN) {
                currentArea = SkyblockArea.UNKNOWN;
                onAreaChanged(SkyblockArea.UNKNOWN);
            }
            return;
        }
        
        SkyblockArea detectedArea = detectArea();
        if (detectedArea != currentArea) {
            SkyblockArea previousArea = currentArea;
            currentArea = detectedArea;
            onAreaChanged(previousArea);
        }
    }
    
    /**
     * Versucht den aktuellen Bereich zu ermitteln
     */
    private SkyblockArea detectArea() {
        // Methode 1: Aus Scoreboard (Sidebar)
        SkyblockArea fromScoreboard = detectAreaFromScoreboard();
        if (fromScoreboard != SkyblockArea.UNKNOWN) {
            return fromScoreboard;
        }
        
        // Methode 2: Aus Tab-Liste
        SkyblockArea fromTabList = detectAreaFromTabList();
        if (fromTabList != SkyblockArea.UNKNOWN) {
            return fromTabList;
        }
        
        // Methode 3: Aus Welt-Name (falls verfügbar)
        SkyblockArea fromWorld = detectAreaFromWorldName();
        if (fromWorld != SkyblockArea.UNKNOWN) {
            return fromWorld;
        }
        
        return SkyblockArea.UNKNOWN;
    }
    
    /**
     * Versucht den Bereich aus dem Scoreboard zu ermitteln
     */
    private SkyblockArea detectAreaFromScoreboard() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return SkyblockArea.UNKNOWN;
        
        Scoreboard scoreboard = mc.player.getScoreboard();
        if (scoreboard == null) return SkyblockArea.UNKNOWN;
        
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (objective == null) return SkyblockArea.UNKNOWN;
        
        // In MC 1.21.5 hat sich die Scoreboard-API geändert
        // Wir verwenden einen alternativen Ansatz
        Collection<String> playerNames = scoreboard.getObjectiveNames();
        
        for (String name : playerNames) {
            if (name == null) continue;
            String line = name;
            
            // Entferne Minecraft-Formatierungscodes
            String cleanLine = line.replaceAll("§[0-9a-fk-or]", "");
            
            // Suche nach "Area: ..." Zeilen
            var areaMatcher = AREA_PATTERN.matcher(cleanLine);
            if (areaMatcher.find()) {
                String areaName = areaMatcher.group(1).trim();
                lastKnownLocation = areaName;
                return SkyblockArea.fromString(areaName);
            }
            
            // Suche nach "♪ ..." Zeilen (Location)
            var locationMatcher = LOCATION_PATTERN.matcher(cleanLine);
            if (locationMatcher.find()) {
                String location = locationMatcher.group(1).trim();
                lastKnownLocation = location;
                return SkyblockArea.fromString(location);
            }
            
            // Direkte Suche nach bekannten Bereichsnamen
            SkyblockArea directMatch = SkyblockArea.fromString(cleanLine);
            if (directMatch != SkyblockArea.UNKNOWN) {
                lastKnownLocation = cleanLine;
                return directMatch;
            }
        }
        
        return SkyblockArea.UNKNOWN;
    }
    
    /**
     * Versucht den Bereich aus der Tab-Liste zu ermitteln
     */
    private SkyblockArea detectAreaFromTabList() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getNetworkHandler() == null) return SkyblockArea.UNKNOWN;
        
        Collection<PlayerListEntry> playerList = mc.getNetworkHandler().getPlayerList();
        
        for (PlayerListEntry entry : playerList) {
            Text displayName = entry.getDisplayName();
            if (displayName == null) continue;
            
            String displayText = displayName.getString();
            if (displayText == null || displayText.trim().isEmpty()) continue;
            
            // Entferne Formatierungscodes
            String cleanText = displayText.replaceAll("§[0-9a-fk-or]", "");
            
            // Suche nach Area-Informationen
            var areaMatcher = AREA_PATTERN.matcher(cleanText);
            if (areaMatcher.find()) {
                String areaName = areaMatcher.group(1).trim();
                lastKnownLocation = areaName;
                return SkyblockArea.fromString(areaName);
            }
            
            // Suche nach Location-Informationen
            var locationMatcher = LOCATION_PATTERN.matcher(cleanText);
            if (locationMatcher.find()) {
                String location = locationMatcher.group(1).trim();
                lastKnownLocation = location;
                return SkyblockArea.fromString(location);
            }
            
            // Direkte Bereichserkennung
            SkyblockArea directMatch = SkyblockArea.fromString(cleanText);
            if (directMatch != SkyblockArea.UNKNOWN) {
                lastKnownLocation = cleanText;
                return directMatch;
            }
        }
        
        return SkyblockArea.UNKNOWN;
    }
    
    /**
     * Versucht den Bereich aus dem Welt-Namen zu ermitteln
     */
    private SkyblockArea detectAreaFromWorldName() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return SkyblockArea.UNKNOWN;
        
        // Fabric hat möglicherweise keinen direkten Zugang zum Server-Weltnamen
        // Aber wir können versuchen, aus anderen Quellen zu schließen
        
        return SkyblockArea.UNKNOWN;
    }
    
    /**
     * Wird aufgerufen wenn sich der Bereich ändert
     */
    private void onAreaChanged(SkyblockArea previousArea) {
        FabricScathaPro scathaPro = FabricScathaPro.getInstance();
        if (scathaPro != null) {
            scathaPro.log("Area changed from " + previousArea.getDisplayName() + 
                         " to " + currentArea.getDisplayName() + 
                         " (Location: " + lastKnownLocation + ")");
            
            // Aktualisiere Global Variables
            scathaPro.variables.currentArea = currentArea;
            
            // Triggere Area-spezifische Events
            if (currentArea.isCrystalHollows() && !previousArea.isCrystalHollows()) {
                onEnterCrystalHollows();
            } else if (!currentArea.isCrystalHollows() && previousArea.isCrystalHollows()) {
                onExitCrystalHollows();
            }
        }
    }
    
    /**
     * Wird aufgerufen beim Betreten der Crystal Hollows
     */
    private void onEnterCrystalHollows() {
        FabricScathaPro scathaPro = FabricScathaPro.getInstance();
        if (scathaPro != null) {
            scathaPro.log("Entered Crystal Hollows - activating Scatha-Pro features");
            scathaPro.variables.firstCrystalHollowsTickPending = true;
        }
    }
    
    /**
     * Wird aufgerufen beim Verlassen der Crystal Hollows
     */
    private void onExitCrystalHollows() {
        FabricScathaPro scathaPro = FabricScathaPro.getInstance();
        if (scathaPro != null) {
            scathaPro.log("Exited Crystal Hollows - deactivating some features");
            
            // Reset some state
            scathaPro.variables.firstCrystalHollowsTickPending = true;
            scathaPro.variables.lastHeat = -1;
            scathaPro.variables.lastCrystalHollowsDay = -1;
        }
    }
    
    /**
     * Für Development/Testing: Manuell einen Bereich setzen
     */
    public void setCurrentAreaForTesting(SkyblockArea area) {
        if (area != currentArea) {
            SkyblockArea previousArea = currentArea;
            currentArea = area;
            lastKnownLocation = area.getDisplayName();
            onAreaChanged(previousArea);
        }
    }
}