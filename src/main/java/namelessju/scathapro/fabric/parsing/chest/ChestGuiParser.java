package namelessju.scathapro.fabric.parsing.chest;

import net.minecraft.client.gui.screen.Screen;
import namelessju.scathapro.fabric.FabricScathaPro;

/**
 * Interface für Chest-GUI Parser.
 */
public interface ChestGuiParser {
    String getId();
    boolean canParse(Screen screen);
    void parse(Screen screen, FabricScathaPro sp);
}