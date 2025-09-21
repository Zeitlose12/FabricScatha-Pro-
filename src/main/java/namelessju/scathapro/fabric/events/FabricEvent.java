package namelessju.scathapro.fabric.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Basis-Event-Klasse für das Fabric Scatha-Pro Event-System
 * Ersetzt das Forge Event-System mit einem einfachen Callback-basierten Ansatz
 */
public abstract class FabricEvent
{
    private static final List<EventListener<?>> listeners = new ArrayList<>();
    
    /**
     * Registriert einen Event-Listener für eine bestimmte Event-Klasse
     */
    @SuppressWarnings("unchecked")
    public static <T extends FabricEvent> void register(Class<T> eventClass, Consumer<T> handler)
    {
        listeners.add(new EventListener<>(eventClass, (Consumer<FabricEvent>) handler));
    }
    
    /**
     * Feuert ein Event und benachrichtigt alle registrierten Listener
     */
    @SuppressWarnings("unchecked")
    public static <T extends FabricEvent> void post(T event)
    {
        for (EventListener<?> listener : listeners)
        {
            if (listener.eventClass.isInstance(event))
            {
                try 
                {
                    ((Consumer<FabricEvent>) listener.handler).accept(event);
                }
                catch (Exception e)
                {
                    System.err.println("Error in event listener for " + event.getClass().getSimpleName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Event-Listener Wrapper-Klasse
     */
    private static class EventListener<T extends FabricEvent>
    {
        final Class<T> eventClass;
        final Consumer<FabricEvent> handler;
        
        EventListener(Class<T> eventClass, Consumer<FabricEvent> handler)
        {
            this.eventClass = eventClass;
            this.handler = handler;
        }
    }
}