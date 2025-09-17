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

public class SophisticatedBackpacksTab extends TabBase {
    private final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(LegendaryTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 0;
    private final int TAB_ICON_TEX_Y = 138;

    public SophisticatedBackpacksTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        try {
            Class<?> packetDistributorClass = Class.forName("net.neoforged.neoforge.network.PacketDistributor");
            Class<?> payloadClass = Class.forName("net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackOpenPayload");

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
            LegendaryTabs.LOGGER.error("Failed to open Sophisticated Backpacks screen: " + e.getMessage());
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
            if (LegendaryTabs.curiosLoaded) {
                try {
                    Class<?> curiosAPIClass = Class.forName("top.theillusivec4.curios.api.CuriosApi");
                    Method getCuriosInventoryMethod = curiosAPIClass.getDeclaredMethod("getCuriosInventory", net.minecraft.world.entity.LivingEntity.class);
                    Object curiosInventory = getCuriosInventoryMethod.invoke(null, player);

                    if (curiosInventory != null) {
                        Method getAllEquippedMethod = curiosInventory.getClass().getDeclaredMethod("getAllEquippedItems");
                        Iterable<?> equippedItems = (Iterable<?>) getAllEquippedMethod.invoke(curiosInventory);

                        for (Object equippedItem : equippedItems) {
                            Method getStackMethod = equippedItem.getClass().getDeclaredMethod("getStack");
                            ItemStack stack = (ItemStack) getStackMethod.invoke(equippedItem);
                            if (!stack.isEmpty() && backpackItemClass.isInstance(stack.getItem())) {
                                return true;
                            }
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
        int texOffsetX = 0;
        if (hover)
            texOffsetX = 54;
        gui.blit(TAB_ICONS, x, y, TAB_ICON_TEX_X + texOffsetX, TAB_ICON_TEX_Y, TAB_WIDTH, TAB_HEIGHT);

        ItemStack iconStack;
        try {
            Class<?> itemsClass = Class.forName("net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems");
            Field backpackField = itemsClass.getField("BACKPACK");
            Object supplier = backpackField.get(null);
            Method getMethod = supplier.getClass().getMethod("get");
            Item backpackItem = (Item) getMethod.invoke(supplier);
            iconStack = new ItemStack(backpackItem);
        } catch (Exception e) {
            iconStack = new ItemStack(Items.BUNDLE);
        }

        gui.renderItem(iconStack, x + 5, y + 3);
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
        return Component.translatable("tooltip." + LegendaryTabs.MOD_ID + ".tab.sophisticated_backpacks.description");
    }

    @Override
    public void initTabOnScreens() {
        if (Config.Baked.includeOpenedScreenTab)
            TabsMenu.addTabToScreen(this, InventoryScreen.class, (player) -> 176, (player) -> 166, 35);

        if (LegendaryTabs.curiosLoaded)
            TabsMenu.addTabToScreen(this, CuriosScreen.class, (player) -> 176, (player) -> 166, 35);
    }
}