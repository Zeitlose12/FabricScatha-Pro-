package namelessju.scathapro.fabric.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import namelessju.scathapro.fabric.FabricScathaPro;

/**
 * Fabric Command-Registry
 * Zentrale Verwaltung und Registrierung aller Commands
 */
public class FabricCommandRegistry
{
    private final FabricScathaPro scathaPro;
    
    // Command-Instanzen
    private final FabricMainCommand mainCommand;
    private final FabricChancesCommand chancesCommand;
    private final FabricAverageMoneyCommand averageMoneyCommand;
    private final FabricDevCommand devCommand;
    
    public FabricCommandRegistry(FabricScathaPro scathaPro)
    {
        this.scathaPro = scathaPro;
        
        scathaPro.log("Command-Registry wird initialisiert...");
        
        // Command-Instanzen erstellen
        mainCommand = new FabricMainCommand(scathaPro);
        chancesCommand = new FabricChancesCommand(scathaPro);
        averageMoneyCommand = new FabricAverageMoneyCommand(scathaPro);
        devCommand = new FabricDevCommand(scathaPro);
        
        scathaPro.log("Command-Registry initialisiert");
    }
    
    /**
     * Registriert alle Commands bei Fabric
     */
    public void registerCommands()
    {
        scathaPro.log("Commands werden registriert...");
        
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            // Haupt-Command registrieren
            mainCommand.register(dispatcher);
            
            // Weitere Commands registrieren
            chancesCommand.register(dispatcher);
            averageMoneyCommand.register(dispatcher);
            devCommand.register(dispatcher);
            
            scathaPro.log("Alle Commands erfolgreich registriert");
        });
    }
    
    // Getter für Command-Instanzen
    public FabricMainCommand getMainCommand() { return mainCommand; }
    public FabricChancesCommand getChancesCommand() { return chancesCommand; }
    public FabricAverageMoneyCommand getAverageMoneyCommand() { return averageMoneyCommand; }
    public FabricDevCommand getDevCommand() { return devCommand; }
}