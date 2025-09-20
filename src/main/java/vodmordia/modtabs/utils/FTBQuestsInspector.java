package vodmordia.modtabs.utils;

import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import vodmordia.modtabs.ModTabs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class FTBQuestsInspector {
    private static Item cachedBookItem = null;
    private static boolean searchAttempted = false;

    // Inspection methods removed - use documentation for debugging closed-source mods

    /**
     * Attempts to get the FTB Quests book item via reflection (cached)
     */
    public static Item tryGetBookItem() {
        // Return cached result if already attempted
        if (searchAttempted) {
            return cachedBookItem;
        }

        searchAttempted = true;

        try {
            // Get FTB Quests book item: dev.ftb.mods.ftbquests.registry.ModItems.BOOK
            Class<?> itemsClass = Class.forName("dev.ftb.mods.ftbquests.registry.ModItems");
            Field bookField = itemsClass.getField("BOOK");
            Object registrySupplier = bookField.get(null);

            // Call .get() method on Architectury's DeferredRegister$Entry
            Method getMethod = registrySupplier.getClass().getMethod("get");
            getMethod.setAccessible(true); // Bypass module access restrictions
            Object item = getMethod.invoke(registrySupplier);

            if (item instanceof Item) {
                cachedBookItem = (Item) item;
                return cachedBookItem;
            }
        } catch (Exception e) {
            // Silently fail and use fallback
        }
        return null; // Will use vanilla book fallback
    }
}
