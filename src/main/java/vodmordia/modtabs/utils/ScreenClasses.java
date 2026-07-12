package vodmordia.modtabs.utils;

/**
 * Fully-qualified class names for screens referenced by name rather than by class
 * literal. Values are {@code public static final String} so they remain valid as
 * {@code switch} case labels and as compile-time constants.
 */
public final class ScreenClasses {
    private ScreenClasses() {}

    // -- Vanilla ----------------------------------------------------------
    public static final String VANILLA_INVENTORY =
            "net.minecraft.client.gui.screens.inventory.InventoryScreen";
}
