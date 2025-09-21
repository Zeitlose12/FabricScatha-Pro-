package namelessju.scathapro.fabric.chat.parsers;

import namelessju.scathapro.fabric.chat.ChatMessageType;
import namelessju.scathapro.fabric.chat.events.ChatEvent;

/**
 * Interface für Chat-Message-Parser
 * Jeder Parser ist für einen bestimmten Typ von Chat-Nachrichten zuständig
 */
public interface ChatMessageParser {
    
    /**
     * Versucht eine Chat-Nachricht zu parsen
     * 
     * @param cleanMessage Nachricht ohne Formatierungscodes
     * @param rawMessage Original-Nachricht mit Formatierungscodes
     * @param messageType Typ der Nachricht
     * @return ChatEvent wenn erfolgreich geparst, null sonst
     */
    ChatEvent parseMessage(String cleanMessage, String rawMessage, ChatMessageType messageType);
    
    /**
     * Gibt an ob dieser Parser exklusiv ist
     * Exklusive Parser stoppen die weitere Verarbeitung wenn sie erfolgreich waren
     * 
     * @return true wenn exklusiv, false sonst
     */
    default boolean isExclusiveParser() {
        return false;
    }
    
    /**
     * Gibt die Priorität des Parsers an
     * Niedrigere Zahlen = höhere Priorität
     * 
     * @return Priorität (0 = höchste Priorität)
     */
    default int getPriority() {
        return 100;
    }
    
    /**
     * Gibt eine Beschreibung des Parsers zurück
     * 
     * @return Beschreibung
     */
    default String getDescription() {
        return this.getClass().getSimpleName();
    }
}