package sfiomn.legendarytabs.client.tabs_menu;

import com.illusivesoulworks.diet.client.screen.DietScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import net.bandit.reskillable.client.screen.SkillScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.violetmoon.quark.addons.oddities.client.screen.BackpackInventoryScreen;
import sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen;
import sfiomn.legendarytabs.LegendaryTabs;
import sfiomn.legendarytabs.api.tabs_menu.TabBase;
import sfiomn.legendarytabs.api.tabs_menu.TabsMenu;
import sfiomn.legendarytabs.config.Config;
import sfiomn.legendarytabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreenV2;

public class ReskillableReimaginedTab extends TabBase {
    private final ResourceLocation TAB_ICONS = new ResourceLocation(LegendaryTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 27;
    private final int TAB_ICON_TEX_Y = 0;

    public ReskillableReimaginedTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        if (LegendaryTabs.reskillableReimaginedLoaded)
            Minecraft.getInstance().setScreen(new SkillScreen());
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.reskillableTabEnabled && LegendaryTabs.reskillableReimaginedLoaded;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        int texOffsetX = 0;
        if (hover)
            texOffsetX = 54;

        gui.blit(TAB_ICONS, x, y,TAB_ICON_TEX_X + texOffsetX, TAB_ICON_TEX_Y, TAB_WIDTH, TAB_HEIGHT);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return LegendaryTabs.reskillableReimaginedLoaded && currentScreen instanceof SkillScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + LegendaryTabs.MOD_ID + ".tab.reskillable_reimagined.description");
    }

    @Override
    public void initTabOnScreens() {
        TabsMenu.addTabToScreen(this, InventoryScreen.class, (player) -> 176, (player) -> 166, 30);

        if (LegendaryTabs.curiosLoaded)
            TabsMenu.addTabToScreen(this, CuriosScreenV2.class, (player) -> 176, (player) -> 166, 30);

        if (LegendaryTabs.legendarySurvivalOverhaulLoaded)
            TabsMenu.addTabToScreen(this, BodyHealthScreen.class, (player) -> 176, (player) -> 183, 30);

        if (LegendaryTabs.reskillableLoaded)
            TabsMenu.addTabToScreen(this, majik.rereskillable.client.screen.SkillScreen.class, (player) -> 176, (player) -> 166, 30);

        if (LegendaryTabs.reskillableReimaginedLoaded && Config.Baked.includeOpenedScreenTab)
            TabsMenu.addTabToScreen(this, SkillScreen.class, (player) -> 176, (player) -> 166, 30);

        if (LegendaryTabs.quarkOdditiesLoaded)
            TabsMenu.addTabToScreen(this, BackpackInventoryScreen.class, (player) -> 176, (player) -> 224, 30);

        if (LegendaryTabs.cosmeticArmorLoaded)
            TabsMenu.addTabToScreen(this, GuiCosArmorInventory.class, (player) -> 176, (player) -> 166, 30);

        if (LegendaryTabs.backpackedLoaded)
            TabsMenu.addTabToScreen(this, BackpackScreen.class, (IntegrationUtils::getBackpackWidth), (IntegrationUtils::getBackpackHeight), 30);

        if (LegendaryTabs.travelersBackpackLoaded)
            TabsMenu.addTabToScreen(this, com.tiviacz.travelersbackpack.client.screens.BackpackScreen.class, IntegrationUtils::getTravelersBackpackWidth, IntegrationUtils::getTravelersBackpackHeight, 30);

        if (LegendaryTabs.dietLoaded)
            TabsMenu.addTabToScreen(this, DietScreen.class, (player) -> 248, IntegrationUtils::getDietHeight, 30);
    }
}
