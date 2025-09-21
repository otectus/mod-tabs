package vodmordia.modtabs.client.tabs_menu;

//import com.illusivesoulworks.diet.client.screen.DietScreen;
//import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import dev.ftb.mods.ftbquests.client.FTBQuestsClient;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
//import majik.rereskillable.client.screen.SkillScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
//import org.violetmoon.quark.addons.oddities.client.screen.BackpackInventoryScreen;
//import sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.api.tabs_menu.TabDisplayMode;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import vodmordia.modtabs.utils.FTBQuestsInspector;
import top.theillusivec4.curios.client.gui.CuriosScreen;

public class FtbQuestsTab extends TabBase {

    public FtbQuestsTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        if (ModTabs.ftbQuestsLoaded && player.level().isClientSide)
            FTBQuestsClient.openGui();
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.ftbQuestsTabEnabled && ModTabs.ftbQuestsLoaded;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(getQuestBookItem(), 5, 5)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(getQuestBookItem(), 5, 5)
            .render(gui, x, y, hover, true);
    }

    private ItemStack getQuestBookItem() {
        // Try to get FTB Quests book item via reflection using inspector
        try {
            Class<?> inspectorClass = Class.forName("vodmordia.modtabs.utils.FTBQuestsInspector");
            Method getBookMethod = inspectorClass.getMethod("tryGetBookItem");
            Item bookItem = (Item) getBookMethod.invoke(null);

            if (bookItem != null) {
                return new ItemStack(bookItem);
            }
        } catch (Exception e) {
            // Fall through to fallback
        }
        // Fallback to vanilla book
        return new ItemStack(Items.BOOK);
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
        // Try to register known FTB Quests screen classes with inverted display at the top
        String[] possibleScreenClasses = {
            "dev.ftb.mods.ftblibrary.ui.ScreenWrapper", // This is the actual FTB Quests screen!
            "dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen",
            "dev.ftb.mods.ftbquests.client.gui.QuestionScreen",
            "dev.ftb.mods.ftbquests.client.gui.QuestsScreen",
            "dev.ftb.mods.ftbquests.client.screens.QuestScreen",
            "dev.ftb.mods.ftbquests.client.QuestScreen"
        };

        for (String className : possibleScreenClasses) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Screen> screenClass = (Class<? extends Screen>) Class.forName(className);

                // Force register with inverted display at the top - override any existing registration
                // This is necessary because FTB Teams might have already registered ScreenWrapper with default settings
                if (className.equals("dev.ftb.mods.ftblibrary.ui.ScreenWrapper")) {
                    TabsMenu.forceRegisterScreenWithAllTabs(screenClass,
                        (player) -> 176, // Standard GUI width (same as Cobblemon and other screens)
                        (player) -> 166, // Standard GUI height (same as Cobblemon and other screens)
                        TabDisplayMode.INVERTED,
                        TabPositioning.SCREEN_TOP);
                } else {
                    TabsMenu.registerScreenWithAllTabs(screenClass,
                        (player) -> 176, // Standard GUI width (same as Cobblemon and other screens)
                        (player) -> 166, // Standard GUI height (same as Cobblemon and other screens)
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
