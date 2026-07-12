package vodmordia.modtabs.config;

public class Config
{
	// Old NeoForge config system completely removed - now using MidnightConfig only

	/**
	 * Single entry point for anything that mutates {@link ModTabsConfig} statics at
	 * runtime and needs the change reflected in hot paths: the MidnightLib config
	 * screen closing, and the layout editor's per-tab write helpers. Runtime code
	 * reads the {@link Baked} snapshot, never the MidnightConfig statics directly.
	 */
	public static void rebake()
	{
		Baked.bakeClient();
	}

	public static class Baked
	{
		// Global icon-offset & paging from the General tab of the in-game settings modal.
		public static int iconOffsetTop;
		public static int iconOffsetRight;
		public static int iconOffsetBottom;
		public static int iconOffsetLeft;

		// Tab enable/disable settings. inventoryTabEnabled is also read reflectively
		// (Baked.class.getField(key + "TabEnabled")) by GlobalSettingsPanel.
		public static boolean inventoryTabEnabled;
		public static boolean allowEditing;

		// Custom icon settings
		public static String inventoryTabCustomIcon;
		public static int inventoryTabIconScale;
		public static int inventoryTabIconNudgeUp;
		public static int inventoryTabIconNudgeDown;
		public static int inventoryTabIconNudgeLeft;
		public static int inventoryTabIconNudgeRight;

		// Nearby-container tabs (chests / barrels / etc. discovered around the player)
		public static boolean nearbyContainersTabEnabled;
		public static int nearbyContainersTabRange;
		public static boolean nearbyContainersTabRequireLineOfSight;

		// Tab display visibility settings
		public static TabDisplayVisibility inventoryTabDisplayVisibility;

		public static int inventoryTabOrder;

		public static void bakeClient()
		{
			iconOffsetTop = ModTabsConfig.iconOffsetTop;
			iconOffsetRight = ModTabsConfig.iconOffsetRight;
			iconOffsetBottom = ModTabsConfig.iconOffsetBottom;
			iconOffsetLeft = ModTabsConfig.iconOffsetLeft;

			inventoryTabEnabled = ModTabsConfig.inventoryTabEnabled;
			allowEditing = ModTabsConfig.allowEditing;

			inventoryTabCustomIcon = ModTabsConfig.inventoryTabCustomIcon;
			inventoryTabIconScale = ModTabsConfig.inventoryTabIconScale;
			inventoryTabIconNudgeUp = ModTabsConfig.inventoryTabIconNudgeUp;
			inventoryTabIconNudgeDown = ModTabsConfig.inventoryTabIconNudgeDown;
			inventoryTabIconNudgeLeft = ModTabsConfig.inventoryTabIconNudgeLeft;
			inventoryTabIconNudgeRight = ModTabsConfig.inventoryTabIconNudgeRight;

			nearbyContainersTabEnabled = ModTabsConfig.nearbyContainersTabEnabled;
			nearbyContainersTabRange = ModTabsConfig.nearbyContainersTabRange;
			nearbyContainersTabRequireLineOfSight = ModTabsConfig.nearbyContainersTabRequireLineOfSight;

			inventoryTabDisplayVisibility = ModTabsConfig.inventoryTabDisplayVisibility;
			inventoryTabOrder = ModTabsConfig.inventoryTabOrder;
		}
	}
}
