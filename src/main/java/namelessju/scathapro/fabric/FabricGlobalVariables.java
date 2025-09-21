package namelessju.scathapro.fabric;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Fabric-Version der GlobalVariables-Klasse
 * Enthält alle wichtigen Spielvariablen für das Scatha-Pro Mod
 */
public class FabricGlobalVariables
{
    // TODO: Chat-Zeilen System implementieren wenn Mixins portiert sind
    public List<Object> chatLines = null;
    
    public final List<Runnable> runNextTick = Lists.newArrayList();
    public final List<Runnable> runAfterNextRender = Lists.newArrayList();
    public Screen openGuiNextTick = null;
    
    public long lastWorldJoinTime = -1;
    public int currentAreaCheckTimeIndex = 0;
    // TODO: SkyblockArea enum portieren
    public Object currentArea = null;
    
    public float magicFind = -1f;
    public float wormBestiaryMagicFind = -1f;
    public float petLuck = -1f;
    
    public HashMap<Integer, Integer> previousScathaPets = null;
    public ItemStack lastProjectileWeaponUsed = null;
    
    public int regularWormKills = 0;
    public int scathaKills = 0;
    
    public long wormSpawnCooldownStartTime = -1;
    
    public long lastWormSpawnTime = -1;
    public long lastScathaKillTime = -1;
    
    public int rarePetDrops = 0;
    public int epicPetDrops = 0;
    public int legendaryPetDrops = 0;
    
    public int scathaKillsAtLastDrop = -1;
    public boolean dropDryStreakInvalidated = false;
    
    public long lastKillTime = -1;
    public long lastPetDropTime = -1;
    
    public LocalDate lastPlayedDate = null;
    public int scathaFarmingStreak = 0;
    public int scathaFarmingStreakHighscore = 0;
    public LocalDate lastScathaFarmedDate = null;
    
    public boolean sneakingBefore = false;
    public long lastSneakStartTime = -1;
    
    /** Used for the high heat alert and doesn't get updated if the alert is disabled! */
    public int lastHeat = -1;
    /** -1 = waiting for first time update packet; -2 = packet received, may now update this variable to the actual day */
    public int lastCrystalHollowsDay = -1;
    public int lastOldLobbyAlertTriggerDay = -1;
    
    /** The time when the ability should be used (after spawn cooldown!) */
    public long anomalousDesireReadyTime = -1;
    /** The time when the ability is actually available again */
    public long anomalousDesireCooldownEndTime = -1;
    public long anomalousDesireStartTime = -1;
    public boolean anomalousDesireWastedForRecovery = false;
    
    public boolean firstWorldTickPending = true;
    public boolean firstCrystalHollowsTickPending = true;

    public List<Text> cachedChatMessages = Lists.newArrayList();
    public List<Text> cachedCrystalHollowsMessages = Lists.newArrayList();
    
    public boolean scappaModeActiveTemp = false;
    public boolean scappaModeUnlocked = false;
    public boolean overlayIconGooglyEyesUnlocked = false;
    
    public float avgMoneyCalcScathaPriceRare = -1f;
    public float avgMoneyCalcScathaPriceEpic = -1f;
    public float avgMoneyCalcScathaPriceLegendary = -1f;
    public float avgMoneyCalcMagicFind = -1f;
    public float avgMoneyCalcPetLuck = -1f;
    public float avgMoneyCalcScathaRate = -1f;
    
    public boolean cheaterDetected = false;
    public short lastAprilFoolsJokeShownYear = -1;
    public byte aprilFoolsJokeRevealTickTimer = 0;
    
    public int antiSleepAlertTickTimer = 0;
    public int nextAntiSleepAlertTriggerTickCount = -1;
    
    // Session-Tracking
    public int sessionScathaKills = 0;

    // Lobby-gebundene Zähler
    public int lobbyWormKills = 0;
    public int lobbyScathaKills = 0;

    // Tagesbasierte Zähler (real day)
    public int dayWormKills = 0;
    public int dayScathaKills = 0;

    // Spawn-Streaks (in einer Lobby)
    public int consecutiveRegularWormSpawns = 0;
    public int consecutiveScathaSpawns = 0;
    
    
    public FabricGlobalVariables()
    {
        // Chat-Zeilen Setup wird später implementiert wenn Mixins portiert sind
        this.runNextTick.add(() -> {
            // TODO: Chat GUI Access implementieren
            chatLines = Lists.newArrayList();
        });
    }
    
    
    public String getMagicFindString()
    {
        // TODO: UnicodeSymbol portieren
        return Formatting.AQUA.toString() + "✯ " + formatNumber(magicFind, 2) + Formatting.RESET + Formatting.AQUA;
    }
    
    public String getBestiaryMagicFindString()
    {
        return Formatting.AQUA.toString() + "✯ " + formatNumber(wormBestiaryMagicFind, 2) + Formatting.RESET + Formatting.AQUA;
    }
    
    public String getTotalMagicFindString()
    {
        float totalMagicFind = getTotalMagicFind();
        return Formatting.AQUA.toString() + "✯ " + formatNumber(totalMagicFind, 2) + Formatting.RESET + Formatting.AQUA;
    }
    
