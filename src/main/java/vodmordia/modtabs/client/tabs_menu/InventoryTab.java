package vodmordia.modtabs.client.tabs_menu;

//import com.illusivesoulworks.diet.client.screen.DietScreen;
//import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
//import majik.rereskillable.client.screen.SkillScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
//import org.violetmoon.quark.addons.oddities.client.screen.BackpackInventoryScreen;
import sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;


public class InventoryTab extends TabBase {
    private final ResourceLocation INVENTORY_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/inventory.png");

    public InventoryTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        InventoryScreen newGui = new InventoryScreen(player);
        Minecraft.getInstance().setScreen(newGui);
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.inventoryTabEnabled;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(INVENTORY_ICON, 5, 4, 16, 16)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(INVENTORY_ICON, 5, 4, 16, 16)
            .render(gui, x, y, hover, true);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        // InventoryTab should always be visible as it represents the "home" screen
        return false;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.inventory.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register the vanilla inventory screen and common mod screens with all tabs
        if (Config.Baked.includeOpenedScreenTab)
            TabsMenu.registerScreenWithAllTabs(InventoryScreen.class, (player) -> 176, (player) -> 166);

        if (ModTabs.legendarySurvivalOverhaulLoaded)
            TabsMenu.registerScreenWithAllTabs(BodyHealthScreen.class, (player) -> 176, (player) -> 183);

        // if (ModTabs.reskillableLoaded)
        //     TabsMenu.registerScreenWithAllTabs(SkillScreen.class, (player) -> 176, (player) -> 166);

        // if (ModTabs.reskillableReimaginedLoaded)
        //     TabsMenu.registerScreenWithAllTabs(net.bandit.reskillable.client.screen.SkillScreen.class, (player) -> 176, (player) -> 166);

        if (ModTabs.curiosLoaded)
            TabsMenu.registerScreenWithAllTabs(CuriosScreen.class, (player) -> 176, (player) -> 166);

        // if (ModTabs.quarkOdditiesLoaded)
        //     TabsMenu.registerScreenWithAllTabs(BackpackInventoryScreen.class, (player) -> 176, (player) -> 224);

        // Cosmetic Armor screen registration moved to CosmeticArmorTab.initTabOnScreens()

        // Backpacked integration temporarily disabled - mod is in active development
        /*if (ModTabs.backpackedLoaded)
            TabsMenu.registerScreenWithAllTabs(BackpackScreen.class, IntegrationUtils::getBackpackWidth, IntegrationUtils::getBackpackHeight);*/

        // Travelers Backpack screen registration moved to TravelersBackpackTab.initTabOnScreens()

        // if (ModTabs.dietLoaded)
        //     TabsMenu.registerScreenWithAllTabs(DietScreen.class, (player) -> 248, IntegrationUtils::getDietHeight);
    }
}
