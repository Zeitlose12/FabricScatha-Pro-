package namelessju.scathapro.fabric.state;

public final class ClientState {
    private static final ClientState INSTANCE = new ClientState();

    public static ClientState get() { return INSTANCE; }

    // Beispielzustand – wird bei Bedarf erweitert
    private int scathaKills = 0;
    private int wormKills = 0;
    private int streak = 0;
    private long lastWormSpawnMs = 0L;
    private long lastScathaSpawnMs = 0L;
    
    // Pet drops tracking
    private int bluePetDrops = 0;
    private int purplePetDrops = 0;
    private int orangePetDrops = 0;

    private ClientState() {}

    public int getScathaKills() { return scathaKills; }
    public void setScathaKills(int v) { scathaKills = Math.max(0, v); }
    public void addScathaKills(int delta) { setScathaKills(scathaKills + delta); }

    public int getWormKills() { return wormKills; }
    public void setWormKills(int v) { wormKills = Math.max(0, v); }
    public void addWormKills(int delta) { setWormKills(wormKills + delta); }

    public int getStreak() { return streak; }
    public void setStreak(int v) { streak = Math.max(0, v); }
    public void addStreak(int delta) { setStreak(streak + delta); }

    public int getTotalKills() { return Math.max(0, scathaKills) + Math.max(0, wormKills); }

    public long getLastWormSpawnMs() { return lastWormSpawnMs; }
    public void setLastWormSpawnMs(long t) { lastWormSpawnMs = t; }
    public long getLastScathaSpawnMs() { return lastScathaSpawnMs; }
    public void setLastScathaSpawnMs(long t) { lastScathaSpawnMs = t; }
    
    // Pet drops getters/setters
    public int getBluePetDrops() { return bluePetDrops; }
    public void setBluePetDrops(int v) { bluePetDrops = Math.max(0, v); }
    public void addBluePetDrops(int delta) { setBluePetDrops(bluePetDrops + delta); }
    
    public int getPurplePetDrops() { return purplePetDrops; }
    public void setPurplePetDrops(int v) { purplePetDrops = Math.max(0, v); }
    public void addPurplePetDrops(int delta) { setPurplePetDrops(purplePetDrops + delta); }
    
    public int getOrangePetDrops() { return orangePetDrops; }
    public void setOrangePetDrops(int v) { orangePetDrops = Math.max(0, v); }
    public void addOrangePetDrops(int delta) { setOrangePetDrops(orangePetDrops + delta); }
    
    public int getTotalPetDrops() { return bluePetDrops + purplePetDrops + orangePetDrops; }
}
