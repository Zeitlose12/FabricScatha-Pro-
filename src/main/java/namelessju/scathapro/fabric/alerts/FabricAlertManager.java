package namelessju.scathapro.fabric.alerts;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.sound.FabricSoundManager;
import namelessju.scathapro.fabric.chat.events.ChatEvent;
import namelessju.scathapro.fabric.chat.events.ChatEventType;

/**
 * Manager für verschiedene Alert-Typen
 * Koordiniert Sound-, Visual- und andere Alert-Systeme
 */
public class FabricAlertManager {
    
    private final FabricScathaPro scathaPro;
    private final FabricSoundManager soundManager;
    private FabricAlertTitleOverlay titleOverlay;
    private AlertModeManager modeManager;
    
    // Alert-Einstellungen
    private boolean alertsEnabled = true;
    private boolean visualAlertsEnabled = true;
    private boolean soundAlertsEnabled = true;
    
    // Alert-Typ-Einstellungen
    private boolean petDropAlertsEnabled = true;
    private boolean wormSpawnAlertsEnabled = true;
    private boolean scathaSpawnAlertsEnabled = true;
    private boolean goblinSpawnAlertsEnabled = true;
    private boolean jerrySpawnAlertsEnabled = true;
    private boolean achievementAlertsEnabled = true;
    private boolean highHeatAlertsEnabled = false;
    
    public FabricAlertManager(FabricScathaPro scathaPro, FabricSoundManager soundManager) {
        this.scathaPro = scathaPro;
        this.soundManager = soundManager;
        
        loadAlertSettings();
        scathaPro.log("Alert-Manager initialisiert");
    }
    
    /**
     * Setzt das Title-Overlay (wird später initialisiert)
     */
    public void setTitleOverlay(FabricAlertTitleOverlay titleOverlay) {
        this.titleOverlay = titleOverlay;
    }

    /**
     * Setzt den AlertModeManager
     */
    public void setModeManager(AlertModeManager modeManager) {
        this.modeManager = modeManager;
    }
    
    /**
     * Lädt Alert-Einstellungen aus der Config
     */
    private void loadAlertSettings() {
        try {
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            if (cfg != null) {
                this.alertsEnabled = cfg.alertsEnabled;
                this.visualAlertsEnabled = cfg.alertsDisplayEnabled;
                this.soundAlertsEnabled = cfg.alertsEnabled;
                this.petDropAlertsEnabled = cfg.scathaPetDropAlert;
                this.wormSpawnAlertsEnabled = cfg.regularWormSpawnAlert;
                this.scathaSpawnAlertsEnabled = cfg.scathaSpawnAlert;
                this.goblinSpawnAlertsEnabled = cfg.goblinSpawnAlert;
                this.jerrySpawnAlertsEnabled = cfg.jerrySpawnAlert;
                this.achievementAlertsEnabled = cfg.playAchievementAlerts;
                this.highHeatAlertsEnabled = cfg.highHeatAlert;
            }
        } catch (Exception e) {
            scathaPro.logError("Fehler beim Laden der Alert-Settings: " + e.getMessage());
        }
        
        scathaPro.logDebug("Alert-Einstellungen geladen");
    }
    
    /**
     * Triggert einen Alert basierend auf einem Chat-Event
     */
    public void triggerAlert(ChatEvent event) {
        if (!alertsEnabled) {
            scathaPro.logDebug("Alerts deaktiviert - Alert wird nicht getriggert");
            return;
        }
        
        switch (event.getType()) {
            case PET_DROP_RARE:
            case PET_DROP_EPIC:
            case PET_DROP_LEGENDARY:
                triggerPetDropAlert(event);
                break;
                
            case WORM_SPAWN:
                triggerWormSpawnAlert(event);
                break;
                
            case SCATHA_SPAWN:
                triggerScathaSpawnAlert(event);
                break;
                
            case GOBLIN_SPAWN:
                triggerGoblinSpawnAlert(event);
                break;
                
            case JERRY_SPAWN:
                triggerJerrySpawnAlert(event);
                break;
                
            case ACHIEVEMENT_UNLOCK:
            case MILESTONE_REACHED:
                triggerAchievementAlert(event);
                break;
                
            case HEAT_UPDATE:
                triggerHeatAlert(event);
                break;
                
            default:
                scathaPro.logDebug("Kein Alert-Handler für Event-Typ: " + event.getType());
                break;
        }
    }
    
