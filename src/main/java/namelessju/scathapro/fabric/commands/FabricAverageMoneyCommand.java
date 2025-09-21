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
 * Fabric Average Money Command - Durchschnittliche Scatha-Farming-Profit-Berechnung
 */
public class FabricAverageMoneyCommand
{
    private final FabricScathaPro scathaPro;
    
    public FabricAverageMoneyCommand(FabricScathaPro scathaPro)
    {
        this.scathaPro = scathaPro;
    }
    
    /**
     * Registriert den Command bei Fabric
     * Wird über FabricMainCommand aufgerufen (/sp averageMoney)
     */
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        // Dieser Command wird hauptsächlich über /sp averageMoney aufgerufen
        // Könnte aber auch als eigenständiger Command registriert werden
    }
    
    /**
     * Berechnet durchschnittliche Scatha-Farming-Profite
     */
    public int calculateAverageMoney(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendChatDivider();
        sendMessage(Formatting.YELLOW + "Scatha Farming Average Money Calculation:");
        
        // TODO: Korrekte Werte für Scatha-Items implementieren
        // Basis-Item-Preise (Beispielwerte)
        double scathaFleshPrice = 50000; // 50k per flesh
        double scathaScalePrice = 150000; // 150k per scale
        double hardstonePrice = 5; // 5 coins per hardstone
        
        // Pet-Preise (Beispielwerte)
        double rarePetPrice = 10000000; // 10M
        double epicPetPrice = 50000000; // 50M
        double legendaryPetPrice = 200000000; // 200M
        
        // Drop-Raten berechnen
        double scathaKillRate = calculateScathaKillRate();
        double hardstoneMineRate = calculateHardstoneMineRate();
        
        // TODO: Pet-Drop-Chancen aus FabricChancesCommand verwenden
        double rareChance = 0.0005; // 0.05%
        double epicChance = 0.0001; // 0.01%
        double legendaryChance = 0.00002; // 0.002%
        
        // Durchschnittliche Profite berechnen
        double scathaProfit = scathaKillRate * (scathaFleshPrice + scathaScalePrice * 0.1); // 10% Scale-Chance
        double hardstoneProfit = hardstoneMineRate * hardstonePrice;
        double petProfit = scathaKillRate * (
            rareChance * rarePetPrice +
            epicChance * epicPetPrice +
            legendaryChance * legendaryPetPrice
        );
        
        double totalProfit = scathaProfit + hardstoneProfit + petProfit;
        
        // Ergebnisse anzeigen
        sendMessage(Formatting.GREEN + "Estimated profits per hour:");
        sendMessage(" Scatha Items: " + Formatting.GOLD + formatMoney(scathaProfit));
        sendMessage(" Hardstone: " + Formatting.GRAY + formatMoney(hardstoneProfit));
        sendMessage(" Pet Drops: " + Formatting.LIGHT_PURPLE + formatMoney(petProfit));
        sendMessage("");
        sendMessage(Formatting.YELLOW + "Total: " + Formatting.GREEN + formatMoney(totalProfit) + "/h");
        
        // Zusätzliche Statistiken
        sendMessage("");
        sendMessage(Formatting.GRAY + "Based on:");
        sendMessage(" " + String.format("%.1f", scathaKillRate) + " Scatha kills/hour");
        sendMessage(" " + String.format("%.0f", hardstoneMineRate) + " Hardstone mined/hour");
        sendMessage(" Current Magic Find: " + scathaPro.variables.getMagicFindString());
        sendMessage(" Current Pet Luck: " + scathaPro.variables.getPetLuckString());
        
        sendChatDivider();
        
        return 1;
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Berechnet geschätzte Scatha-Kill-Rate pro Stunde
     */
    private double calculateScathaKillRate()
    {
        // TODO: Basierend auf Spieler-Statistiken und Equipment berechnen
        // Beispiel: Durchschnittlich 30 Scatha-Kills pro Stunde
        return 30.0;
    }
    
    /**
     * Berechnet geschätzte Hardstone-Mine-Rate pro Stunde
     */
    private double calculateHardstoneMineRate()
    {
        // TODO: Basierend auf Mining-Speed und Effizienz berechnen
        // Beispiel: 10000 Hardstone pro Stunde
        return 10000.0;
    }
    
    /**
     * Formatiert Geld-Betrag als lesbaren String
     */
    private String formatMoney(double amount)
    {
        if (amount >= 1000000000)
            return String.format("%.1fB", amount / 1000000000.0);
        else if (amount >= 1000000)
            return String.format("%.1fM", amount / 1000000.0);
        else if (amount >= 1000)
            return String.format("%.1fK", amount / 1000.0);
        else
            return String.format("%.0f", amount);
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