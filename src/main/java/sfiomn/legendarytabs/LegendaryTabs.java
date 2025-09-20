package sfiomn.legendarytabs;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import sfiomn.legendarytabs.api.tabs_menu.TabsMenu;
import sfiomn.legendarytabs.client.tabs_menu.*;
import sfiomn.legendarytabs.config.Config;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Mod(LegendaryTabs.MOD_ID)
public class LegendaryTabs
{
    public static final String MOD_ID = "legendarytabs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Path configPath = FMLPaths.CONFIGDIR.get();
    public static Path modConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "legendarytabs");

    public static boolean backpackedLoaded = false;
    public static boolean travelersBackpackLoaded = false;
    public static boolean legendarySurvivalOverhaulLoaded = false;
    public static boolean curiosLoaded = false;
    public static boolean reskillableLoaded = false;
    public static boolean reskillableReimaginedLoaded = false;
    public static boolean ftbQuestsLoaded = false;
    public static boolean ftbTeamsLoaded = false;
    public static boolean quarkOdditiesLoaded = false;
    public static boolean cosmeticArmorLoaded = false;
    public static boolean mapAtlasesLoaded = false;
    public static boolean xaerosMapLoaded = false;
    public static boolean journeyMapLoaded = false;
    public static boolean dietLoaded = false;
    public static boolean passiveSkillTreeLoaded = false;
    public static boolean pufferfishsSkillsLoaded = false;
    public static boolean l2HostilityLoaded = false;
    public static boolean l2LibraryLoaded = false;
    public static boolean l2ComplementsLoaded = false;
    public static boolean l2ArtifactsLoaded = false;
    public static boolean sophisticatedBackpacksLoaded = false;
    public static boolean cobblemonLoaded = false;
    public static boolean modularGolemsLoaded = false;
    public static boolean arsElixirumLoaded = false;

    public LegendaryTabs(IEventBus modEventBus, ModContainer modContainer)
    {
        IEventBus neoForgeBus = NeoForge.EVENT_BUS;

        modEventBus.addListener(this::onModConfigLoadEvent);
        modEventBus.addListener(this::onModConfigReloadEvent);

        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC, "legendarytabs-client.toml");

        modIntegration(neoForgeBus);
    }

    private void modIntegration(IEventBus neoForgeBus)
    {
        backpackedLoaded = ModList.get().isLoaded("backpacked");
        travelersBackpackLoaded = ModList.get().isLoaded("travelersbackpack");
        curiosLoaded = ModList.get().isLoaded("curios");
        reskillableLoaded = ModList.get().isLoaded("rereskillable");
        reskillableReimaginedLoaded = ModList.get().isLoaded("reskillable");
        ftbQuestsLoaded = ModList.get().isLoaded("ftbquests");
        ftbTeamsLoaded = ModList.get().isLoaded("ftbteams");
        quarkOdditiesLoaded = ModList.get().isLoaded("quarkoddities");
        legendarySurvivalOverhaulLoaded = ModList.get().isLoaded("legendarysurvivaloverhaul");
        cosmeticArmorLoaded = ModList.get().isLoaded("cosmeticarmorreworked");
        mapAtlasesLoaded = ModList.get().isLoaded("map_atlases");
        xaerosMapLoaded = ModList.get().isLoaded("xaeroworldmap");
        journeyMapLoaded = ModList.get().isLoaded("journeymap");
        dietLoaded = ModList.get().isLoaded("diet");
        passiveSkillTreeLoaded = ModList.get().isLoaded("skilltree");
        pufferfishsSkillsLoaded = ModList.get().isLoaded("puffish_skills");
        l2HostilityLoaded = ModList.get().isLoaded("l2hostility");
        l2LibraryLoaded = ModList.get().isLoaded("l2library");
        l2ComplementsLoaded = ModList.get().isLoaded("l2complements");
        l2ArtifactsLoaded = ModList.get().isLoaded("l2artifacts");
        sophisticatedBackpacksLoaded = ModList.get().isLoaded("sophisticatedbackpacks");
        cobblemonLoaded = ModList.get().isLoaded("cobblemon");
        modularGolemsLoaded = ModList.get().isLoaded("modulargolems");
        arsElixirumLoaded = ModList.get().isLoaded("elixirum");
        LOGGER.info("Mod detection - Ars Elixirum loaded: {}", arsElixirumLoaded);

        if (backpackedLoaded)
            LOGGER.debug("Backpacked is loaded, enabling compatibility");

        if (travelersBackpackLoaded)
            LOGGER.debug("Travelers Backpack is loaded, enabling compatibility");

        if (reskillableLoaded)
            LOGGER.debug("Rereskillable is loaded, enabling compatibility");

        if (reskillableReimaginedLoaded)
            LOGGER.debug("Reskillable Reimagined is loaded, enabling compatibility");

        if (ftbQuestsLoaded)
            LOGGER.debug("FTB Quests is loaded, enabling compatibility");

        if (ftbTeamsLoaded)
            LOGGER.debug("FTB Teams is loaded, enabling compatibility");

        if (curiosLoaded)
            LOGGER.debug("Curios is loaded, enabling compatibility");

        if (quarkOdditiesLoaded)
            LOGGER.debug("Quark Oddities is loaded, enabling compatibility");

        if (legendarySurvivalOverhaulLoaded)
            LOGGER.debug("Legendary Survival Overhaul is loaded, enabling compatibility");

        if (cosmeticArmorLoaded)
            LOGGER.debug("Cosmetic Armor is loaded, enabling compatibility");

        if (mapAtlasesLoaded)
            LOGGER.debug("Map Atlases is loaded, enabling compatibility");

        if (xaerosMapLoaded)
            LOGGER.debug("Xaero's Map is loaded, enabling compatibility");

        if (journeyMapLoaded)
            LOGGER.debug("Journey Map is loaded, enabling compatibility");

        if (dietLoaded)
            LOGGER.debug("Diet is loaded, enabling compatibility");

        if (passiveSkillTreeLoaded)
            LOGGER.debug("Passive Skill Tree is loaded, enabling compatibility");

        if (pufferfishsSkillsLoaded)
            LOGGER.debug("Pufferfish's Skills is loaded, enabling compatibility");

        if (l2HostilityLoaded)
            LOGGER.debug("L2 Hostility is loaded, enabling compatibility");

        if (l2LibraryLoaded)
            LOGGER.debug("L2 Library is loaded, enabling compatibility");

        if (l2ComplementsLoaded)
            LOGGER.debug("L2 Complements is loaded, enabling compatibility");

        if (l2ArtifactsLoaded)
            LOGGER.debug("L2 Artifacts is loaded, enabling compatibility");

        if (sophisticatedBackpacksLoaded)
            LOGGER.debug("Sophisticated Backpacks is loaded, enabling compatibility");

        if (cobblemonLoaded)
            LOGGER.debug("Cobblemon is loaded, enabling compatibility");

        if (modularGolemsLoaded)
            LOGGER.debug("Modular Golems is loaded, enabling compatibility");

        if (arsElixirumLoaded) {
            LOGGER.debug("Ars Elixirum is loaded, enabling compatibility");
        }

    }

    @SubscribeEvent
    private void onModConfigLoadEvent(ModConfigEvent.Loading event)
    {
        final ModConfig config = event.getConfig();

        if (config.getSpec() == Config.CLIENT_SPEC)
            Config.Baked.bakeClient();
    }

    @SubscribeEvent
    private void onModConfigReloadEvent(ModConfigEvent.Reloading event)
    {
        final ModConfig config = event.getConfig();

        // Since client config is not shared, we want it to update instantly whenever it's saved
        if (config.getSpec() == Config.CLIENT_SPEC)
            Config.Baked.bakeClient();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            Config.Baked.bakeClient();
            TabsMenu.register(new InventoryTab());

            if (LegendaryTabs.backpackedLoaded)
                TabsMenu.register(new BackpackedTab());
            if (LegendaryTabs.travelersBackpackLoaded)
                TabsMenu.register(new TravelersBackpackTab());
            if (LegendaryTabs.legendarySurvivalOverhaulLoaded)
                TabsMenu.register(new BodyDamageTab());
            if (LegendaryTabs.ftbQuestsLoaded) {
                LOGGER.info("Registering FtbQuestsTab");

                // Run inspection to find FTB Quests book item
                try {
                    Class<?> inspectorClass = Class.forName("sfiomn.legendarytabs.utils.FTBQuestsInspector");
                    Method inspectMethod = inspectorClass.getMethod("inspectModClasses");
                    inspectMethod.invoke(null);
                } catch (Exception e) {
                    LOGGER.warn("Failed to run FTB Quests inspection: " + e.getMessage());
                }

                TabsMenu.register(new FtbQuestsTab());
            }
            if (LegendaryTabs.ftbTeamsLoaded) {
                LOGGER.info("Registering FtbTeamsTab");

                // Run inspection to find FTB Teams items
                try {
                    Class<?> inspectorClass = Class.forName("sfiomn.legendarytabs.utils.FTBTeamsInspector");
                    Method inspectMethod = inspectorClass.getMethod("inspectModClasses");
                    inspectMethod.invoke(null);
                } catch (Exception e) {
                    LOGGER.warn("Failed to run FTB Teams inspection: " + e.getMessage());
                }

                TabsMenu.register(new FtbTeamsTab());
            }
            if (LegendaryTabs.reskillableLoaded)
                TabsMenu.register(new ReskillableTab());
            if (LegendaryTabs.reskillableReimaginedLoaded)
                TabsMenu.register(new ReskillableReimaginedTab());
            if (LegendaryTabs.mapAtlasesLoaded)
                TabsMenu.register(new MapAtlasesTab());
            if (LegendaryTabs.xaerosMapLoaded)
                TabsMenu.register(new XaerosMapTab());
            if (LegendaryTabs.journeyMapLoaded)
                TabsMenu.register(new JourneyMapTab());
            if (LegendaryTabs.dietLoaded)
                TabsMenu.register(new DietTab());
            if (LegendaryTabs.passiveSkillTreeLoaded)
                TabsMenu.register(new PassiveSkillTreeTab());
            if (LegendaryTabs.pufferfishsSkillsLoaded)
                TabsMenu.register(new PufferfishsSkillsTab());
            if (LegendaryTabs.l2HostilityLoaded)
                TabsMenu.register(new L2HostilityDifficultyTab());
            if (LegendaryTabs.l2LibraryLoaded || LegendaryTabs.l2ComplementsLoaded)
                TabsMenu.register(new L2AttributeTab());
            if (LegendaryTabs.l2ArtifactsLoaded)
                TabsMenu.register(new L2ArtifactsTab());
            if (LegendaryTabs.sophisticatedBackpacksLoaded)
                TabsMenu.register(new SophisticatedBackpacksTab());
            if (LegendaryTabs.cobblemonLoaded)
                TabsMenu.register(new CobblemonTab());
            if (LegendaryTabs.modularGolemsLoaded)
                TabsMenu.register(new ModularGolemsTab());
            if (LegendaryTabs.arsElixirumLoaded) {
                LOGGER.info("Registering ArsElixirumTab");

                // Run inspection to find actual class names
                try {
                    Class<?> inspectorClass = Class.forName("sfiomn.legendarytabs.utils.ArsElixirumInspector");
                    Method inspectMethod = inspectorClass.getMethod("inspectModClasses");
                    inspectMethod.invoke(null);
                } catch (Exception e) {
                    LOGGER.warn("Failed to run Ars Elixirum inspection: " + e.getMessage());
                }

                TabsMenu.register(new ArsElixirumTab());
            } else {
                LOGGER.info("Ars Elixirum not loaded, skipping tab registration");
            }
        }
    }
}
