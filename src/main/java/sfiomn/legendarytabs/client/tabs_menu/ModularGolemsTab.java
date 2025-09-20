package sfiomn.legendarytabs.client.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import sfiomn.legendarytabs.LegendaryTabs;
import sfiomn.legendarytabs.api.tabs_menu.TabBase;
import sfiomn.legendarytabs.api.tabs_menu.TabsMenu;
import sfiomn.legendarytabs.config.Config;
import top.theillusivec4.curios.client.gui.CuriosScreen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ModularGolemsTab extends TabBase {
    private final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(LegendaryTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 0; // Empty tab normal state
    private final int TAB_ICON_TEX_Y = 138; // Empty tab background in bottom row

    public ModularGolemsTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        try {
            // Use reflection to access TrackerTab.Type.ALIVE.createScreen()
            Class<?> trackerTabClass = Class.forName("dev.xkmc.modulargolems.content.client.tracker.TrackerTab");
            Class<?> typeEnum = Class.forName("dev.xkmc.modulargolems.content.client.tracker.TrackerTab$Type");

            // Get the ALIVE enum constant
            Object aliveType = null;
            for (Object enumConstant : typeEnum.getEnumConstants()) {
                if (enumConstant.toString().equals("ALIVE")) {
                    aliveType = enumConstant;
                    break;
                }
            }

            if (aliveType != null) {
                // Call createScreen() method on the ALIVE enum constant
                Method createScreenMethod = typeEnum.getMethod("createScreen");
                Object screen = createScreenMethod.invoke(aliveType);

                // Set the screen and let Minecraft handle initialization
                Minecraft.getInstance().setScreen((Screen) screen);
                return;
            }

            LegendaryTabs.LOGGER.error("Could not find ALIVE type in TrackerTab.Type enum");

        } catch (Exception e) {
            LegendaryTabs.LOGGER.error("Failed to open Modular Golems screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.inventoryTabEnabled; // Use a generic config for now
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        // Render tab background
        int texOffsetX = 0;
        if (hover)
            texOffsetX = 54; // Hover state is at X=54
        gui.blit(TAB_ICONS, x, y, TAB_ICON_TEX_X + texOffsetX, TAB_ICON_TEX_Y, TAB_WIDTH, TAB_HEIGHT);

        // Try to get Modular Golems' HOLDER_GOLEM item via reflection, fallback to vanilla item
        ItemStack iconStack;
        try {
            Class<?> itemsClass = Class.forName("dev.xkmc.modulargolems.init.registrate.GolemItems");
            Field holderGolemField = itemsClass.getField("HOLDER_GOLEM");
            Object registryObject = holderGolemField.get(null);
            Method getMethod = registryObject.getClass().getMethod("get");
            Item holderGolem = (Item) getMethod.invoke(registryObject);
            iconStack = new ItemStack(holderGolem);
        } catch (Exception e) {
            // Fallback to iron golem spawn egg
            iconStack = new ItemStack(Items.IRON_GOLEM_SPAWN_EGG);
        }

        gui.renderItem(iconStack, x + 5, y + 4);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            // Check for any GolemInfoScreen (includes AliveGolemPage, DeadGolemPage, etc.)
            Class<?> screenClass = Class.forName("dev.xkmc.modulargolems.content.client.tracker.GolemInfoScreen");
            return screenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + LegendaryTabs.MOD_ID + ".tab.modular_golems.description");
    }

    @Override
    public void initTabOnScreens() {
        if (Config.Baked.includeOpenedScreenTab)
            TabsMenu.addTabToScreen(this, InventoryScreen.class, (player) -> 176, (player) -> 166, 45);

        if (LegendaryTabs.curiosLoaded)
            TabsMenu.addTabToScreen(this, CuriosScreen.class, (player) -> 176, (player) -> 166, 45);

        // Add to other compatible screens as needed
    }
}