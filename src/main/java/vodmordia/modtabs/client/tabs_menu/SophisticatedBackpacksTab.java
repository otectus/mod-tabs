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

public class SophisticatedBackpacksTab extends TabBase {

    public SophisticatedBackpacksTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        try {
            Class<?> packetDistributorClass = Class.forName("net.neoforged.neoforge.network.PacketDistributor");
            Class<?> payloadClass = Class.forName("net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackOpenPayload");

            // Use the default constructor - let Sophisticated Backpacks handle all the logic
            Object payload = payloadClass.getDeclaredConstructor().newInstance();

            // Find the sendToServer method with 2 parameters
            for (Method method : packetDistributorClass.getDeclaredMethods()) {
                if (method.getName().equals("sendToServer") && method.getParameterCount() == 2) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    // Create empty array for the second parameter
                    Object[] emptyArray = (Object[]) java.lang.reflect.Array.newInstance(paramTypes[1].getComponentType(), 0);
                    method.invoke(null, payload, emptyArray);
                    return;
                }
            }

        } catch (Exception e) {
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.sophisticatedBackpacksTabEnabled && hasBackpack(player);
    }

    private boolean hasBackpack(Player player) {
        try {
            Class<?> backpackItemClass = Class.forName("net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem");

            // Check main inventory
            for (ItemStack stack : player.getInventory().items) {
                if (!stack.isEmpty() && backpackItemClass.isInstance(stack.getItem())) {
                    return true;
                }
            }

            // Check armor slots
            for (ItemStack stack : player.getInventory().armor) {
                if (!stack.isEmpty() && backpackItemClass.isInstance(stack.getItem())) {
                    return true;
                }
            }

            // Check Curios if available
            if (ModTabs.curiosLoaded) {
                try {
                    Class<?> curiosAPIClass = Class.forName("top.theillusivec4.curios.api.CuriosApi");
                    Method getCuriosInventoryMethod = curiosAPIClass.getDeclaredMethod("getCuriosInventory", net.minecraft.world.entity.LivingEntity.class);
                    Object optionalCuriosInventory = getCuriosInventoryMethod.invoke(null, player);

                    Method isPresentMethod = optionalCuriosInventory.getClass().getDeclaredMethod("isPresent");
                    boolean isPresent = (Boolean) isPresentMethod.invoke(optionalCuriosInventory);

                    if (isPresent) {
                        Method getMethod = optionalCuriosInventory.getClass().getDeclaredMethod("get");
                        Object curiosInventory = getMethod.invoke(optionalCuriosInventory);

                        Method findCuriosMethod = curiosInventory.getClass().getDeclaredMethod("findCurios", java.util.function.Predicate.class);

                        java.util.function.Predicate<ItemStack> backpackPredicate = stack ->
                            !stack.isEmpty() && backpackItemClass.isInstance(stack.getItem());

                        @SuppressWarnings("unchecked")
                        java.util.List<Object> curioResults = (java.util.List<Object>) findCuriosMethod.invoke(curiosInventory, backpackPredicate);

                        if (!curioResults.isEmpty()) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    // Silently ignore Curios errors
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(getBackpackItem(), 5, 3)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(getBackpackItem(), 5, 3)
            .render(gui, x, y, hover, true);
    }

    private ItemStack getBackpackItem() {
        try {
            Class<?> itemsClass = Class.forName("net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems");
            Field backpackField = itemsClass.getField("BACKPACK");
            Object supplier = backpackField.get(null);
            Method getMethod = supplier.getClass().getMethod("get");
            Item backpackItem = (Item) getMethod.invoke(supplier);
            return new ItemStack(backpackItem);
        } catch (Exception e) {
            return new ItemStack(Items.BUNDLE);
        }
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> screenClass = Class.forName("net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen");
            return screenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.sophisticated_backpacks.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register only this tab's own screen
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Screen> backpackScreenClass = (Class<? extends Screen>) Class.forName("net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen");
            TabsMenu.registerScreenWithAllTabs(backpackScreenClass, (player) -> 176, (player) -> 166);
        } catch (ClassNotFoundException e) {
            // Mod not available
        }
    }

}