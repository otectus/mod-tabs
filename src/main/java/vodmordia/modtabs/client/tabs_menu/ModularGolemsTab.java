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
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import top.theillusivec4.curios.client.gui.CuriosScreen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ModularGolemsTab extends TabBase {

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


        } catch (Exception e) {
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.modularGolemsTabEnabled;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(getHolderGolemItem(), 5, 4)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(getHolderGolemItem(), 5, 4)
            .render(gui, x, y, hover, true);
    }

    private ItemStack getHolderGolemItem() {
        // Try to get Modular Golems' HOLDER_GOLEM item via reflection, fallback to vanilla item
        try {
            Class<?> itemsClass = Class.forName("dev.xkmc.modulargolems.init.registrate.GolemItems");
            Field holderGolemField = itemsClass.getField("HOLDER_GOLEM");
            Object registryObject = holderGolemField.get(null);
            Method getMethod = registryObject.getClass().getMethod("get");
            Item holderGolem = (Item) getMethod.invoke(registryObject);
            return new ItemStack(holderGolem);
        } catch (Exception e) {
            // Fallback to iron golem spawn egg
            return new ItemStack(Items.IRON_GOLEM_SPAWN_EGG);
        }
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
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.modular_golems.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register only this tab's own screen - the Modular Golems info screen
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Screen> golemInfoScreenClass = (Class<? extends Screen>) Class.forName("dev.xkmc.modulargolems.content.client.tracker.GolemInfoScreen");
            TabsMenu.registerScreenWithAllTabs(golemInfoScreenClass, (player) -> 176, (player) -> 166);
        } catch (ClassNotFoundException e) {
            // Mod not available
        }
    }
}