    /**
     * Pet Drop Alert
     */
    private void triggerPetDropAlert(ChatEvent event) {
        if (!petDropAlertsEnabled) return;
        
        String petType = event.getPetType();
        String rarity = event.getPetRarity();
        String player = event.getPlayerName();
        
        scathaPro.log("🎉 PET DROP ALERT: " + petType + " (" + rarity + ")" + 
                     (player != null ? " für " + player : ""));
        
        // Sound Alert
        if (soundAlertsEnabled) {
            String defaultKey = switch (rarity == null ? "" : rarity.toLowerCase()) {
                case "epic", "purple" -> "pet_drop_epic";
                case "legendary", "orange", "gold" -> "pet_drop_legendary";
                default -> "pet_drop_rare";
            };
            playModeSound(defaultKey, 1.0f);
        }
        
        // Visual Alert
        if (visualAlertsEnabled && titleOverlay != null) {
            String title = getColoredRarity(rarity) + petType + "!";
            String subtitle = "Pet Drop " + (player != null ? "für " + player : "");
            titleOverlay.showAlert(title, subtitle, AlertType.PET_DROP, rarity);
        }
        
        // Chat-Nachricht (optional)
        // triggerChatAlert("🎉 " + rarity + " " + petType + " Pet Drop!");
    }
    
    /**
     * Worm Spawn Alert
     */
    private void triggerWormSpawnAlert(ChatEvent event) {
        if (!wormSpawnAlertsEnabled) return;
        
        scathaPro.log("🐛 WORM SPAWN ALERT");
        
        // Sound Alert
        if (soundAlertsEnabled) {
            playModeSound("worm_spawn", 1.0f);
        }
        
        // Visual Alert
        if (visualAlertsEnabled && titleOverlay != null) {
            String title = "§6Worm Spawn!";
            if (modeManager != null && modeManager.getCurrent() != null) {
                title += " §7(" + modeManager.getCurrent().displayName() + ")";
            }
            titleOverlay.showAlert(title, "Ein Worm ist gespawnt!", AlertType.WORM_SPAWN);
        }
    }
    
    /**
     * Scatha Spawn Alert
     */
    private void triggerScathaSpawnAlert(ChatEvent event) {
        if (!scathaSpawnAlertsEnabled) return;
        
        scathaPro.log("🕷️ SCATHA SPAWN ALERT");
        
        // Sound Alert  
        if (soundAlertsEnabled) {
            playModeSound("scatha_spawn", 1.0f);
        }
        
        // Visual Alert
        if (visualAlertsEnabled && titleOverlay != null) {
            String title = "§4§lSCATHA SPAWN!";
            if (modeManager != null && modeManager.getCurrent() != null) {
                title += " §7(" + modeManager.getCurrent().displayName() + ")";
            }
            titleOverlay.showAlert(title, "Ein Scatha ist gespawnt!", AlertType.SCATHA_SPAWN);
        }
    }
    
    /**
     * Goblin Spawn Alert
     */
    private void triggerGoblinSpawnAlert(ChatEvent event) {
        if (!goblinSpawnAlertsEnabled) return;
        
        scathaPro.log("👹 GOBLIN SPAWN ALERT");
        
        if (soundAlertsEnabled) {
            playModeSound("goblin_spawn", 1.0f);
        }
        
        if (visualAlertsEnabled && titleOverlay != null) {
            String title = "§2Goblin Spawn!";
            if (modeManager != null && modeManager.getCurrent() != null) {
                title += " §7(" + modeManager.getCurrent().displayName() + ")";
            }
            titleOverlay.showAlert(title, "Ein Goblin ist gespawnt!", AlertType.GOBLIN_SPAWN);
        }
    }
    
    /**
     * Jerry Spawn Alert
     */
    private void triggerJerrySpawnAlert(ChatEvent event) {
        if (!jerrySpawnAlertsEnabled) return;
        
        scathaPro.log("🎄 JERRY SPAWN ALERT");
        
        if (soundAlertsEnabled) {
            playModeSound("jerry_spawn", 1.0f);
        }
        
        if (visualAlertsEnabled && titleOverlay != null) {
            String title = "§bJerry Spawn!";
            if (modeManager != null && modeManager.getCurrent() != null) {
                title += " §7(" + modeManager.getCurrent().displayName() + ")";
            }
            titleOverlay.showAlert(title, "Jerry ist gespawnt!", AlertType.JERRY_SPAWN);
        }
    }
    
    /**
     * Achievement/Milestone Alert
     */
    private void triggerAchievementAlert(ChatEvent event) {
        if (!achievementAlertsEnabled) return;
        
        scathaPro.log("🏆 ACHIEVEMENT ALERT: " + event.getCleanMessage());
        
        if (soundAlertsEnabled) {
            String defaultKey = "achievement_unlock";
            playModeSound(defaultKey, 1.0f);
        }
        
        if (visualAlertsEnabled && titleOverlay != null) {
            titleOverlay.showAlert("§6§lAchievement!", event.getCleanMessage(), AlertType.ACHIEVEMENT);
        }
    }
    
