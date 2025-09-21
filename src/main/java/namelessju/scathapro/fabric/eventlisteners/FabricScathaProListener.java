package namelessju.scathapro.fabric.eventlisteners;

import namelessju.scathapro.fabric.FabricScathaPro;
import net.minecraft.client.MinecraftClient;

/**
 * Fabric-Basis-Klasse für alle Scatha-Pro Event-Listener
 * Portiert von ScathaProListener.java
 */
public abstract class FabricScathaProListener
{
    protected final FabricScathaPro scathaPro;
    protected final MinecraftClient mc;
    
    public FabricScathaProListener(FabricScathaPro scathaPro)
    {
        this.scathaPro = scathaPro;
        this.mc = scathaPro.getMinecraft();
    }
}