package namelessju.scathapro.fabric.entitydetection;

import java.util.ArrayList;
import java.util.List;

import namelessju.scathapro.fabric.Constants;
import namelessju.scathapro.fabric.FabricScathaPro;
import namelessju.scathapro.fabric.events.FabricEvent;
import namelessju.scathapro.fabric.events.FabricWormEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;

/**
 * Fabric-Version der DetectedWorm-Klasse
 * Repräsentiert einen erkannten Worm/Scatha mit spezifischer Logik
 */
public class FabricDetectedWorm extends FabricDetectedEntity
{
    /**
     * Findet einen DetectedWorm anhand der ID
     */
    public static FabricDetectedWorm getById(int id)
    {
        FabricDetectedEntity detectedEntity = FabricDetectedEntity.getById(id);
        if (detectedEntity instanceof FabricDetectedWorm)
        {
            return (FabricDetectedWorm) detectedEntity;
        }
        return null;
    }
    
    // Worm-spezifische Properties
    public final boolean isScatha;
    private long lastAttackTime = -1;
    private long lastFireAspectAttackTime = -1;
    private int lastFireAspectLevel = 0;
    
    private final List<String> hitWeapons = new ArrayList<>();
    private boolean hitWithDirt = false;
    private boolean hitWithJuju = false;
    private boolean hitWithTerminator = false;
    private boolean hitWithGemstone = false;
    private boolean wasHitWithPerfectGemstoneGauntlet = false;
    
    // TODO: Scappa-Sound implementieren wenn Sound-System portiert
    // public FabricScappaSound scappaSound = null;
    public boolean lootsharePossible = false;
    
    public FabricDetectedWorm(ArmorStandEntity entity, boolean isScatha)
    {
        super(entity);
        this.isScatha = isScatha;
    }
    
    @Override
    public long getMaxLifetime()
    {
        return Constants.wormLifetime;
    }
    
    @Override
    protected void onRegistration()
    {
        // Worm-Spawn-Event feuern
        long timeSincePreviousSpawn = calculateTimeSincePreviousSpawn();
        FabricEvent.post(new FabricWormEvent.WormSpawnEvent(this, timeSincePreviousSpawn));
    }
    
    @Override
    protected void onChangedEntity()
    {
        // TODO: Scappa-Sound-Entity aktualisieren
        // if (this.scappaSound != null) this.scappaSound.entity = this.getEntity();
    }
    
