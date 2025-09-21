package namelessju.scathapro.fabric.commands;

import com.mojang.brigadier.CommandDispatcher;
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
 * Fabric Chances Command (/scacha)
 * Berechnet und zeigt Scatha Pet Drop-Chancen
 */
public class FabricChancesCommand
{
    public static final String COMMAND_NAME = "scacha";
    private final FabricScathaPro scathaPro;
    
    public FabricChancesCommand(FabricScathaPro scathaPro)
    {
        this.scathaPro = scathaPro;
    }
    
    /**
     * Registriert den Command bei Fabric
     */
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        dispatcher.register(ClientCommandManager.literal(COMMAND_NAME)
            .executes(this::executeChances)
            .then(ClientCommandManager.literal("help")
                .executes(this::executeHelp)
            )
        );
    }
    
    /**
     * Hauptausführung - zeigt Pet-Drop-Chancen
     */
    private int executeChances(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendChatDivider();
        sendMessage(Formatting.YELLOW + "Scatha Pet Drop Chances:");
        
        // TODO: Pet-Chance-Berechnung implementieren
        // Basis-Chancen (ohne Modifiers)
        double baseRareChance = 0.0005; // 0.05%
        double baseEpicChance = 0.0001; // 0.01%
        double baseLegendaryChance = 0.00002; // 0.002%
        
        // TODO: Magic Find und Pet Luck berücksichtigen
        // double magicFind = scathaPro.variables.magicFind;
        // double petLuck = scathaPro.variables.petLuck;
        double magicFind = 0; // Temporär
        double petLuck = 0; // Temporär
        
        // Berechnete Chancen mit Modifiern
        double rareChance = calculateChanceWithModifiers(baseRareChance, magicFind, petLuck);
        double epicChance = calculateChanceWithModifiers(baseEpicChance, magicFind, petLuck);
        double legendaryChance = calculateChanceWithModifiers(baseLegendaryChance, magicFind, petLuck);
        
        // Chancen anzeigen
        sendMessage(Formatting.BLUE + "Rare: " + Formatting.WHITE + formatChance(rareChance) + 
                   " (1 in " + formatOneIn((int)(1.0 / rareChance)) + ")");
        sendMessage(Formatting.DARK_PURPLE + "Epic: " + Formatting.WHITE + formatChance(epicChance) + 
                   " (1 in " + formatOneIn((int)(1.0 / epicChance)) + ")");
        sendMessage(Formatting.GOLD + "Legendary: " + Formatting.WHITE + formatChance(legendaryChance) + 
                   " (1 in " + formatOneIn((int)(1.0 / legendaryChance)) + ")");
        
        // Aktuelle Drops anzeigen
        sendMessage("");
        sendMessage(Formatting.YELLOW + "Your current pet drops:");
        sendMessage(Formatting.BLUE + "Rare: " + scathaPro.variables.rarePetDrops);
        sendMessage(Formatting.DARK_PURPLE + "Epic: " + scathaPro.variables.epicPetDrops);
        sendMessage(Formatting.GOLD + "Legendary: " + scathaPro.variables.legendaryPetDrops);
        
        sendChatDivider();
        
        return 1;
    }
    
    /**
     * Hilfe-Command
     */
    private int executeHelp(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendChatDivider();
        sendMessage(Formatting.YELLOW + "Scatha Pet Drop Chance Calculator Help:");
        sendMessage(Formatting.AQUA + "/" + COMMAND_NAME + Formatting.RESET + " - Calculate your current Scatha pet drop chances");
        sendMessage(Formatting.AQUA + "/" + COMMAND_NAME + " help" + Formatting.RESET + " - Shows this help message");
        sendMessage("");
        sendMessage(Formatting.GRAY + "The calculation uses your saved Magic Find and Pet Luck values.");
        sendMessage(Formatting.GRAY + "Update them with " + Formatting.WHITE + "/sp profileStats update");
        sendChatDivider();
        
        return 1;
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Berechnet Chance mit Magic Find und Pet Luck Modifiern
     */
    private double calculateChanceWithModifiers(double baseChance, double magicFind, double petLuck)
    {
        // TODO: Korrekte Formel für Magic Find und Pet Luck implementieren
        // Vereinfachte Berechnung für jetzt
        double magicFindMultiplier = 1.0 + (magicFind / 100.0);
        double petLuckMultiplier = 1.0 + (petLuck / 100.0);
        
        return baseChance * magicFindMultiplier * petLuckMultiplier;
    }
    
    /**
     * Formatiert eine Chance als Prozent-String
     */
    private String formatChance(double chance)
    {
        if (chance >= 0.01)
            return String.format("%.2f%%", chance * 100);
        else if (chance >= 0.001)
            return String.format("%.3f%%", chance * 100);
        else
            return String.format("%.4f%%", chance * 100);
    }
    
    /**
     * Formatiert "1 in X" Wert
     */
    private String formatOneIn(int value)
    {
        if (value >= 1000000)
            return String.format("%.1fM", value / 1000000.0);
        else if (value >= 1000)
            return String.format("%.1fK", value / 1000.0);
        else
            return String.valueOf(value);
    }
    
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