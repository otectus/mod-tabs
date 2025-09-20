package vodmordia.modtabs.client.tabs_menu;

//import com.illusivesoulworks.diet.client.screen.DietScreen;
//import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
//import com.mrcrayfish.backpacked.network.Network;
//import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
//import com.mrcrayfish.backpacked.platform.Services;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
//import majik.rereskillable.client.screen.SkillScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
//import org.violetmoon.quark.addons.oddities.client.screen.BackpackInventoryScreen;
//import sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;


public class BackpackedTab extends TabBase {
    private final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 27;
    private final int TAB_ICON_TEX_Y = 46;

    public BackpackedTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        // Backpacked integration temporarily disabled - mod is in active development
        /*if (Config.Baked.backpackTabEnabled && player.level().isClientSide && hasBackpack(player)) {
            Network.getPlay().sendToServer(new MessageOpenBackpack());
        }*/
    }

    @Override
    public boolean isEnabled(Player player) {
        // Backpacked integration temporarily disabled - mod is in active development
        return false;
        //return Config.Baked.backpackTabEnabled && hasBackpack(player);
    }

    /*private boolean hasBackpack(Player player) {
        try {
            return Services.BACKPACK.isBackpackVisible(player);
        } catch (Exception e) {
            return false;
        }
    }*/

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        int texOffsetX = 0;
        if (hover)
            texOffsetX = 54;

        gui.blit(TAB_ICONS, x, y,TAB_ICON_TEX_X + texOffsetX, TAB_ICON_TEX_Y, TAB_WIDTH, TAB_HEIGHT);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        // Backpacked integration temporarily disabled - mod is in active development
        return false;
        //return currentScreen instanceof BackpackScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.backpack.description");
    }

    @Override
    public void initTabOnScreens() {
        TabsMenu.addTabToScreen(this, InventoryScreen.class, (player) -> 176, (player) -> 166, 20);

        // Disabled mods - commented out until they're updated to NeoForge 1.21.1
        // if (ModTabs.legendarySurvivalOverhaulLoaded)
        //     TabsMenu.addTabToScreen(this, BodyHealthScreen.class, (player) -> 176, (player) -> 183, 20);

        // if (ModTabs.reskillableLoaded)
        //     TabsMenu.addTabToScreen(this, SkillScreen.class, (player) -> 176, (player) -> 166, 20);

        // if (ModTabs.reskillableReimaginedLoaded)
        //     TabsMenu.addTabToScreen(this, net.bandit.reskillable.client.screen.SkillScreen.class, (player) -> 176, (player) -> 166, 20);

        if (ModTabs.curiosLoaded)
            TabsMenu.addTabToScreen(this, CuriosScreen.class, (player) -> 176, (player) -> 166, 20);

        // if (ModTabs.quarkOdditiesLoaded)
        //     TabsMenu.addTabToScreen(this, BackpackInventoryScreen.class, (player) -> 176, (player) -> 224, 20);

        if (ModTabs.cosmeticArmorLoaded)
            TabsMenu.addTabToScreen(this, GuiCosArmorInventory.class, (player) -> 176, (player) -> 166, 20);

        // Backpacked integration temporarily disabled - mod is in active development
        /*if (ModTabs.backpackedLoaded && Config.Baked.includeOpenedScreenTab)
            TabsMenu.addTabToScreen(this, BackpackScreen.class, IntegrationUtils::getBackpackWidth, IntegrationUtils::getBackpackHeight, 20);*/

        if (ModTabs.travelersBackpackLoaded)
            TabsMenu.addTabToScreen(this, com.tiviacz.travelersbackpack.client.screens.BackpackScreen.class, IntegrationUtils::getTravelersBackpackWidth, IntegrationUtils::getTravelersBackpackHeight, 20);

        // if (ModTabs.dietLoaded)
        //     TabsMenu.addTabToScreen(this, DietScreen.class, (player) -> 248, IntegrationUtils::getDietHeight, 20);
    }
}
