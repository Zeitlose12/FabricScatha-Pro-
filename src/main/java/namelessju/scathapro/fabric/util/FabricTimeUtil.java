package namelessju.scathapro.fabric.util;

/**
 * Zeit-Utility-Klasse für Fabric
 */
public class FabricTimeUtil {
    
    /**
     * Gibt den aktuellen Zeitstempel in Millisekunden zurück
     */
    public static long now() {
        return System.currentTimeMillis();
    }
    
    /**
     * Konvertiert einen Zeitstempel zu einem lesbaren String
     */
    public static String formatTimestamp(long timestamp) {
        return new java.util.Date(timestamp).toString();
    }
    
    /**
     * Berechnet die Zeitdifferenz zwischen zwei Zeitstempeln in Sekunden
     */
    public static long getSecondsDifference(long startTime, long endTime) {
        return (endTime - startTime) / 1000;
    }
    
    /**
     * Berechnet die Zeitdifferenz zwischen zwei Zeitstempeln in Minuten
     */
    public static long getMinutesDifference(long startTime, long endTime) {
        return getSecondsDifference(startTime, endTime) / 60;
    }
    
    /**
     * Berechnet die Zeitdifferenz zwischen zwei Zeitstempeln in Stunden
     */
    public static long getHoursDifference(long startTime, long endTime) {
        return getMinutesDifference(startTime, endTime) / 60;
    }
    
    /**
     * Prüft ob ein Zeitstempel heute ist
     */
    public static boolean isToday(long timestamp) {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTimeInMillis(System.currentTimeMillis());
        cal2.setTimeInMillis(timestamp);
        
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);
    }
}