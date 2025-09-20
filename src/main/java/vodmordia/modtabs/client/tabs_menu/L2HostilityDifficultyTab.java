package vodmordia.modtabs.client.tabs_menu;

// import dev.xkmc.l2hostility.content.menu.tab.DifficultyScreen; // Available at runtime only
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import top.theillusivec4.curios.client.gui.CuriosScreen;

public class L2HostilityDifficultyTab extends TabBase {
    private final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 0; // Empty tab normal state (both can use same empty slot)
    private final int TAB_ICON_TEX_Y = 138; // Empty tab background in bottom row

    public L2HostilityDifficultyTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        try {
            // Use reflection to access the constructor
            Class<?> screenClass = Class.forName("dev.xkmc.l2hostility.content.menu.tab.DifficultyScreen");
            
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
                    Object screen = constructor.newInstance(Component.translatable("l2hostility.difficulty.title"));
                    Minecraft.getInstance().setScreen((Screen) screen);
                    return;
                }
            }
            
            ModTabs.LOGGER.error("No suitable constructor found for DifficultyScreen");
            
        } catch (Exception e) {
            ModTabs.LOGGER.error("Failed to open L2 Difficulty screen: " + e.getMessage());
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
        
        // Render L2's zombie head icon (same as L2Hostility uses)
        ItemStack iconStack = new ItemStack(Items.ZOMBIE_HEAD);
        gui.renderItem(iconStack, x + 5, y + 4);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> screenClass = Class.forName("dev.xkmc.l2hostility.content.menu.tab.DifficultyScreen");
            return screenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.l2_hostility_difficulty.description");
    }

    @Override
    public void initTabOnScreens() {
        if (Config.Baked.includeOpenedScreenTab)
            TabsMenu.addTabToScreen(this, InventoryScreen.class, (player) -> 176, (player) -> 166, 30);

        if (ModTabs.curiosLoaded)
            TabsMenu.addTabToScreen(this, CuriosScreen.class, (player) -> 176, (player) -> 166, 30);

        // Add to other compatible screens as needed
    }
}