    public String getPetLuckString()
    {
        return Formatting.LIGHT_PURPLE.toString() + "♣ " + (petLuck >= 0f ? formatNumber(petLuck, 2) : Formatting.OBFUSCATED + "?" + Formatting.RESET + Formatting.LIGHT_PURPLE);
    }
    
    public String getEffectiveMagicFindString()
    {
        float totalMagicFind = getTotalMagicFind();
        return Formatting.BLUE + (totalMagicFind >= 0f && petLuck >= 0f ? formatNumber(totalMagicFind + petLuck, 2) : Formatting.OBFUSCATED + "?" + Formatting.RESET + Formatting.BLUE);
    }
    
    public float getTotalMagicFind()
    {
        float totalMagicFind = -1f;
        if (magicFind >= 0) totalMagicFind = magicFind;
        if (wormBestiaryMagicFind >= 0)
        {
            if (totalMagicFind >= 0) totalMagicFind += wormBestiaryMagicFind;
            else totalMagicFind = wormBestiaryMagicFind;
        }
        return totalMagicFind;
    }
    
    public void startWormSpawnCooldown(boolean forceRestart)
    {
        if (!forceRestart && wormSpawnCooldownStartTime >= Constants.pingTreshold) return;
        wormSpawnCooldownStartTime = System.currentTimeMillis(); // TimeUtil.now() ersetzt
    }
    
    public void addRegularWormKill()
    {
        if (regularWormKills >= 0) regularWormKills++;
        // TODO: WormStatsType.addRegularWormKill() implementieren wenn portiert
    }
    
    public void addScathaKill()
    {
        if (scathaKills >= 0) scathaKills++;
        // TODO: WormStatsType.addScathaKill() implementieren wenn portiert
    }
    
    public void resetForNewLobby()
    {
        firstWorldTickPending = true;
        firstCrystalHollowsTickPending = true;
        currentAreaCheckTimeIndex = 0;
        // TODO: Config und SkyblockArea implementieren
        currentArea = null; // Temporär - später: devMode ? SkyblockArea.CRYSTAL_HOLLOWS : null
        previousScathaPets = null;
        // Lobby-gebundene Zähler zurücksetzen
        lobbyWormKills = 0;
        lobbyScathaKills = 0;
        consecutiveRegularWormSpawns = 0;
        consecutiveScathaSpawns = 0;
        // TODO: WormStatsType.resetForNewLobby() implementieren
        lastWormSpawnTime = -1;
        wormSpawnCooldownStartTime = -1;
        lastHeat = -1;
        lastCrystalHollowsDay = -1;
        lastOldLobbyAlertTriggerDay = -1;
        sneakingBefore = false;
        anomalousDesireWastedForRecovery = false;
        antiSleepAlertTickTimer = 0;
    }
    
    public void setRandomAntiSleepAlertTriggerMinutes()
    {
        // TODO: Config-System und RNG implementieren
        int intervalMax = 30 * 20 * 60; // Temporärer Wert
        int intervalMin = 15 * 20 * 60; // Temporärer Wert
        nextAntiSleepAlertTriggerTickCount = intervalMin + (intervalMax > intervalMin ? (int)(Math.random() * (intervalMax - intervalMin)) : 0);
    }
    
    /**
     * Setzt alle Mod-Daten zurück (für Dev-Command)
     */
    public void resetAllData()
    {
        // Pet-Drops zurücksetzen
        rarePetDrops = 0;
        epicPetDrops = 0;
        legendaryPetDrops = 0;
        
        // Kill-Zähler zurücksetzen
        regularWormKills = 0;
        scathaKills = 0;
        sessionScathaKills = 0;
        
        // Streak-Daten zurücksetzen
        scathaFarmingStreak = 0;
        scathaFarmingStreakHighscore = 0;
        lastScathaFarmedDate = null;
        
        // Timing-Daten zurücksetzen
        lastWormSpawnTime = -1;
        lastScathaKillTime = -1;
        lastKillTime = -1;
        lastPetDropTime = -1;
        wormSpawnCooldownStartTime = -1;
        
        // Drop-Streak-Daten zurücksetzen
        scathaKillsAtLastDrop = -1;
        dropDryStreakInvalidated = false;
        
        // Stats zurücksetzen
        magicFind = -1f;
        wormBestiaryMagicFind = -1f;
        petLuck = -1f;
        
        // Temporäre Daten zurücksetzen
        scappaModeActiveTemp = false;
        cheaterDetected = false;
        
        // Cache leeren
        if (cachedChatMessages != null) cachedChatMessages.clear();
        if (cachedCrystalHollowsMessages != null) cachedCrystalHollowsMessages.clear();
    }
    
    // Hilfsmethode für Zahlenformatierung (ersetzt TextUtil temporär)
    private String formatNumber(float number, int decimals)
    {
        if (number < 0) return Formatting.OBFUSCATED + "?" + Formatting.RESET;
        return String.format("%." + decimals + "f", number);
    }
}