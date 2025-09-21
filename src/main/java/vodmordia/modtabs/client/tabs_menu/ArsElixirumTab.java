package vodmordia.modtabs.client.tabs_menu;

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
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabDisplayMode;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import top.theillusivec4.curios.client.gui.CuriosScreen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ArsElixirumTab extends TabBase {

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
            }

            Minecraft.getInstance().setScreen((Screen) screen);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.arsElixirumTabEnabled;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(getGlassCauldronItem(), 5, 4)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(getGlassCauldronItem(), 5, 4)
            .render(gui, x, y, hover, true);
    }

    private ItemStack getGlassCauldronItem() {
        // Get glass cauldron icon from Ars Elixirum
        try {
            Class<?> itemsClass = Class.forName("dev.obscuria.elixirum.registry.ElixirumItems");
            Field itemField = itemsClass.getField("GLASS_CAULDRON");
            Object registryObject = itemField.get(null);

            // Get item from Fragmentum Deferred object
            Method getMethod = registryObject.getClass().getMethod("get");
            Item glassCauldron = (Item) getMethod.invoke(registryObject);
            return new ItemStack(glassCauldron);
        } catch (Exception e) {
            // Fallback to brewing stand
            return new ItemStack(Items.BREWING_STAND);
        }
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
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.ars_elixirum.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register the Ars Elixirum screen with tabs at the bottom (normal, non-inverted)
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Screen> elixirumScreenClass = (Class<? extends Screen>) Class.forName("dev.obscuria.elixirum.client.screen.ElixirumScreen");
            TabsMenu.registerScreenWithAllTabs(elixirumScreenClass,
                (player) -> 176, // Standard GUI width
                (player) -> 166, // Standard GUI height
                TabDisplayMode.NORMAL, // Normal display mode (not inverted)
                TabPositioning.SCREEN_BOTTOM, // Position tabs at the bottom of the screen
                0); // No offset from screen bottom edge
        } catch (ClassNotFoundException e) {
            // Mod not available
        }
    }
}
