package namelessju.scathapro.fabric.eventlisteners;

import namelessju.scathapro.fabric.FabricScathaPro;

/**
 * Fabric Event-Listener-Manager
 * Zentrale Verwaltung und Initialisierung aller Event-Listener-Klassen
 */
public class FabricEventListenerManager
{
    private final FabricScathaPro scathaPro;
    
    // Event-Listener-Instanzen
    private final FabricScathaProGameplayListeners gameplayListeners;
    private final FabricScathaProTickListeners tickListeners;
    private final FabricWormEventListeners wormEventListeners;
    // TODO: Weitere Listener hinzufügen wenn portiert
    // private final FabricLoopListeners loopListeners;
    // private final FabricMiscListeners miscListeners; 
    // private final FabricGuiListeners guiListeners;
    // private final FabricScathaProMiscListeners scathaProMiscListeners;
    
    public FabricEventListenerManager(FabricScathaPro scathaPro)
    {
        this.scathaPro = scathaPro;
        
        scathaPro.log("Event-Listener-Manager wird initialisiert...");
        
        // Event-Listener initialisieren (registrieren sich automatisch)
        gameplayListeners = new FabricScathaProGameplayListeners(scathaPro);
        tickListeners = new FabricScathaProTickListeners(scathaPro);
        wormEventListeners = new FabricWormEventListeners(scathaPro);
        
        // TODO: Weitere Listener initialisieren wenn portiert
        // loopListeners = new FabricLoopListeners(scathaPro);
        // miscListeners = new FabricMiscListeners(scathaPro);
        // guiListeners = new FabricGuiListeners(scathaPro);  
        // scathaProMiscListeners = new FabricScathaProMiscListeners(scathaPro);
        
        scathaPro.log("Event-Listener-Manager erfolgreich initialisiert");
    }
    
    /**
     * Gibt die Gameplay-Listener-Instanz zurück
     */
    public FabricScathaProGameplayListeners getGameplayListeners()
    {
        return gameplayListeners;
    }
    
    /**
     * Gibt die Tick-Listener-Instanz zurück
     */
    public FabricScathaProTickListeners getTickListeners()
    {
        return tickListeners;
    }
    
    /**
     * Gibt die Worm-Event-Listener-Instanz zurück
     */
    public FabricWormEventListeners getWormEventListeners()
    {
        return wormEventListeners;
    }
    
    // TODO: Getter für weitere Listener hinzufügen wenn portiert
}