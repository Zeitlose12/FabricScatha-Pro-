package namelessju.scathapro.fabric.chat.events;

import java.util.HashMap;
import java.util.Map;

/**
 * Repräsentiert ein Chat-Event mit Typ und Daten
 */
public class ChatEvent {
    
    private final ChatEventType type;
    private final String originalMessage;
    private final String cleanMessage;
    private final long timestamp;
    private final Map<String, Object> data;
    
    public ChatEvent(ChatEventType type, String originalMessage, String cleanMessage) {
        this.type = type;
        this.originalMessage = originalMessage;
        this.cleanMessage = cleanMessage;
        this.timestamp = System.currentTimeMillis();
        this.data = new HashMap<>();
    }
    
    public ChatEventType getType() {
        return type;
    }
    
    public String getOriginalMessage() {
        return originalMessage;
    }
    
    public String getCleanMessage() {
        return cleanMessage;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    // Convenience methods für häufig genutzte Datentypen
    
    public ChatEvent withData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
    
    public String getString(String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }
    
    public Integer getInt(String key) {
        Object value = data.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    public Double getDouble(String key) {
        Object value = data.get(key);
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    public Boolean getBoolean(String key) {
        Object value = data.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }
    
    // Pet Drop spezifische Convenience Methods
    public String getPetType() {
        return getString("petType");
    }
    
    public String getPetRarity() {
        return getString("rarity");
    }
    
    public String getPlayerName() {
        return getString("player");
    }
    
    // Spawn Event spezifische Methods
    public String getEntityType() {
        return getString("entityType");
    }
    
    public Integer getX() {
        return getInt("x");
    }
    
    public Integer getY() {
        return getInt("y");
    }
    
    public Integer getZ() {
        return getInt("z");
    }
    
    // Stats spezifische Methods
    public Double getMagicFind() {
        return getDouble("magicFind");
    }
    
    public Double getPetLuck() {
        return getDouble("petLuck");
    }
    
    public Integer getHeat() {
        return getInt("heat");
    }
    
    public Integer getDay() {
        return getInt("day");
    }
    
    @Override
    public String toString() {
        return String.format("ChatEvent{type=%s, message='%s', data=%s, timestamp=%d}", 
                           type, cleanMessage, data, timestamp);
    }
}