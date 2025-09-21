package namelessju.scathapro.fabric.chat.parsers;

import namelessju.scathapro.fabric.chat.ChatMessageType;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser für allgemeine Statistik-Nachrichten
 * Erkennt verschiedene Stats-Updates in SkyBlock
 */
public class GenericStatsParser implements ChatMessageParser {
    
    // Stats Update Patterns
    private static final Pattern MINING_SPEED_PATTERN = Pattern.compile(
        "(?:Mining Speed: ([\\d,]+)|Your mining speed is now ([\\d,]+))",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern MITHRIL_POWDER_PATTERN = Pattern.compile(
        "(?:\\+([\\d,]+) Mithril Powder|You received ([\\d,]+) Mithril Powder)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern GEMSTONE_POWDER_PATTERN = Pattern.compile(
        "(?:\\+([\\d,]+) Gemstone Powder|You received ([\\d,]+) Gemstone Powder)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern COMMISSION_PROGRESS_PATTERN = Pattern.compile(
        "(?:Commission progress: ([\\d,]+)/([\\d,]+)|Progress: ([\\d,]+)/([\\d,]+))",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern COMMISSION_COMPLETE_PATTERN = Pattern.compile(
        "(?:Commission Complete!|You completed|COMMISSION COMPLETED)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern SKILL_EXP_PATTERN = Pattern.compile(
        "(?:\\+([\\d,\\.]+) (.+) \\(([\\d,\\.]+)/([\\d,\\.]+)\\)|\\+([\\d,\\.]+) (.+) Experience)",
        Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public ChatEvent parseMessage(String cleanMessage, String rawMessage, ChatMessageType messageType) {
        if (messageType != ChatMessageType.SYSTEM && messageType != ChatMessageType.SERVER) {
            return null;
        }
        
        // Prüfe Mining Speed Updates
        Matcher miningSpeedMatcher = MINING_SPEED_PATTERN.matcher(cleanMessage);
        if (miningSpeedMatcher.find()) {
            String speedValue = null;
            for (int i = 1; i <= miningSpeedMatcher.groupCount(); i++) {
                if (miningSpeedMatcher.group(i) != null) {
                    speedValue = miningSpeedMatcher.group(i).replace(",", "");
                    break;
                }
            }
            
            if (speedValue != null) {
                try {
                    int speed = Integer.parseInt(speedValue);
                    ChatEvent event = new ChatEvent(ChatEventType.STATS_UPDATE, rawMessage, cleanMessage);
                    event.withData("statType", "MINING_SPEED");
                    event.withData("value", speed);
                    event.withData("rawValue", speedValue);
                    return event;
                } catch (NumberFormatException e) {
                    // Ignoriere ungültige Zahlen
                }
            }
        }
        
        // Prüfe Mithril Powder Updates
        Matcher mithrilPowderMatcher = MITHRIL_POWDER_PATTERN.matcher(cleanMessage);
        if (mithrilPowderMatcher.find()) {
            String powderValue = null;
            for (int i = 1; i <= mithrilPowderMatcher.groupCount(); i++) {
                if (mithrilPowderMatcher.group(i) != null) {
                    powderValue = mithrilPowderMatcher.group(i).replace(",", "");
                    break;
                }
            }
            
            if (powderValue != null) {
                try {
                    int powder = Integer.parseInt(powderValue);
                    ChatEvent event = new ChatEvent(ChatEventType.STATS_UPDATE, rawMessage, cleanMessage);
                    event.withData("statType", "MITHRIL_POWDER");
                    event.withData("value", powder);
                    event.withData("rawValue", powderValue);
                    return event;
                } catch (NumberFormatException e) {
                    // Ignoriere ungültige Zahlen
                }
            }
        }
        
        // Prüfe Gemstone Powder Updates
        Matcher gemstonePowderMatcher = GEMSTONE_POWDER_PATTERN.matcher(cleanMessage);
        if (gemstonePowderMatcher.find()) {
            String powderValue = null;
            for (int i = 1; i <= gemstonePowderMatcher.groupCount(); i++) {
                if (gemstonePowderMatcher.group(i) != null) {
                    powderValue = gemstonePowderMatcher.group(i).replace(",", "");
                    break;
                }
            }
            
            if (powderValue != null) {
                try {
                    int powder = Integer.parseInt(powderValue);
                    ChatEvent event = new ChatEvent(ChatEventType.STATS_UPDATE, rawMessage, cleanMessage);
                    event.withData("statType", "GEMSTONE_POWDER");
                    event.withData("value", powder);
                    event.withData("rawValue", powderValue);
                    return event;
                } catch (NumberFormatException e) {
                    // Ignoriere ungültige Zahlen
                }
            }
        }
        
        // Prüfe Commission Complete
        if (COMMISSION_COMPLETE_PATTERN.matcher(cleanMessage).find()) {
            ChatEvent event = new ChatEvent(ChatEventType.COMMISSION_COMPLETE, rawMessage, cleanMessage);
            return event;
        }
        
        // Prüfe Commission Progress
        Matcher commissionProgressMatcher = COMMISSION_PROGRESS_PATTERN.matcher(cleanMessage);
        if (commissionProgressMatcher.find()) {
            try {
                String current = null, total = null;
                
                if (commissionProgressMatcher.group(1) != null && commissionProgressMatcher.group(2) != null) {
                    current = commissionProgressMatcher.group(1).replace(",", "");
                    total = commissionProgressMatcher.group(2).replace(",", "");
                } else if (commissionProgressMatcher.group(3) != null && commissionProgressMatcher.group(4) != null) {
                    current = commissionProgressMatcher.group(3).replace(",", "");
                    total = commissionProgressMatcher.group(4).replace(",", "");
                }
                
                if (current != null && total != null) {
                    int currentInt = Integer.parseInt(current);
                    int totalInt = Integer.parseInt(total);
                    
                    ChatEvent event = new ChatEvent(ChatEventType.COMMISSION_PROGRESS, rawMessage, cleanMessage);
                    event.withData("current", currentInt);
                    event.withData("total", totalInt);
                    event.withData("progress", (double) currentInt / totalInt);
                    return event;
                }
            } catch (NumberFormatException e) {
                // Ignoriere ungültige Zahlen
            }
        }
        
        // Prüfe Skill Experience
        Matcher skillExpMatcher = SKILL_EXP_PATTERN.matcher(cleanMessage);
        if (skillExpMatcher.find()) {
            try {
                String expGained, skillName, currentExp, maxExp;
                
                if (skillExpMatcher.group(1) != null && skillExpMatcher.group(2) != null) {
                    // Pattern: "+123.4 Mining (1500/2000)"
                    expGained = skillExpMatcher.group(1).replace(",", "");
                    skillName = skillExpMatcher.group(2);
                    currentExp = skillExpMatcher.group(3) != null ? skillExpMatcher.group(3).replace(",", "") : null;
                    maxExp = skillExpMatcher.group(4) != null ? skillExpMatcher.group(4).replace(",", "") : null;
                } else {
                    // Pattern: "+123.4 Mining Experience"
                    expGained = skillExpMatcher.group(5).replace(",", "");
                    skillName = skillExpMatcher.group(6).replace(" Experience", "");
                    currentExp = null;
                    maxExp = null;
                }
                
                double expGainedDouble = Double.parseDouble(expGained);
                
                ChatEvent event = new ChatEvent(ChatEventType.SKILL_EXP_GAIN, rawMessage, cleanMessage);
                event.withData("skillName", skillName);
                event.withData("expGained", expGainedDouble);
                
                if (currentExp != null && maxExp != null) {
                    event.withData("currentExp", Double.parseDouble(currentExp));
                    event.withData("maxExp", Double.parseDouble(maxExp));
                    event.withData("progress", Double.parseDouble(currentExp) / Double.parseDouble(maxExp));
                }
                
                return event;
            } catch (NumberFormatException e) {
                // Ignoriere ungültige Zahlen
            }
        }
        
        return null;
    }
    
    @Override
    public boolean isExclusiveParser() {
        return false; // Andere Parser können auch Stats-Nachrichten verarbeiten
    }
    
    @Override
    public int getPriority() {
        return 90; // Niedrigste Priorität (Fallback-Parser)
    }
    
    @Override
    public String getDescription() {
        return "Generic Stats Parser - erkennt allgemeine Statistik-Updates";
    }
}
