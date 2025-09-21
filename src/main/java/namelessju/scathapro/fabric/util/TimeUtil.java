package namelessju.scathapro.fabric.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

/**
 * Utility-Klasse für Zeit-bezogene Funktionen
 * Portiert von der ursprünglichen TimeUtil Klasse
 */
public class TimeUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Gibt die aktuelle Zeit in Millisekunden zurück
     */
    public static long now() {
        return System.currentTimeMillis();
    }
    
    /**
     * Gibt das aktuelle Datum zurück
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * Gibt die aktuelle Datum/Zeit zurück
     */
    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }
    
    /**
     * Prüft ob heute der 1. April ist (April Fools)
     */
    public static boolean isAprilFools() {
        LocalDate today = today();
        return today.getMonth() == Month.APRIL && today.getDayOfMonth() == 1;
    }
    
    /**
     * Prüft ob heute ein bestimmtes Datum ist
     */
    public static boolean isToday(Month month, int day) {
        LocalDate today = today();
        return today.getMonth() == month && today.getDayOfMonth() == day;
    }
    
    /**
     * Formatiert ein Datum als String
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Formatiert eine DateTime als String
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Formatiert eine Zeitdauer in lesbarer Form
     */
    public static String formatDuration(long milliseconds) {
        if (milliseconds < 0) return "0s";
        
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + "d " + (hours % 24) + "h " + (minutes % 60) + "m";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m " + (seconds % 60) + "s";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }
    
    /**
     * Formatiert eine kurze Zeitdauer (nur Sekunden/Minuten)
     */
    public static String formatShortDuration(long milliseconds) {
        if (milliseconds < 0) return "0s";
        
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        
        if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }
    
    /**
     * Formatiert eine Zeitdauer als Dezimalzahl (z.B. "3.5s")
     */
    public static String formatDecimalDuration(long milliseconds) {
        if (milliseconds < 0) return "0.0s";
        
        double seconds = milliseconds / 1000.0;
        if (seconds >= 60) {
            double minutes = seconds / 60.0;
            return String.format("%.1fm", minutes);
        } else {
            return String.format("%.1fs", seconds);
        }
    }
    
    /**
     * Konvertiert Sekunden zu Millisekunden
     */
    public static long secondsToMillis(double seconds) {
        return (long) (seconds * 1000);
    }
    
    /**
     * Konvertiert Minuten zu Millisekunden
     */
    public static long minutesToMillis(double minutes) {
        return (long) (minutes * 60 * 1000);
    }
    
    /**
     * Konvertiert Stunden zu Millisekunden
     */
    public static long hoursToMillis(double hours) {
        return (long) (hours * 60 * 60 * 1000);
    }
    
    /**
     * Prüft ob eine bestimmte Zeit vergangen ist
     */
    public static boolean hasTimePassed(long startTime, long duration) {
        return now() - startTime >= duration;
    }
    
    /**
     * Berechnet die verbleibende Zeit
     */
    public static long getRemainingTime(long startTime, long duration) {
        long elapsed = now() - startTime;
        return Math.max(0, duration - elapsed);
    }
    
    /**
     * Prüft ob ein Timestamp gültig ist (nicht -1 oder 0)
     */
    public static boolean isValidTimestamp(long timestamp) {
        return timestamp > 0;
    }
    
    /**
     * Gibt die Zeit seit einem Timestamp zurück
     */
    public static long getTimeSince(long timestamp) {
        if (!isValidTimestamp(timestamp)) return -1;
        return now() - timestamp;
    }
    
    /**
     * Parst ein Datum aus einem String
     */
    public static LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Parst eine DateTime aus einem String
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, DATETIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Prüft ob zwei LocalDates den gleichen Tag repräsentieren
     */
    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) return false;
        return date1.equals(date2);
    }
    
    /**
     * Prüft ob ein LocalDate heute ist
     */
    public static boolean isToday(LocalDate date) {
        return isSameDay(date, today());
    }
    
    /**
     * Berechnet die Tage zwischen zwei Daten
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return -1;
        return java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }
    
    /**
     * Fügt Tage zu einem Datum hinzu
     */
    public static LocalDate addDays(LocalDate date, int days) {
        if (date == null) return null;
        return date.plusDays(days);
    }
    
    /**
     * Subtrahiert Tage von einem Datum
     */
    public static LocalDate subtractDays(LocalDate date, int days) {
        if (date == null) return null;
        return date.minusDays(days);
    }
    
    /**
     * Für Development: Simulation von April Fools
     */
    private static boolean forceAprilFools = false;
    
    public static void setForceAprilFools(boolean force) {
        forceAprilFools = force;
    }
    
    public static boolean isAprilFoolsForced() {
        return forceAprilFools || isAprilFools();
    }
}