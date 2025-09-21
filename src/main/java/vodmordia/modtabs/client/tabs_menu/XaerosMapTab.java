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
import vodmordia.modtabs.api.tabs_menu.TabDisplayMode;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;
import xaero.map.WorldMapSession;
import xaero.map.gui.GuiMap;


public class XaerosMapTab extends TabBase {
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
        TabRenderer.builder()
            .withBackground()
            .withCustomIcon((context) -> {
                try {
                    context.gui.blit(XAEROS_WORLDMAP_ICON, context.x + 6, context.y + 5, 0, 0, 14, 14, 16, 16);
                } catch (Exception e) {
                    context.gui.fill(context.x + 7, context.y + 5, context.x + 19, context.y + 17, 0xFF8B4513);
                    context.gui.fill(context.x + 9, context.y + 7, context.x + 17, context.y + 15, 0xFF90EE90);
                }
            })
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withCustomIcon((context) -> {
                try {
                    context.gui.blit(XAEROS_WORLDMAP_ICON, context.x + 6, context.y + 5, 0, 0, 14, 14, 16, 16);
                } catch (Exception e) {
                    context.gui.fill(context.x + 7, context.y + 5, context.x + 19, context.y + 17, 0xFF8B4513);
                    context.gui.fill(context.x + 9, context.y + 7, context.x + 17, context.y + 15, 0xFF90EE90);
                }
            })
            .render(gui, x, y, hover, true);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> guiMapClass = Class.forName("xaero.map.gui.GuiMap");
            return guiMapClass.isInstance(currentScreen);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.xaeros_map.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register Xaero's World Map screen with custom bottom-left positioning
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Screen> guiMapClass = (Class<? extends Screen>) Class.forName("xaero.map.gui.GuiMap");

            // Register with custom positioning - bottom-left like Pufferfish but not inverted
            TabsMenu.registerScreenWithCustomPosition(guiMapClass,
                (player) -> 176, // Standard GUI width like other tabs
                (player) -> 166, // Standard GUI height like other tabs
                TabDisplayMode.NORMAL, // Normal display mode (not inverted)
                (screen) -> 16, // Left position - 16px offset from left edge
                (screen) -> screen.height); // Bottom position - TabButton will subtract TAB_HEIGHT for normal mode

        } catch (ClassNotFoundException e) {
            // Xaero's World Map mod not available
        }
    }
}
