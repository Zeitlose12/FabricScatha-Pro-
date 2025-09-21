package namelessju.scathapro.fabric.events;

import net.minecraft.world.World;

/**
 * Fabric World-Events für Scatha-Pro
 * Events die beim Welt-Join und -Leave auftreten
 */
public abstract class FabricWorldEvent extends FabricEvent
{
    public final World world;
    public final long joinTime;
    
    protected FabricWorldEvent(World world, long joinTime)
    {
        this.world = world;
        this.joinTime = joinTime;
    }
    
    /**
     * Event wird gefeuert wenn eine neue Welt betreten wird
     */
    public static class WorldJoinEvent extends FabricWorldEvent
    {
        public WorldJoinEvent(World world, long joinTime)
        {
            super(world, joinTime);
        }
    }
    
    /**
     * Event wird gefeuert wenn eine Welt verlassen wird
     */
    public static class WorldLeaveEvent extends FabricWorldEvent
    {
        public WorldLeaveEvent(World world, long leaveTime)
        {
            super(world, leaveTime);
        }
    }
}