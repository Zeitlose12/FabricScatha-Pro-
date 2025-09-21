package namelessju.scathapro.fabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import namelessju.scathapro.fabric.Constants;
import namelessju.scathapro.fabric.FabricScathaPro;

/**
 * Fabric Main-Command (/scathapro, /sp)
 * Haupt-Command mit Hilfe, Settings und verschiedenen Subcommands
 */
public class FabricMainCommand
{
    public static final String COMMAND_NAME = "scathapro";
    private final FabricScathaPro scathaPro;
    
    public FabricMainCommand(FabricScathaPro scathaPro)
    {
        this.scathaPro = scathaPro;
    }
    
    /**
     * Registriert den Command bei Fabric
     */
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        dispatcher.register(ClientCommandManager.literal(COMMAND_NAME)
            .executes(this::executeMain)
            .then(ClientCommandManager.literal("help")
                .executes(ctx -> executeHelp(ctx, 1))
                .then(ClientCommandManager.argument("page", IntegerArgumentType.integer(1, 2))
                    .executes(ctx -> executeHelp(ctx, IntegerArgumentType.getInteger(ctx, "page")))
                )
            )
            .then(ClientCommandManager.literal("settings")
                .executes(this::executeSettings)
            )
            .then(ClientCommandManager.literal("config")
                .executes(this::executeSettings)
            )
            .then(ClientCommandManager.literal("achievements")
                .executes(this::executeAchievements)
            )
            .then(ClientCommandManager.literal("dailyStreak")
                .executes(this::executeDailyStreak)
            )
            .then(ClientCommandManager.literal("daily")
                .executes(this::executeDailyStreak)
            )
            .then(ClientCommandManager.literal("streak")
                .executes(this::executeDailyStreak)
            )
            .then(ClientCommandManager.literal("profileStats")
                .executes(this::executeProfileStats)
                .then(ClientCommandManager.literal("update")
                    .executes(this::executeProfileStatsUpdate)
                    .then(ClientCommandManager.literal("confirm")
                        .executes(this::executeProfileStatsUpdateConfirm)
                    )
                )
            )
            .then(ClientCommandManager.literal("setPetDrops")
                .then(ClientCommandManager.argument("rare", IntegerArgumentType.integer(0))
                    .then(ClientCommandManager.argument("epic", IntegerArgumentType.integer(0))
                        .then(ClientCommandManager.argument("legendary", IntegerArgumentType.integer(0))
                            .executes(this::executeSetPetDrops)
                        )
                    )
                )
            )
            .then(ClientCommandManager.literal("toggleOverlay")
                .executes(this::executeToggleOverlay)
            )
            .then(ClientCommandManager.literal("to")
                .executes(this::executeToggleOverlay)
            )
            .then(ClientCommandManager.literal("checkUpdate")
                .executes(this::executeCheckUpdate)
            )
            .then(ClientCommandManager.literal("resetSettings")
                .executes(this::executeResetSettings)
            )
            .then(ClientCommandManager.literal("averageMoney")
                .executes(this::executeAverageMoney)
            )
            .then(ClientCommandManager.literal("avgMoney")
                .executes(this::executeAverageMoney)
            )
        );
        
