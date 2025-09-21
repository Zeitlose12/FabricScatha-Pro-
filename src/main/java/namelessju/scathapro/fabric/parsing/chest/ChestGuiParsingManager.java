package namelessju.scathapro.fabric.parsing.chest;

import net.minecraft.client.gui.screen.Screen;
import namelessju.scathapro.fabric.FabricScathaPro;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager zum Parsen von Chest-basierten GUIs (Container).
 */
public class ChestGuiParsingManager {
    private final FabricScathaPro sp;
    private final List<ChestGuiParser> parsers = new ArrayList<>();

    public ChestGuiParsingManager(FabricScathaPro sp) {
        this.sp = sp;
        registerDefaults();
    }

    private void registerDefaults() {
        register(new WormBestiaryParser());
        register(new ProfileStatsParser());
        register(new DebugChestLoggerParser());
    }

    public void register(ChestGuiParser parser) {
        if (parser != null && !parsers.contains(parser)) parsers.add(parser);
    }

    public int getParserCount() { return parsers.size(); }

    public void update(Screen current) {
        if (current == null) return;
        // Versuche alle Parser; erster erfolgreicher stoppt
        for (ChestGuiParser p : parsers) {
            try {
                if (p.canParse(current)) {
                    sp.logDebug("ChestGuiParsing: " + p.getId());
                    p.parse(current, sp);
                    break;
                }
            } catch (Exception ignored) {}
        }
    }
}