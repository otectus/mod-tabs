package sfiomn.legendarytabs.utils;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import sfiomn.legendarytabs.LegendaryTabs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ArsElixirumInspector {

    public static void inspectModClasses() {
        LegendaryTabs.LOGGER.info("=== ARS ELIXIRUM INSPECTION ===");

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

        LegendaryTabs.LOGGER.info("=== END INSPECTION ===");
    }

    private static void tryAlternativeDiscovery() {
        LegendaryTabs.LOGGER.info("Trying alternative discovery methods...");

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
                LegendaryTabs.LOGGER.info("FOUND MAIN CLASS: {}", className);
                String packageName = clazz.getPackage().getName();
                LegendaryTabs.LOGGER.info("Package: {}", packageName);

                // Now try to find related classes in this package
                tryPackageFromMainClass(packageName);
                return; // Found something, stop searching
            } catch (ClassNotFoundException e) {
                // Continue trying
            } catch (Exception e) {
                LegendaryTabs.LOGGER.warn("Error checking class {}: {}", className, e.getMessage());
            }
        }

        LegendaryTabs.LOGGER.warn("No Ars Elixirum classes found through alternative discovery");
    }

    private static void tryModListDiscovery() {
        LegendaryTabs.LOGGER.info("Trying ModList discovery...");

        try {
            ModContainer modContainer = ModList.get().getModContainerById("elixirum").orElse(null);
            if (modContainer != null) {
                LegendaryTabs.LOGGER.info("Found mod container for elixirum: {}", modContainer.getModId());
                LegendaryTabs.LOGGER.info("Mod info: {}", modContainer.getModInfo().getDisplayName());

                // Since we already found the actual classes through JAR inspection,
                // we don't need the mod instance - just log that we found the container
                LegendaryTabs.LOGGER.info("Mod container verification successful");
            } else {
                LegendaryTabs.LOGGER.warn("No mod container found for 'elixirum'");
            }
        } catch (Exception e) {
            LegendaryTabs.LOGGER.warn("Error during ModList discovery: {}", e.getMessage());
        }
    }

    private static void tryPackageFromMainClass(String basePackage) {
        LegendaryTabs.LOGGER.info("Exploring package structure from: {}", basePackage);

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
                    LegendaryTabs.LOGGER.info("FOUND CLASS: {}", className);

                    if (className.contains("Items")) {
                        inspectItemsClass(clazz);
                    }
                    if (className.contains("Screen") || className.contains("Gui")) {
                        inspectScreenClass(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // Expected for most attempts
                } catch (Exception e) {
                    LegendaryTabs.LOGGER.warn("Error inspecting {}: {}", className, e.getMessage());
                }
            }
        }
    }

    private static void inspectPackage(String rootPackage) {
        LegendaryTabs.LOGGER.info("Inspecting package: {}", rootPackage);

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
                LegendaryTabs.LOGGER.info("FOUND CLASS: {}", className);

                // If this is likely an items class, inspect its fields
                if (className.contains("Items")) {
                    inspectItemsClass(clazz);
                }

                // If this is likely a screen class, log it
                if (className.contains("Screen") || className.contains("Gui")) {
                    LegendaryTabs.LOGGER.info("FOUND SCREEN CLASS: {}", className);
                    inspectScreenClass(clazz);
                }

            } catch (ClassNotFoundException e) {
                // This is expected for most attempts
            } catch (Exception e) {
                LegendaryTabs.LOGGER.warn("Error inspecting class {}: {}", className, e.getMessage());
            }
        }
    }

    private static void inspectItemsClass(Class<?> itemsClass) {
        LegendaryTabs.LOGGER.info("Inspecting items class: {}", itemsClass.getName());

        Field[] fields = itemsClass.getDeclaredFields();
        for (Field field : fields) {
            try {
                // Only look at public static fields
                if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                    Object value = field.get(null);
                    if (value instanceof Item || field.getType().getSimpleName().contains("Registry") ||
                        field.getType().getSimpleName().contains("Supplier")) {
                        LegendaryTabs.LOGGER.info("  Found field: {} (type: {})", field.getName(), field.getType().getSimpleName());
                    }
                }
            } catch (Exception e) {
                // Skip problematic fields
            }
        }
    }

    private static void inspectScreenClass(Class<?> screenClass) {
        LegendaryTabs.LOGGER.info("Inspecting screen class: {}", screenClass.getName());

        // Check if it extends Screen
        if (Screen.class.isAssignableFrom(screenClass)) {
            LegendaryTabs.LOGGER.info("  This class extends Screen!");

            // Look at constructors
            Arrays.stream(screenClass.getDeclaredConstructors()).forEach(constructor -> {
                LegendaryTabs.LOGGER.info("  Constructor with {} parameters: {}",
                    constructor.getParameterCount(),
                    Arrays.toString(constructor.getParameterTypes()));
            });
        }
    }
}