package sfiomn.legendarytabs;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import sfiomn.legendarytabs.api.tabs_menu.TabsMenu;
import sfiomn.legendarytabs.client.tabs_menu.*;
import sfiomn.legendarytabs.config.Config;

import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(LegendaryTabs.MOD_ID)
public class LegendaryTabs
{
    public static final String MOD_ID = "legendarytabs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Path configPath = FMLPaths.CONFIGDIR.get();
    public static Path modConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "legendarytabs");

    public static boolean backpackedLoaded = false;
    public static boolean legendarySurvivalOverhaulLoaded = false;
    public static boolean curiosLoaded = false;
    public static boolean reskillableLoaded = false;
    public static boolean reskillableReimaginedLoaded = false;
    public static boolean ftbQuestsLoaded = false;
    public static boolean ftbTeamsLoaded = false;
    public static boolean quarkOdditiesLoaded = false;
    public static boolean cosmeticArmorLoaded = false;

    public LegendaryTabs(FMLJavaModLoadingContext context)
    {
        IEventBus modBus = context.getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        modBus.addListener(this::onModConfigLoadEvent);
        modBus.addListener(this::onModConfigReloadEvent);

        Config.register(context);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        modIntegration(forgeBus);
    }

    private void modIntegration(IEventBus forgeBus)
    {
        backpackedLoaded = ModList.get().isLoaded("backpacked");
        curiosLoaded = ModList.get().isLoaded("curios");
        reskillableLoaded = ModList.get().isLoaded("rereskillable");
        reskillableReimaginedLoaded = ModList.get().isLoaded("reskillable");
        ftbQuestsLoaded = ModList.get().isLoaded("ftbquests");
        ftbTeamsLoaded = ModList.get().isLoaded("ftbteams");
        quarkOdditiesLoaded = ModList.get().isLoaded("quarkoddities");
        legendarySurvivalOverhaulLoaded = ModList.get().isLoaded("legendarysurvivaloverhaul");
        cosmeticArmorLoaded = ModList.get().isLoaded("cosmeticarmorreworked");

        if (backpackedLoaded)
            LOGGER.debug("Backpacked is loaded, enabling compatibility");

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
    }

    private void onModConfigLoadEvent(ModConfigEvent.Loading event)
    {
        final ModConfig config = event.getConfig();

        if (config.getSpec() == Config.CLIENT_SPEC)
            Config.Baked.bakeClient();
    }

    private void onModConfigReloadEvent(ModConfigEvent.Reloading event)
    {
        final ModConfig config = event.getConfig();

        // Since client config is not shared, we want it to update instantly whenever it's saved
        if (config.getSpec() == Config.CLIENT_SPEC)
            Config.Baked.bakeClient();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            Config.Baked.bakeClient();
            TabsMenu.register(new InventoryTab());

            if (LegendaryTabs.backpackedLoaded)
                TabsMenu.register(new BackpackTab());
            if (LegendaryTabs.legendarySurvivalOverhaulLoaded)
                TabsMenu.register(new BodyDamageTab());
            if (LegendaryTabs.ftbQuestsLoaded)
                TabsMenu.register(new FtbQuestsTab());
            if (LegendaryTabs.ftbTeamsLoaded)
                TabsMenu.register(new FtbTeamsTab());
            if (LegendaryTabs.reskillableLoaded)
                TabsMenu.register(new ReskillableTab());
            if (LegendaryTabs.reskillableReimaginedLoaded)
                TabsMenu.register(new ReskillableReimaginedTab());
        }
    }
}