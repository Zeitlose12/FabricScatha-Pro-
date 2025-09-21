package namelessju.scathapro.fabric.achievements;

import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.events.FabricEvent;
import namelessju.scathapro.fabric.events.FabricWormEvent;
import namelessju.scathapro.fabric.events.FabricScathaProEvents;

import java.time.LocalDate;

/**
 * Automatische Achievement-Logik: hört auf Events und schaltet Achievements frei.
 */
public class FabricAchievementLogicManager {
    private final FabricScathaPro sp;

    public FabricAchievementLogicManager(FabricScathaPro sp) {
        this.sp = sp;
        registerListeners();
    }

    private void registerListeners() {
        // Worm Kill -> Kills zählen und Meilensteine prüfen, plus Lobby/Day Zähler
        FabricEvent.register(FabricWormEvent.WormKillEvent.class, this::onWormKill);
        // Pet-Drop -> Rarity-Zähler und Achievements prüfen
        FabricEvent.register(FabricScathaProEvents.ScathaPetDropEvent.class, this::onScathaPetDrop);
    }

    private void onWormKill(FabricWormEvent.WormKillEvent e) {
        if (e == null || e.worm == null || sp == null) return;
        ensureRealDayCounters();
        boolean scatha = e.worm.isScatha;
        if (scatha) {
            sp.variables.scathaKills = Math.max(0, sp.variables.scathaKills) + 1;
            sp.variables.sessionScathaKills = Math.max(0, sp.variables.sessionScathaKills) + 1;
            sp.variables.lastScathaKillTime = System.currentTimeMillis();
            // Lobby/Day counters
            sp.variables.lobbyScathaKills = Math.max(0, sp.variables.lobbyScathaKills) + 1;
            sp.variables.dayScathaKills = Math.max(0, sp.variables.dayScathaKills) + 1;
            checkScathaKillMilestones(sp.variables.scathaKills);
        } else {
            sp.variables.regularWormKills = Math.max(0, sp.variables.regularWormKills) + 1;
            // Lobby/Day counters
            sp.variables.lobbyWormKills = Math.max(0, sp.variables.lobbyWormKills) + 1;
            sp.variables.dayWormKills = Math.max(0, sp.variables.dayWormKills) + 1;
            checkWormKillMilestones(sp.variables.regularWormKills);
        }

        // Lobby-Kill Achievements (Worms in einer Lobby)
        unlockIfReached(FabricAchievement.lobby_kills_1, sp.variables.lobbyWormKills, 25);
        unlockIfReached(FabricAchievement.lobby_kills_2, sp.variables.lobbyWormKills, 50);
        unlockIfReached(FabricAchievement.lobby_kills_3, sp.variables.lobbyWormKills, 100);

        // Day-Kill Achievements (Worms an einem echten Tag)
        unlockIfReached(FabricAchievement.day_kills_1, sp.variables.dayWormKills, 50);
        unlockIfReached(FabricAchievement.day_kills_2, sp.variables.dayWormKills, 100);
        unlockIfReached(FabricAchievement.day_kills_3, sp.variables.dayWormKills, 250);

        // Scatha Farming Streak (real day, auf Scatha-Kill prüfen)
        if (scatha) updateScathaFarmingStreak();

        // Persistiere aktualisierte Stats
        if (sp.getPersistentData() != null) sp.getPersistentData().save(sp);
    }

    private void onScathaPetDrop(FabricScathaProEvents.ScathaPetDropEvent e) {
        // Die Variablen wurden bereits im EventManager erhöht -> nur noch Achievements prüfen
        int rare = Math.max(0, sp.variables.rarePetDrops);
        int epic = Math.max(0, sp.variables.epicPetDrops);
        int legendary = Math.max(0, sp.variables.legendaryPetDrops);
        int total = rare + epic + legendary;

        // Per-Rarity Schwellen
        unlockIfReached(FabricAchievement.scatha_pet_drop_1_rare, rare, 1);
        unlockIfReached(FabricAchievement.scatha_pet_drop_2_rare, rare, 3);
        unlockIfReached(FabricAchievement.scatha_pet_drop_3_rare, rare, 10);

        unlockIfReached(FabricAchievement.scatha_pet_drop_1_epic, epic, 1);
        unlockIfReached(FabricAchievement.scatha_pet_drop_2_epic, epic, 3);
        unlockIfReached(FabricAchievement.scatha_pet_drop_3_epic, epic, 10);

        unlockIfReached(FabricAchievement.scatha_pet_drop_1_legendary, legendary, 1);
        unlockIfReached(FabricAchievement.scatha_pet_drop_2_legendary, legendary, 3);
        unlockIfReached(FabricAchievement.scatha_pet_drop_3_legendary, legendary, 10);

        // Alle Rarities mindestens 1x
        if (rare >= 1 && epic >= 1 && legendary >= 1) {
            unlock(FabricAchievement.scatha_pet_drop_each);
        }

        // Beliebige Drops: 10/25/50/100 + repeatable alle 12
        unlockIfReached(FabricAchievement.scatha_pet_drop_any_1, total, 10);
        unlockIfReached(FabricAchievement.scatha_pet_drop_any_2, total, 25);
        unlockIfReached(FabricAchievement.scatha_pet_drop_any_3, total, 50);
        unlockIfReached(FabricAchievement.scatha_pet_drop_any_4, total, 100);
        // repeatable 12er Blöcke
        FabricAchievement.scatha_pet_drop_any_repeatable.setRepeatingProgress(0, total, true);

        // Persistieren
        if (sp.getPersistentData() != null) sp.getPersistentData().save(sp);
    }

