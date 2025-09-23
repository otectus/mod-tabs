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
import vodmordia.modtabs.integration.ModIntegrationManager;
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


    public ModTabs(IEventBus modEventBus, ModContainer modContainer)
    {
        IEventBus neoForgeBus = NeoForge.EVENT_BUS;

        // Initialize MidnightConfig
        MidnightConfig.init(MOD_ID, ModTabsConfig.class);

        // Initialize mod integration manager
        ModIntegrationManager.detectLoadedMods();
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

            // Register all tabs - each tab's isEnabled() method handles mod detection via @TabConfig
            TabsMenu.register(new InventoryTab());
            TabsMenu.register(new BackpackedTab());
            TabsMenu.register(new TravelersBackpackTab());
            TabsMenu.register(new BodyDamageTab());

            // Run inspection for FTB Quests if needed
            try {
                Class<?> inspectorClass = Class.forName("vodmordia.modtabs.utils.FTBQuestsInspector");
                Method inspectMethod = inspectorClass.getMethod("inspect");
                inspectMethod.invoke(null);
            } catch (Exception e) {
                LOGGER.warn("Failed to run FTB Quests inspection: " + e.getMessage());
            }
            TabsMenu.register(new FtbQuestsTab());

            // Run inspection for FTB Teams if needed
            try {
                Class<?> inspectorClass = Class.forName("vodmordia.modtabs.utils.FTBTeamsInspector");
                Method inspectMethod = inspectorClass.getMethod("inspectModClasses");
                inspectMethod.invoke(null);
            } catch (Exception e) {
                LOGGER.warn("Failed to run FTB Teams inspection: " + e.getMessage());
            }
            TabsMenu.register(new FtbTeamsTab());

            TabsMenu.register(new ReskillableTab());
            TabsMenu.register(new ReskillableReimaginedTab());
            TabsMenu.register(new MapAtlasesTab());
            TabsMenu.register(new XaerosMapTab());
            TabsMenu.register(new JourneyMapTab());
            TabsMenu.register(new DietTab());
            TabsMenu.register(new PassiveSkillTreeTab());
            TabsMenu.register(new PufferfishsSkillsTab());
            TabsMenu.register(new L2HostilityDifficultyTab());
            TabsMenu.register(new L2AttributeTab());
            TabsMenu.register(new L2ArtifactsTab());
            TabsMenu.register(new SophisticatedBackpacksTab());
            TabsMenu.register(new CosmeticArmorTab());
            TabsMenu.register(new CobblemonTab());
            TabsMenu.register(new DraconicEvolutionTab());
            TabsMenu.register(new ModularGolemsTab());

            // Run inspection for Ars Elixirum if needed
            try {
                Class<?> inspectorClass = Class.forName("vodmordia.modtabs.utils.ArsElixirumInspector");
                Method inspectMethod = inspectorClass.getMethod("inspectModClasses");
                inspectMethod.invoke(null);
            } catch (Exception e) {
                LOGGER.warn("Failed to run Ars Elixirum inspection: " + e.getMessage());
            }
            TabsMenu.register(new ArsElixirumTab());

            TabsMenu.register(new ArsNouveauTab());
            TabsMenu.register(new AdvancementsTab());

            // Finalize all pending screen registrations now that all tabs are registered
            TabsMenu.finalizePendingRegistrations();
        }
    }
}