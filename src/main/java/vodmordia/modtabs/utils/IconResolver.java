package vodmordia.modtabs.utils;

import net.minecraft.resources.Identifier;
import vodmordia.modtabs.ModTabs;

/**
 * Utility for resolving custom icon strings into texture paths.
 * Supports three formats:
 * 1. Simple filename: "my_icon.png" -> loads from config/modtabs/icons/my_icon.png
 * 2. Identifier: "modtabs:textures/gui/my_icon.png" -> loads from resources
 * 3. Empty/null -> use default (returns null)
 */
public class IconResolver {

    /**
     * Resolves an icon config string and loads the appropriate texture.
     *
     * @param iconConfig The icon configuration string from config
     * @param tabId Unique identifier for this tab (used for texture registration)
     * @return Identifier of the loaded texture, or null if using default
     */
    public static Identifier resolveIcon(String iconConfig, String tabId) {
        // If empty or null, use default
        if (iconConfig == null || iconConfig.trim().isEmpty()) {
            return null;
        }

        String trimmed = iconConfig.trim();

        // Check if it's a Identifier (contains ':')
        if (trimmed.contains(":")) {
            try {
                Identifier resourceLocation = Identifier.parse(trimmed);
                // Validate the texture exists in resources
                if (DynamicTextureLoader.validateResourceTexture(resourceLocation)) {
                    ModTabs.LOGGER.info("Resolved icon '" + iconConfig + "' as resource texture for tab: " + tabId);
                    return resourceLocation;
                } else {
                    ModTabs.LOGGER.warn("Resource texture not found: " + trimmed + " for tab: " + tabId);
                    return null;
                }
            } catch (Exception e) {
                ModTabs.LOGGER.error("Invalid Identifier format: " + trimmed + " for tab: " + tabId);
                return null;
            }
        } else {
            // Simple filename - load from config/modtabs/icons/. The textureId must include
            // the filename so swapping to a different file for the same tab yields a fresh
            // DynamicTexture; a tabId-only key would re-serve the previously-cached image.
            String filePath = "config/modtabs/icons/" + trimmed;
            String safeName = trimmed.toLowerCase().replaceAll("[^a-z0-9_]", "_");
            Identifier texture = DynamicTextureLoader.loadTextureFromFile(
                    filePath, "tab_" + tabId + "__" + safeName);

            if (texture != null) {
                ModTabs.LOGGER.info("Resolved icon '" + iconConfig + "' as file texture for tab: " + tabId);
            } else {
                ModTabs.LOGGER.warn("Failed to load icon file: " + filePath + " for tab: " + tabId);
            }

            return texture;
        }
    }

    /**
     * Check if an icon config string is valid (not empty)
     */
    public static boolean hasCustomIcon(String iconConfig) {
        return iconConfig != null && !iconConfig.trim().isEmpty();
    }
}
