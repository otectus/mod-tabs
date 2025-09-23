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

@TabConfig(configKey = "reskillableTab", defaultEnabled = true, defaultOrder = 0)
public class ReskillableTab extends SimpleTextureTab {
    private static final ResourceLocation RESKILLABLE_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/reskillable.png");

    public ReskillableTab() {
        super(RESKILLABLE_ICON);
    }

    @Override
    public void openTargetScreen(Player player) {
        if (ModIntegrationManager.isModLoaded(ModIntegration.RESKILLABLE)) {
            // Minecraft.getInstance().setScreen(new SkillScreen());
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.reskillableTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.RESKILLABLE);
    }


    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return false; // Reskillable mod doesn't have a screen class available for checking
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.reskillable.description");
    }

    @Override
    public void initTabOnScreens() {
        // Reskillable doesn't have a dedicated screen that can be registered
        // It opens the skill screen programmatically, so we don't register any screen
        // All screens will automatically have this tab through the new system
    }
}

