package vodmordia.modtabs;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.client.tabs_menu.*;
import vodmordia.modtabs.client.tabs_menu.AdvancementsTab;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.config.ModTabsConfig;
import eu.midnightdust.lib.config.MidnightConfig;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Mod(ModTabs.MOD_ID)
public class ModTabs
{
    public static final String MOD_ID = "modtabs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Path configPath = FMLPaths.CONFIGDIR.get();
    public static Path modConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "modtabs");

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

    public ModTabs(IEventBus modEventBus, ModContainer modContainer)
    {
        IEventBus neoForgeBus = NeoForge.EVENT_BUS;

        // Initialize MidnightConfig
        MidnightConfig.init(MOD_ID, ModTabsConfig.class);

        // Old NeoForge config system removed - using MidnightConfig only

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

    // Old config event handlers removed - MidnightConfig handles reloading automatically

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            Config.Baked.bakeClient();
            TabsMenu.register(new InventoryTab());

            if (ModTabs.backpackedLoaded)
                TabsMenu.register(new BackpackedTab());
            if (ModTabs.travelersBackpackLoaded)
                TabsMenu.register(new TravelersBackpackTab());
            if (ModTabs.legendarySurvivalOverhaulLoaded)
                TabsMenu.register(new BodyDamageTab());
            if (ModTabs.ftbQuestsLoaded) {
                LOGGER.info("Registering FtbQuestsTab");

                // Run inspection to find FTB Quests book item
                try {
                    Class<?> inspectorClass = Class.forName("vodmordia.modtabs.utils.FTBQuestsInspector");
                    Method inspectMethod = inspectorClass.getMethod("inspectModClasses");
                    inspectMethod.invoke(null);
                } catch (Exception e) {
                    LOGGER.warn("Failed to run FTB Quests inspection: " + e.getMessage());
                }

                TabsMenu.register(new FtbQuestsTab());
            }
            if (ModTabs.ftbTeamsLoaded) {
                LOGGER.info("Registering FtbTeamsTab");

                // Run inspection to find FTB Teams items
                try {
                    Class<?> inspectorClass = Class.forName("vodmordia.modtabs.utils.FTBTeamsInspector");
                    Method inspectMethod = inspectorClass.getMethod("inspectModClasses");
                    inspectMethod.invoke(null);
                } catch (Exception e) {
                    LOGGER.warn("Failed to run FTB Teams inspection: " + e.getMessage());
                }

                TabsMenu.register(new FtbTeamsTab());
            }
            if (ModTabs.reskillableLoaded)
                TabsMenu.register(new ReskillableTab());
            if (ModTabs.reskillableReimaginedLoaded)
                TabsMenu.register(new ReskillableReimaginedTab());
            if (ModTabs.mapAtlasesLoaded)
                TabsMenu.register(new MapAtlasesTab());
            if (ModTabs.xaerosMapLoaded)
                TabsMenu.register(new XaerosMapTab());
            if (ModTabs.journeyMapLoaded)
                TabsMenu.register(new JourneyMapTab());
            if (ModTabs.dietLoaded)
                TabsMenu.register(new DietTab());
            if (ModTabs.passiveSkillTreeLoaded)
                TabsMenu.register(new PassiveSkillTreeTab());
            if (ModTabs.pufferfishsSkillsLoaded)
                TabsMenu.register(new PufferfishsSkillsTab());
            if (ModTabs.l2HostilityLoaded)
                TabsMenu.register(new L2HostilityDifficultyTab());
            if (ModTabs.l2LibraryLoaded || ModTabs.l2ComplementsLoaded)
                TabsMenu.register(new L2AttributeTab());
            if (ModTabs.l2ArtifactsLoaded)
                TabsMenu.register(new L2ArtifactsTab());
            if (ModTabs.sophisticatedBackpacksLoaded)
                TabsMenu.register(new SophisticatedBackpacksTab());
            if (ModTabs.cosmeticArmorLoaded)
                TabsMenu.register(new CosmeticArmorTab());
            if (ModTabs.cobblemonLoaded)
                TabsMenu.register(new CobblemonTab());
            if (ModTabs.modularGolemsLoaded)
                TabsMenu.register(new ModularGolemsTab());
            if (ModTabs.arsElixirumLoaded) {
                LOGGER.info("Registering ArsElixirumTab");

                // Run inspection to find actual class names
                try {
                    Class<?> inspectorClass = Class.forName("vodmordia.modtabs.utils.ArsElixirumInspector");
                    Method inspectMethod = inspectorClass.getMethod("inspectModClasses");
                    inspectMethod.invoke(null);
                } catch (Exception e) {
                    LOGGER.warn("Failed to run Ars Elixirum inspection: " + e.getMessage());
                }

                TabsMenu.register(new ArsElixirumTab());
            } else {
                LOGGER.info("Ars Elixirum not loaded, skipping tab registration");
            }

            // Register vanilla advancements tab
            TabsMenu.register(new AdvancementsTab());

            // Finalize all pending screen registrations now that all tabs are registered
            TabsMenu.finalizePendingRegistrations();
        }
    }
}