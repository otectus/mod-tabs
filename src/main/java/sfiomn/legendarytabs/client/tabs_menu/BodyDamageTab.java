package sfiomn.legendarytabs.client.tabs_menu;

//import com.illusivesoulworks.diet.client.screen.DietScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
//import majik.rereskillable.client.screen.SkillScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
//import org.violetmoon.quark.addons.oddities.client.screen.BackpackInventoryScreen;
import sfiomn.legendarysurvivaloverhaul.client.ClientHooks;
import sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen;
import sfiomn.legendarytabs.LegendaryTabs;
import sfiomn.legendarytabs.api.tabs_menu.TabBase;
import sfiomn.legendarytabs.api.tabs_menu.TabsMenu;
import sfiomn.legendarytabs.config.Config;
import sfiomn.legendarytabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;

import static sfiomn.legendarysurvivaloverhaul.config.Config.Baked.localizedBodyDamageEnabled;

public class BodyDamageTab extends TabBase {
    private final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(LegendaryTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 0;
    private final int TAB_ICON_TEX_Y = 23;

    public BodyDamageTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        if (LegendaryTabs.legendarySurvivalOverhaulLoaded && localizedBodyDamageEnabled)
            ClientHooks.openBodyHealthScreen(player);
    }

    @Override
    public boolean isEnabled(Player player) {
        return LegendaryTabs.legendarySurvivalOverhaulLoaded && Config.Baked.bodyDamageTabEnabled && localizedBodyDamageEnabled;
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
        return localizedBodyDamageEnabled && currentScreen instanceof BodyHealthScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + LegendaryTabs.MOD_ID + ".tab.body_damage.description");
    }

    @Override
    public void initTabOnScreens() {
        TabsMenu.addTabToScreen(this, InventoryScreen.class, (player) -> 176, (player) -> 166, 50);

        if (LegendaryTabs.curiosLoaded)
            TabsMenu.addTabToScreen(this, CuriosScreen.class, (player) -> 176, (player) -> 166, 50);

        if (LegendaryTabs.legendarySurvivalOverhaulLoaded && Config.Baked.includeOpenedScreenTab)
            TabsMenu.addTabToScreen(this, BodyHealthScreen.class, (player) -> 176, (player) -> 183, 50);

        //if (LegendaryTabs.reskillableLoaded)
        //    TabsMenu.addTabToScreen(this, SkillScreen.class, (player) -> 176, (player) -> 166, 50);

        // if (LegendaryTabs.reskillableReimaginedLoaded)
        //     TabsMenu.addTabToScreen(this, net.bandit.reskillable.client.screen.SkillScreen.class, (player) -> 176, (player) -> 166, 50);

        //if (LegendaryTabs.quarkOdditiesLoaded)
        //    TabsMenu.addTabToScreen(this, BackpackInventoryScreen.class, (player) -> 176, (player) -> 224, 50);

        if (LegendaryTabs.cosmeticArmorLoaded)
            TabsMenu.addTabToScreen(this, GuiCosArmorInventory.class, (player) -> 176, (player) -> 166, 50);

        if (LegendaryTabs.backpackedLoaded)
            TabsMenu.addTabToScreen(this, BackpackScreen.class, (IntegrationUtils::getBackpackWidth), (IntegrationUtils::getBackpackHeight), 50);

        if (LegendaryTabs.travelersBackpackLoaded)
            TabsMenu.addTabToScreen(this, com.tiviacz.travelersbackpack.client.screens.BackpackScreen.class, IntegrationUtils::getTravelersBackpackWidth, IntegrationUtils::getTravelersBackpackHeight, 50);

        //if (LegendaryTabs.dietLoaded)
        //    TabsMenu.addTabToScreen(this, DietScreen.class, (player) -> 248, IntegrationUtils::getDietHeight, 50);
    }
}
