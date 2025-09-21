package namelessju.scathapro.fabric.save;

import net.fabricmc.loader.api.FabricLoader;
import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.persist.FabricPersistentData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Save/Backup Manager für Scatha-Pro
 */
public class FabricSaveManager {
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final int MAX_BACKUPS = 10;

    public static Path getBackupDir() {
        return FabricPersistentData.getDataDir().resolve("backups");
    }

    public void backupCurrent(FabricScathaPro sp) {
        try {
            Path src = FabricPersistentData.getDataFile();
            if (!Files.exists(src)) return; // noch nichts zu sichern
            Path dir = getBackupDir();
            Files.createDirectories(dir);
            String name = "persistent-" + LocalDateTime.now().format(TS) + ".json";
            Files.copy(src, dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            pruneOldBackups(sp);
            sp.logDebug("Backup erstellt: " + name);
        } catch (IOException e) {
            sp.logError("Backup fehlgeschlagen: " + e.getMessage());
        }
    }

    public void pruneOldBackups(FabricScathaPro sp) {
        try {
            Path dir = getBackupDir();
            if (!Files.exists(dir)) return;
            List<Path> files = Files.list(dir)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparingLong(this::mtime).reversed())
                    .collect(Collectors.toList());
            for (int i = MAX_BACKUPS; i < files.size(); i++) {
                try { Files.deleteIfExists(files.get(i)); } catch (IOException ignored) {}
            }
        } catch (IOException e) {
            sp.logError("Prune Backups fehlgeschlagen: " + e.getMessage());
        }
    }

    private long mtime(Path p) {
        try { return Files.getLastModifiedTime(p).toMillis(); } catch (IOException e) { return 0; }
    }
}