package namelessju.scathapro.fabric.detect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import namelessju.scathapro.fabric.ScathaProFabric;
import namelessju.scathapro.fabric.state.ClientState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DetectorManager {
    private final Set<UUID> prevWorms = new HashSet<>();
    private final Set<UUID> prevScathas = new HashSet<>();
    private final Set<UUID> prevGoblins = new HashSet<>();
    private final Set<UUID> prevJerries = new HashSet<>();

    private final Set<UUID> currWorms = new HashSet<>();
    private final Set<UUID> currScathas = new HashSet<>();
    private final Set<UUID> currGoblins = new HashSet<>();
    private final Set<UUID> currJerries = new HashSet<>();

    // Heuristik-Daten
    private final Map<UUID, Long> lastHitMs = new HashMap<>();
    private final Map<UUID, Vec3d> lastPos = new HashMap<>();
    private final Map<UUID, Long> lastSeenMs = new HashMap<>();
    private final Map<UUID, Float> lastHealth = new HashMap<>();
    private final Map<UUID, Long> lastDamageMs = new HashMap<>();

    public void tick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (world == null || mc.player == null) return;

        currWorms.clear(); currScathas.clear(); currGoblins.clear(); currJerries.clear();

        // Eingabe: Attack erkennen und Ziel erfassen
        captureAttackTarget(mc);

        try {
            java.util.List<Entity> list = new java.util.ArrayList<>();
            // Versuche: world.iterateEntities()
            try {
                java.lang.reflect.Method m = ClientWorld.class.getMethod("iterateEntities");
                Object it = m.invoke(world);
                if (it instanceof Iterable<?>) {
                    for (Object o : (Iterable<?>) it) {
                        if (o instanceof Entity) list.add((Entity) o);
                    }
                }
            } catch (NoSuchMethodException nsme) {
                // Fallback: world.getEntities().forEach(Consumer)
                try {
                    java.lang.reflect.Method gm = ClientWorld.class.getMethod("getEntities");
                    Object lookup = gm.invoke(world);
                    if (lookup != null) {
                        java.lang.reflect.Method forEach = lookup.getClass().getMethod("forEach", java.util.function.Consumer.class);
                        forEach.invoke(lookup, (java.util.function.Consumer<Entity>) list::add);
                    }
                } catch (Throwable ignore) {}
            }

            long now = System.currentTimeMillis();
            for (Entity e : list) {
                if (!(e instanceof LivingEntity)) continue;
                LivingEntity le = (LivingEntity) e;
                String name = e.getDisplayName() != null ? e.getDisplayName().getString() : "";
                UUID id = e.getUuid();
                if (name == null) name = "";
                String lower = name.toLowerCase();
                if (lower.contains("worm")) currWorms.add(id);
                else if (lower.contains("scatha")) currScathas.add(id);
                else if (lower.contains("goblin")) currGoblins.add(id);
                else if (lower.contains("jerry")) currJerries.add(id);
                // Track last seen, position und Health
                lastSeenMs.put(id, now);
                lastPos.put(id, e.getPos());
                try {
                    float h = le.getHealth();
                    Float prev = lastHealth.put(id, h);
                    if (prev != null && h < prev - 0.01f) {
                        lastDamageMs.put(id, now);
                    }
                } catch (Throwable ignored) {}
            }
        } catch (Throwable t) {
            // fail-safe gegen Mapping/Iteration-Änderungen
        }

        // Despawn -> als Kill werten (verfeinerte Heuristik)
        long now = System.currentTimeMillis();
        for (UUID id : diff(prevWorms, currWorms)) {
            if (isLikelyPlayerKill(mc, id, now)) {
                ClientState.get().addWormKills(1);
                namelessju.scathapro.fabric.util.FabricSoundUtil.playModSound("alert_modes/meme/regular_worm_spawn");
                if (namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG != null) {
                    namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.wormKills = ClientState.get().getWormKills();
                    namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.save();
                }
                debug("Worm despawn -> +1 Worm-Kill ("+ClientState.get().getWormKills()+")");
            }
        }
        for (UUID id : diff(prevScathas, currScathas)) {
            if (isLikelyPlayerKill(mc, id, now)) {
                ClientState.get().addScathaKills(1);
                ClientState.get().addStreak(1);
                namelessju.scathapro.fabric.util.FabricSoundUtil.playModSound("alert_modes/meme/scatha_spawn");
                if (namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG != null) {
                    namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.scathaKills = ClientState.get().getScathaKills();
                    namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.streak = ClientState.get().getStreak();
                    namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG.save();
                }
                debug("Scatha despawn -> +1 Scatha-Kill ("+ClientState.get().getScathaKills()+"), Streak="+ClientState.get().getStreak());
            }
        }
        for (UUID id : diff(prevGoblins, currGoblins)) {
            debug("Goblin despawn erkannt");
        }
        for (UUID id : diff(prevJerries, currJerries)) {
            debug("Jerry despawn erkannt");
        }

        // Spawns: curr - prev
        for (UUID id : diff(currWorms, prevWorms)) {
            namelessju.scathapro.fabric.util.FabricSoundUtil.playModSound("alert_modes/meme/regular_worm_spawn");
            namelessju.scathapro.fabric.state.ClientState.get().setLastWormSpawnMs(System.currentTimeMillis());
            var cfgShow = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            if (cfgShow != null && cfgShow.alertsDisplayEnabled) {
                namelessju.scathapro.fabric.util.FabricHudUtil.showOverlayMessage(cfgShow.alertWormMessage);
            }
            debug("Worm spawn erkannt");
        }
        for (UUID id : diff(currScathas, prevScathas)) {
            namelessju.scathapro.fabric.util.FabricSoundUtil.playModSound("alert_modes/meme/scatha_spawn");
            namelessju.scathapro.fabric.state.ClientState.get().setLastScathaSpawnMs(System.currentTimeMillis());
            var cfgShow = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
            if (cfgShow != null && cfgShow.alertsDisplayEnabled) {
                namelessju.scathapro.fabric.util.FabricHudUtil.showOverlayMessage(cfgShow.alertScathaMessage);
            }
            debug("Scatha spawn erkannt");
        }
        for (UUID id : diff(currGoblins, prevGoblins)) {
            namelessju.scathapro.fabric.util.FabricSoundUtil.playModSound("alert_modes/meme/goblin_spawn");
            debug("Goblin spawn erkannt");
        }
        for (UUID id : diff(currJerries, prevJerries)) {
            namelessju.scathapro.fabric.util.FabricSoundUtil.playModSound("alert_modes/meme/jerry_spawn");
            debug("Jerry spawn erkannt");
        }

        // Aktuelle -> Vorherige austauschen
        prevWorms.clear(); prevWorms.addAll(currWorms);
        prevScathas.clear(); prevScathas.addAll(currScathas);
        prevGoblins.clear(); prevGoblins.addAll(currGoblins);
        prevJerries.clear(); prevJerries.addAll(currJerries);
    }

    private boolean isLikelyPlayerKill(MinecraftClient mc, UUID id, long now) {
        if (mc.player == null) return false;
        Long hit = lastHitMs.get(id);
        Long dmg = lastDamageMs.get(id);
        boolean recentHit = (hit != null && now - hit <= 2000);
        boolean recentDmg = (dmg != null && now - dmg <= 1000);
        Vec3d pos = lastPos.get(id);
        if (pos == null) return false;
        double distSq = mc.player.squaredDistanceTo(pos);
        boolean close = distSq <= (8.0 * 8.0);
        // Blickrichtung-Check
        Vec3d look = mc.player.getRotationVec(1.0f);
        Vec3d toTarget = pos.subtract(mc.player.getPos()).normalize();
        double dot = look.dotProduct(toTarget);
        boolean aiming = dot >= 0.96; // ~15° Kegel
        return close && aiming && (recentHit || recentDmg);
    }

    private void captureAttackTarget(MinecraftClient mc) {
        try {
            KeyBinding attack = mc.options.attackKey;
            while (attack.wasPressed()) {
                HitResult hr = mc.crosshairTarget;
                if (hr != null && hr.getType() == HitResult.Type.ENTITY) {
                    Entity target = ((EntityHitResult) hr).getEntity();
                    if (target != null) {
                        lastHitMs.put(target.getUuid(), System.currentTimeMillis());
                        lastPos.put(target.getUuid(), target.getPos());
                    }
                }
            }
        } catch (Throwable t) {
            // fail-safe
        }
    }

    private Set<UUID> diff(Set<UUID> a, Set<UUID> b) {
        Set<UUID> res = new HashSet<>(a);
        res.removeAll(b);
        return res;
    }

    private void debug(String msg) {
        var cfg = namelessju.scathapro.fabric.ScathaProFabricClient.CONFIG;
        if (cfg != null && cfg.debugLogs) {
            ScathaProFabric.LOGGER.info("[Detect] {}", msg);
        }
    }
}