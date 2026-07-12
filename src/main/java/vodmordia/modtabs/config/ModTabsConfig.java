package vodmordia.modtabs.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModTabsConfig extends MidnightConfig {

    public static class CommentText {}

    @Comment(category = "tabs") public static CommentText placement;

    // Edited via the in-game global-settings modal (General tab); hidden from the
    // MidnightConfig list to avoid duplicate UIs.
    @Entry(category = "tabs") @Hidden public static int iconOffsetTop = 0;
    @Entry(category = "tabs") @Hidden public static int iconOffsetRight = 0;
    @Entry(category = "tabs") @Hidden public static int iconOffsetBottom = 0;
    @Entry(category = "tabs") @Hidden public static int iconOffsetLeft = 0;

    @Comment(category = "tabs") public static CommentText spacer_placement;

    @Comment(category = "tabs") public static CommentText inventory;

    // --- Sticky tabs ---------------------------------------------------------
    // Sticky tabs are pinned to the leading (left) end of the bar and stay
    // visible across pagination. Any tab can be sticky; toggled in-game via the
    // Global Settings "Sticky" panel. Inventory is sticky by default. Hidden
    // from the MidnightConfig list since they're edited only through that panel.
    @Entry(category = "tabs") @Hidden public static boolean inventoryTabSticky = true;

    // Modpack-maker lock. When false, all three layout-editor entry points
    // (Shift+Z, long-press on a tab, the Edit button in LayoutEditorButtons) are
    // no-ops, so packs can ship a fixed tab layout that players can't rearrange.
    @Entry(category = "tabs")
    public static boolean allowEditing = true;

    @Entry(category = "tabs") @Hidden
    public static boolean inventoryTabEnabled = true;


    @Entry(category = "tabs") @Hidden
    public static String inventoryTabCustomIcon = "";
    @Entry(category = "tabs") @Hidden
    public static int inventoryTabIconScale = 100;
    @Entry(category = "tabs") @Hidden
    public static int inventoryTabIconNudgeUp = 0;
    @Entry(category = "tabs") @Hidden
    public static int inventoryTabIconNudgeDown = 0;
    @Entry(category = "tabs") @Hidden
    public static int inventoryTabIconNudgeLeft = 0;
    @Entry(category = "tabs") @Hidden
    public static int inventoryTabIconNudgeRight = 0;
    @Entry(category = "tabs") @Hidden
    public static int inventoryTabOrder = 0;

    @Entry(category = "tabs") @Hidden
    public static TabDisplayVisibility inventoryTabDisplayVisibility = TabDisplayVisibility.YES;

    @Comment(category = "tabs") public static CommentText spacerNearbyContainers;

    @Comment(category = "tabs") public static CommentText nearbyContainersHeader;

    @Entry(category = "tabs")
    public static boolean nearbyContainersTabEnabled = false;

    @Entry(category = "tabs", min = 1, max = 16)
    public static int nearbyContainersTabRange = 5;

    @Entry(category = "tabs")
    public static boolean nearbyContainersTabRequireLineOfSight = false;
}
