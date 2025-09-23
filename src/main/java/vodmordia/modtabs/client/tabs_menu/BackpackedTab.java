package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.CustomIconTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

@TabConfig(configKey = "backpackedTab", defaultEnabled = false, defaultOrder = 0)
public class BackpackedTab extends CustomIconTab {
    public BackpackedTab() {
        super((context) -> {
            // Use the backpack icon from tab_menu_buttons.png
            ResourceLocation tabIcons = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
            int texOffsetX = context.hover ? 54 : 0;
            context.gui.blit(tabIcons, context.x, context.y, 27 + texOffsetX, 46, 26, 30);
        });
    }

    @Override
    public void openTargetScreen(Player player) {
        // Backpacked integration temporarily disabled - mod is in active development
        /*if (Config.Baked.backpackTabEnabled && player.level().isClientSide && hasBackpack(player)) {
            Network.getPlay().sendToServer(new MessageOpenBackpack());
        }*/
    }

    @Override
    public boolean isEnabled(Player player) {
        // Backpacked integration temporarily disabled - mod is in active development
        return false && Config.Baked.backpackedTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.BACKPACKED);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        // Backpacked integration temporarily disabled - mod is in active development
        return false;
        //return currentScreen instanceof BackpackScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.backpack.description");
    }

    @Override
    public void initTabOnScreens() {
        // Backpacked integration temporarily disabled - mod is in active development
        // When enabled, this tab would appear on all screens through ScreenRegistry
        // For now, keep the tab disabled until Backpacked mod is updated
    }
}
