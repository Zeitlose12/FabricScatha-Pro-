package namelessju.scathapro.fabric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import namelessju.scathapro.fabric.commands.FabricCommandRegistry;
import namelessju.scathapro.fabric.events.FabricEventManager;
import namelessju.scathapro.fabric.events.DemoEventListeners;
import namelessju.scathapro.fabric.eventlisteners.FabricEventListenerManager;
import namelessju.scathapro.fabric.overlay.FabricOverlay;
import namelessju.scathapro.fabric.overlay.FabricOverlayRenderer;
import namelessju.scathapro.fabric.areas.AreaDetector;
import namelessju.scathapro.fabric.areas.SkyblockArea;
import namelessju.scathapro.fabric.util.TimeUtil;
import namelessju.scathapro.fabric.sound.FabricSoundManager;
import namelessju.scathapro.fabric.alerts.FabricAlertManager;
import namelessju.scathapro.fabric.achievements.FabricAchievementManager;

/**
 * Fabric-Hauptklasse für Scatha-Pro
 * Ersetzt die original ScathaPro.java-Klasse und verwaltet alle Mod-Instanzen
 */
public class FabricScathaPro
{
    public static final String TRUE_MODNAME = "Scatha-Pro";
    public static final String MODID = "scathapro";
    public static final String VERSION = "1.3.2.1";
    
    // Dynamischer Mod-Name für April Fools
    public static final String DYNAMIC_MODNAME = TimeUtil.isAprilFoolsForced() ? "Schata-Por" : TRUE_MODNAME;
    
    
    private static FabricScathaPro instance;
    
    public final FabricGlobalVariables variables;
    public final FabricEventManager eventManager;
    public final FabricEventListenerManager eventListenerManager;
    public final AreaDetector areaDetector;
    
    // Alert-System
    private FabricSoundManager soundManager;
    private FabricAlertManager alertManager;
    
    // Achievement-System
    private FabricAchievementManager achievementManager;
    
    private final Logger logger;
    private final MinecraftClient minecraft;
    
    // TODO: Manager-Instanzen implementieren wenn portiert
    // private final FabricConfig config;
    private final namelessju.scathapro.fabric.persist.FabricPersistentData persistentData;
    private final FabricOverlay overlay;
    private final FabricOverlayRenderer overlayRenderer;
    private final namelessju.scathapro.fabric.alerts.FabricAlertTitleOverlay alertTitleOverlay;
    private final namelessju.scathapro.fabric.alerts.AlertModeManager alertModeManager;
    private final namelessju.scathapro.fabric.alerts.CustomAlertModeManager customAlertModeManager;
    // private final FabricCustomAlertModeManager customAlertModeManager;
private final namelessju.scathapro.fabric.achievements.FabricAchievementLogicManager achievementLogicManager;
    private final namelessju.scathapro.fabric.input.FabricInputManager inputManager;
    private final namelessju.scathapro.fabric.parsing.chest.ChestGuiParsingManager chestGuiParsingManager;
    private final namelessju.scathapro.fabric.save.FabricSaveManager saveManager;
    private final namelessju.scathapro.fabric.migration.ConfigMigration migration;
    private final namelessju.scathapro.fabric.webapi.WebApiCredentials webApiCredentials;
    private final namelessju.scathapro.fabric.webapi.HypixelWebApiClient webApiClient;
    private final FabricCommandRegistry commandRegistry;
    
    
    public static FabricScathaPro getInstance()
    {
        return instance;
    }
    
    public MinecraftClient getMinecraft() { return minecraft; }
    public FabricEventManager getEventManager() { return eventManager; }
    public FabricEventListenerManager getEventListenerManager() { return eventListenerManager; }
    public FabricCommandRegistry getCommandRegistry() { return commandRegistry; }
    public FabricOverlay getOverlay() { return overlay; }
    public FabricOverlayRenderer getOverlayRenderer() { 
        // Hinweis: Overlay-Renderer ist null da wir V2Renderer verwenden
        return overlayRenderer; 
    }
    public AreaDetector getAreaDetector() { return areaDetector; }
    public FabricSoundManager getSoundManager() { return soundManager; }
    public FabricAlertManager getAlertManager() { return alertManager; }
    public FabricAchievementManager getAchievementManager() { return achievementManager; }
    public namelessju.scathapro.fabric.persist.FabricPersistentData getPersistentData() { return persistentData; }
    public namelessju.scathapro.fabric.save.FabricSaveManager getSaveManager() { return saveManager; }
    
