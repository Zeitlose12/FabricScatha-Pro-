package namelessju.scathapro.fabric.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import namelessju.scathapro.fabric.FabricScathaPro;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager für Alert-Sounds in der Scatha-Pro Mod
 * Verwaltet das Abspielen von verschiedenen Alert-Sounds
 */
public class FabricSoundManager {
    
    private final FabricScathaPro scathaPro;
    private final MinecraftClient mc;
    
    // Sound-Registry
    private final Map<String, SoundEvent> registeredSounds = new HashMap<>();
    
    // Sound-Settings
    private float masterVolume = 1.0f;
    private boolean soundsEnabled = true;
    private boolean muteCrystalHollowsSounds = false;
    
    public FabricSoundManager(FabricScathaPro scathaPro) {
        this.scathaPro = scathaPro;
        this.mc = MinecraftClient.getInstance();
        
        registerSounds();
        loadSoundSettings();
    }
    
    /**
     * Registriert alle Alert-Sounds
     */
    private void registerSounds() {
        // Pet Drop Sounds
        registerSound("pet_drop_rare", "scathapro:alerts.pet_drop_rare");
        registerSound("pet_drop_epic", "scathapro:alerts.pet_drop_epic");
        registerSound("pet_drop_legendary", "scathapro:alerts.pet_drop_legendary");
        
        // Spawn Sounds
        registerSound("worm_spawn", "scathapro:alerts.worm_spawn");
        registerSound("scatha_spawn", "scathapro:alerts.scatha_spawn");
        registerSound("goblin_spawn", "scathapro:alerts.goblin_spawn");
        registerSound("jerry_spawn", "scathapro:alerts.jerry_spawn");
        
        // Event Sounds
        registerSound("achievement_unlock", "scathapro:alerts.achievement");
        registerSound("milestone_reached", "scathapro:alerts.milestone");
        registerSound("high_heat", "scathapro:alerts.high_heat");
        registerSound("cooldown_ready", "scathapro:alerts.cooldown_ready");
        
        // Generic Sounds
        registerSound("notification", "scathapro:alerts.notification");
        registerSound("warning", "scathapro:alerts.warning");
        registerSound("error", "scathapro:alerts.error");
        
        scathaPro.log("Alert-Sounds registriert: " + registeredSounds.size() + " Sounds");
    }
    
    /**
     * Registriert einen einzelnen Sound
     */
    private void registerSound(String key, String soundId) {
        try {
            Identifier identifier = Identifier.of(soundId);
            SoundEvent soundEvent = SoundEvent.of(identifier);
            registeredSounds.put(key, soundEvent);
            scathaPro.logDebug("Sound registriert: " + key + " -> " + soundId);
        } catch (Exception e) {
            scathaPro.logError("Fehler beim Registrieren des Sounds: " + key + " - " + e.getMessage());
        }
    }
    
    /**
     * Lädt Sound-Einstellungen aus der Config
     */
    private void loadSoundSettings() {
        try {
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            if (cfg != null) {
                this.masterVolume = Math.max(0f, Math.min(1f, cfg.soundVolume));
                // In diesem Setup wird 'alertsEnabled' als globaler Sound-Schalter genutzt
                this.soundsEnabled = cfg.alertsEnabled;
                this.muteCrystalHollowsSounds = cfg.muteCrystalHollowsSounds;
            }
        } catch (Exception e) {
            scathaPro.logError("Fehler beim Laden der Sound-Settings: " + e.getMessage());
        }
        
        scathaPro.logDebug("Sound-Einstellungen geladen: Volume=" + masterVolume + ", Enabled=" + soundsEnabled + ", MuteCH=" + muteCrystalHollowsSounds);
    }
    
    /**
     * Spielt einen Alert-Sound ab
     */
    public void playAlertSound(String soundKey) {
        playAlertSound(soundKey, 1.0f, 1.0f);
    }
    
