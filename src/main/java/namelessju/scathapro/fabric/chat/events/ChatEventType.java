package namelessju.scathapro.fabric.chat.events;

/**
 * Enum für verschiedene Chat-Event-Typen
 */
public enum ChatEventType {
    // Pet-Drop Events
    PET_DROP_RARE,
    PET_DROP_EPIC, 
    PET_DROP_LEGENDARY,
    
    // Spawn Events  
    WORM_SPAWN,
    SCATHA_SPAWN,
    GOBLIN_SPAWN,
    JERRY_SPAWN,
    
    // Stats Events
    MAGIC_FIND_UPDATE,
    PET_LUCK_UPDATE,
    HEAT_UPDATE,
    DAY_CHANGE,
    DAY_UPDATE,
    
    // Lobby Events
    LOBBY_JOIN,
    AREA_CHANGE,
    
    // Achievement Events
    ACHIEVEMENT_UNLOCK,
    MILESTONE_REACHED,
    
    // Other Events
    WORM_DESPAWN,
    SCATHA_KILL,
    REGULAR_WORM_KILL,
    
    // Commission Events
    COMMISSION_COMPLETE,
    COMMISSION_PROGRESS,
    
    // Skill Events
    SKILL_EXP_GAIN,
    SKILL_LEVEL_UP,
    
    // Generic Events
    STATS_UPDATE,
    SERVER_MESSAGE,
    
    // Unknown
    UNKNOWN
}