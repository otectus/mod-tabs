package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.SimpleItemTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;
import vodmordia.modtabs.utils.FTBQuestsInspector;

@TabConfig(configKey = "ftbQuestsTab", defaultEnabled = true, defaultOrder = 0)
public class FtbQuestsTab extends SimpleItemTab {

    public FtbQuestsTab() {
        super(FtbQuestsTab::getQuestBookItem);
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.ftbQuestsTabEnabled && player.level().isClientSide) {
            try {
                Class<?> ftbQuestsClientClass = Class.forName("dev.ftb.mods.ftbquests.client.FTBQuestsClient");
                java.lang.reflect.Method openGuiMethod = ftbQuestsClientClass.getMethod("openGui");
                openGuiMethod.invoke(null);
            } catch (Exception e) {
                // FTB Quests not present or failed to open GUI
            }
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.ftbQuestsTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.FTB_QUESTS);
    }

    private static ItemStack getQuestBookItem() {
        Item bookItem = FTBQuestsInspector.tryGetBookItem();
        return new ItemStack(bookItem != null ? bookItem : Items.BOOK);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            // Always return false so this tab is never disabled - we want it visible on all screens including FTB Quests screens
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.ftb_quests.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register FTB Quests screen classes with inverted display at the top
        ScreenRegistry.builder()
            .withStandardDimensions()
            .inverted()
            .atTop()
            .registerAllTabs(
                "dev.ftb.mods.ftblibrary.ui.ScreenWrapper", // Main FTB Quests screen
                "dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen",
                "dev.ftb.mods.ftbquests.client.gui.QuestionScreen",
                "dev.ftb.mods.ftbquests.client.gui.QuestsScreen",
                "dev.ftb.mods.ftbquests.client.screens.QuestScreen",
                "dev.ftb.mods.ftbquests.client.QuestScreen"
            );

        // Force register ScreenWrapper to override any existing registration from FTB Teams
        ScreenRegistry.builder()
            .withStandardDimensions()
            .inverted()
            .atTop()
            .forceRegisterAllTabs("dev.ftb.mods.ftblibrary.ui.ScreenWrapper");
    }
}
