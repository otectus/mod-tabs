package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabDisplayMode;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;

public class AdvancementsTab extends TabBase {
    private final ResourceLocation ADVANCEMENTS_ICON = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/advancements/challenge_frame_obtained.png");

    public AdvancementsTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        AdvancementsScreen newGui = new AdvancementsScreen(Minecraft.getInstance().getConnection().getAdvancements());
        Minecraft.getInstance().setScreen(newGui);
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.advancementsTabEnabled;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(ADVANCEMENTS_ICON, 5, 4, 16, 16)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(ADVANCEMENTS_ICON, 5, 4, 16, 16)
            .render(gui, x, y, hover, true);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return currentScreen instanceof AdvancementsScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.advancements.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register the advancements screen with tabs displayed inverted at the top
        TabsMenu.registerScreenWithAllTabs(AdvancementsScreen.class,
            (player) -> 176,
            (player) -> 166,
            TabDisplayMode.INVERTED,
            TabPositioning.SCREEN_TOP);
    }
}