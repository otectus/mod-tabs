package vodmordia.modtabs;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.client.tabs_menu.InventoryTab;
import vodmordia.modtabs.client.tabs_menu.NearbyContainersProvider;
import vodmordia.modtabs.client.keybinds.ModKeybinds;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.config.ModTabsConfig;
import vodmordia.modtabs.integration.ModIntegrationManager;
import eu.midnightdust.lib.config.MidnightConfig;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Minimal 26.1.2 core port of Mod Tabs: only the tab framework and the vanilla
 * inventory/nearby-container tabs are kept. The ~45 third-party integration tabs,
 * the custom-JSON-tab subsystem, and the integration network packets from
 * upstream have been stripped.
 */
@Mod(ModTabs.MOD_ID)
public class ModTabs
{
    public static final String MOD_ID = "modtabs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Path configPath = FMLPaths.CONFIGDIR.get();
    public static Path modConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "modtabs");

    public ModTabs(IEventBus modEventBus, ModContainer modContainer, Dist dist)
    {
        // MidnightConfig.init is deferred to FMLCommonSetupEvent (enqueueWork) to avoid
        // a ConcurrentModificationException race with MidnightLib's own constructor —
        // NeoForge dispatches @Mod constructors in parallel, and MidnightConfig mutates
        // shared static LinkedHashMaps without synchronization.
        modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) ->
            event.enqueueWork(() -> MidnightConfig.init(MOD_ID, ModTabsConfig.class))
        );

        // Initialize mod integration manager (detects which optional mods are loaded)
        ModIntegrationManager.detectLoadedMods();

        // 26.1: the EventBusSubscriber.Bus enum was removed; register the client-only
        // mod-bus listeners manually instead of via the annotation.
        if (dist == Dist.CLIENT) {
            modEventBus.addListener(ClientModEvents::registerKeys);
            modEventBus.addListener(ClientModEvents::onClientSetup);
            modEventBus.addListener(ClientModEvents::onAddReloadListeners);
        }
    }

    public static class ClientModEvents
    {
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(ModKeybinds.TAB_CYCLE);
            event.register(ModKeybinds.TAB_CYCLE_BACK);
        }

        public static void onAddReloadListeners(net.neoforged.neoforge.client.event.AddClientReloadListenersEvent event) {
            // Custom-icon DynamicTextures are registered outside the resource system, so
            // F3+T doesn't refresh them on its own. Release them all on reload; the
            // generation bump makes ConfigurableIconTab re-resolve on its next render.
            event.addListener(
                    net.minecraft.resources.Identifier.fromNamespaceAndPath(MOD_ID, "dynamic_icon_textures"),
                    (net.minecraft.server.packs.resources.ResourceManagerReloadListener) manager ->
                            vodmordia.modtabs.utils.DynamicTextureLoader.clearLoadedTextures());
        }

        public static void onClientSetup(FMLClientSetupEvent event)
        {
            Config.Baked.bakeClient();

            // Initialize icons directory for custom tab icons
            vodmordia.modtabs.utils.DynamicTextureLoader.getIconsDirectory();

            // Home/inventory tab (appears on the inventory + vanilla container screens).
            // Third-party integration tabs (e.g. for backpack mods) are intentionally not
            // part of this minimal core port.
            TabsMenu.register(new InventoryTab());

            // One transient tab per nearby container block (chests, barrels, modded
            // inventories, …). Discovered fresh on every screen-init.
            TabsMenu.registerDynamicProvider(new NearbyContainersProvider());

            // Mount the tab row on the vanilla container screens too so the bar follows the
            // player when they click a nearby-chest tab.
            vodmordia.modtabs.api.tabs_menu.ScreenRegistry.registerStandardScreens(
                    net.minecraft.client.gui.screens.inventory.ContainerScreen.class,
                    net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen.class,
                    net.minecraft.client.gui.screens.inventory.DispenserScreen.class,
                    net.minecraft.client.gui.screens.inventory.HopperScreen.class
            );

            // Finalize all pending screen registrations now that all tabs are registered
            TabsMenu.finalizePendingRegistrations();
        }
    }
}
