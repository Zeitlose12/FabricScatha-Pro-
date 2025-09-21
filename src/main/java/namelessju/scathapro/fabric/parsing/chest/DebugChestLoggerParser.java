package namelessju.scathapro.fabric.parsing.chest;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import namelessju.scathapro.fabric.FabricScathaPro;

/**
 * Debug-Parser: loggt Chest-Titel und einige Slot-Namen, wenn Debug-Logs aktiv sind.
 * Greift nur, wenn keine anderen Parser vorher gegriffen haben oder zur Diagnose.
 */
public class DebugChestLoggerParser implements ChestGuiParser {
    @Override public String getId() { return "debug_logger"; }

    @Override
    public boolean canParse(Screen screen) {
        // Greife breit, aber nur wenn Debug-Logs aktiv sind
        try {
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            return cfg != null && cfg.debugLogs && screen instanceof HandledScreen<?>;
        } catch (Exception e) { return false; }
    }

    @Override
    public void parse(Screen screen, FabricScathaPro sp) {
        if (!(screen instanceof HandledScreen<?> hs)) return;
        try {
            Text title = screen.getTitle();
            sp.logDebug("[ChestDebug] Title: " + (title != null ? title.getString() : "<null>"));
            ScreenHandler handler = hs.getScreenHandler();
            if (handler != null && handler.slots != null) {
                int logged = 0;
                for (Slot s : handler.slots) {
                    if (s == null) continue;
                    ItemStack st = s.getStack();
                    if (st == null || st.isEmpty()) continue;
                    sp.logDebug("[ChestDebug] Slot " + s.getIndex() + ": " + safe(st.getName()));
                    if (++logged >= 5) break; // begrenzen
                }
            }
        } catch (Exception ignored) {}
    }

    private static String safe(Text t) { try { return t != null ? t.getString() : ""; } catch (Exception e) { return ""; } }
}
