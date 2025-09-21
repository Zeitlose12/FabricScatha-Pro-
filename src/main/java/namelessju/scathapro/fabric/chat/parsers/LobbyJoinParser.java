package namelessju.scathapro.fabric.chat.parsers;

import namelessju.scathapro.fabric.chat.ChatMessageType;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser für Lobby-Join-Nachrichten
 * Erkennt wenn Spieler eine neue Lobby betreten
 */
public class LobbyJoinParser implements ChatMessageParser {
    
    // Lobby Join Patterns
    private static final Pattern LOBBY_JOIN_PATTERN = Pattern.compile(
        "(?:Welcome to Hypixel SkyBlock!|You are now connected to (.+)|Sending you to (.+)|Connected to (.+))",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern AREA_JOIN_PATTERN = Pattern.compile(
        "(?:You are now in the (.+)!|Welcome to (.+)!|Entering (.+)...)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern CRYSTAL_HOLLOWS_PATTERN = Pattern.compile(
        "(?:Welcome to the Crystal Hollows!|You have entered the Crystal Hollows|Crystal Hollows)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern SERVER_CHANGE_PATTERN = Pattern.compile(
        "(?:You were sent from (.+) to (.+)|Server transfer complete)",
        Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public ChatEvent parseMessage(String cleanMessage, String rawMessage, ChatMessageType messageType) {
        if (messageType != ChatMessageType.SYSTEM && messageType != ChatMessageType.SERVER) {
            return null;
        }
        
        // Prüfe Crystal Hollows spezifisch
        if (CRYSTAL_HOLLOWS_PATTERN.matcher(cleanMessage).find()) {
            ChatEvent event = new ChatEvent(ChatEventType.LOBBY_JOIN, rawMessage, cleanMessage);
            event.withData("area", "Crystal Hollows");
            event.withData("isCrystalHollows", true);
            event.withData("joinType", "CRYSTAL_HOLLOWS");
            return event;
        }
        
        // Prüfe Server-Wechsel
        Matcher serverChangeMatcher = SERVER_CHANGE_PATTERN.matcher(cleanMessage);
        if (serverChangeMatcher.find()) {
            ChatEvent event = new ChatEvent(ChatEventType.LOBBY_JOIN, rawMessage, cleanMessage);
            event.withData("joinType", "SERVER_TRANSFER");
            
            if (serverChangeMatcher.groupCount() >= 2) {
                String from = serverChangeMatcher.group(1);
                String to = serverChangeMatcher.group(2);
                if (from != null) event.withData("fromServer", from.trim());
                if (to != null) event.withData("toServer", to.trim());
            }
            
            return event;
        }
        
        // Prüfe Area-Join
        Matcher areaJoinMatcher = AREA_JOIN_PATTERN.matcher(cleanMessage);
        if (areaJoinMatcher.find()) {
            String areaName = null;
            
            // Finde die erste nicht-null Gruppe
            for (int i = 1; i <= areaJoinMatcher.groupCount(); i++) {
                if (areaJoinMatcher.group(i) != null) {
                    areaName = areaJoinMatcher.group(i).trim();
                    break;
                }
            }
            
            if (areaName != null) {
                ChatEvent event = new ChatEvent(ChatEventType.LOBBY_JOIN, rawMessage, cleanMessage);
                event.withData("area", areaName);
                event.withData("joinType", "AREA_JOIN");
                
                // Spezielle Areas markieren
                if (areaName.toLowerCase().contains("crystal hollows")) {
                    event.withData("isCrystalHollows", true);
                } else if (areaName.toLowerCase().contains("dwarven mines")) {
                    event.withData("isDwarvenMines", true);
                }
                
                return event;
            }
        }
        
        // Prüfe allgemeine Lobby-Join
        Matcher lobbyJoinMatcher = LOBBY_JOIN_PATTERN.matcher(cleanMessage);
        if (lobbyJoinMatcher.find()) {
            ChatEvent event = new ChatEvent(ChatEventType.LOBBY_JOIN, rawMessage, cleanMessage);
            event.withData("joinType", "GENERAL_JOIN");
            
            // Versuche Server-Name zu extrahieren
            if (lobbyJoinMatcher.groupCount() >= 1) {
                String serverName = null;
                
                for (int i = 1; i <= lobbyJoinMatcher.groupCount(); i++) {
                    if (lobbyJoinMatcher.group(i) != null) {
                        serverName = lobbyJoinMatcher.group(i).trim();
                        break;
                    }
                }
                
                if (serverName != null) {
                    event.withData("server", serverName);
                }
            }
            
            return event;
        }
        
        return null;
    }
    
    @Override
    public boolean isExclusiveParser() {
        return false; // Andere Parser können auch Lobby-Join-Nachrichten verarbeiten
    }
    
    @Override
    public int getPriority() {
        return 30; // Relativ hohe Priorität
    }
    
    @Override
    public String getDescription() {
        return "Lobby Join Parser - erkennt Lobby/Area-Wechsel in SkyBlock";
    }
}