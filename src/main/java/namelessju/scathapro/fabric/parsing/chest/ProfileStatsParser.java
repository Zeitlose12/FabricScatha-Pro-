package namelessju.scathapro.fabric.parsing.chest;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import namelessju.scathapro.fabric.FabricScathaPro;

/**
 * Parser für Profil-Statistiken in Chest-GUIs.
 */
public class ProfileStatsParser implements ChestGuiParser {
    @Override public String getId() { return "profile_stats"; }

    @Override
    public boolean canParse(Screen screen) {
        try {
            Text title = screen.getTitle();
            if (title == null) return false;
            String s = title.getString().toLowerCase();
            return s.contains("profile") || s.contains("stats");
        } catch (Exception e) { return false; }
    }

    @Override
    public void parse(Screen screen, FabricScathaPro sp) {
        // TODO: Relevante Werte aus Items/Tooltips extrahieren
        sp.logDebug("ProfileStatsParser: parse() aufgerufen (Stub)");
    }
}