    // TODO: Getter für Manager implementieren wenn portiert
    // public FabricConfig getConfig() { return config; }
    // public FabricPersistentData getPersistentData() { return persistentData; }
    // public FabricOverlay getOverlay() { return overlay; }
    public namelessju.scathapro.fabric.alerts.FabricAlertTitleOverlay getAlertTitleOverlay() { return alertTitleOverlay; }
    public namelessju.scathapro.fabric.alerts.AlertModeManager getAlertModeManager() { return alertModeManager; }
    public namelessju.scathapro.fabric.alerts.CustomAlertModeManager getCustomAlertModeManager() { return customAlertModeManager; }
    // public FabricCustomAlertModeManager getCustomAlertModeManager() { return customAlertModeManager; }
    // public FabricAchievementManager getAchievementManager() { return achievementManager; }
    // public FabricAchievementLogicManager getAchievementLogicManager() { return achievementLogicManager; }
    public namelessju.scathapro.fabric.input.FabricInputManager getInputManager() { return inputManager; }
    public namelessju.scathapro.fabric.parsing.chest.ChestGuiParsingManager getChestGuiParsingManager() { return chestGuiParsingManager; }
    public namelessju.scathapro.fabric.webapi.WebApiCredentials getWebApiCredentials() { return webApiCredentials; }
    public namelessju.scathapro.fabric.webapi.HypixelWebApiClient getWebApiClient() { return webApiClient; }
    
    
    public FabricScathaPro()
    {
        instance = this;
        
        logger = LogManager.getLogger(MODID);
        minecraft = MinecraftClient.getInstance();
        
        log("[FabricScathaPro] Starting initialization...");
        log("[FabricScathaPro] Creating core components...");
        
        try {
            variables = new FabricGlobalVariables();
            log("[FabricScathaPro] Variables initialized");
            
            eventManager = new FabricEventManager(this);
            log("[FabricScathaPro] Event manager initialized");
            
            eventListenerManager = new FabricEventListenerManager(this);
            log("[FabricScathaPro] Event listener manager initialized");
            
            areaDetector = new AreaDetector();
            log("[FabricScathaPro] Area detector initialized");
            
            soundManager = new FabricSoundManager(this);
            log("[FabricScathaPro] Sound manager initialized");
            
            alertManager = new FabricAlertManager(this, soundManager);
            log("[FabricScathaPro] Alert manager initialized");

            // Alert-Title-Overlay und Mode-Manager
            alertTitleOverlay = new namelessju.scathapro.fabric.alerts.FabricAlertTitleOverlay(this);
            alertModeManager = new namelessju.scathapro.fabric.alerts.AlertModeManager(this);
            customAlertModeManager = new namelessju.scathapro.fabric.alerts.CustomAlertModeManager();
            customAlertModeManager.load();
            for (var m : customAlertModeManager.getAll()) alertModeManager.register(m);
            alertManager.setTitleOverlay(alertTitleOverlay);
            alertManager.setModeManager(alertModeManager);
            
            achievementManager = new FabricAchievementManager(this);
            log("[FabricScathaPro] Achievement manager initialized");

            // Persistenz/Save/Migration
            persistentData = new namelessju.scathapro.fabric.persist.FabricPersistentData();
            saveManager = new namelessju.scathapro.fabric.save.FabricSaveManager();
            migration = new namelessju.scathapro.fabric.migration.ConfigMigration();

            // Web API
            webApiCredentials = new namelessju.scathapro.fabric.webapi.WebApiCredentials();
            webApiCredentials.load();
            webApiClient = new namelessju.scathapro.fabric.webapi.HypixelWebApiClient(webApiCredentials);

            // MIGRATION (best-effort) vor dem Laden, damit Backups vorhanden sind
            migration.tryMigrate(this);

// Persistente Daten laden
            persistentData.load(this);

            // Achievement-Logik
            achievementLogicManager = new namelessju.scathapro.fabric.achievements.FabricAchievementLogicManager(this);
            log("[FabricScathaPro] Achievement logic manager initialized");

            commandRegistry = new FabricCommandRegistry(this);
            log("[FabricScathaPro] Command registry initialized");
            
            overlay = new FabricOverlay(this);
            log("[FabricScathaPro] Overlay initialized");

            // Input-Manager (Client-Registrierung folgt in onInitializeClient)
            inputManager = new namelessju.scathapro.fabric.input.FabricInputManager(this);

            // Chest GUI Parsing Manager
            chestGuiParsingManager = new namelessju.scathapro.fabric.parsing.chest.ChestGuiParsingManager(this);
            
            // Overlay-Renderer deaktiviert - Rendering erfolgt über ClientHooks
            overlayRenderer = null; // new FabricOverlayRenderer(this);
            log("[FabricScathaPro] Overlay renderer disabled (using ClientHooks instead)");
        } catch (Exception e) {
            logger.error("[FabricScathaPro] Error during component initialization:", e);
            throw e;
        }
        
        log("[FabricScathaPro] Constructor completed successfully");
        
        // TODO: Manager initialisieren wenn portiert
        // SaveManager.updateOldSaveLocations();
        // config = new FabricConfig();
        // config.init();
        // achievementManager = new FabricAchievementManager(this);
        // achievementLogicManager = new FabricAchievementLogicManager(this);
        // overlay = new FabricOverlay(this);
        // alertTitleOverlay = new FabricAlertTitleOverlay(config);
        // alertModeManager = new FabricAlertModeManager(config);
        // customAlertModeManager = new FabricCustomAlertModeManager(this);
        // inputManager = new FabricInputManager(this);
        // chestGuiParsingManager = new FabricChestGuiParsingManager(this);
        // persistentData = new FabricPersistentData(this);
        // persistentData.loadFile();
        // commandRegistry = new FabricCommandRegistry(this);
        
        log("Fabric Scatha-Pro Basis-Initialisierung abgeschlossen");
    }
    
