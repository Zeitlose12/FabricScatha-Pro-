package namelessju.scathapro.fabric.chat.parsers;

import namelessju.scathapro.fabric.chat.ChatMessageType;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MagicFindParser implements ChatMessageParser {
    
    private static final Pattern MAGIC_FIND_PATTERN = Pattern.compile(
        ".*magic find.*?(\\d+(?:\\.\\d+)?).*", Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public ChatEvent parseMessage(String cleanMessage, String rawMessage, ChatMessageType messageType) {
        Matcher matcher = MAGIC_FIND_PATTERN.matcher(cleanMessage);
        if (matcher.find()) {
            String magicFindStr = matcher.group(1);
            try {
                double magicFind = Double.parseDouble(magicFindStr);
                return new ChatEvent(ChatEventType.MAGIC_FIND_UPDATE, rawMessage, cleanMessage)
                    .withData("magicFind", magicFind);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    @Override
    public int getPriority() { return 50; }
}
