package namelessju.scathapro.fabric.util;

import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * NBT-Helfer (reflektierend), kompatibel mit 1.21.5 Data-Components und älteren Yarn-Mappings.
 */
public final class NBTUtil {
    private NBTUtil() {}

    // === Public API ===
    public static String getSkyblockId(ItemStack stack) {
        try {
            Object root = getNbtRoot(stack);
            if (root == null) return null;
            Object ea = getCompound(root, "ExtraAttributes");
            if (ea == null) return null;
            String id = getString(ea, "id");
            return (id != null && !id.isEmpty()) ? id : null;
        } catch (Throwable ignored) { return null; }
    }

    public static int getEnchantLevel(ItemStack stack, String enchantId) {
        try {
            Object root = getNbtRoot(stack);
            if (root == null) return 0;
            Object ea = getCompound(root, "ExtraAttributes");
            if (ea == null) return 0;
            Object ench = getCompound(ea, "enchantments");
            if (ench == null) return 0;
            Integer lvl = getInt(ench, enchantId);
            return lvl != null ? lvl : 0;
        } catch (Throwable ignored) { return 0; }
    }

    public static boolean hasPerfectGemsGauntlet(ItemStack stack) {
        try {
            Object root = getNbtRoot(stack);
            if (root == null) return false;
            Object ea = getCompound(root, "ExtraAttributes");
            if (ea == null) return false;
            Object gems = getCompound(ea, "gems");
            if (gems == null) return false;
            String[] slots = new String[]{"JADE_0","AMBER_0","SAPPHIRE_0","AMETHYST_0","TOPAZ_0"};
            for (String s : slots) {
                String v = getString(gems, s);
                if (!"PERFECT".equalsIgnoreCase(v)) return false;
            }
            return true;
        } catch (Throwable ignored) { return false; }
    }

    // === Reflective helpers ===
    private static Object getNbtRoot(ItemStack stack) {
        try {
            // Try legacy: ItemStack#getNbt()
            Method mGetNbt = findMethod(stack.getClass(), "getNbt");
            if (mGetNbt != null) return mGetNbt.invoke(stack);
        } catch (Throwable ignored) {}
        try {
            // 1.21.x: DataComponentTypes.CUSTOM_DATA
            Class<?> dct = Class.forName("net.minecraft.component.DataComponentTypes");
            Field f = dct.getField("CUSTOM_DATA");
            Object CUSTOM_DATA = f.get(null);
            Method mGet = findMethod(stack.getClass(), "get", CUSTOM_DATA.getClass());
            if (mGet != null) {
                Object comp = mGet.invoke(stack, CUSTOM_DATA);
                Object nbtComponent = unwrapOptional(comp);
                if (nbtComponent != null) {
                    // Try common accessors on NBT component
                    Method mGetNbt = findMethod(nbtComponent.getClass(), "getNbt");
                    if (mGetNbt != null) return mGetNbt.invoke(nbtComponent);
                    Method mCopyNbt = findMethod(nbtComponent.getClass(), "copyNbt");
                    if (mCopyNbt != null) return mCopyNbt.invoke(nbtComponent);
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private static boolean containsKey(Object nbtCompound, String key) {
        Method m = findMethod(nbtCompound.getClass(), "contains", String.class);
        if (m != null) {
            try { return (boolean)m.invoke(nbtCompound, key); } catch (Throwable ignored) {}
        }
        // Fallback: contains(String,int) old signature
        Method m2 = findMethod(nbtCompound.getClass(), "contains", String.class, int.class);
        if (m2 != null) {
            try { return (boolean)m2.invoke(nbtCompound, key, 0); } catch (Throwable ignored) {}
        }
        return false;
    }

    private static Object getCompound(Object nbtCompound, String key) {
        try {
            if (!containsKey(nbtCompound, key)) return null;
            Method m = findMethod(nbtCompound.getClass(), "getCompound", String.class);
            if (m != null) {
                Object res = m.invoke(nbtCompound, key);
                Object unwrapped = unwrapOptional(res);
                return unwrapped != null ? unwrapped : res;
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private static String getString(Object nbtCompound, String key) {
        try {
            if (!containsKey(nbtCompound, key)) return null;
            Method m = findMethod(nbtCompound.getClass(), "getString", String.class);
            if (m != null) {
                Object res = m.invoke(nbtCompound, key);
                Object unwrapped = unwrapOptional(res);
                return (unwrapped != null ? (String)unwrapped : (String)res);
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private static Integer getInt(Object nbtCompound, String key) {
        try {
            if (!containsKey(nbtCompound, key)) return 0;
            Method m = findMethod(nbtCompound.getClass(), "getInt", String.class);
            if (m != null) {
                Object res = m.invoke(nbtCompound, key);
                Object unwrapped = unwrapOptional(res);
                return (unwrapped != null ? (Integer)unwrapped : (Integer)res);
            }
        } catch (Throwable ignored) {}
        return 0;
    }

    private static Method findMethod(Class<?> c, String name, Class<?>... params) {
        try { return c.getMethod(name, params); } catch (Throwable ignored) { return null; }
    }

    private static Object unwrapOptional(Object o) {
        if (o == null) return null;
        if (o instanceof Optional) {
            Optional<?> opt = (Optional<?>) o;
            return opt.orElse(null);
        }
        // Support other optionals via reflection (e.g., com.mojang.datafixers.util)
        try {
            Class<?> optClass = o.getClass();
            if (optClass.getName().toLowerCase().contains("optional")) {
                Method isPresent = findMethod(optClass, "isPresent");
                Method get = findMethod(optClass, "get");
                if (isPresent != null && get != null) {
                    if ((boolean)isPresent.invoke(o)) return get.invoke(o);
                    return null;
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }
}
