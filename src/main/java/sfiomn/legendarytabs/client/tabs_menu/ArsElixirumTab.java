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

public class ArsElixirumTab extends TabBase {
    private final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(LegendaryTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 0; // Empty tab normal state
    private final int TAB_ICON_TEX_Y = 138; // Empty tab background in bottom row

    public ArsElixirumTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        try {
            Class<?> screenClass = Class.forName("dev.obscuria.elixirum.client.screen.ElixirumScreen");
            java.lang.reflect.Constructor<?> constructor = screenClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object screen = constructor.newInstance();

            // Set the section to COLLECTION by setting the static selectedSection field
            try {
                Class<?> sectionTypeClass = Class.forName("dev.obscuria.elixirum.client.screen.section.AbstractSection$Type");
                Field collectionField = sectionTypeClass.getField("COLLECTION");
                Object collectionSection = collectionField.get(null);

                Field selectedSectionField = screenClass.getDeclaredField("selectedSection");
                selectedSectionField.setAccessible(true);
                selectedSectionField.set(null, collectionSection);
            } catch (Exception e) {
                // Section setting failed, but still open screen
            }

            Minecraft.getInstance().setScreen((Screen) screen);
        } catch (Exception e) {
            LegendaryTabs.LOGGER.error("Failed to open Ars Elixirum screen: " + e.getMessage());
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.arsElixirumTabEnabled;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        // Render tab background
        int texOffsetX = 0;
        if (hover)
            texOffsetX = 54; // Hover state is at X=54
        gui.blit(TAB_ICONS, x, y, TAB_ICON_TEX_X + texOffsetX, TAB_ICON_TEX_Y, TAB_WIDTH, TAB_HEIGHT);

        // Get glass cauldron icon from Ars Elixirum
        ItemStack iconStack;
        try {
            Class<?> itemsClass = Class.forName("dev.obscuria.elixirum.registry.ElixirumItems");
            Field itemField = itemsClass.getField("GLASS_CAULDRON");
            Object registryObject = itemField.get(null);

            // Get item from Fragmentum Deferred object
            Method getMethod = registryObject.getClass().getMethod("get");
            Item glassCauldron = (Item) getMethod.invoke(registryObject);
            iconStack = new ItemStack(glassCauldron);
        } catch (Exception e) {
            // Fallback to brewing stand
            iconStack = new ItemStack(Items.BREWING_STAND);
        }

        gui.renderItem(iconStack, x + 5, y + 3);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> screenClass = Class.forName("dev.obscuria.elixirum.client.screen.ElixirumScreen");
            return screenClass.isInstance(currentScreen);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + LegendaryTabs.MOD_ID + ".tab.ars_elixirum.description");
    }

    @Override
    public void initTabOnScreens() {
        if (Config.Baked.includeOpenedScreenTab)
            TabsMenu.addTabToScreen(this, InventoryScreen.class, (player) -> 176, (player) -> 166, 35);

        if (LegendaryTabs.curiosLoaded)
            TabsMenu.addTabToScreen(this, CuriosScreen.class, (player) -> 176, (player) -> 166, 35);

        // Add to other compatible screens as needed
    }
}