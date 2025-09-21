package namelessju.scathapro.fabric.chat;

import net.minecraft.text.Text;
import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;
import namelessju.scathapro.fabric.chat.parsers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Zentrale Klasse für die Verarbeitung von Chat-Nachrichten
 * Analysiert Hypixel-Chat-Messages und triggert entsprechende Events
 */
public class ChatMessageProcessor {
    
    private static final List<ChatMessageParser> parsers = new ArrayList<>();
    
    // Pattern für verschiedene Chat-Typen
    private static final Pattern SERVER_MESSAGE_PATTERN = Pattern.compile("^(?!.*:).*$"); // Keine "Player:" Prefix
    private static final Pattern SYSTEM_MESSAGE_PATTERN = Pattern.compile("^\\[.*\\]|^§[0-9a-fk-or].*");
    
    static {
        // Registriere alle Chat-Parser
        registerParsers();
    }
    
    private static void registerParsers() {
        parsers.add(new PetDropParser());
        parsers.add(new WormSpawnParser());
        parsers.add(new ScathaSpawnParser());
        parsers.add(new MagicFindParser());
        parsers.add(new HeatParser());
        parsers.add(new DayParser());
        parsers.add(new LobbyJoinParser());
        parsers.add(new GenericStatsParser());
    }
    
    /**
     * Verarbeitet eine Chat-Nachricht
     * @param message Die Chat-Nachricht als Text-Objekt
     */
    public static void processChatMessage(Text message) {
        if (message == null) return;
        
        try {
            // Konvertiere zu String und bereinige
            String rawText = message.getString();
            if (rawText == null || rawText.trim().isEmpty()) return;
            
            // Entferne Minecraft-Formatierungscodes für Analyse
            String cleanText = rawText.replaceAll("§[0-9a-fk-or]", "");
            
            // Debug-Logging (wenn aktiviert)
            logChatMessage(cleanText);
            
            // Bestimme Message-Typ
            ChatMessageType messageType = determineMessageType(cleanText);
            
            // Verarbeite nur relevante Messages
            if (!isRelevantMessage(messageType, cleanText)) {
                return;
            }
            
            // Lasse alle Parser versuchen, die Nachricht zu parsen
            for (ChatMessageParser parser : parsers) {
                try {
                    ChatEvent event = parser.parseMessage(cleanText, rawText, messageType);
                    if (event != null) {
                        // Event erfolgreich geparst - triggere es
                        triggerChatEvent(event);
                        
                        // Manche Parser sind exklusiv (z.B. Pet-Drop-Parser)
                        if (parser.isExclusiveParser()) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[Scatha-Pro] Error in parser " + parser.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("[Scatha-Pro] Error processing chat message: " + e.getMessage());
        }
    }
    
    /**
     * Bestimmt den Typ einer Chat-Nachricht
     */
    private static ChatMessageType determineMessageType(String cleanText) {
        // System/Server-Nachrichten
        if (SYSTEM_MESSAGE_PATTERN.matcher(cleanText).find()) {
            return ChatMessageType.SYSTEM;
        }
        
        // Server-Nachrichten (ohne Player-Prefix)
        if (SERVER_MESSAGE_PATTERN.matcher(cleanText).matches()) {
            return ChatMessageType.SERVER;
        }
        
        // Player-Chat
        if (cleanText.contains(":")) {
            return ChatMessageType.PLAYER;
        }
        
        return ChatMessageType.UNKNOWN;
    }
    
    /**
     * Prüft ob eine Nachricht für uns relevant ist
     */
    private static boolean isRelevantMessage(ChatMessageType type, String cleanText) {
        // Ignoriere Player-Chat (meistens nicht relevant)
        if (type == ChatMessageType.PLAYER) {
            return false;
        }
        
        // Ignoriere leere Nachrichten
        if (cleanText.trim().isEmpty()) {
            return false;
        }
        
        // Ignoriere sehr kurze Nachrichten (meist nicht relevant)
        if (cleanText.trim().length() < 3) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Triggert ein Chat-Event
     */
    private static void triggerChatEvent(ChatEvent event) {
        try {
            FabricScathaPro scathaPro = FabricScathaPro.getInstance();
            if (scathaPro != null && scathaPro.getEventManager() != null) {
                // Triggere Event über das Event-System
                scathaPro.getEventManager().triggerChatEvent(event);
                
                // Debug-Logging
                if (isDebugEnabled()) {
                    scathaPro.logDebug("Chat Event triggered: " + event.getType() + " - " + event.getData());
                }
            }
        } catch (Exception e) {
            System.err.println("[Scatha-Pro] Error triggering chat event: " + e.getMessage());
        }
    }
    
    /**
     * Debug-Logging für Chat-Nachrichten
     */
    private static void logChatMessage(String cleanText) {
        if (isDebugEnabled() && isChatLoggingEnabled()) {
            FabricScathaPro scathaPro = FabricScathaPro.getInstance();
            if (scathaPro != null) {
                scathaPro.logDebug("Chat: " + cleanText);
            }
        }
    }
    
    /**
     * Prüft ob Debug-Modus aktiviert ist
     */
    private static boolean isDebugEnabled() {
        // TODO: Implementiere Config-Check
        return true; // Temporär für Development
    }
    
    /**
     * Prüft ob Chat-Logging aktiviert ist
     */
    private static boolean isChatLoggingEnabled() {
        // TODO: Implementiere Config-Check für Chat-Logging
        return false; // Standardmäßig aus (zu viel Spam)
    }
    
    /**
     * Registriert einen neuen Chat-Parser
     */
    public static void registerParser(ChatMessageParser parser) {
        if (parser != null && !parsers.contains(parser)) {
            parsers.add(parser);
        }
    }
    
    /**
     * Entfernt einen Chat-Parser
     */
    public static void unregisterParser(ChatMessageParser parser) {
        parsers.remove(parser);
    }
    
    /**
     * Gibt alle registrierten Parser zurück
     */
    public static List<ChatMessageParser> getRegisteredParsers() {
        return new ArrayList<>(parsers);
    }
}