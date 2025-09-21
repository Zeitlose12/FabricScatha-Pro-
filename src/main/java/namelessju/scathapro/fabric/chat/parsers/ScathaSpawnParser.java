package namelessju.scathapro.fabric.chat.parsers;

import namelessju.scathapro.fabric.chat.ChatMessageType;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;

import java.util.regex.Pattern;

public class ScathaSpawnParser implements ChatMessageParser {
    
    private static final Pattern SCATHA_SPAWN_PATTERN = Pattern.compile(
        ".*scatha.*spawn.*", Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public ChatEvent parseMessage(String cleanMessage, String rawMessage, ChatMessageType messageType) {
        if (SCATHA_SPAWN_PATTERN.matcher(cleanMessage).find()) {
            return new ChatEvent(ChatEventType.SCATHA_SPAWN, rawMessage, cleanMessage)
                .withData("entityType", "scatha");
        }
        return null;
    }
    
    @Override
    public int getPriority() { return 5; }
}