package namelessju.scathapro.fabric.parsing.chest;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import namelessju.scathapro.fabric.FabricScathaPro;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser für Worm Bestiary GUI – erkennt Titel und extrahiert Bestiary/Kill-Stats heuristisch.
 */
public class WormBestiaryParser implements ChestGuiParser {
    @Override public String getId() { return "worm_bestiary"; }

    private static final Pattern BESTIARY_PATTERN = Pattern.compile("(?i)(?:bestiary|worm\s*bestiary)\\s*([\\d,]+)\\s*/\\s*([\\d,]+)");
    private static final Pattern WORM_KILLS_PATTERN = Pattern.compile("(?i)worm(?:s)?\\s*kills?:\\s*([\\d,]+)");
    private static final Pattern SCATHA_KILLS_PATTERN = Pattern.compile("(?i)scatha(?:s)?\\s*kills?:\\s*([\\d,]+)");

    @Override
    public boolean canParse(Screen screen) {
        try {
            Text title = screen.getTitle();
            return title != null && title.getString().toLowerCase().contains("bestiary");
        } catch (Exception e) { return false; }
    }

    @Override
    public void parse(Screen screen, FabricScathaPro sp) {
        try {
            if (!(screen instanceof HandledScreen<?> hs)) return;
            ScreenHandler handler = hs.getScreenHandler();
            if (handler == null || handler.slots == null) return;

            int foundWormKills = -1;
            int foundScathaKills = -1;
            int bestiaryCurrent = -1;
            int bestiaryMax = -1;

            List<Slot> slots = handler.slots;
            for (Slot slot : slots) {
                if (slot == null) continue;
                ItemStack stack = slot.getStack();
                if (stack == null || stack.isEmpty()) continue;

                String name = safe(stack.getName());
                // Prüfe Name
                if (name != null && !name.isEmpty()) {
                    Matcher m = BESTIARY_PATTERN.matcher(name);
                    while (m.find()) {
                        int cur = parseInt(m.group(1));
                        int max = parseInt(m.group(2));
                        if (cur > bestiaryCurrent) { bestiaryCurrent = cur; bestiaryMax = max; }
                    }
                    foundWormKills = Math.max(foundWormKills, matchSingle(WORM_KILLS_PATTERN, name));
                    foundScathaKills = Math.max(foundScathaKills, matchSingle(SCATHA_KILLS_PATTERN, name));
                }

                // Versuche grob, zusätzliche Strings zu bekommen (z. B. via item count/lore-fallback)
                // Wir vermeiden Tooltips wegen API-Unterschieden; weitere Heuristiken können später ergänzt werden.
            }

            boolean changed = false;
            if (foundWormKills >= 0 && foundWormKills != sp.variables.regularWormKills) {
                sp.variables.regularWormKills = foundWormKills;
                changed = true;
                sp.logDebug("Bestiary: Worm Kills = " + foundWormKills);
            }
            if (foundScathaKills >= 0 && foundScathaKills != sp.variables.scathaKills) {
                sp.variables.scathaKills = foundScathaKills;
                changed = true;
                sp.logDebug("Bestiary: Scatha Kills = " + foundScathaKills);
            }
            if (bestiaryCurrent >= 0) {
                // Optional: könnte in eigenen Feldern gespeichert werden; hier nur Log zwecks Nachweis
                sp.logDebug("Bestiary Progress: " + bestiaryCurrent + (bestiaryMax > 0 ? "/" + bestiaryMax : ""));
            }

            if (changed && sp.getPersistentData() != null) {
                sp.getPersistentData().save(sp);
            }
        } catch (Exception e) {
            sp.logError("WormBestiaryParser Fehler: " + e.getMessage());
        }
    }

    private static String safe(Text t) {
        try { return t != null ? t.getString() : ""; } catch (Exception e) { return ""; }
    }


    private static int matchSingle(Pattern p, String s) {
        if (s == null) return -1;
        Matcher m = p.matcher(s);
        if (m.find()) return parseInt(m.group(1));
        return -1;
    }

    private static int parseInt(String n) {
        try { return Integer.parseInt(n.replace(",", "").trim()); } catch (Exception e) { return -1; }
    }

}
