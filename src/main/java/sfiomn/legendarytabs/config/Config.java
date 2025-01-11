package sfiomn.legendarytabs.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import sfiomn.legendarytabs.LegendaryTabs;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config
{
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;

	static
	{
		final Pair<Client, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = client.getRight();
		CLIENT = client.getLeft();
	}

	public static void register(FMLJavaModLoadingContext context)
	{
		Path configPath = LegendaryTabs.modConfigPath;

		try {
			Files.createDirectory(configPath);
		} catch (FileAlreadyExistsException ignored) {
		} catch (IOException e) {
			LegendaryTabs.LOGGER.error("Failed to create Legendary Tabs config directory " + configPath);
			e.printStackTrace();
		}


		context.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC, LegendaryTabs.MOD_ID + "/" + LegendaryTabs.MOD_ID +"-client.toml");
	}

	public static class Client
	{
		public final ForgeConfigSpec.BooleanValue inventoryTabEnabled;
		public final ForgeConfigSpec.BooleanValue backpackTabEnabled;
		public final ForgeConfigSpec.BooleanValue bodyDamageTabEnabled;
		public final ForgeConfigSpec.BooleanValue reskillableTabEnabled;
		public final ForgeConfigSpec.BooleanValue ftbQuestsTabEnabled;
		public final ForgeConfigSpec.BooleanValue ftbTeamsTabEnabled;

		public final ForgeConfigSpec.IntValue tabsMenuOffsetX;
		public final ForgeConfigSpec.IntValue tabsMenuOffsetY;

		Client(ForgeConfigSpec.Builder builder)
		{
			builder.push("tabs-menu").comment(" Configuration about the new tabs added on top of defined screens");
			tabsMenuOffsetX = builder
					.comment(" The X and Y offset of the tabs menu. Set both to 0 for no offset.", " By default, will be rendered above minecraft menus. Set it to 10000 to disable it completely.")
					.defineInRange("Season Cards Display X Offset", 2, -10000, 10000);
			tabsMenuOffsetY = builder
					.defineInRange("Season Cards Display Y Offset", 0, -10000, 10000);
			inventoryTabEnabled = builder
					.comment(" If enabled, show the inventory button in the tabs menu.")
					.define("Inventory Tab Enabled ", true);
			backpackTabEnabled = builder
					.comment(" If enabled, show the backpack button for Backpacked mod in the tabs menu.")
					.define("Backpack Tab Enabled ", true);
			bodyDamageTabEnabled = builder
					.comment(" If enabled, show the body damage button for Legendarysurvivaloverhaul in the tabs menu.")
					.define("Body Damage Tab Enabled ", true);
			reskillableTabEnabled = builder
					.comment(" If enabled, show the reskillable button for Rereskillable or Reskillable Reimagined in the tabs menu.")
					.define("Reskillable Tab Enabled ", true);
			ftbQuestsTabEnabled = builder
					.comment(" If enabled, show the ftb quests button in the tabs menu.")
					.define("FTB Quests Tab Enabled ", true);
			ftbTeamsTabEnabled = builder
					.comment(" If enabled, show the ftb teams button in the tabs menu.")
					.define("FTB Teams Tab Enabled ", true);
			builder.pop();
		}
	}

	public static class Server
	{
		Server(ForgeConfigSpec.Builder builder)
		{

		}
	}

	public static class Baked
	{
		// Tabs Menu
		public static int tabsMenuOffsetX;
		public static int tabsMenuOffsetY;

		public static boolean inventoryTabEnabled;
		public static boolean backpackTabEnabled;
		public static boolean bodyDamageTabEnabled;
		public static boolean reskillableTabEnabled;
		public static boolean ftbQuestsTabEnabled;
		public static boolean ftbTeamsTabEnabled;

		public static void bakeClient()
		{
			LegendaryTabs.LOGGER.debug("Load Client configuration from file");
			try
			{
				tabsMenuOffsetX = CLIENT.tabsMenuOffsetX.get();
				tabsMenuOffsetY = CLIENT.tabsMenuOffsetY.get();

				inventoryTabEnabled = CLIENT.inventoryTabEnabled.get();
				backpackTabEnabled = CLIENT.backpackTabEnabled.get();
				bodyDamageTabEnabled = CLIENT.bodyDamageTabEnabled.get();
				reskillableTabEnabled = CLIENT.reskillableTabEnabled.get();
				ftbQuestsTabEnabled = CLIENT.ftbQuestsTabEnabled.get();
				ftbTeamsTabEnabled = CLIENT.ftbTeamsTabEnabled.get();
			}
			catch (Exception e)
			{
				LegendaryTabs.LOGGER.warn("An exception was caused trying to load the client config for Legendary Survival Overhaul.");
				e.printStackTrace();
			}
		}
	}
}