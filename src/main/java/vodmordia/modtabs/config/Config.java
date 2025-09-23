package vodmordia.modtabs.config;

import vodmordia.modtabs.ModTabs;

public class Config
{
	// Old NeoForge config system completely removed - now using MidnightConfig only

	public static class Baked
	{
		// Tab enable/disable settings
		public static boolean inventoryTabEnabled;
		public static boolean backpackedTabEnabled;
		public static boolean backpackTabEnabled;
		public static boolean travelersBackpackTabEnabled;
		public static boolean mapAtlasesTabEnabled;
		public static boolean xaerosMapTabEnabled;
		public static boolean journeyMapTabEnabled;
		public static boolean bodyDamageTabEnabled;
		public static boolean reskillableTabEnabled;
		public static boolean reskillableReimaginedTabEnabled;
		public static boolean ftbQuestsTabEnabled;
		public static boolean ftbTeamsTabEnabled;
		public static boolean dietTabEnabled;
		public static boolean pufferfishSkillsTabEnabled;
		public static boolean passiveSkillTreeTabEnabled;
		public static boolean sophisticatedBackpacksTabEnabled;
		public static boolean cobblemonTabEnabled;
		public static boolean arsElixirumTabEnabled;
		public static boolean arsNouveauTabEnabled;
		public static boolean l2HostilityTabEnabled;
		public static boolean l2AttributesTabEnabled;
		public static boolean l2ArtifactsTabEnabled;
		public static boolean cosmeticArmorTabEnabled;
		public static boolean modularGolemsTabEnabled;
		public static boolean advancementsTabEnabled;
		public static boolean draconicEvolutionTabEnabled;
		public static boolean stickyInventoryTab;

		// Tab order overrides
		public static int inventoryTabOrder;
		public static int backpackedTabOrder;
		public static int backpackTabOrder;
		public static int travelersBackpackTabOrder;
		public static int bodyDamageTabOrder;
		public static int reskillableTabOrder;
		public static int reskillableReimaginedTabOrder;
		public static int ftbQuestsTabOrder;
		public static int ftbTeamsTabOrder;
		public static int mapAtlasesTabOrder;
		public static int xaerosMapTabOrder;
		public static int journeyMapTabOrder;
		public static int dietTabOrder;
		public static int pufferfishSkillsTabOrder;
		public static int passiveSkillTreeTabOrder;
		public static int sophisticatedBackpacksTabOrder;
		public static int cobblemonTabOrder;
		public static int modularGolemsTabOrder;
		public static int arsElixirumTabOrder;
		public static int arsNouveauTabOrder;
		public static int l2HostilityTabOrder;
		public static int l2AttributesTabOrder;
		public static int l2ArtifactsTabOrder;
		public static int cosmeticArmorTabOrder;
		public static int advancementsTabOrder;
		public static int draconicEvolutionTabOrder;

		public static void bakeClient()
		{
			try
			{
				// Read from MidnightConfig fields
				inventoryTabEnabled = ModTabsConfig.inventoryTabEnabled;
				backpackedTabEnabled = ModTabsConfig.backpackedTabEnabled;
				backpackTabEnabled = ModTabsConfig.backpackTabEnabled;
				travelersBackpackTabEnabled = ModTabsConfig.travelersBackpackTabEnabled;
				bodyDamageTabEnabled = ModTabsConfig.bodyDamageTabEnabled;
				reskillableTabEnabled = ModTabsConfig.reskillableTabEnabled;
				reskillableReimaginedTabEnabled = ModTabsConfig.reskillableReimaginedTabEnabled;
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
				arsNouveauTabEnabled = ModTabsConfig.arsNouveauTabEnabled;

				// Additional tabs not in original config
				l2HostilityTabEnabled = ModTabsConfig.l2HostilityTabEnabled;
				l2AttributesTabEnabled = ModTabsConfig.l2AttributesTabEnabled;
				l2ArtifactsTabEnabled = ModTabsConfig.l2ArtifactsTabEnabled;
				cosmeticArmorTabEnabled = ModTabsConfig.cosmeticArmorTabEnabled;
				modularGolemsTabEnabled = ModTabsConfig.modularGolemsTabEnabled;
				advancementsTabEnabled = ModTabsConfig.advancementsTabEnabled;
				draconicEvolutionTabEnabled = ModTabsConfig.draconicEvolutionTabEnabled;
				stickyInventoryTab = ModTabsConfig.stickyInventoryTab;

				// Load tab order overrides
				inventoryTabOrder = ModTabsConfig.inventoryTabOrder;
				backpackedTabOrder = ModTabsConfig.backpackedTabOrder;
				backpackTabOrder = ModTabsConfig.backpackTabOrder;
				travelersBackpackTabOrder = ModTabsConfig.travelersBackpackTabOrder;
				bodyDamageTabOrder = ModTabsConfig.bodyDamageTabOrder;
				reskillableTabOrder = ModTabsConfig.reskillableTabOrder;
				reskillableReimaginedTabOrder = ModTabsConfig.reskillableReimaginedTabOrder;
				ftbQuestsTabOrder = ModTabsConfig.ftbQuestsTabOrder;
				ftbTeamsTabOrder = ModTabsConfig.ftbTeamsTabOrder;
				mapAtlasesTabOrder = ModTabsConfig.mapAtlasesTabOrder;
				xaerosMapTabOrder = ModTabsConfig.xaerosMapTabOrder;
				journeyMapTabOrder = ModTabsConfig.journeyMapTabOrder;
				dietTabOrder = ModTabsConfig.dietTabOrder;
				pufferfishSkillsTabOrder = ModTabsConfig.pufferfishSkillsTabOrder;
				passiveSkillTreeTabOrder = ModTabsConfig.passiveSkillTreeTabOrder;
				sophisticatedBackpacksTabOrder = ModTabsConfig.sophisticatedBackpacksTabOrder;
				cobblemonTabOrder = ModTabsConfig.cobblemonTabOrder;
				modularGolemsTabOrder = ModTabsConfig.modularGolemsTabOrder;
				arsElixirumTabOrder = ModTabsConfig.arsElixirumTabOrder;
				arsNouveauTabOrder = ModTabsConfig.arsNouveauTabOrder;
				l2HostilityTabOrder = ModTabsConfig.l2HostilityTabOrder;
				l2AttributesTabOrder = ModTabsConfig.l2AttributesTabOrder;
				l2ArtifactsTabOrder = ModTabsConfig.l2ArtifactsTabOrder;
				cosmeticArmorTabOrder = ModTabsConfig.cosmeticArmorTabOrder;
				advancementsTabOrder = ModTabsConfig.advancementsTabOrder;
				draconicEvolutionTabOrder = ModTabsConfig.draconicEvolutionTabOrder;
			}
			catch (Exception e)
			{
				ModTabs.LOGGER.warn("An exception was caused trying to load the client config for Mod Tabs.");
				e.printStackTrace();
			}
		}
	}
}
