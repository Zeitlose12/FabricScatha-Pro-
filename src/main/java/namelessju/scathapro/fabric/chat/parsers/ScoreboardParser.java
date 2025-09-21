package namelessju.scathapro.fabric.chat.parsers;

import net.minecraft.client.MinecraftClient;
import namelessju.scathapro.fabric.FabricScathaPro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scoreboard-Parser: erkennt "Day <n>" aus der Sidebar (Crystal Hollows) und postet Events.
 * Defensiv implementiert, um API-Änderungen zu tolerieren.
 */
public class ScoreboardParser {
    private static final Pattern DAY_PATTERN = Pattern.compile(".*\\bDay\\s+(\\d+)\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern HEAT_PATTERN = Pattern.compile(".*\\bHeat\\s*:?\\s*(\\d{1,3})%?\\b.*", Pattern.CASE_INSENSITIVE);

    private Integer lastDay = null;
    private Integer lastHeat = null;

    public void update(FabricScathaPro sp) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc == null || mc.player == null || mc.world == null) return;

            // Sammle mögliche Textquellen aus dem Sidebar-Scoreboard
            List<String> lines = new ArrayList<>();
            tryCollectSidebarTexts(mc, lines);

            Integer foundDay = null;
            Integer foundHeat = null;
            for (String line : lines) {
                if (foundDay == null) {
                    Integer d = tryParseDay(line);
                    if (d != null) { foundDay = d; }
                }
                if (foundHeat == null) {
                    Integer h = tryParseHeat(line);
                    if (h != null) { foundHeat = h; }
                }
                if (foundDay != null && foundHeat != null) break;
            }

            if (foundDay != null && (lastDay == null || !foundDay.equals(lastDay))) {
                lastDay = foundDay;
                sp.variables.lastCrystalHollowsDay = foundDay;
                // Event posten
                namelessju.scathapro.fabric.events.FabricEvent.post(
                    new namelessju.scathapro.fabric.events.FabricScathaProEvents.CrystalHollowsDayStartedEvent(foundDay)
                );
                sp.logDebug("Scoreboard Day erkannt: " + foundDay);
            }

            // Heat aus Scoreboard: lastHeat aktualisieren und ggf. Alert triggern
            if (foundHeat != null && (lastHeat == null || !foundHeat.equals(lastHeat))) {
                lastHeat = foundHeat;
                sp.variables.lastHeat = foundHeat;
                sp.logDebug("Scoreboard Heat: " + foundHeat);
                try {
                    var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
                    boolean enabled = cfg != null && cfg.highHeatAlert;
                    int trigger = cfg != null ? Math.max(1, Math.min(100, cfg.highHeatAlertTriggerValue)) : 98;
                    if (enabled && foundHeat >= trigger && sp.getAlertManager() != null) {
                        var e = new namelessju.scathapro.fabric.chat.events.ChatEvent(
                            namelessju.scathapro.fabric.chat.events.ChatEventType.HEAT_UPDATE,
                            "Scoreboard Heat: " + foundHeat,
                            "Scoreboard Heat: " + foundHeat
                        ).withData("heat", foundHeat);
                        sp.getAlertManager().triggerAlert(e);
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
    }

    private void tryCollectSidebarTexts(MinecraftClient mc, List<String> out) {
        // Strategie A: Offizielle Scoreboard-API
        try {
            var world = mc.world;
            var scoreboard = world.getScoreboard();
            // Slot: Sidebar. Einige Mappings verwenden ScoreboardDisplaySlot.SIDEBAR
            Object objective = null;
            try {
                var displaySlotClass = Class.forName("net.minecraft.scoreboard.ScoreboardDisplaySlot");
                var sidebarField = displaySlotClass.getField("SIDEBAR");
                Object sidebarEnum = sidebarField.get(null);
                var getObj = scoreboard.getClass().getMethod("getObjectiveForSlot", displaySlotClass);
                objective = getObj.invoke(scoreboard, sidebarEnum);
            } catch (Throwable t) {
                // Fallback (ältere Mappings): getObjectiveForSlot(int) mit Slot=1
                try {
                    var getObj = scoreboard.getClass().getMethod("getObjectiveForSlot", int.class);
                    objective = getObj.invoke(scoreboard, 1);
                } catch (Throwable ignored) {}
            }

            if (objective != null) {
                // Titel extrahieren
                try {
                    var getDisplayName = objective.getClass().getMethod("getDisplayName");
                    Object title = getDisplayName.invoke(objective);
                    if (title != null) out.add(toPlain(title));
                } catch (Throwable ignored) {}

                // Zeilen extrahieren (Spieler-Scores repräsentieren Zeilen)
                try {
                    var getAllScores = scoreboard.getClass().getMethod("getAllPlayerScores", objective.getClass());
                    Object col = getAllScores.invoke(scoreboard, objective);
                    if (col instanceof Collection<?> c) {
                        for (Object scoreObj : c) {
                            // Versuche verschiedene Zugriffe auf die Zeilenbezeichnung
                            String line = tryExtractLineText(scoreObj);
                            if (line != null && !line.isEmpty()) out.add(line);
                            if (out.size() > 30) break; // Begrenzen
                        }
                    }
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }

    private String tryExtractLineText(Object scoreObj) {
        // Bekannte Kandidaten: getPlayerName(), getHolder().getName(), getScoreHolder().getName()
        try {
            var m = scoreObj.getClass().getMethod("getPlayerName");
            Object name = m.invoke(scoreObj);
            if (name instanceof String s) return s;
        } catch (Throwable ignored) {}
        try {
            var getHolder = scoreObj.getClass().getMethod("getHolder");
            Object holder = getHolder.invoke(scoreObj);
            if (holder != null) {
                try {
                    var m = holder.getClass().getMethod("getName");
                    Object n = m.invoke(holder);
                    if (n instanceof String s) return s;
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
        try {
            var getScoreHolder = scoreObj.getClass().getMethod("getScoreHolder");
            Object holder = getScoreHolder.invoke(scoreObj);
            if (holder != null) {
                try {
                    var m = holder.getClass().getMethod("getName");
                    Object n = m.invoke(holder);
                    if (n instanceof String s) return s;
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private static String toPlain(Object textLike) {
        try {
            // Text-Objekt hat meist getString()
            var m = textLike.getClass().getMethod("getString");
            Object s = m.invoke(textLike);
            if (s instanceof String str) return str;
        } catch (Throwable ignored) {}
        return String.valueOf(textLike);
    }

    private Integer tryParseDay(String s) {
        if (s == null) return null;
        Matcher m = DAY_PATTERN.matcher(s);
        if (m.matches()) {
            try { return Integer.parseInt(m.group(1)); } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private Integer tryParseHeat(String s) {
        if (s == null) return null;
        Matcher m = HEAT_PATTERN.matcher(s);
        if (m.matches()) {
            try {
                int h = Integer.parseInt(m.group(1));
                if (h >= 0 && h <= 200) return h; // Tolerant
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
