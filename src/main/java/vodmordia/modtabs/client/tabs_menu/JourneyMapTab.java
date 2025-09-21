package vodmordia.modtabs.client.tabs_menu;

import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import journeymap.client.ui.UIManager;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;


public class JourneyMapTab extends TabBase {
    private final ResourceLocation JOURNEYMAP_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/journeymap.png");

    public JourneyMapTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        Minecraft.getInstance().setScreen(null);
        UIManager.INSTANCE.openFullscreenMap();
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.journeyMapTabEnabled;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(JOURNEYMAP_ICON, 5, 4, 16, 16)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(JOURNEYMAP_ICON, 5, 4, 16, 16)
            .render(gui, x, y, hover, true);
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
