package namelessju.scathapro.fabric.migration;

import net.fabricmc.loader.api.FabricLoader;
import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.persist.FabricPersistentData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Einfache Heuristik zur Migration alter Forge-Dateien in die Fabric-Struktur.
 * Führt nur Kopie/Umbenennung aus, keine komplexe Key-Transformation.
 */
public class ConfigMigration {

    public void tryMigrate(FabricScathaPro sp) {
        try {
            Path gameDir = FabricLoader.getInstance().getGameDir();
            Path configDir = gameDir.resolve("config");

            // Kandidaten: alte Forge-Dateien/Ordner
            Path forgeMain = configDir.resolve("scathapro.json");
            Path forgeDir = configDir.resolve("scathapro");

            // Zielordner
            Path targetDir = FabricPersistentData.getDataDir();
            Files.createDirectories(targetDir);

            boolean migrated = false;

            if (Files.exists(forgeMain)) {
                Path target = targetDir.resolve("forge-scathapro.json.backup");
                Files.copy(forgeMain, target, StandardCopyOption.REPLACE_EXISTING);
                migrated = true;
            }
            if (Files.exists(forgeDir) && Files.isDirectory(forgeDir)) {
                // Kopiere Dateien flach
                try (var s = Files.list(forgeDir)) {
                    for (Path p : (Iterable<Path>) s::iterator) {
                        Path target = targetDir.resolve(p.getFileName().toString() + ".backup");
                        try { Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING); } catch (IOException ignored) {}
                        migrated = true;
                    }
                }
            }

            if (migrated) {
                sp.log("Forge->Fabric Migration: Sicherungen im Datenordner abgelegt (keine Schlüsselkonvertierung) ");
            }
        } catch (Exception e) {
            sp.logError("Migration fehlgeschlagen: " + e.getMessage());
        }
    }
}