        // Alias /sp registrieren
        dispatcher.register(ClientCommandManager.literal("sp")
            .redirect(dispatcher.getRoot().getChild(COMMAND_NAME))
        );
    }
    
    // ===== COMMAND EXECUTORS =====
    
    /**
     * Haupt-Command ohne Argumente -> zeigt Hilfe
     */
    private int executeMain(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        return executeHelp(ctx, 1);
    }
    
    /**
     * Hilfe-Command
     */
    private int executeHelp(CommandContext<FabricClientCommandSource> ctx, int page) throws CommandSyntaxException
    {
        sendChatDivider();
        sendMessage(Formatting.YELLOW + FabricScathaPro.DYNAMIC_MODNAME + " commands:");
        
        switch (page)
        {
            case 1:
                sendHelpEntry(COMMAND_NAME, "(\"help\")", "/sp", "Shows this help message");
                sendHelpEntry("", "settings", "", "Opens the mod's settings menu", true);
                sendHelpEntry("", "achievements", "", "Opens the achievements menu", true);
                sendHelpEntry("scacha", "(\"help\")", "/scacha", "Check/calculate Scatha pet drop chances", true);
                sendHelpEntry("averageMoney", "", "/sp averageMoney/avgMoney", "Calculate average Scatha farming profits");
                sendHelpEntry("", "dailyStreak", "daily, streak", "Shows information about your daily Scatha farming streak", true);
                sendHelpEntry("", "profileStats", "", "Check/update the values that the mod uses when displaying profile stats", true);
                break;
                
            case 2:
                sendHelpEntry("", "setPetDrops <rare> <epic> <legendary>", "", "Set your pet drop counter to the specified numbers");
                sendHelpEntry("", "toggleOverlay", "to", "Toggles the overlay visibility", true);
                sendHelpEntry("", "checkUpdate", "", "Check for mod updates", true);
                sendHelpEntry("", "resetSettings", "", "Reset all settings");
                break;
                
            default:
                sendMessage(Formatting.RED + "Something went wrong! (invalid page number)");
        }
        
        sendMessage(Formatting.YELLOW + "Help page " + page + "/2 - /sp help <page>");
        sendChatDivider();
        
        return 1;
    }
    
    /**
     * Settings-Command
     */
    private int executeSettings(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendMessage(Formatting.GREEN + "Settings-GUI wird geöffnet...");
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null)
        {
            mc.execute(() -> {
                mc.setScreen(new namelessju.scathapro.fabric.gui.SettingsScreen(mc.currentScreen));
            });
        }
        
        return 1;
    }
    
    /**
     * Achievements-Command
     */
    private int executeAchievements(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendMessage(Formatting.GREEN + "Achievements-GUI wird geöffnet...");
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null)
        {
            mc.execute(() -> mc.setScreen(new namelessju.scathapro.fabric.gui.AchievementScreen(mc.currentScreen)));
        }
        return 1;
    }
    
    /**
     * Daily-Streak-Command
     */
    private int executeDailyStreak(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        boolean farmedToday = scathaPro.variables.lastScathaFarmedDate != null && 
                               scathaPro.variables.lastScathaFarmedDate.equals(namelessju.scathapro.fabric.util.TimeUtil.today());
        
        sendChatDivider();
        sendMessage(Formatting.YELLOW + "Daily Scatha farming streak:");
        sendMessage("Current streak: " + Formatting.GREEN + scathaPro.variables.scathaFarmingStreak + 
                   " day" + (scathaPro.variables.scathaFarmingStreak != 1 ? "s" : ""));
        sendMessage("Highest streak: " + Formatting.GOLD + scathaPro.variables.scathaFarmingStreakHighscore + 
                   " day" + (scathaPro.variables.scathaFarmingStreakHighscore != 1 ? "s" : ""));
        
        if (farmedToday)
            sendMessage(Formatting.GREEN + "✓ You have farmed Scathas today!");
        else
            sendMessage(Formatting.RED + "✗ You haven't farmed Scathas yet today...");
            
        sendChatDivider();
        
        return 1;
    }
    
    /**
     * Profile-Stats-Command
     */
    private int executeProfileStats(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendMessage(Formatting.YELLOW + "Saved Scatha farming profile stats:");
        sendMessage(" " + scathaPro.variables.getMagicFindString() + " Magic Find");
        sendMessage(" " + scathaPro.variables.getBestiaryMagicFindString() + " Worm Bestiary Magic Find");
        sendMessage(" " + scathaPro.variables.getPetLuckString() + " Pet Luck");
        
        // TODO: Clickable update commands implementieren
        sendMessage(Formatting.GRAY + "Use /sp profileStats update to update these values");
        
        return 1;
    }
    
    /**
     * Profile-Stats-Update-Command
     */
    private int executeProfileStatsUpdate(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendMessage(Formatting.YELLOW + "Equip everything (armor, pet, weapon) you use when killing a Scatha and then use:");
        sendMessage(Formatting.GREEN + "/sp profileStats update confirm");
        
        return 1;
    }
    
    /**
     * Profile-Stats-Update-Confirm-Command
     */
    private int executeProfileStatsUpdateConfirm(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        // TODO: ProfileStatsParser implementieren
        // scathaPro.getChestGuiParsingManager().profileStatsParser.enabled = true;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.player != null)
        {
            mc.player.networkHandler.sendChatMessage("/sbmenu");
        }
        
        sendMessage(Formatting.GREEN + "Profile stats update initiated!");
        
        return 1;
    }
    
    /**
     * Set-Pet-Drops-Command
     */
    private int executeSetPetDrops(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        int rare = IntegerArgumentType.getInteger(ctx, "rare");
        int epic = IntegerArgumentType.getInteger(ctx, "epic");
        int legendary = IntegerArgumentType.getInteger(ctx, "legendary");
        
        if (rare > Constants.maxLegitPetDropsAmount || epic > Constants.maxLegitPetDropsAmount || 
            legendary > Constants.maxLegitPetDropsAmount)
        {
            sendMessage(Formatting.RED + "Pet drop amount too large! Maximum: " + Constants.maxLegitPetDropsAmount);
            return 0;
        }
        
        // Pet-Drop-Counter setzen
        scathaPro.variables.rarePetDrops = rare;
        scathaPro.variables.epicPetDrops = epic;
        scathaPro.variables.legendaryPetDrops = legendary;
        
        sendMessage(Formatting.GREEN + "Pet drops set to: " + 
                   Formatting.BLUE + rare + " rare" + Formatting.RESET + ", " +
                   Formatting.DARK_PURPLE + epic + " epic" + Formatting.RESET + ", " +
                   Formatting.GOLD + legendary + " legendary");
        
        // TODO: Persistent Data speichern
        // scathaPro.getPersistentData().savePetDrops();
        
        // TODO: Overlay aktualisieren
        // scathaPro.getOverlay().updatePetDrops();
        
        return 1;
    }
    
    /**
     * Toggle-Overlay-Command
     */
    private int executeToggleOverlay(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        // TODO: Overlay-Toggle implementieren
        sendMessage(Formatting.YELLOW + "Overlay visibility toggled!");
        
        return 1;
    }
    
    /**
     * Check-Update-Command
     */
    private int executeCheckUpdate(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        sendMessage(Formatting.YELLOW + "Checking for updates...");
        namelessju.scathapro.fabric.update.FabricUpdateChecker.check(true);
        return 1;
    }
    
    /**
     * Reset-Settings-Command
     */
    private int executeResetSettings(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        // TODO: Settings-Reset implementieren
        sendMessage(Formatting.RED + "Settings reset functionality not yet implemented");
        
        return 1;
    }
    
    /**
     * Average-Money-Command
     */
    private int executeAverageMoney(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException
    {
        // Delegiere an FabricAverageMoneyCommand
        return scathaPro.getCommandRegistry().getAverageMoneyCommand().calculateAverageMoney(ctx);
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
    
    /**
     * Sendet einen Hilfe-Eintrag
     */
    private void sendHelpEntry(String command, String args, String alias, String description)
    {
        sendHelpEntry(command, args, alias, description, false);
    }
    
    /**
     * Sendet einen Hilfe-Eintrag mit optionaler Klick-Funktionalität
     */
    private void sendHelpEntry(String command, String args, String alias, String description, boolean clickable)
    {
        String commandText = command.isEmpty() ? "" : "/" + command;
        String argsText = args.isEmpty() ? "" : " " + args;
        String aliasText = alias.isEmpty() ? "" : " (" + alias + ")";
        
        String fullCommand = commandText + argsText + aliasText;
        String message = Formatting.AQUA + fullCommand + Formatting.RESET + " - " + Formatting.WHITE + description;
        
        sendMessage(message);
    }
}