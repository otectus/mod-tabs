package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.ConfigurableIconTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

@TabConfig(configKey = "journeyMapTab", defaultEnabled = true, defaultOrder = 0)
public class JourneyMapTab extends ConfigurableIconTab {
    private static final ResourceLocation JOURNEYMAP_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/journeymap.png");

    public JourneyMapTab() {
        super(JOURNEYMAP_ICON, Config.Baked.journeyMapTabCustomIcon, "journeyMap");
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.journeyMapTabEnabled && player.level().isClientSide) {
            try {
                Class<?> uiManagerClass = Class.forName("journeymap.client.ui.UIManager");
                java.lang.reflect.Field instanceField = uiManagerClass.getField("INSTANCE");
                Object uiManagerInstance = instanceField.get(null);
                java.lang.reflect.Method openFullscreenMapMethod = uiManagerClass.getMethod("openFullscreenMap");
                Minecraft.getInstance().setScreen(null);
                openFullscreenMapMethod.invoke(uiManagerInstance);
            } catch (Exception e) {
                // JourneyMap not present or failed to open map
            }
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.journeyMapTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.JOURNEY_MAP);
    }


    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return false;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.journey_map.description");
    }

    @Override
    public void initTabOnScreens() {
        // JourneyMap doesn't have a dedicated screen that can be registered
        // It opens the map programmatically, so we don't register any screen
        // All screens will automatically have this tab through the new system
    }
}
