package namelessju.scathapro.fabric.chat;

/**
 * Enum für verschiedene Typen von Chat-Nachrichten
 */
public enum ChatMessageType {
    SYSTEM,    // System-Nachrichten (mit [Prefix])
    SERVER,    // Server-Nachrichten (ohne Player-Prefix)
    PLAYER,    // Player-Chat (mit "Player:" Prefix)
    UNKNOWN    // Unbekannter Typ
}