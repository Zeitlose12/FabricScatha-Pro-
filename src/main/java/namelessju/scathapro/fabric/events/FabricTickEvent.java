package namelessju.scathapro.fabric.events;

import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Fabric-Version der TickEvent-Klasse
 * Portiert von Forge Event-System
 */
public abstract class FabricTickEvent extends FabricEvent
{
    /**
     * Wird beim ersten Ingame-Tick gefeuert
     */
    public static class FirstIngameTickEvent extends FabricTickEvent {}
    
    /**
     * Wird beim ersten Welt-Tick gefeuert
     */
    public static class FirstWorldTickEvent extends FabricTickEvent
    {
        public final ClientPlayerEntity player;
        
        public FirstWorldTickEvent(ClientPlayerEntity player)
        {
            this.player = player;
        }
    }
    
    /**
     * Wird beim ersten Crystal Hollows-Tick gefeuert
     */
    public static class FirstCrystalHollowsTickEvent extends FabricTickEvent {}

    /**
     * Wird bei jedem Crystal Hollows-Tick gefeuert
     */
    public static class CrystalHollowsTickEvent extends FabricTickEvent
    {
        public final long now;
        
        public CrystalHollowsTickEvent(long now)
        {
            this.now = now;
        }
    }
}