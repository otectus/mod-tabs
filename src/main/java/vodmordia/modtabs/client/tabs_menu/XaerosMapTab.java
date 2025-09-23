package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.CustomIconTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.api.tabs_menu.TabDisplayMode;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;
import xaero.map.WorldMapSession;
import xaero.map.gui.GuiMap;

@TabConfig(configKey = "xaerosMapTab", defaultEnabled = true, defaultOrder = 0)
public class XaerosMapTab extends CustomIconTab {

    public XaerosMapTab() {
        super((context) -> {
            try {
                context.gui.blit(ResourceLocation.fromNamespaceAndPath("xaeroworldmap", "icon.png"),
                    context.x + 6, context.y + 5, 0, 0, 14, 14, 16, 16);
            } catch (Exception e) {
                context.gui.fill(context.x + 7, context.y + 5, context.x + 19, context.y + 17, 0xFF8B4513);
                context.gui.fill(context.x + 9, context.y + 7, context.x + 17, context.y + 15, 0xFF90EE90);
            }
        });
    }

    @Override
    public void openTargetScreen(Player player) {
        Minecraft.getInstance().setScreen(new GuiMap((Screen)null, (Screen)null, WorldMapSession.getCurrentSession().getMapProcessor(), Minecraft.getInstance().getCameraEntity()));
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.xaerosMapTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.XAEROS_MAP);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> guiMapClass = Class.forName("xaero.map.gui.GuiMap");
            return guiMapClass.isInstance(currentScreen);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.xaeros_map.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register Xaero's World Map screen with custom bottom-left positioning
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Screen> guiMapClass = (Class<? extends Screen>) Class.forName("xaero.map.gui.GuiMap");

            // Register with custom positioning - bottom-left positioning
            TabsMenu.registerScreenWithCustomPosition(guiMapClass,
                (player) -> 176, // Standard GUI width like other tabs
                (player) -> 166, // Standard GUI height like other tabs
                TabDisplayMode.NORMAL, // Normal display mode (not inverted)
                (screen) -> 16, // Left position - 16px offset from left edge
                (screen) -> screen.height); // Bottom position - TabButton will subtract TAB_HEIGHT for normal mode

        } catch (ClassNotFoundException e) {
            // Xaero's World Map mod not available
        }
    }
}
