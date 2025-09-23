package vodmordia.modtabs.client.tabs_menu;

import journeymap.client.ui.UIManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.SimpleTextureTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

@TabConfig(configKey = "journeyMapTab", defaultEnabled = true, defaultOrder = 0)
public class JourneyMapTab extends SimpleTextureTab {
    private static final ResourceLocation JOURNEYMAP_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/journeymap.png");

    public JourneyMapTab() {
        super(JOURNEYMAP_ICON);
    }

    @Override
    public void openTargetScreen(Player player) {
        Minecraft.getInstance().setScreen(null);
        UIManager.INSTANCE.openFullscreenMap();
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
