package vodmordia.modtabs.client.tabs_menu;

import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import dev.ftb.mods.ftbteams.net.OpenGUIMessage;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
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
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;

public class FtbTeamsTab extends TabBase {

    public FtbTeamsTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        if (ModTabs.ftbTeamsLoaded && player.level().isClientSide) {
            (new OpenGUIMessage()).sendToServer();
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.ftbTeamsTabEnabled && ModTabs.ftbTeamsLoaded;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        ResourceLocation teamsTexture = ResourceLocation.fromNamespaceAndPath("ftbteams", "textures/teams.png");
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(teamsTexture, 5, 5, 16, 16)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        ResourceLocation teamsTexture = ResourceLocation.fromNamespaceAndPath("ftbteams", "textures/teams.png");
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(teamsTexture, 5, 5, 16, 16)
            .render(gui, x, y, hover, true);
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
        // Try to register FTB Teams screen classes with inverted display at the top
        // FTB Teams uses the same ScreenWrapper as FTB Quests, so we'll register the same class
        String[] possibleScreenClasses = {
            "dev.ftb.mods.ftblibrary.ui.ScreenWrapper", // This is the actual FTB Teams screen!
            "dev.ftb.mods.ftbteams.client.gui.TeamsScreen",
            "dev.ftb.mods.ftbteams.client.screens.TeamsScreen",
            "dev.ftb.mods.ftbteams.client.TeamsScreen"
        };

        for (String className : possibleScreenClasses) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Screen> screenClass = (Class<? extends Screen>) Class.forName(className);

                // Force register with inverted display at the top - override any existing registration
                // This is necessary because FTB Quests might have already registered ScreenWrapper with default settings
                if (className.equals("dev.ftb.mods.ftblibrary.ui.ScreenWrapper")) {
                    TabsMenu.forceRegisterScreenWithAllTabs(screenClass,
                        (player) -> 176, // Standard GUI width (same as FTB Quests and other screens)
                        (player) -> 166, // Standard GUI height (same as FTB Quests and other screens)
                        TabDisplayMode.INVERTED,
                        TabPositioning.SCREEN_TOP);
                } else {
                    TabsMenu.registerScreenWithAllTabs(screenClass,
                        (player) -> 176, // Standard GUI width (same as FTB Quests and other screens)
                        (player) -> 166, // Standard GUI height (same as FTB Quests and other screens)
                        TabDisplayMode.INVERTED,
                        TabPositioning.SCREEN_TOP);
                }

                // Since we found one working class, we can break or continue to register multiple
                // For now, let's continue to register all found screens
            } catch (ClassNotFoundException e) {
                // Screen class not found, try next one
            } catch (Exception e) {
                // Error registering screen
            }
        }
    }
}
