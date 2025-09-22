package vodmordia.modtabs.config;

import vodmordia.modtabs.ModTabs;

public class Config
{
	// Old NeoForge config system completely removed - now using MidnightConfig only

	public static class Baked
	{
		// Tab enable/disable settings
		public static boolean inventoryTabEnabled;
		public static boolean backpackTabEnabled;
		public static boolean travelersBackpackTabEnabled;
		public static boolean mapAtlasesTabEnabled;
		public static boolean xaerosMapTabEnabled;
		public static boolean journeyMapTabEnabled;
		public static boolean bodyDamageTabEnabled;
		public static boolean reskillableTabEnabled;
		public static boolean ftbQuestsTabEnabled;
		public static boolean ftbTeamsTabEnabled;
		public static boolean dietTabEnabled;
		public static boolean pufferfishSkillsTabEnabled;
		public static boolean passiveSkillTreeTabEnabled;
		public static boolean sophisticatedBackpacksTabEnabled;
		public static boolean cobblemonTabEnabled;
		public static boolean arsElixirumTabEnabled;
		public static boolean l2HostilityTabEnabled;
		public static boolean l2AttributesTabEnabled;
		public static boolean l2ArtifactsTabEnabled;
		public static boolean cosmeticArmorTabEnabled;
		public static boolean modularGolemsTabEnabled;
		public static boolean advancementsTabEnabled;
		public static boolean stickyInventoryTab;

		public static void bakeClient()
		{
			ModTabs.LOGGER.debug("Load Client configuration from MidnightConfig");
			try
			{
				// Read from MidnightConfig fields
				inventoryTabEnabled = ModTabsConfig.inventoryTabEnabled;
				backpackTabEnabled = ModTabsConfig.backpackTabEnabled;
				travelersBackpackTabEnabled = ModTabsConfig.travelersBackpackTabEnabled;
				bodyDamageTabEnabled = ModTabsConfig.bodyDamageTabEnabled;
				reskillableTabEnabled = ModTabsConfig.reskillableTabEnabled;
				ftbQuestsTabEnabled = ModTabsConfig.ftbQuestsTabEnabled;
				mapAtlasesTabEnabled = ModTabsConfig.mapAtlasesTabEnabled;
				xaerosMapTabEnabled = ModTabsConfig.xaerosMapTabEnabled;
				journeyMapTabEnabled = ModTabsConfig.journeyMapTabEnabled;
				ftbTeamsTabEnabled = ModTabsConfig.ftbTeamsTabEnabled;
				dietTabEnabled = ModTabsConfig.dietTabEnabled;
				pufferfishSkillsTabEnabled = ModTabsConfig.pufferfishSkillsTabEnabled;
				passiveSkillTreeTabEnabled = ModTabsConfig.passiveSkillTreeTabEnabled;
				sophisticatedBackpacksTabEnabled = ModTabsConfig.sophisticatedBackpacksTabEnabled;
				cobblemonTabEnabled = ModTabsConfig.cobblemonTabEnabled;
				arsElixirumTabEnabled = ModTabsConfig.arsElixirumTabEnabled;

				// Additional tabs not in original config
				l2HostilityTabEnabled = ModTabsConfig.l2HostilityTabEnabled;
				l2AttributesTabEnabled = ModTabsConfig.l2AttributesTabEnabled;
				l2ArtifactsTabEnabled = ModTabsConfig.l2ArtifactsTabEnabled;
				cosmeticArmorTabEnabled = ModTabsConfig.cosmeticArmorTabEnabled;
				modularGolemsTabEnabled = ModTabsConfig.modularGolemsTabEnabled;
				advancementsTabEnabled = ModTabsConfig.advancementsTabEnabled;
				stickyInventoryTab = ModTabsConfig.stickyInventoryTab;
			}
			catch (Exception e)
			{
				ModTabs.LOGGER.warn("An exception was caused trying to load the client config for Mod Tabs.");
				e.printStackTrace();
			}
		}
	}
}
