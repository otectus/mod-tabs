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
//import sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;


public class DietTab extends TabBase {
    private final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 0;
    private final int TAB_ICON_TEX_Y = 69;

    public DietTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        // Commented out until Diet mod is updated to NeoForge 1.21.1
        //if (Config.Baked.dietTabEnabled && player.level().isClientSide) {
        //    Minecraft.getInstance().setScreen(new DietScreen(Minecraft.getInstance().screen instanceof InventoryScreen));
        //}
    }

    @Override
    public boolean isEnabled(Player player) {
        // Commented out until Diet mod is updated to NeoForge 1.21.1
        return false; //Config.Baked.dietTabEnabled;
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
        // Commented out until Diet mod is updated to NeoForge 1.21.1
        return false; //currentScreen instanceof DietScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.diet.description");
    }

    @Override
    public void initTabOnScreens() {
        // Diet mod is temporarily disabled - mod is not updated to NeoForge 1.21.1
        // All screens will automatically have this tab through the new system
        // Once Diet mod is updated, we can register the DietScreen here
    }
}
