package vodmordia.modtabs.client.tabs_menu;

import dev.ftb.mods.ftbteams.net.OpenGUIMessage;
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

@TabConfig(configKey = "ftbTeamsTab", defaultEnabled = true, defaultOrder = 0)
public class FtbTeamsTab extends SimpleTextureTab {
    private static final ResourceLocation TEAMS_TEXTURE = ResourceLocation.fromNamespaceAndPath("ftbteams", "textures/teams.png");

    public FtbTeamsTab() {
        super(TEAMS_TEXTURE);
    }

    @Override
    public void openTargetScreen(Player player) {
        if (ModIntegrationManager.isModLoaded(ModIntegration.FTB_TEAMS) && player.level().isClientSide) {
            (new OpenGUIMessage()).sendToServer();
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.ftbTeamsTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.FTB_TEAMS);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return false;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.ftb_teams.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register FTB Teams screen classes with inverted display at the top
        // FTB Teams uses the same ScreenWrapper as FTB Quests
        ScreenRegistry.builder()
            .withStandardDimensions()
            .inverted()
            .atTop()
            .registerAllTabs(
                "dev.ftb.mods.ftblibrary.ui.ScreenWrapper", // Main FTB Teams screen
                "dev.ftb.mods.ftbteams.client.gui.TeamsScreen",
                "dev.ftb.mods.ftbteams.client.screens.TeamsScreen",
                "dev.ftb.mods.ftbteams.client.TeamsScreen"
            );

        // Force register ScreenWrapper to override any existing registration from FTB Quests
        ScreenRegistry.builder()
            .withStandardDimensions()
            .inverted()
            .atTop()
            .forceRegisterAllTabs("dev.ftb.mods.ftblibrary.ui.ScreenWrapper");
    }
}