    /**
     * High Heat Alert
     */
    private void triggerHeatAlert(ChatEvent event) {
        if (!highHeatAlertsEnabled) return;
        
        Integer heat = event.getHeat();
        int trigger = 98;
        try {
            var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            if (cfg != null) trigger = Math.max(1, Math.min(100, cfg.highHeatAlertTriggerValue));
        } catch (Exception ignored) {}
        if (heat != null && heat >= trigger) {
            scathaPro.log("🔥 HIGH HEAT ALERT: " + heat);
            
            if (soundAlertsEnabled) {
                playModeSound("high_heat", 1.0f);
            }
            
            if (visualAlertsEnabled && titleOverlay != null) {
                titleOverlay.showAlert("§c§lHIGH HEAT!", "Heat: " + heat, AlertType.HIGH_HEAT);
            }
        }
    }
    
    private void playModeSound(String defaultKey, float baseVolume) {
        String key = defaultKey;
        float vol = baseVolume;
        if (modeManager != null && modeManager.getCurrent() instanceof namelessju.scathapro.fabric.alerts.CustomAlertMode cm) {
            key = cm.mapSound(defaultKey);
            vol = cm.getVolume(defaultKey, baseVolume);
        }
        try {
            soundManager.playAlertSound(key, vol, 1.0f);
        } catch (Exception ignored) {}
    }
    
    /**
     * Gibt die farbige Rarity zurück
     */
    private String getColoredRarity(String rarity) {
        if (rarity == null) return "§f";
        
        return switch (rarity.toLowerCase()) {
            case "rare", "blue" -> "§9";
            case "epic", "purple" -> "§5";
            case "legendary", "orange", "gold" -> "§6";
            case "mythic", "pink" -> "§d";
            default -> "§f";
        };
    }
    
    /**
     * Manueller Alert-Trigger für Testing
     */
    public void triggerTestAlert(AlertType alertType) {
        switch (alertType) {
            case PET_DROP:
                ChatEvent testPetEvent = new ChatEvent(ChatEventType.PET_DROP_LEGENDARY, 
                                                      "TEST: PET DROP! Scatha (Legendary)", 
                                                      "TEST: PET DROP! Scatha (Legendary)");
                testPetEvent.withData("petType", "Scatha")
                          .withData("rarity", "Legendary")
                          .withData("player", "TestPlayer");
                triggerPetDropAlert(testPetEvent);
                break;
                
            case SCATHA_SPAWN:
                triggerScathaSpawnAlert(null);
                break;
                
            case WORM_SPAWN:
                triggerWormSpawnAlert(null);
                break;
                
            default:
                scathaPro.log("Test-Alert für " + alertType + " ausgelöst");
                if (soundAlertsEnabled) {
                    soundManager.playAlertSound("notification");
                }
                break;
        }
    }
    
    // Getter und Setter
    
    public boolean isAlertsEnabled() {
        return alertsEnabled;
    }
    
    public void setAlertsEnabled(boolean enabled) {
        this.alertsEnabled = enabled;
        scathaPro.logDebug("Alerts " + (enabled ? "aktiviert" : "deaktiviert"));
    }
    
    public boolean isVisualAlertsEnabled() {
        return visualAlertsEnabled;
    }
    
    public void setVisualAlertsEnabled(boolean enabled) {
        this.visualAlertsEnabled = enabled;
    }
    
    public boolean isSoundAlertsEnabled() {
        return soundAlertsEnabled;
    }
    
    public void setSoundAlertsEnabled(boolean enabled) {
        this.soundAlertsEnabled = enabled;
    }
    
    // Alert-Typ-spezifische Einstellungen
    
    public void setPetDropAlertsEnabled(boolean enabled) {
        this.petDropAlertsEnabled = enabled;
    }
    
    public void setWormSpawnAlertsEnabled(boolean enabled) {
        this.wormSpawnAlertsEnabled = enabled;
    }
    
    public void setScathaSpawnAlertsEnabled(boolean enabled) {
        this.scathaSpawnAlertsEnabled = enabled;
    }
    
    public void setGoblinSpawnAlertsEnabled(boolean enabled) {
        this.goblinSpawnAlertsEnabled = enabled;
    }
    
    public void setJerrySpawnAlertsEnabled(boolean enabled) {
        this.jerrySpawnAlertsEnabled = enabled;
    }
    
    public void setAchievementAlertsEnabled(boolean enabled) {
        this.achievementAlertsEnabled = enabled;
    }
    
    public void setHighHeatAlertsEnabled(boolean enabled) {
        this.highHeatAlertsEnabled = enabled;
    }
}
