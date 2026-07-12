package vodmordia.modtabs.utils;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import vodmordia.modtabs.ModTabs;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for dynamically loading PNG textures from the file system
 * and registering them with Minecraft's texture manager.
 */
public class DynamicTextureLoader {

    private static final Map<String, Identifier> loadedTextures = new HashMap<>();
    private static final String DYNAMIC_TEXTURE_PREFIX = "modtabs_dynamic";

    // Bumped whenever the cache is cleared (resource reload), so cached Identifier
    // holders (ConfigurableIconTab.lastResolved) know to re-resolve rather than keep
    // pointing at a released texture.
    private static int generation = 0;

    public static int generation() {
        return generation;
    }

    /**
     * The map and the TextureManager register/release calls are client-thread only —
     * a ConcurrentHashMap would paper over an ordering bug rather than fix one, so we
     * assert instead of synchronizing.
     */
    private static boolean wrongThread(String op) {
        if (Minecraft.getInstance().isSameThread()) return false;
        ModTabs.LOGGER.error("DynamicTextureLoader.{} called off the client thread", op, new Throwable());
        return true;
    }

    /**
     * Load a texture from a file path and register it with Minecraft's texture manager.
     * The path should be relative to the game directory (e.g., "config/modtabs/icons/my_icon.png")
     *
     * @param filePath The file path relative to the game directory
     * @param textureId A unique identifier for this texture
     * @return The Identifier of the loaded texture, or null if loading failed
     */
    public static Identifier loadTextureFromFile(String filePath, String textureId) {
        if (wrongThread("loadTextureFromFile")) return null;
        // Check if we've already loaded this texture
        if (loadedTextures.containsKey(textureId)) {
            return loadedTextures.get(textureId);
        }

        try {
            // Resolve the path relative to the game directory
            Path gameDir = Paths.get(".").toAbsolutePath().normalize();
            Path texturePath = gameDir.resolve(filePath).normalize();

            // Verify the file exists
            if (!Files.exists(texturePath)) {
                ModTabs.LOGGER.warn("Custom texture file not found: " + texturePath);
                return null;
            }

            // Verify the file is a PNG
            if (!filePath.toLowerCase().endsWith(".png")) {
                ModTabs.LOGGER.warn("Custom texture must be a PNG file: " + filePath);
                return null;
            }

            // Load the image using NativeImage
            NativeImage nativeImage;
            try (FileInputStream fis = new FileInputStream(texturePath.toFile())) {
                nativeImage = NativeImage.read(fis);
            }

            // Create a DynamicTexture from the NativeImage
            DynamicTexture dynamicTexture = new DynamicTexture(() -> "modtabs-dynamic-icon", nativeImage);

            // Create a unique Identifier for this texture
            // ResourceLocations must be lowercase, so convert textureId to lowercase
            Identifier textureLocation = Identifier.fromNamespaceAndPath(
                ModTabs.MOD_ID,
                DYNAMIC_TEXTURE_PREFIX + "/" + textureId.toLowerCase()
            );

            // Register the texture with Minecraft's texture manager
            Minecraft.getInstance().getTextureManager().register(textureLocation, dynamicTexture);

            // Cache the Identifier
            loadedTextures.put(textureId, textureLocation);

            ModTabs.LOGGER.info("Successfully loaded custom texture: " + filePath + " as " + textureLocation);
            return textureLocation;

        } catch (IOException e) {
            ModTabs.LOGGER.error("Failed to load custom texture from " + filePath + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Validate that a Identifier can be loaded from the resource pack system.
     * This is used for "customTexture" paths that reference textures in resource packs.
     *
     * @param textureLocation The Identifier to validate
     * @return true if the texture exists, false otherwise
     */
    public static boolean validateResourceTexture(Identifier textureLocation) {
        try {
            // Check if the texture exists in the resource manager
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.getResourceManager().getResource(textureLocation).isPresent()) {
                return true;
            } else {
                ModTabs.LOGGER.warn("Custom texture not found in resources: " + textureLocation);
                return false;
            }
        } catch (Exception e) {
            ModTabs.LOGGER.warn("Error validating custom texture: " + textureLocation + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Clear all loaded dynamic textures from memory.
     * This should be called when reloading custom tabs.
     */
    public static void clearLoadedTextures() {
        if (wrongThread("clearLoadedTextures")) return;
        for (Map.Entry<String, Identifier> entry : loadedTextures.entrySet()) {
            try {
                Minecraft.getInstance().getTextureManager().release(entry.getValue());
            } catch (Exception e) {
                ModTabs.LOGGER.warn("Failed to release texture: " + entry.getValue());
            }
        }
        loadedTextures.clear();
        generation++;
        ModTabs.LOGGER.info("Cleared all dynamically loaded textures");
    }

    /**
     * Release every texture loaded for one tab (keys are {@code "tab_<tabId>__<file>"},
     * see IconResolver). Called when a tab's custom-icon config changes so the superseded
     * file's GPU texture is freed instead of accumulating for the rest of the session.
     */
    public static void releaseTexturesForTab(String tabId) {
        if (wrongThread("releaseTexturesForTab")) return;
        String prefix = "tab_" + tabId + "__";
        var it = loadedTextures.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Identifier> entry = it.next();
            if (!entry.getKey().startsWith(prefix)) continue;
            try {
                Minecraft.getInstance().getTextureManager().release(entry.getValue());
            } catch (Exception e) {
                ModTabs.LOGGER.warn("Failed to release texture: " + entry.getValue());
            }
            it.remove();
        }
    }

    /**
     * Get the icons directory path for custom textures.
     * Creates the directory if it doesn't exist.
     *
     * @return The path to the icons directory
     */
    public static Path getIconsDirectory() {
        Path iconsPath = Paths.get(ModTabs.modConfigPath.toString(), "icons");
        try {
            if (!Files.exists(iconsPath)) {
                Files.createDirectories(iconsPath);
                ModTabs.LOGGER.info("Created custom icons directory: " + iconsPath);
            }
        } catch (IOException e) {
            ModTabs.LOGGER.error("Failed to create icons directory: " + e.getMessage());
        }
        return iconsPath;
    }
}
