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
import vodmordia.modtabs.api.tabs_menu.SimpleTextureTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;
import top.theillusivec4.curios.client.gui.CuriosScreen;


@TabConfig(configKey = "dietTab", defaultEnabled = false, defaultOrder = 0)
public class DietTab extends SimpleTextureTab {
    private static final ResourceLocation DIET_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/diet.png");

    public DietTab() {
        super(DIET_ICON);
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
        return false && Config.Baked.dietTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.DIET);
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
        // Once Diet mod is updated, we can register the DietScreen here:
        // ScreenRegistry.builder()
        //     .withDietDimensions()
        //     .withPositioning(TabPositioning.GUI_RELATIVE)
        //     .registerAllTabs("com.illusivesoulworks.diet.client.screen.DietScreen");
    }
}
