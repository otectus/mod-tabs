package sfiomn.legendarytabs.client.tabs_menu;

import com.illusivesoulworks.diet.client.screen.DietScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.platform.Services;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import majik.rereskillable.client.screen.SkillScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.violetmoon.quark.addons.oddities.client.screen.BackpackInventoryScreen;
import sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen;
import sfiomn.legendarytabs.LegendaryTabs;
import sfiomn.legendarytabs.api.tabs_menu.TabBase;
import sfiomn.legendarytabs.api.tabs_menu.TabsMenu;
import sfiomn.legendarytabs.config.Config;
import sfiomn.legendarytabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreenV2;


public class TravelersBackpackTab extends TabBase {
    private final ResourceLocation TAB_ICONS = new ResourceLocation(LegendaryTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 27;
    private final int TAB_ICON_TEX_Y = 46;

    public TravelersBackpackTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.travelersBackpackTabEnabled && CapabilityUtils.isWearingBackpack(player)) {
            ServerboundActionTagPacket.create(1, new Object[0]);
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.travelersBackpackTabEnabled && CapabilityUtils.isWearingBackpack(player);
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
        return currentScreen instanceof com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + LegendaryTabs.MOD_ID + ".tab.backpack.description");
    }

    @Override
    public void initTabOnScreens() {
        TabsMenu.addTabToScreen(this, InventoryScreen.class, (player) -> 176, (player) -> 166, 20);

        if (LegendaryTabs.legendarySurvivalOverhaulLoaded)
            TabsMenu.addTabToScreen(this, BodyHealthScreen.class, (player) -> 176, (player) -> 183, 20);

        if (LegendaryTabs.reskillableLoaded)
            TabsMenu.addTabToScreen(this, SkillScreen.class, (player) -> 176, (player) -> 166, 20);

        if (LegendaryTabs.reskillableReimaginedLoaded)
            TabsMenu.addTabToScreen(this, net.bandit.reskillable.client.screen.SkillScreen.class, (player) -> 176, (player) -> 166, 20);

        if (LegendaryTabs.curiosLoaded)
            TabsMenu.addTabToScreen(this, CuriosScreenV2.class, (player) -> 176, (player) -> 166, 20);

        if (LegendaryTabs.quarkOdditiesLoaded)
            TabsMenu.addTabToScreen(this, BackpackInventoryScreen.class, (player) -> 176, (player) -> 224, 20);

        if (LegendaryTabs.cosmeticArmorLoaded)
            TabsMenu.addTabToScreen(this, GuiCosArmorInventory.class, (player) -> 176, (player) -> 166, 20);

        if (LegendaryTabs.backpackedLoaded)
            TabsMenu.addTabToScreen(this, BackpackScreen.class, IntegrationUtils::getBackpackWidth, IntegrationUtils::getBackpackHeight, 20);

        if (LegendaryTabs.travelersBackpackLoaded && Config.Baked.includeOpenedScreenTab)
            TabsMenu.addTabToScreen(this, com.tiviacz.travelersbackpack.client.screens.BackpackScreen.class, IntegrationUtils::getTravelersBackpackWidth, IntegrationUtils::getTravelersBackpackHeight, 20);

        if (LegendaryTabs.dietLoaded)
            TabsMenu.addTabToScreen(this, DietScreen.class, (player) -> 248, IntegrationUtils::getDietHeight, 20);
    }
}