    /**
     * Wird von ScathaProFabric.onInitialize() aufgerufen
     */
    public void onInitialize()
    {
        log("Fabric Scatha-Pro wird geladen...");
        
        // Event-Listener registrieren
        eventManager.registerEventListeners();

        // Day-Alert-Listener registrieren
        namelessju.scathapro.fabric.alerts.DayAlertListeners.register(this);
        // Bedrock-Wall-Alert Listener registrieren
        namelessju.scathapro.fabric.alerts.WallAlertListeners.register(this);
        // Area-Event Listener registrieren
        namelessju.scathapro.fabric.alerts.AreaEventListeners.register(this);

        // Nach Initialisierung eine Sicherung erstellen (nur falls Datei existiert)
        getSaveManager().backupCurrent(this);
        
        // Demo Event-Listener registrieren (zum Testen)
        DemoEventListeners.registerDemoListeners();
        
        // Commands registrieren
        commandRegistry.registerCommands();
        
        // TODO: Input-Manager implementieren
        // inputManager.register();
        
        // TODO: Resource-Pack-Injection implementieren
        // injectCustomAlertModeResourcePack();
        
        // TODO: Version-Check implementieren  
        // handleVersionUpdate();
        
        // TODO: Overlay aktualisieren
        // overlay.updateOverlayFull();
        
        log("Fabric Scatha-Pro erfolgreich geladen!");
    }
    
    /**
     * Wird von ScathaProFabricClient.onInitializeClient() aufgerufen
     */
    public void onInitializeClient()
    {
        log("Fabric Scatha-Pro Client wird initialisiert...");
        
        // Client-spezifische Initialisierung
        // Keybindings/Input-Manager
        try {
            if (inputManager != null) {
                inputManager.register();
            }
        } catch (Exception e) {
            logError("Fehler beim Registrieren des Input-Managers: " + e.getMessage());
        }
        
        log("Fabric Scatha-Pro Client initialisiert");
    }
    
    // TODO: Event-Listener Registration implementieren
    /*
    private void registerEventListeners()
    {
        // Fabric Events registrieren statt MinecraftForge.EVENT_BUS
        // ClientTickEvents.END_CLIENT_TICK.register(client -> {...});
        // ClientLifecycleEvents.CLIENT_STARTED.register(client -> {...});
        // etc.
    }
    */
    
    
    public void log(String message)
    {
        logger.info("[{}] {}", TRUE_MODNAME, message);
    }
    
    public void logWarning(String message)
    {
        logger.warn("[{}] {}", TRUE_MODNAME, message);
    }
    
    public void logError(String message)
    {
        logger.error("[{}] {}", TRUE_MODNAME, message);
    }
    
    public void logDebug(String message)
    {
        try {
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            if (cfg != null && !cfg.debugLogs) return;
        } catch (Exception ignored) {}
        logger.info("[{}] (DEBUG) {}", TRUE_MODNAME, message);
    }
    
    
    public boolean isInCrystalHollows()
    {
        if (variables.currentArea instanceof SkyblockArea) {
            return ((SkyblockArea) variables.currentArea).isCrystalHollows();
        }
        // Fallback: Prüfe über AreaDetector
        return areaDetector.getCurrentArea().isCrystalHollows();
    }
    
    public boolean isScappaModeActive()
    {
        // TODO: Config implementieren
        // return variables.scappaModeUnlocked && (variables.scappaModeActiveTemp || getConfig().getBoolean(Config.Key.scappaMode));
        return variables.scappaModeUnlocked && variables.scappaModeActiveTemp; // Temporär
    }
}