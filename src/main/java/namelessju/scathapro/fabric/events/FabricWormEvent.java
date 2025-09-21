package namelessju.scathapro.fabric.events;

import namelessju.scathapro.fabric.entitydetection.FabricDetectedWorm;

/**
 * Fabric-Version des WormEvent-Systems
 * Jetzt mit echten FabricDetectedWorm-Instanzen
 */
public abstract class FabricWormEvent extends FabricEvent
{
    public final FabricDetectedWorm worm;
    
    public FabricWormEvent(FabricDetectedWorm worm)
    {
        this.worm = worm;
    }
    
    /**
     * Worm-Spawn Event
     */
    public static class WormSpawnEvent extends FabricWormEvent
    {
        public final long timeSincePreviousSpawn;
        
        public WormSpawnEvent(FabricDetectedWorm worm, long timeSincePreviousSpawn)
        {
            super(worm);
            this.timeSincePreviousSpawn = timeSincePreviousSpawn;
        }
    }
    
    /**
     * Worm-Despawn Event  
     */
    public static class WormDespawnEvent extends FabricWormEvent
    {
        public WormDespawnEvent(FabricDetectedWorm worm)
        {
            super(worm);
        }
    }
    
    /**
     * Worm-Hit Event
     */
    public static class WormHitEvent extends FabricWormEvent
    {
        public WormHitEvent(FabricDetectedWorm worm)
        {
            super(worm);
        }
    }
    
    /**
     * Worm-Kill Event
     */
    public static class WormKillEvent extends FabricWormEvent
    {
        public WormKillEvent(FabricDetectedWorm worm)
        {
            super(worm);
        }
    }
    
    /**
     * Worm-PreSpawn Event
     */
    public static class WormPreSpawnEvent extends FabricWormEvent
    {
        public WormPreSpawnEvent(FabricDetectedWorm worm)
        {
            super(worm);
        }
    }
}