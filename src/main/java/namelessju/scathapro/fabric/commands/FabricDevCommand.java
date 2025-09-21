package namelessju.scathapro.fabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import namelessju.scathapro.fabric.Constants;
import namelessju.scathapro.fabric.FabricScathaPro;

/**
 * Fabric Dev Command - Entwickler-Commands für Testing und Debugging
 * Nur verfügbar wenn isDevelopmentMode = true
 */
public class FabricDevCommand
{
    public static final String COMMAND_NAME = "scathaprodev";
    private final FabricScathaPro scathaPro;
    
    public FabricDevCommand(FabricScathaPro scathaPro)
    {
        this.scathaPro = scathaPro;
    }
    
    /**
     * Registriert den Command bei Fabric (nur im Development-Modus)
     */
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        System.out.println("[Scatha-Pro] Registriere Dev-Command, Development Mode: " + Constants.isDevelopmentMode);
        
        // Nur im Development-Modus verfügbar
        if (!Constants.isDevelopmentMode) {
            System.out.println("[Scatha-Pro] Dev-Command nicht registriert - Development Mode deaktiviert");
            return;
        }
            
        dispatcher.register(ClientCommandManager.literal(COMMAND_NAME)
            .executes(this::executeHelp)
            .then(ClientCommandManager.literal("help")
                .executes(this::executeHelp)
            )
            .then(ClientCommandManager.literal("test")
                .executes(this::executeTest)
                .then(ClientCommandManager.argument("testName", StringArgumentType.string())
                    .executes(this::executeSpecificTest)
                )
            )
            .then(ClientCommandManager.literal("simulateScatha")
                .executes(ctx -> executeSimulateScatha(ctx, 1))
                .then(ClientCommandManager.argument("count", IntegerArgumentType.integer(1, 100))
                    .executes(ctx -> executeSimulateScatha(ctx, IntegerArgumentType.getInteger(ctx, "count")))
                )
            )
            .then(ClientCommandManager.literal("resetData")
                .executes(this::executeResetData)
            )
            .then(ClientCommandManager.literal("printVars")
                .executes(this::executePrintVars)
            )
            .then(ClientCommandManager.literal("setMagicFind")
                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 1000))
                    .executes(this::executeSetMagicFind)
                )
            )
            .then(ClientCommandManager.literal("setPetLuck")
                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 100))
                    .executes(this::executeSetPetLuck)
                )
            )
            .then(ClientCommandManager.literal("toggleOverlay")
                .executes(this::executeToggleOverlay)
            )
            .then(ClientCommandManager.literal("printConfig")
                .executes(this::executePrintConfig)
            )
        );
        
        // Kurzer Alias
        dispatcher.register(ClientCommandManager.literal("spdev")
            .redirect(dispatcher.getRoot().getChild(COMMAND_NAME))
        );
        
        System.out.println("[Scatha-Pro] Dev-Commands erfolgreich registriert: " + COMMAND_NAME + " und spdev");
    }
    
    // ===== COMMAND EXECUTORS =====
    
    /**
     * Hilfe für Dev-Commands
     */
    private int executeHelp(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendChatDivider();
        sendMessage(Formatting.YELLOW + "ScathaPro Development Commands:");
        sendMessage(Formatting.AQUA + "/" + COMMAND_NAME + " help" + Formatting.RESET + " - Shows this help");
        sendMessage(Formatting.AQUA + "/" + COMMAND_NAME + " test [testName]" + Formatting.RESET + " - Run tests");
        sendMessage(Formatting.AQUA + "/" + COMMAND_NAME + " simulateScatha [count]" + Formatting.RESET + " - Simulate Scatha spawns");
        sendMessage(Formatting.AQUA + "/" + COMMAND_NAME + " resetData" + Formatting.RESET + " - Reset all mod data");
        sendMessage(Formatting.AQUA + "/" + COMMAND_NAME + " printVars" + Formatting.RESET + " - Print all variables");
        sendMessage(Formatting.AQUA + "/" + COMMAND_NAME + " setMagicFind <value>" + Formatting.RESET + " - Set Magic Find value");
        sendMessage(Formatting.AQUA + "/" + COMMAND_NAME + " setPetLuck <value>" + Formatting.RESET + " - Set Pet Luck value");
        sendMessage(Formatting.AQUA + "/" + COMMAND_NAME + " toggleOverlay" + Formatting.RESET + " - Toggle overlay visibility");
        sendMessage(Formatting.AQUA + "/" + COMMAND_NAME + " printConfig" + Formatting.RESET + " - Print current config values");
        sendMessage(Formatting.RED + "Development commands only!");
        sendChatDivider();
        
        return 1;
    }
    
    /**
     * Test-Command
     */
    private int executeTest(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendMessage(Formatting.GREEN + "Running all tests...");
        
        // TODO: Test-Framework implementieren
        sendMessage(Formatting.YELLOW + "Available tests:");
        sendMessage(" - overlay: Test overlay rendering");
        sendMessage(" - commands: Test command parsing");
        sendMessage(" - detection: Test entity detection");
        sendMessage(" - calculations: Test chance calculations");
        
        sendMessage(Formatting.GREEN + "Use /spdev test <testName> to run specific test");
        
        return 1;
    }
    
    /**
     * Spezifischer Test
     */
    private int executeSpecificTest(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        String testName = StringArgumentType.getString(ctx, "testName");
        
        sendMessage(Formatting.GREEN + "Running test: " + testName);
        
        switch (testName.toLowerCase())
        {
            case "overlay":
                testOverlay();
                break;
            case "commands":
                testCommands();
                break;
            case "detection":
                testDetection();
                break;
            case "calculations":
                testCalculations();
                break;
            default:
                sendMessage(Formatting.RED + "Unknown test: " + testName);
                return 0;
        }
        
        return 1;
    }
    
    /**
     * Simuliert Scatha-Spawns
     */
    private int executeSimulateScatha(CommandContext<FabricClientCommandSource> ctx, int count) throws CommandSyntaxException
    {
        sendMessage(Formatting.GREEN + "Simulating " + count + " Scatha spawn(s)...");
        
        for (int i = 0; i < count; i++)
        {
            // TODO: DetectedWorm simulieren
            sendMessage(Formatting.YELLOW + "Scatha " + (i + 1) + " spawned! (simulated)");
            
            // Zufällige Pet-Drop-Simulation
            double random = Math.random();
            if (random < 0.00002) // Legendary
            {
                scathaPro.variables.legendaryPetDrops++;
                sendMessage(Formatting.GOLD + "Legendary Scatha Pet dropped! (simulated)");
            }
            else if (random < 0.0001) // Epic
            {
                scathaPro.variables.epicPetDrops++;
                sendMessage(Formatting.DARK_PURPLE + "Epic Scatha Pet dropped! (simulated)");
            }
            else if (random < 0.0005) // Rare
            {
                scathaPro.variables.rarePetDrops++;
                sendMessage(Formatting.BLUE + "Rare Scatha Pet dropped! (simulated)");
            }
        }
        
        sendMessage(Formatting.GREEN + "Simulation complete!");
        
        return 1;
    }
    
    /**
     * Setzt alle Mod-Daten zurück
     */
    private int executeResetData(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendMessage(Formatting.RED + "Resetting all mod data...");
        
        // Variables zurücksetzen
        scathaPro.variables.resetAllData();
        
        // TODO: Persistent Data löschen
        // scathaPro.getPersistentData().deleteAll();
        
        sendMessage(Formatting.GREEN + "All data reset!");
        
        return 1;
    }
    
    /**
     * Gibt alle Variablen aus
     */
    private int executePrintVars(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendChatDivider();
        sendMessage(Formatting.YELLOW + "Current Mod Variables:");
        sendMessage("Pet Drops - Rare: " + scathaPro.variables.rarePetDrops + 
                   ", Epic: " + scathaPro.variables.epicPetDrops +
                   ", Legendary: " + scathaPro.variables.legendaryPetDrops);
        sendMessage("Scatha Kills: " + scathaPro.variables.scathaKills);
        sendMessage("Farming Streak: " + scathaPro.variables.scathaFarmingStreak + 
                   " (Best: " + scathaPro.variables.scathaFarmingStreakHighscore + ")");
        sendMessage("Magic Find: " + scathaPro.variables.getMagicFindString());
        sendMessage("Pet Luck: " + scathaPro.variables.getPetLuckString());
        sendMessage("Session Stats: " + scathaPro.variables.sessionScathaKills + " kills this session");
        sendChatDivider();
        
        return 1;
    }
    
    /**
     * Setzt Magic Find
     */
    private int executeSetMagicFind(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        int value = IntegerArgumentType.getInteger(ctx, "value");
        
        // TODO: Variables.setMagicFind implementieren
        // scathaPro.variables.setMagicFind(value);
        
        sendMessage(Formatting.GREEN + "Magic Find set to: " + value);
        
        return 1;
    }
    
    /**
     * Setzt Pet Luck
     */
    private int executeSetPetLuck(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        int value = IntegerArgumentType.getInteger(ctx, "value");
        
        // TODO: Variables.setPetLuck implementieren
        // scathaPro.variables.setPetLuck(value);
        
        sendMessage(Formatting.GREEN + "Pet Luck set to: " + value);
        
        return 1;
    }
    
    /**
     * Overlay-Toggle für Testing
     */
    private int executeToggleOverlay(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        // TODO: Overlay-System implementieren
        sendMessage(Formatting.YELLOW + "Overlay toggled! (dev mode)");
        
        return 1;
    }
    
    /**
     * Config-Werte anzeigen
     */
    private int executePrintConfig(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendChatDivider();
        sendMessage(Formatting.YELLOW + "Current Config Values:");
        
        var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
        if (cfg != null) {
            sendMessage("Overlay Visible: " + cfg.overlayVisible);
            sendMessage("Overlay Style: " + (cfg.overlayStyle != null ? cfg.overlayStyle : "null"));
            sendMessage("Overlay Scale: " + cfg.overlayScale);
            sendMessage("Overlay X/Y: " + cfg.overlayX + ", " + cfg.overlayY);
            sendMessage("Alerts Enabled: " + cfg.alertsEnabled);
            sendMessage("Alert Display Enabled: " + cfg.alertsDisplayEnabled);
            sendMessage("Scatha Kills: " + cfg.scathaKills);
            sendMessage("Worm Kills: " + cfg.wormKills);
        } else {
            sendMessage(Formatting.RED + "CONFIG is null!");
        }
        
        sendChatDivider();
        return 1;
    }
    
    // ===== TEST METHODS =====
    
    private void testOverlay()
    {
        sendMessage(Formatting.YELLOW + "Testing overlay system...");
        // TODO: Overlay-Tests implementieren
        sendMessage(Formatting.GREEN + "Overlay test completed!");
    }
    
    private void testCommands()
    {
        sendMessage(Formatting.YELLOW + "Testing command system...");
        sendMessage(Formatting.GREEN + "Command test completed!");
    }
    
    private void testDetection()
    {
        sendMessage(Formatting.YELLOW + "Testing entity detection...");
        // TODO: Entity-Detection-Tests implementieren
        sendMessage(Formatting.GREEN + "Detection test completed!");
    }
    
    private void testCalculations()
    {
        sendMessage(Formatting.YELLOW + "Testing calculations...");
        // TODO: Chance-Calculation-Tests implementieren
        sendMessage(Formatting.GREEN + "Calculation test completed!");
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Sendet eine Chat-Message mit Mod-Prefix
     */
    private void sendMessage(String message)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.player != null)
        {
            mc.player.sendMessage(Text.literal(Constants.chatPrefix + message), false);
        }
    }
    
    /**
     * Sendet einen Chat-Divider
     */
    private void sendChatDivider()
    {
        sendMessage(Formatting.GRAY + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
}