    /**
     * Spielt einen Alert-Sound mit custom Volume und Pitch ab
     */
    public void playAlertSound(String soundKey, float volume, float pitch) {
        if (!soundsEnabled) {
            scathaPro.logDebug("Sounds deaktiviert - Sound wird nicht abgespielt: " + soundKey);
            return;
        }
        
        if (muteCrystalHollowsSounds && scathaPro.isInCrystalHollows()) {
            scathaPro.logDebug("Crystal Hollows Sounds gemutet - Sound wird nicht abgespielt: " + soundKey);
            return;
        }
        
        SoundEvent soundEvent = registeredSounds.get(soundKey);
        if (soundEvent == null) {
            scathaPro.logError("Unbekannter Sound: " + soundKey);
            return;
        }
        
        try {
            if (mc.player != null) {
                float finalVolume = volume * masterVolume;
                
                // Sound abspielen
                SoundManager soundManager = mc.getSoundManager();
                PositionedSoundInstance soundInstance = PositionedSoundInstance.master(
                    soundEvent, pitch, finalVolume
                );
                
                soundManager.play(soundInstance);
                scathaPro.logDebug("Sound abgespielt: " + soundKey + " (Volume: " + finalVolume + ", Pitch: " + pitch + ")");
            }
        } catch (Exception e) {
            scathaPro.logError("Fehler beim Abspielen des Sounds: " + soundKey + " - " + e.getMessage());
        }
    }
    
    /**
     * Spielt einen Pet-Drop Alert-Sound ab
     */
    public void playPetDropSound(String rarity) {
        String soundKey = switch (rarity.toLowerCase()) {
            case "rare", "blue" -> "pet_drop_rare";
            case "epic", "purple" -> "pet_drop_epic";
            case "legendary", "orange", "gold" -> "pet_drop_legendary";
            default -> "notification";
        };
        
        playAlertSound(soundKey);
    }
    
    /**
     * Spielt einen Spawn Alert-Sound ab
     */
    public void playSpawnSound(String entityType) {
        String soundKey = switch (entityType.toLowerCase()) {
            case "worm", "larva" -> "worm_spawn";
            case "scatha" -> "scatha_spawn";
            case "goblin" -> "goblin_spawn";
            case "jerry" -> "jerry_spawn";
            default -> "notification";
        };
        
        playAlertSound(soundKey);
    }
    
    /**
     * Stoppt alle aktuell abgespielten Sounds
     */
    public void stopAllSounds() {
        try {
            mc.getSoundManager().stopAll();
            scathaPro.logDebug("Alle Sounds gestoppt");
        } catch (Exception e) {
            scathaPro.logError("Fehler beim Stoppen der Sounds: " + e.getMessage());
        }
    }
    
    /**
     * Test-Methode für Sound-Testing
     */
    public void testSound(String soundKey) {
        scathaPro.log("Testing Sound: " + soundKey);
        playAlertSound(soundKey, 0.8f, 1.0f);
    }
    
    // Getter und Setter
    
    public float getMasterVolume() {
        return masterVolume;
    }
    
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        scathaPro.logDebug("Master Volume gesetzt: " + this.masterVolume);
    }
    
    public boolean isSoundsEnabled() {
        return soundsEnabled;
    }
    
    public void setSoundsEnabled(boolean enabled) {
        this.soundsEnabled = enabled;
        scathaPro.logDebug("Sounds " + (enabled ? "aktiviert" : "deaktiviert"));
    }
    
    public boolean isMuteCrystalHollowsSounds() {
        return muteCrystalHollowsSounds;
    }
    
    public void setMuteCrystalHollowsSounds(boolean mute) {
        this.muteCrystalHollowsSounds = mute;
        scathaPro.logDebug("Crystal Hollows Sounds " + (mute ? "gemutet" : "aktiviert"));
    }
    
    /**
     * Gibt alle verfügbaren Sound-Keys zurück
     */
    public String[] getAvailableSounds() {
        return registeredSounds.keySet().toArray(new String[0]);
    }
    
    /**
     * Prüft ob ein Sound existiert
     */
    public boolean soundExists(String soundKey) {
        return registeredSounds.containsKey(soundKey);
    }
}