    private void checkWormKillMilestones(int count) {
        unlockIfReached(FabricAchievement.worm_kills_1, count, 1);
        unlockIfReached(FabricAchievement.worm_kills_2, count, 10);
        unlockIfReached(FabricAchievement.worm_kills_3, count, 120);
        unlockIfReached(FabricAchievement.worm_bestiary_max, count, 400);
        unlockIfReached(FabricAchievement.worm_kills_4, count, 1000);
        unlockIfReached(FabricAchievement.worm_kills_5, count, 10000);
        unlockIfReached(FabricAchievement.worm_kills_6, count, 25000);
        unlockIfReached(FabricAchievement.worm_kills_7, count, 100000);
    }

    private void checkScathaKillMilestones(int count) {
        unlockIfReached(FabricAchievement.scatha_kills_1, count, 1);
        unlockIfReached(FabricAchievement.scatha_kills_2, count, 10);
        unlockIfReached(FabricAchievement.scatha_kills_3, count, 100);
        unlockIfReached(FabricAchievement.scatha_kills_4, count, 1000);
        unlockIfReached(FabricAchievement.scatha_kills_5, count, 10000);
        // Repeatable jede 1000
        FabricAchievement.scatha_kills_repeatable.setRepeatingProgress(0, count, true);
    }

    private void unlockIfReached(FabricAchievement a, int count, int threshold) {
        if (count >= threshold) unlock(a);
    }

    private void unlock(FabricAchievement a) {
        try {
            sp.getAchievementManager().unlockAchievement(a);
        } catch (Exception ignored) {}
    }

    private void ensureRealDayCounters() {
        LocalDate today = LocalDate.now();
        if (sp.variables.lastPlayedDate == null || !sp.variables.lastPlayedDate.equals(today)) {
            // Tageswechsel – Zähler zurücksetzen und Event posten
            sp.variables.dayWormKills = 0;
            sp.variables.dayScathaKills = 0;
            sp.variables.lastPlayedDate = today;
            FabricEvent.post(new FabricScathaProEvents.DailyStatsResetEvent(today));
        }
    }

    private void updateScathaFarmingStreak() {
        LocalDate today = LocalDate.now();
        LocalDate last = sp.variables.lastScathaFarmedDate;
        if (last == null) {
            sp.variables.scathaFarmingStreak = 1;
        } else if (last.plusDays(1).equals(today)) {
            sp.variables.scathaFarmingStreak = Math.max(1, sp.variables.scathaFarmingStreak + 1);
        } else if (!last.equals(today)) {
            sp.variables.scathaFarmingStreak = 1;
        }
        sp.variables.lastScathaFarmedDate = today;

        // Unlock Streak Achievements
        unlockIfReached(FabricAchievement.scatha_farming_streak_1, sp.variables.scathaFarmingStreak, 3);
        unlockIfReached(FabricAchievement.scatha_farming_streak_2, sp.variables.scathaFarmingStreak, 5);
        unlockIfReached(FabricAchievement.scatha_farming_streak_3, sp.variables.scathaFarmingStreak, 7);
        unlockIfReached(FabricAchievement.scatha_farming_streak_4, sp.variables.scathaFarmingStreak, 14);
        unlockIfReached(FabricAchievement.scatha_farming_streak_5, sp.variables.scathaFarmingStreak, 30);

        FabricEvent.post(new FabricScathaProEvents.DailyScathaFarmingStreakChangedEvent(
            0, sp.variables.scathaFarmingStreak, today));

        // Business days (Mon-Fri) – unlock am Freitag wenn 5-Tage-Streak
        switch (today.getDayOfWeek()) {
            case FRIDAY -> {
                if (sp.variables.scathaFarmingStreak >= 5) {
                    unlock(FabricAchievement.scatha_farming_streak_business_days);
                }
            }
            case SUNDAY -> {
                // Weekend (Sa+So) – unlock am Sonntag wenn 2-Tage-Streak
                if (sp.variables.scathaFarmingStreak >= 2) {
                    unlock(FabricAchievement.scatha_farming_streak_weekend);
                }
            }
            default -> {}
        }
    }
}