package vodmordia.modtabs.api.tabs_menu;

import vodmordia.modtabs.config.ModTabsConfig;

/**
 * Annotation-driven lookup of per-tab metadata (order, stickiness) from live
 * {@link ModTabsConfig} fields.
 */
public class TabRegistry {

    /**
     * Get the order for a tab instance using annotation-based lookup: reads the live
     * {@code <configKey>Order} field on ModTabsConfig so the global-settings modal's
     * reorder writes are picked up at sort time.
     */
    public static int getTabOrder(TabBase tab) {
        TabConfig annotation = tab.getClass().getAnnotation(TabConfig.class);
        if (annotation != null) {
            return getConfigValueByKey(annotation.configKey() + "Order", annotation.defaultOrder());
        }
        return 0;
    }

    /**
     * Whether a tab is sticky, read from the live {@code <configKey>Sticky} field on
     * {@link ModTabsConfig} so the global-settings modal's writes are picked up immediately
     * after a re-init. Tabs without a {@code @TabConfig} or without the field are never sticky.
     */
    public static boolean isTabSticky(TabBase tab) {
        TabConfig annotation = tab.getClass().getAnnotation(TabConfig.class);
        if (annotation == null) {
            return false;
        }
        try {
            var field = ModTabsConfig.class.getField(annotation.configKey() + "Sticky");
            if (field.getType() == boolean.class) {
                return field.getBoolean(null);
            }
        } catch (Exception e) {
            // Field doesn't exist or can't be accessed → not sticky.
        }
        return false;
    }

    /**
     * Get a config value by key with fallback
     */
    private static int getConfigValueByKey(String key, int defaultValue) {
        try {
            // Use reflection to get the config value from ModTabsConfig
            var field = ModTabsConfig.class.getDeclaredField(key);
            if (field.getType() == int.class) {
                field.setAccessible(true);
                return field.getInt(null);
            }
        } catch (Exception e) {
            // Field doesn't exist or can't be accessed, return default
        }
        return defaultValue;
    }
}
