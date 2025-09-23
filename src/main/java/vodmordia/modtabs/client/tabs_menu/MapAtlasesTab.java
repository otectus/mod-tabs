package vodmordia.modtabs.client.tabs_menu;

//import com.illusivesoulworks.diet.client.screen.DietScreen;
//import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
//import majik.rereskillable.client.screen.SkillScreen;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//import org.violetmoon.quark.addons.oddities.client.screen.BackpackInventoryScreen;
import pepjebs.mapatlases.item.MapAtlasItem;
import pepjebs.mapatlases.networking.C2S2COpenAtlasScreenPacket;
import pepjebs.mapatlases.utils.MapAtlasesAccessUtils;
//import sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.SimpleTextureTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;
import top.theillusivec4.curios.client.gui.CuriosScreen;


@TabConfig(configKey = "mapAtlasesTab", defaultEnabled = true, defaultOrder = 0)
public class MapAtlasesTab extends SimpleTextureTab {
    private static final ResourceLocation MAP_ATLAS_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/map_atlas.png");

    public MapAtlasesTab() {
        super(MAP_ATLAS_ICON);
    }

    @Override
    public void openTargetScreen(Player player) {
        ItemStack atlas = MapAtlasesAccessUtils.getAtlasFromPlayerByConfig(player);
        if (atlas.getItem() instanceof MapAtlasItem) {
            NetworkHelper.sendToServer(new C2S2COpenAtlasScreenPacket());
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.mapAtlasesTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.MAP_ATLASES) && MapAtlasesAccessUtils.getAtlasFromPlayerByConfig(player).getItem() instanceof MapAtlasItem;
    }


    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return false;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.map_atlases.description");
    }

    @Override
    public void initTabOnScreens() {
        // Map Atlases doesn't have a dedicated screen that can be registered
        // It opens the atlas GUI programmatically, so we don't register any screen
        // All screens will automatically have this tab through the new system
    }
}
