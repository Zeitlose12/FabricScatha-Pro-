package namelessju.scathapro.fabric;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScathaProFabric implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("scathapro");
    
    private FabricScathaPro scathaPro;

    @Override
    public void onInitialize() {
        LOGGER.info("[ScathaProFabric] Starting server-side initialization...");
        
        try {
            // Scatha-Pro Hauptklasse initialisieren
            LOGGER.info("[ScathaProFabric] Creating FabricScathaPro instance...");
            scathaPro = new FabricScathaPro();
            
            LOGGER.info("[ScathaProFabric] Calling onInitialize()...");
            scathaPro.onInitialize();
            
            LOGGER.info("[ScathaProFabric] Server-side initialization completed successfully!");
        } catch (Exception e) {
            LOGGER.error("[ScathaProFabric] Error during server-side initialization:", e);
            throw e;
        }
    }
}
