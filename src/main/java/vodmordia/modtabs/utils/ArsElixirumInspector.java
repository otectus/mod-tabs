package vodmordia.modtabs.utils;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import vodmordia.modtabs.ModTabs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ArsElixirumInspector {
    private static Item cachedCauldronItem = null;
    private static boolean searchAttempted = false;

    /**
     * Attempts to get the Glass Cauldron item via reflection (cached)
     */
    public static Item tryGetCauldronItem() {
        // Return cached result if already attempted
        if (searchAttempted) {
            return cachedCauldronItem;
        }

        searchAttempted = true;

        // Try possible package structures and item names
        String[] possiblePackages = {
            "dev.obscuria.elixirum.registry",
            "alexthw.elixirum.registry",
            "elixirum.registry",
            "alexthw.elixirum.init",
            "elixirum.init",
            "dev.obscuria.elixirum.init",
            "alexthw.ars_elixirum.registry",
            "ars_elixirum.registry"
        };

        String[] possibleClasses = {"ElixirumItems", "ModItems", "Items"};
        String[] possibleFields = {"GLASS_CAULDRON", "glass_cauldron", "CAULDRON", "cauldron"};

        for (String packageName : possiblePackages) {
            for (String className : possibleClasses) {
                for (String fieldName : possibleFields) {
                    try {
                        Class<?> itemsClass = Class.forName(packageName + "." + className);
                        Field itemField = itemsClass.getField(fieldName);
                        Object registryObject = itemField.get(null);

                        // Try to get item from registry object
                        Item item = extractItemFromRegistry(registryObject);
                        if (item != null) {
                            cachedCauldronItem = item;
                            ModTabs.LOGGER.info("Successfully found Ars Elixirum cauldron: {} from {}.{}.{}",
                                cachedCauldronItem, packageName, className, fieldName);
                            return cachedCauldronItem;
                        }
                    } catch (Exception e) {
                        // Continue trying other combinations
                    }
                }
            }
        }

        ModTabs.LOGGER.info("No Ars Elixirum cauldron item found via reflection, will use fallback");
        return null; // Will use brewing stand fallback
    }

    private static Item extractItemFromRegistry(Object registryObject) {
        try {
            // Direct item
            if (registryObject instanceof Item) {
                return (Item) registryObject;
            }

            // Try .get() method (common pattern)
            Method getMethod = registryObject.getClass().getMethod("get");
            Object result = getMethod.invoke(registryObject);
            if (result instanceof Item) {
                return (Item) result;
            }
        } catch (Exception e) {
            // Ignore and return null
        }
        return null;
    }

    public static void inspectModClasses() {
        ModTabs.LOGGER.info("=== ARS ELIXIRUM INSPECTION ===");

        // Try to find ANY class from the mod to understand the package structure
        String[] possibleRootPackages = {
            "alexthw.elixirum",
            "elixirum",
            "alexthw.ars_elixirum",
            "ars_elixirum",
            "net.elixirum",
            "com.elixirum",
            "dev.elixirum",
            "mod.elixirum",
            "io.elixirum"
        };

        for (String rootPackage : possibleRootPackages) {
            inspectPackage(rootPackage);
        }

        // Try alternative discovery methods
        tryAlternativeDiscovery();

        // Try using ModList to get mod info
        tryModListDiscovery();

        ModTabs.LOGGER.info("=== END INSPECTION ===");
    }

    private static void tryAlternativeDiscovery() {
        ModTabs.LOGGER.info("Trying alternative discovery methods...");

        // Try some common mod main class patterns
        String[] possibleMainClasses = {
            "elixirum.Elixirum",
            "elixirum.ElixirumMod",
            "elixirum.Main",
            "alexthw.elixirum.Elixirum",
            "alexthw.elixirum.ElixirumMod",
            "alexthw.elixirum.Main",
            "ars_elixirum.ArsElixirum",
            "ars_elixirum.Main",
            "alexthw.ars_elixirum.ArsElixirum",
            "alexthw.ars_elixirum.Main"
        };

        for (String className : possibleMainClasses) {
            try {
                Class<?> clazz = Class.forName(className);
                ModTabs.LOGGER.info("FOUND MAIN CLASS: {}", className);
                String packageName = clazz.getPackage().getName();
                ModTabs.LOGGER.info("Package: {}", packageName);

                // Now try to find related classes in this package
                tryPackageFromMainClass(packageName);
                return; // Found something, stop searching
            } catch (ClassNotFoundException e) {
                // Continue trying
            } catch (Exception e) {
                ModTabs.LOGGER.warn("Error checking class {}: {}", className, e.getMessage());
            }
        }

        ModTabs.LOGGER.warn("No Ars Elixirum classes found through alternative discovery");
    }

    private static void tryModListDiscovery() {
        ModTabs.LOGGER.info("Trying ModList discovery...");

        try {
            ModContainer modContainer = ModList.get().getModContainerById("elixirum").orElse(null);
            if (modContainer != null) {
                ModTabs.LOGGER.info("Found mod container for elixirum: {}", modContainer.getModId());
                ModTabs.LOGGER.info("Mod info: {}", modContainer.getModInfo().getDisplayName());

                // Since we already found the actual classes through JAR inspection,
                // we don't need the mod instance - just log that we found the container
                ModTabs.LOGGER.info("Mod container verification successful");
            } else {
                ModTabs.LOGGER.warn("No mod container found for 'elixirum'");
            }
        } catch (Exception e) {
            ModTabs.LOGGER.warn("Error during ModList discovery: {}", e.getMessage());
        }
    }

    private static void tryPackageFromMainClass(String basePackage) {
        ModTabs.LOGGER.info("Exploring package structure from: {}", basePackage);

        String[] subPackages = {
            basePackage + ".init",
            basePackage + ".registry",
            basePackage + ".content",
            basePackage + ".items",
            basePackage + ".client.gui",
            basePackage + ".client.screen",
            basePackage + ".gui",
            basePackage + ".screen"
        };

        for (String subPackage : subPackages) {
            String[] classNames = {
                subPackage + ".ModItems",
                subPackage + ".Items",
                subPackage + ".ElixirScreen",
                subPackage + ".ElixirGui",
                subPackage + ".ElixirContainer"
            };

            for (String className : classNames) {
                try {
                    Class<?> clazz = Class.forName(className);
                    ModTabs.LOGGER.info("FOUND CLASS: {}", className);

                    if (className.contains("Items")) {
                        inspectItemsClass(clazz);
                    }
                    if (className.contains("Screen") || className.contains("Gui")) {
                        inspectScreenClass(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // Expected for most attempts
                } catch (Exception e) {
                    ModTabs.LOGGER.warn("Error inspecting {}: {}", className, e.getMessage());
                }
            }
        }
    }

    private static void inspectPackage(String rootPackage) {
        ModTabs.LOGGER.info("Inspecting package: {}", rootPackage);

        // Common class locations to try
        String[] possibleClasses = {
            rootPackage + ".ArsElixirum",
            rootPackage + ".Elixirum",
            rootPackage + ".ElixirumMod",
            rootPackage + ".init.ModItems",
            rootPackage + ".registry.ModItems",
            rootPackage + ".content.ModItems",
            rootPackage + ".client.gui.ElixirScreen",
            rootPackage + ".client.screen.ElixirScreen",
            rootPackage + ".gui.ElixirScreen"
        };

        for (String className : possibleClasses) {
            try {
                Class<?> clazz = Class.forName(className);
                ModTabs.LOGGER.info("FOUND CLASS: {}", className);

                // If this is likely an items class, inspect its fields
                if (className.contains("Items")) {
                    inspectItemsClass(clazz);
                }

                // If this is likely a screen class, log it
                if (className.contains("Screen") || className.contains("Gui")) {
                    ModTabs.LOGGER.info("FOUND SCREEN CLASS: {}", className);
                    inspectScreenClass(clazz);
                }

            } catch (ClassNotFoundException e) {
                // This is expected for most attempts
            } catch (Exception e) {
                ModTabs.LOGGER.warn("Error inspecting class {}: {}", className, e.getMessage());
            }
        }
    }

    private static void inspectItemsClass(Class<?> itemsClass) {
        ModTabs.LOGGER.info("Inspecting items class: {}", itemsClass.getName());

        Field[] fields = itemsClass.getDeclaredFields();
        for (Field field : fields) {
            try {
                // Only look at public static fields
                if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                    Object value = field.get(null);
                    if (value instanceof Item || field.getType().getSimpleName().contains("Registry") ||
                        field.getType().getSimpleName().contains("Supplier")) {
                        ModTabs.LOGGER.info("  Found field: {} (type: {})", field.getName(), field.getType().getSimpleName());
                    }
                }
            } catch (Exception e) {
                // Skip problematic fields
            }
        }
    }

    private static void inspectScreenClass(Class<?> screenClass) {
        ModTabs.LOGGER.info("Inspecting screen class: {}", screenClass.getName());

        // Check if it extends Screen
        if (Screen.class.isAssignableFrom(screenClass)) {
            ModTabs.LOGGER.info("  This class extends Screen!");

            // Look at constructors
            Arrays.stream(screenClass.getDeclaredConstructors()).forEach(constructor -> {
                ModTabs.LOGGER.info("  Constructor with {} parameters: {}",
                    constructor.getParameterCount(),
                    Arrays.toString(constructor.getParameterTypes()));
            });
        }
    }
}
