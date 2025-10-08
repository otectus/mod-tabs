package vodmordia.modtabs.api.tabs_menu;

import net.minecraft.resources.ResourceLocation;
import vodmordia.modtabs.utils.IconResolver;

/**
 * Base class for tabs that support custom icons from config.
 * Extends SimpleTextureTab but allows the icon to be overridden via config.
 */
public abstract class ConfigurableIconTab extends SimpleTextureTab {

    /**
     * Creates a tab with optional custom icon support.
     *
     * @param defaultIcon The default icon texture
     * @param customIconConfig The custom icon config string (can be null/empty for default)
     * @param tabId The unique tab identifier for icon resolution
     */
    protected ConfigurableIconTab(ResourceLocation defaultIcon, String customIconConfig, String tabId) {
        super(resolveIcon(defaultIcon, customIconConfig, tabId));
    }

    /**
     * Resolves the icon to use, prioritizing custom config over default.
     */
    private static ResourceLocation resolveIcon(ResourceLocation defaultIcon, String customIconConfig, String tabId) {
        // Try to resolve custom icon from config
        ResourceLocation customIcon = IconResolver.resolveIcon(customIconConfig, tabId);

        // Return custom icon if found, otherwise return default
        return customIcon != null ? customIcon : defaultIcon;
    }
}
