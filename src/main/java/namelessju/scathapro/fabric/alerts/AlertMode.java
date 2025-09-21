package namelessju.scathapro.fabric.alerts;

/**
 * Repräsentiert einen Alert-Modus (z. B. vanilla, meme, anime, custom)
 */
public interface AlertMode {
    String id();
    String displayName();

    default void onEnter() {}
    default void onExit() {}
}