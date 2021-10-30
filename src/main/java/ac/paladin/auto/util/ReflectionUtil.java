package ac.paladin.auto.util;

import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class ReflectionUtil {

    private static final Method s_getLocaleMethod;

    private static final boolean s_legacy;

    static {
        Method getLocaleMethod = null;
        boolean isLegacy = false;

        try {
            getLocaleMethod = Player.Spigot.class.getDeclaredMethod("getLocale");
            isLegacy = true;
        } catch (NoSuchMethodException ignored) {
        }

        s_getLocaleMethod = getLocaleMethod;
        s_legacy = isLegacy;
    }

    private ReflectionUtil() {
    }

    @SneakyThrows
    public static String getLocale(Player player) {
        if (s_legacy) {
            return (String) s_getLocaleMethod.invoke(player.spigot());
        }
        return player.getLocale();
    }
}
