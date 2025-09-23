package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.SimpleTextureTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.config.Config;

@TabConfig(configKey = "advancementsTab", defaultEnabled = true, defaultOrder = 0)
public class AdvancementsTab extends SimpleTextureTab {
    private static final ResourceLocation ADVANCEMENTS_ICON = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/advancements/challenge_frame_obtained.png");

    public AdvancementsTab() {
        super(ADVANCEMENTS_ICON);
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
        ScreenRegistry.registerInvertedScreens(AdvancementsScreen.class);
    }
}