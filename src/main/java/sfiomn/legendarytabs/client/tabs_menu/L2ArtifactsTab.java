package sfiomn.legendarytabs.client.tabs_menu;

// import dev.xkmc.l2artifacts.content.client.tab.SetEffectScreen; // Available at runtime only
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

public class L2ArtifactsTab extends TabBase {
    private final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(LegendaryTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 0; // Empty tab normal state
    private final int TAB_ICON_TEX_Y = 138; // Empty tab background in bottom row

    public L2ArtifactsTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        try {
            // Use reflection to access the constructor
            Class<?> screenClass = Class.forName("dev.xkmc.l2artifacts.content.client.tab.SetEffectScreen");
            
            // Try no-arg constructor first
            try {
                java.lang.reflect.Constructor<?> constructor = screenClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object screen = constructor.newInstance();
                Minecraft.getInstance().setScreen((Screen) screen);
                return;
            } catch (NoSuchMethodException e) {
                // Fall through to try other constructors
            }
            
            // If no-arg fails, try all available constructors
            java.lang.reflect.Constructor<?>[] constructors = screenClass.getDeclaredConstructors();
            for (java.lang.reflect.Constructor<?> constructor : constructors) {
                constructor.setAccessible(true);
                Class<?>[] paramTypes = constructor.getParameterTypes();
                
                if (paramTypes.length == 0) {
                    Object screen = constructor.newInstance();
                    Minecraft.getInstance().setScreen((Screen) screen);
                    return;
                } else if (paramTypes.length == 1 && paramTypes[0].equals(Component.class)) {
                    Object screen = constructor.newInstance(Component.translatable("l2artifacts.gui.set_effects"));
                    Minecraft.getInstance().setScreen((Screen) screen);
                    return;
                } else if (paramTypes.length == 2 && paramTypes[0].equals(Component.class) && paramTypes[1].equals(int.class)) {
                    // Constructor(Component, int) - try with 0 as the int parameter (page number)
                    Object screen = constructor.newInstance(Component.translatable("l2artifacts.gui.set_effects"), 0);
                    Minecraft.getInstance().setScreen((Screen) screen);
                    return;
                }
            }
            
            LegendaryTabs.LOGGER.error("No suitable constructor found for SetEffectScreen");
            
        } catch (Exception e) {
            LegendaryTabs.LOGGER.error("Failed to open L2 Artifacts screen: " + e.getMessage());
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
        
        // Try to get L2 Artifacts' SELECT item via reflection, fallback to vanilla item
        ItemStack iconStack;
        try {
            Class<?> itemsClass = Class.forName("dev.xkmc.l2artifacts.init.registrate.items.ArtifactItems");
            Field selectField = itemsClass.getField("SELECT");
            Object registryObject = selectField.get(null);
            Method getMethod = registryObject.getClass().getMethod("get");
            Item selectItem = (Item) getMethod.invoke(registryObject);
            iconStack = new ItemStack(selectItem);
        } catch (Exception e) {
            // Fallback to vanilla item
            iconStack = new ItemStack(Items.NETHER_STAR);
        }
        
        gui.renderItem(iconStack, x + 5, y + 3);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> screenClass = Class.forName("dev.xkmc.l2artifacts.content.client.tab.SetEffectScreen");
            return screenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + LegendaryTabs.MOD_ID + ".tab.l2_artifacts.description");
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