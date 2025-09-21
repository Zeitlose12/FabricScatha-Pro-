package namelessju.scathapro.fabric.chat.parsers;

import namelessju.scathapro.fabric.chat.ChatMessageType;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser für Tag/Zeit-Änderungen
 * Erkennt Tageszeit-Wechsel in SkyBlock
 */
public class DayParser implements ChatMessageParser {
    
    // Day/Night Change Patterns
    private static final Pattern DAY_NIGHT_PATTERN = Pattern.compile(
        "(?:The sun is rising\\.\\.\\.|The sun is setting\\.\\.\\.|A new SkyBlock day has begun!|Night has fallen)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern DAY_START_PATTERN = Pattern.compile(
        "(?:The sun is rising\\.\\.\\.|A new SkyBlock day has begun!|Day \\d+ begins)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern NIGHT_START_PATTERN = Pattern.compile(
        "(?:The sun is setting\\.\\.\\.|Night has fallen|The moon rises)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern DAY_NUMBER_PATTERN = Pattern.compile(
        "Day (\\d+)\\s*(?:begins|started|has begun)",
        Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public ChatEvent parseMessage(String cleanMessage, String rawMessage, ChatMessageType messageType) {
        if (messageType != ChatMessageType.SYSTEM && messageType != ChatMessageType.SERVER) {
            return null;
        }
        
        // Prüfe allgemeine Tag/Nacht-Patterns
        if (DAY_NIGHT_PATTERN.matcher(cleanMessage).find()) {
            
            ChatEvent event;
            
            // Bestimme ob Tag oder Nacht
            if (DAY_START_PATTERN.matcher(cleanMessage).find()) {
                event = new ChatEvent(ChatEventType.DAY_CHANGE, rawMessage, cleanMessage);
                event.withData("timeOfDay", "DAY");
                event.withData("isDayStart", true);
                
                // Versuche Day-Nummer zu extrahieren
                Matcher dayMatcher = DAY_NUMBER_PATTERN.matcher(cleanMessage);
                if (dayMatcher.find()) {
                    try {
                        int dayNumber = Integer.parseInt(dayMatcher.group(1));
                        event.withData("dayNumber", dayNumber);
                    } catch (NumberFormatException e) {
                        // Ignoriere ungültige Zahlen
                    }
                }
                
            } else if (NIGHT_START_PATTERN.matcher(cleanMessage).find()) {
                event = new ChatEvent(ChatEventType.DAY_CHANGE, rawMessage, cleanMessage);
                event.withData("timeOfDay", "NIGHT");
                event.withData("isNightStart", true);
                
            } else {
                // Allgemeiner Tag/Nacht-Wechsel
                event = new ChatEvent(ChatEventType.DAY_CHANGE, rawMessage, cleanMessage);
                event.withData("timeOfDay", "UNKNOWN");
            }
            
            return event;
        }
        
        return null;
    }
    
    @Override
    public boolean isExclusiveParser() {
        return false; // Andere Parser können auch Day-Nachrichten verarbeiten
    }
    
    @Override
    public int getPriority() {
        return 80; // Niedrige Priorität
    }
    
    @Override
    public String getDescription() {
        return "Day Parser - erkennt Tag/Nacht-Wechsel in SkyBlock";
    }
}