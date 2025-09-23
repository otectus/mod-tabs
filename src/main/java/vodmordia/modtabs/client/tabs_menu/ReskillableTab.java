package vodmordia.modtabs.client.tabs_menu;

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

@TabConfig(configKey = "reskillableTab", defaultEnabled = true, defaultOrder = 0)
public class ReskillableTab extends SimpleTextureTab {
    private static final ResourceLocation RESKILLABLE_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/reskillable.png");

    public ReskillableTab() {
        super(RESKILLABLE_ICON);
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.reskillableTabEnabled && player.level().isClientSide && ModIntegrationManager.isModLoaded(ModIntegration.RESKILLABLE)) {
            try {
                Class<?> skillScreenClass = Class.forName("majik.rereskillable.client.screen.SkillScreen");
                Screen skillScreen = (Screen) skillScreenClass.getDeclaredConstructor().newInstance();
                Minecraft.getInstance().setScreen(skillScreen);
            } catch (Exception e) {
                // Reskillable mod not present or failed to open screen
            }
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.reskillableTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.RESKILLABLE);
    }


    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> skillScreenClass = Class.forName("majik.rereskillable.client.screen.SkillScreen");
            return skillScreenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.reskillable.description");
    }

    @Override
    public void initTabOnScreens() {
        try {
            Class<?> skillScreenClass = Class.forName("majik.rereskillable.client.screen.SkillScreen");
            @SuppressWarnings("unchecked")
            Class<? extends Screen> screenClass = (Class<? extends Screen>) skillScreenClass;
            ScreenRegistry.builder()
                .withStandardDimensions()
                .withPositioning(vodmordia.modtabs.api.tabs_menu.TabPositioning.GUI_RELATIVE)
                .registerAllTabs(screenClass);
        } catch (ClassNotFoundException e) {
            // Reskillable mod not present, skip registration
        }
    }
}

