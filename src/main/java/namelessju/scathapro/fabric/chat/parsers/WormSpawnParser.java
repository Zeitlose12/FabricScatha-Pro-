package namelessju.scathapro.fabric.chat.parsers;

import namelessju.scathapro.fabric.chat.ChatMessageType;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;

import java.util.regex.Pattern;

public class WormSpawnParser implements ChatMessageParser {
    
    private static final Pattern WORM_SPAWN_PATTERN = Pattern.compile(
        ".*(worm|larva).*spawn.*", Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public ChatEvent parseMessage(String cleanMessage, String rawMessage, ChatMessageType messageType) {
        if (WORM_SPAWN_PATTERN.matcher(cleanMessage).find()) {
            return new ChatEvent(ChatEventType.WORM_SPAWN, rawMessage, cleanMessage)
                .withData("entityType", "worm");
        }
        return null;
    }
    
    @Override
    public int getPriority() { return 10; }
}