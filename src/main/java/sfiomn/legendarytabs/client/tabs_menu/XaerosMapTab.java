package sfiomn.legendarytabs.client.tabs_menu;

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
import sfiomn.legendarytabs.LegendaryTabs;
import sfiomn.legendarytabs.api.tabs_menu.TabBase;
import sfiomn.legendarytabs.api.tabs_menu.TabsMenu;
import sfiomn.legendarytabs.config.Config;
import sfiomn.legendarytabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;
import xaero.map.WorldMapSession;
import xaero.map.gui.GuiMap;


public class XaerosMapTab extends TabBase {
    private final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(LegendaryTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 0; // Empty tab normal state
    private final int TAB_ICON_TEX_Y = 138; // Empty tab background in bottom row

    // Direct reference to Xaero's World Map icon
    private final ResourceLocation XAEROS_WORLDMAP_ICON = ResourceLocation.fromNamespaceAndPath("xaeroworldmap", "icon.png");

    public XaerosMapTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        Minecraft.getInstance().setScreen(new GuiMap((Screen)null, (Screen)null, WorldMapSession.getCurrentSession().getMapProcessor(), Minecraft.getInstance().getCameraEntity()));
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.xaerosMapTabEnabled;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        // Render tab background (empty tab style like L2 mods)
        int texOffsetX = 0;
        if (hover)
            texOffsetX = 54; // Hover state is at X=54
        gui.blit(TAB_ICONS, x, y, TAB_ICON_TEX_X + texOffsetX, TAB_ICON_TEX_Y, TAB_WIDTH, TAB_HEIGHT);

        // Render Xaero's World Map icon directly from the mod
        try {
            // Draw the icon at 14x14 size (2px smaller in each direction), moved up 1px
            gui.blit(XAEROS_WORLDMAP_ICON, x + 6, y + 5, 0, 0, 14, 14, 16, 16);
        } catch (Exception e) {
            // If icon loading fails, draw a fallback (map-like appearance)
            LegendaryTabs.LOGGER.debug("Failed to load Xaero's World Map icon, using fallback");
            // Draw a simple map-like colored rectangle as fallback
            gui.fill(x + 7, y + 5, x + 19, y + 17, 0xFF8B4513); // Brown background
            gui.fill(x + 9, y + 7, x + 17, y + 15, 0xFF90EE90); // Light green center
        }
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return false;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + LegendaryTabs.MOD_ID + ".tab.xaeros_map.description");
    }

    @Override
    public void initTabOnScreens() {
        TabsMenu.addTabToScreen(this, InventoryScreen.class, (player) -> 176, (player) -> 166, 75);

        // if (LegendaryTabs.legendarySurvivalOverhaulLoaded)
        //     TabsMenu.addTabToScreen(this, BodyHealthScreen.class, (player) -> 176, (player) -> 183, 75);

        // if (LegendaryTabs.reskillableLoaded)
        //     TabsMenu.addTabToScreen(this, SkillScreen.class, (player) -> 176, (player) -> 166, 75);

        // if (LegendaryTabs.reskillableReimaginedLoaded)
        //     TabsMenu.addTabToScreen(this, net.bandit.reskillable.client.screen.SkillScreen.class, (player) -> 176, (player) -> 166, 75);

        if (LegendaryTabs.curiosLoaded)
            TabsMenu.addTabToScreen(this, CuriosScreen.class, (player) -> 176, (player) -> 166, 75);

        // if (LegendaryTabs.quarkOdditiesLoaded)
        //     TabsMenu.addTabToScreen(this, BackpackInventoryScreen.class, (player) -> 176, (player) -> 224, 75);

        if (LegendaryTabs.cosmeticArmorLoaded)
            TabsMenu.addTabToScreen(this, GuiCosArmorInventory.class, (player) -> 176, (player) -> 166, 75);

        if (LegendaryTabs.backpackedLoaded)
            // Backpacked integration temporarily disabled - mod is in active development
            //TabsMenu.addTabToScreen(this, BackpackScreen.class, (IntegrationUtils::getBackpackWidth), (IntegrationUtils::getBackpackHeight), 75);

        if (LegendaryTabs.travelersBackpackLoaded)
            TabsMenu.addTabToScreen(this, com.tiviacz.travelersbackpack.client.screens.BackpackScreen.class, IntegrationUtils::getTravelersBackpackWidth, IntegrationUtils::getTravelersBackpackHeight, 75);

        // if (LegendaryTabs.dietLoaded)
        //     TabsMenu.addTabToScreen(this, DietScreen.class, (player) -> 248, IntegrationUtils::getDietHeight, 75);
    }
}
