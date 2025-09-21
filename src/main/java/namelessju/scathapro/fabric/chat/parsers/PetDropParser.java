package namelessju.scathapro.fabric.chat.parsers;

import namelessju.scathapro.fabric.chat.ChatMessageType;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser für Pet-Drop-Nachrichten
 * Erkennt verschiedene Pet-Drop-Formate von Hypixel
 */
public class PetDropParser implements ChatMessageParser {
    
    // Pet Drop Patterns (verschiedene Formate)
    private static final Pattern PET_DROP_PATTERN_1 = Pattern.compile(
        "PET DROP! (.+) \\((.+)\\)"
    );
    
    private static final Pattern PET_DROP_PATTERN_2 = Pattern.compile(
        "(.+) found a (.+) (.+)!"
    );
    
    private static final Pattern PET_DROP_PATTERN_3 = Pattern.compile(
        "RARE DROP! (.+) \\((.+)\\) \\(\\+(.+)% ✯ Magic Find\\)"
    );
    
    private static final Pattern PET_DROP_PATTERN_4 = Pattern.compile(
        "(.+) has obtained (.+) (.+)!"
    );
    
    // Scatha Pet spezifische Patterns
    private static final Pattern SCATHA_PET_PATTERN = Pattern.compile(
        ".*(scatha).*pet.*", Pattern.CASE_INSENSITIVE
    );
    
    // Rarity Patterns
    private static final Pattern RARITY_PATTERN = Pattern.compile(
        "(COMMON|UNCOMMON|RARE|EPIC|LEGENDARY)", Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public ChatEvent parseMessage(String cleanMessage, String rawMessage, ChatMessageType messageType) {
        if (messageType != ChatMessageType.SYSTEM && messageType != ChatMessageType.SERVER) {
            return null;
        }
        
        // Prüfe verschiedene Pet-Drop-Pattern
        ChatEvent event = null;
        
        // Pattern 1: "PET DROP! Scatha (Epic)"
        event = tryParsePattern1(cleanMessage, rawMessage);
        if (event != null) return event;
        
        // Pattern 2: "Player found a Epic Scatha!"
        event = tryParsePattern2(cleanMessage, rawMessage);
        if (event != null) return event;
        
        // Pattern 3: "RARE DROP! Scatha (Epic) (+123% ✯ Magic Find)"
        event = tryParsePattern3(cleanMessage, rawMessage);
        if (event != null) return event;
        
        // Pattern 4: "Player has obtained Epic Scatha!"
        event = tryParsePattern4(cleanMessage, rawMessage);
        if (event != null) return event;
        
        return null;
    }
    
    private ChatEvent tryParsePattern1(String cleanMessage, String rawMessage) {
        Matcher matcher = PET_DROP_PATTERN_1.matcher(cleanMessage);
        if (matcher.find()) {
            String petName = matcher.group(1).trim();
            String rarity = matcher.group(2).trim();
            
            ChatEventType eventType = determineEventType(rarity);
            ChatEvent event = new ChatEvent(eventType, rawMessage, cleanMessage);
            
            event.withData("petType", petName);
            event.withData("rarity", rarity);
            event.withData("format", "pattern1");
            
            // Prüfe ob es ein Scatha Pet ist
            if (isScathaPet(petName)) {
                event.withData("isScathaPet", true);
            }
            
            return event;
        }
        return null;
    }
    
    private ChatEvent tryParsePattern2(String cleanMessage, String rawMessage) {
        Matcher matcher = PET_DROP_PATTERN_2.matcher(cleanMessage);
        if (matcher.find()) {
            String player = matcher.group(1).trim();
            String rarity = matcher.group(2).trim();
            String petName = matcher.group(3).trim();
            
            ChatEventType eventType = determineEventType(rarity);
            ChatEvent event = new ChatEvent(eventType, rawMessage, cleanMessage);
            
            event.withData("player", player);
            event.withData("petType", petName);
            event.withData("rarity", rarity);
            event.withData("format", "pattern2");
            
            if (isScathaPet(petName)) {
                event.withData("isScathaPet", true);
            }
            
            return event;
        }
        return null;
    }
    
    private ChatEvent tryParsePattern3(String cleanMessage, String rawMessage) {
        Matcher matcher = PET_DROP_PATTERN_3.matcher(cleanMessage);
        if (matcher.find()) {
            String petName = matcher.group(1).trim();
            String rarity = matcher.group(2).trim();
            String magicFind = matcher.group(3).trim();
            
            ChatEventType eventType = determineEventType(rarity);
            ChatEvent event = new ChatEvent(eventType, rawMessage, cleanMessage);
            
            event.withData("petType", petName);
            event.withData("rarity", rarity);
            event.withData("magicFind", magicFind);
            event.withData("format", "pattern3");
            
            if (isScathaPet(petName)) {
                event.withData("isScathaPet", true);
            }
            
            return event;
        }
        return null;
    }
    
    private ChatEvent tryParsePattern4(String cleanMessage, String rawMessage) {
        Matcher matcher = PET_DROP_PATTERN_4.matcher(cleanMessage);
        if (matcher.find()) {
            String player = matcher.group(1).trim();
            String rarity = matcher.group(2).trim();
            String petName = matcher.group(3).trim();
            
            ChatEventType eventType = determineEventType(rarity);
            ChatEvent event = new ChatEvent(eventType, rawMessage, cleanMessage);
            
            event.withData("player", player);
            event.withData("petType", petName);
            event.withData("rarity", rarity);
            event.withData("format", "pattern4");
            
            if (isScathaPet(petName)) {
                event.withData("isScathaPet", true);
            }
            
            return event;
        }
        return null;
    }
    
    /**
     * Bestimmt den Event-Typ basierend auf der Rarity
     */
    private ChatEventType determineEventType(String rarity) {
        String rarityLower = rarity.toLowerCase();
        
        switch (rarityLower) {
            case "rare":
            case "blue":
                return ChatEventType.PET_DROP_RARE;
            case "epic":
            case "purple":
                return ChatEventType.PET_DROP_EPIC;
            case "legendary":
            case "orange":
            case "gold":
                return ChatEventType.PET_DROP_LEGENDARY;
            default:
                return ChatEventType.PET_DROP_RARE; // Fallback
        }
    }
    
    /**
     * Prüft ob es sich um ein Scatha Pet handelt
     */
    private boolean isScathaPet(String petName) {
        if (petName == null) return false;
        return SCATHA_PET_PATTERN.matcher(petName).matches();
    }
    
    @Override
    public boolean isExclusiveParser() {
        return true; // Pet Drops sind eindeutig, weitere Parser nicht nötig
    }
    
    @Override
    public int getPriority() {
        return 1; // Sehr hohe Priorität
    }
    
    @Override
    public String getDescription() {
        return "Pet Drop Parser - erkennt Hypixel Pet-Drop-Nachrichten";
    }
}