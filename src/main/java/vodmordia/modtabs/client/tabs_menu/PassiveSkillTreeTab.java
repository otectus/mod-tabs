package vodmordia.modtabs.client.tabs_menu;

//import com.illusivesoulworks.diet.client.screen.DietScreen;
//import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
//import daripher.skilltree.client.screen.SkillTreeScreen;
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


@TabConfig(configKey = "passiveSkillTreeTab", defaultEnabled = true, defaultOrder = 0)
public class PassiveSkillTreeTab extends SimpleTextureTab {
    private static final ResourceLocation SKILL_TREE_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/skill_tree.png");

    public PassiveSkillTreeTab() {
        super(SKILL_TREE_ICON);
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.passiveSkillTreeTabEnabled && player.level().isClientSide) {
            // Minecraft.getInstance().setScreen(new SkillTreeScreen(ResourceLocation.fromNamespaceAndPath("skilltree", "main_tree")));
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.passiveSkillTreeTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.PASSIVE_SKILL_TREE);
    }


    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return false;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.passive_skill_tree.description");
    }

    @Override
    public void initTabOnScreens() {
        // Passive Skill Tree doesn't have a dedicated screen that can be registered
        // It opens the skill tree GUI programmatically, so we don't register any screen
        // All screens will automatically have this tab through the new system
    }
}

