package namelessju.scathapro.fabric.chat.parsers;

import namelessju.scathapro.fabric.chat.ChatMessageType;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser für Hitze-Update-Nachrichten
 * Erkennt Hitze-Änderungen in den Crystal Hollows
 */
public class HeatParser implements ChatMessageParser {
    
    // Heat Update Patterns
    private static final Pattern HEAT_PATTERN = Pattern.compile(
        "(?:Heat: (\\d+)|Your heat is now (\\d+)|Heat increased to (\\d+)|Heat decreased to (\\d+))",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern HIGH_HEAT_PATTERN = Pattern.compile(
        "(?:High heat detected|You are in a high heat area|Heat is dangerously high)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern HEAT_WARNING_PATTERN = Pattern.compile(
        "(?:Heat warning|Your heat is getting high|Temperature rising)",
        Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public ChatEvent parseMessage(String cleanMessage, String rawMessage, ChatMessageType messageType) {
        if (messageType != ChatMessageType.SYSTEM && messageType != ChatMessageType.SERVER) {
            return null;
        }
        
        // Prüfe High Heat Pattern
        if (HIGH_HEAT_PATTERN.matcher(cleanMessage).find()) {
            ChatEvent event = new ChatEvent(ChatEventType.HEAT_UPDATE, rawMessage, cleanMessage);
            event.withData("heatLevel", "HIGH");
            event.withData("isHighHeat", true);
            return event;
        }
        
        // Prüfe Heat Warning Pattern
        if (HEAT_WARNING_PATTERN.matcher(cleanMessage).find()) {
            ChatEvent event = new ChatEvent(ChatEventType.HEAT_UPDATE, rawMessage, cleanMessage);
            event.withData("heatLevel", "WARNING");
            event.withData("isWarning", true);
            return event;
        }
        
        // Prüfe spezifische Hitze-Werte
        Matcher heatMatcher = HEAT_PATTERN.matcher(cleanMessage);
        if (heatMatcher.find()) {
            String heatValue = null;
            
            // Finde die erste nicht-null Gruppe
            for (int i = 1; i <= heatMatcher.groupCount(); i++) {
                if (heatMatcher.group(i) != null) {
                    heatValue = heatMatcher.group(i);
                    break;
                }
            }
            
            if (heatValue != null) {
                try {
                    int heat = Integer.parseInt(heatValue);
                    ChatEvent event = new ChatEvent(ChatEventType.HEAT_UPDATE, rawMessage, cleanMessage);
                    
                    event.withData("heat", heat);
                    event.withData("heatValue", heatValue);
                    
                    // Bestimme Heat-Level
                    if (heat >= 100) {
                        event.withData("heatLevel", "CRITICAL");
                        event.withData("isHighHeat", true);
                    } else if (heat >= 75) {
                        event.withData("heatLevel", "HIGH");
                        event.withData("isHighHeat", true);
                    } else if (heat >= 50) {
                        event.withData("heatLevel", "MEDIUM");
                    } else {
                        event.withData("heatLevel", "LOW");
                    }
                    
                    return event;
                } catch (NumberFormatException e) {
                    // Ignoriere ungültige Zahlen
                    return null;
                }
            }
        }
        
        return null;
    }
    
    @Override
    public boolean isExclusiveParser() {
        return false; // Andere Parser können auch Heat-Nachrichten verarbeiten
    }
    
    @Override
    public int getPriority() {
        return 50; // Mittlere Priorität
    }
    
    @Override
    public String getDescription() {
        return "Heat Parser - erkennt Hitze-Update-Nachrichten in Crystal Hollows";
    }
}