    @Override
    protected void onLeaveWorld(LeaveWorldReason leaveWorldReason)
    {
        FabricScathaPro scathaPro = FabricScathaPro.getInstance();
        
        switch (leaveWorldReason)
        {
            case KILLED:
                boolean countAsKilled = checkIfKilled();
                if (countAsKilled) {
                    // Ability/Weapon Achievements
                    try {
                        var am = FabricScathaPro.getInstance().getAchievementManager();
                        if (am != null) {
                            if (wasHitWithPerfectGemstoneGauntlet) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.kill_perfect_gemstone_gauntlet);
                            if (isScatha && hitWithJuju) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_kill_juju);
                            if (isScatha && hitWithTerminator) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_kill_terminator);
                            if (isScatha && hitWithDirt) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_hit_dirt);
                            if (isScatha && hitWithGemstone) am.unlockAchievement(namelessju.scathapro.fabric.achievements.FabricAchievement.scatha_kill_gemstone);
                        }
                    } catch (Exception ignored) {}
                }
                
                if (countAsKilled)
                {
                    // TODO: Scappa-Sound stoppen
                    // if (scappaSound != null) scappaSound.stop();
                    
                    FabricEvent.post(new FabricWormEvent.WormKillEvent(this));
                    scathaPro.logDebug("Worm als Kill gezählt");
                }
                break;
                
            case LIFETIME_ENDED:
                // TODO: Scappa-Sound stoppen
                // if (scappaSound != null) scappaSound.stop();
                
                FabricEvent.post(new FabricWormEvent.WormDespawnEvent(this));
                scathaPro.logDebug("Worm als Despawn gezählt");
                break;
                
            default:
                break;
        }
    }
    
    /**
     * Registriert einen Angriff auf den Worm
     */
    public void attack(ItemStack weapon)
    {
        long now = System.currentTimeMillis();
        lastAttackTime = now;
        
        // NBT: Skyblock-Item-ID-Erkennung
        String skyblockItemID = namelessju.scathapro.fabric.util.NBTUtil.getSkyblockId(weapon);
        if (skyblockItemID == null) skyblockItemID = getSkyblockItemID(weapon); // Fallback Heuristik
        if (skyblockItemID != null)
        {
            hitWeapons.remove(skyblockItemID);
            String id = skyblockItemID;
            if (id != null) {
                if (id.contains("DIRT")) hitWithDirt = true;
                if (id.contains("JUJU")) hitWithJuju = true;
                if (id.contains("TERMINATOR")) hitWithTerminator = true;
                if (id.contains("GEMSTONE")) hitWithGemstone = true;
            }
            hitWeapons.add(skyblockItemID);
            
            // Gemstone-Gauntlet-Check
            if (!wasHitWithPerfectGemstoneGauntlet && "GEMSTONE_GAUNTLET".equals(skyblockItemID))
            {
                checkPerfectGemstoneGauntlet(weapon);
            }
        }
        
        // Fire-Aspect-Check
        checkFireAspect(weapon, now);
        
        // Worm-Hit-Event feuern
        FabricEvent.post(new FabricWormEvent.WormHitEvent(this));
    }
    
    /**
     * Spielt Scappa-Sound ab
     */
    public void playScappaSound()
    {
        // TODO: Scappa-Sound-System implementieren
        // if (this.scappaSound != null) return;
        // this.scappaSound = FabricScappaSound.play(1f, 1f, this.getEntity());
        FabricScathaPro.getInstance().logDebug("Scappa-Sound für " + (isScatha ? "Scatha" : "Worm") + " gespielt");
    }
    
    // ===== GETTER METHODS =====
    
    public long getLastAttackTime()
    {
        return lastAttackTime;
    }
    
    public String[] getHitWeapons()
    {
        return hitWeapons.toArray(new String[0]);
    }
    
    public int getHitWeaponsCount()
    {
        return hitWeapons.size();
    }
    
    public boolean wasHitWithPerfectGemstoneGauntlet()
    {
        return wasHitWithPerfectGemstoneGauntlet;
    }
    
    public boolean isFireAspectActive()
    {
        if (lastFireAspectLevel <= 0) return false;
        
        float fireAspectDuration = getFireAspectDuration(lastFireAspectLevel);
        if (fireAspectDuration <= 0) return false;
        
        long timeSinceLastFireAspect = System.currentTimeMillis() - lastFireAspectAttackTime;
        return timeSinceLastFireAspect < fireAspectDuration * 1000;
    }
    
    // ===== PRIVATE HELPER METHODS =====
    
    /**
     * Berechnet Zeit seit dem letzten Worm-Spawn
     */
    private long calculateTimeSincePreviousSpawn()
    {
        FabricScathaPro scathaPro = FabricScathaPro.getInstance();
        if (scathaPro.variables.lastWormSpawnTime >= 0L)
        {
            return System.currentTimeMillis() - scathaPro.variables.lastWormSpawnTime;
        }
        return -1L;
    }
    
    /**
     * Prüft ob Worm als getötet zählt
     */
    private boolean checkIfKilled()
    {
        // Direkter Kill-Check
        boolean countAsKilled = getLastAttackTime() >= 0 && 
            System.currentTimeMillis() - getLastAttackTime() < Constants.pingTreshold;
        
        // Fire-Aspect-Kill-Check
        if (!countAsKilled)
        {
            countAsKilled = isFireAspectActive() && 
                (getMaxLifetime() < 0 || getCurrentLifetime() < getMaxLifetime());
        }
        
        // Lootshare-Check
        if (!countAsKilled && this.lootsharePossible)
        {
            countAsKilled = checkLootshareKill();
        }
        
        return countAsKilled;
    }
    
    /**
     * Prüft ob es ein Lootshare-Kill war
     */
    private boolean checkLootshareKill()
    {
        FabricScathaPro scathaPro = FabricScathaPro.getInstance();
        ClientPlayerEntity player = scathaPro.getMinecraft().player;
        
        if (player == null) return false;
        
        Box playerDetectionBox = this.getEntity().getBoundingBox().expand(30f, 30f, 30f);
        
        if (player.getBoundingBox().intersects(playerDetectionBox))
        {
            try {
                var world = player.getWorld();
                java.util.List<net.minecraft.entity.player.PlayerEntity> players = world.getEntitiesByClass(net.minecraft.entity.player.PlayerEntity.class, playerDetectionBox, p -> true);
                int nearbyOtherPlayerCount = (players != null ? players.size() : 0) - 1; // -1 für lokalen Spieler
                if (nearbyOtherPlayerCount > 0)
                {
                    scathaPro.logDebug("Worm als Lootshare-Kill behandelt (" + nearbyOtherPlayerCount + " weitere Spieler)");
                    return true;
                }
            } catch (Exception e) {
                scathaPro.logDebug("Lootshare-Check: Fallback (" + e.getMessage() + ")");
            }
            return false;
        }
        
        return false;
    }
    
    /**
     * Prüft ob Perfect Gemstone Gauntlet verwendet wurde
     */
    private void checkPerfectGemstoneGauntlet(ItemStack weapon)
    {
        try {
            if (namelessju.scathapro.fabric.util.NBTUtil.hasPerfectGemsGauntlet(weapon)) {
                wasHitWithPerfectGemstoneGauntlet = true;
                FabricScathaPro.getInstance().logDebug("Perfect Gemstone Gauntlet: PERFECT-Gems erkannt");
                return;
            }
        } catch (Exception ignored) {}
        // Fallback: Log ohne Flag setzen
        FabricScathaPro.getInstance().logDebug("Perfect Gemstone Gauntlet Check (Fallback)");
    }
    
    /**
     * Prüft Fire-Aspect-Enchantment
     */
    private void checkFireAspect(ItemStack weapon, long now)
    {
        // Enchantment-Checks
        int level = namelessju.scathapro.fabric.util.NBTUtil.getEnchantLevel(weapon, "fire_aspect");
        if (level > 0) {
            lastFireAspectLevel = level;
            lastFireAspectAttackTime = now;
            FabricScathaPro.getInstance().logDebug("Fire Aspect Level " + lastFireAspectLevel + " erkannt (NBT)");
        } else {
        // NbtCompound enchantments = NBTUtil.getSkyblockTagCompound(weapon, "enchantments");
        // if (enchantments != null)
        // {
        //     lastFireAspectLevel = enchantments.getInt("fire_aspect");
        //     if (lastFireAspectLevel > 0) lastFireAspectAttackTime = now;
        // }
        
        // Temporär: Fire-Aspect-Levels simulieren
        if (Math.random() < 0.1) // 10% Chance auf Fire-Aspect
        {
            lastFireAspectLevel = (int)(Math.random() * 3) + 1; // Level 1-3
            lastFireAspectAttackTime = now;
            FabricScathaPro.getInstance().logDebug("Fire Aspect Level " + lastFireAspectLevel + " erkannt");
        }
        }
    }
    
    /**
     * Gibt Fire-Aspect-Dauer basierend auf Level zurück
     */
    private float getFireAspectDuration(int level)
    {
        switch (level)
        {
            case 1: return 4f;
            case 2: return 8f; 
            case 3: return 12f;
            default: return 0f;
        }
    }
    
    /**
     * Extrahiert Skyblock-Item-ID aus ItemStack
     * TODO: Durch echten NBTUtil ersetzen
     */
    private String getSkyblockItemID(ItemStack itemStack)
    {
        if (itemStack == null || itemStack.isEmpty()) return null;
        
        // TODO: Echte Skyblock-NBT-Analyse implementieren
        // NbtCompound nbt = itemStack.getNbt();
        // if (nbt != null && nbt.contains("ExtraAttributes"))
        // {
        //     NbtCompound extraAttributes = nbt.getCompound("ExtraAttributes");
        //     if (extraAttributes.contains("id"))
        //     {
        //         return extraAttributes.getString("id");
        //     }
        // }
        
        // Temporär: Item-Namen-basierte ID-Simulation
        String itemName = itemStack.getName().getString().toLowerCase();
        if (itemName.contains("dirt")) return "DIRT";
        if (itemName.contains("terminator")) return "TERMINATOR";
        if (itemName.contains("juju")) return "JUJU_SHORTBOW";
        if (itemName.contains("gauntlet")) return "GEMSTONE_GAUNTLET";
        
        return itemName.toUpperCase().replace(" ", "_");
    }
}