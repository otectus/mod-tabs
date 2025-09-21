package vodmordia.modtabs.client.tabs_menu;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;

public class TravelersBackpackTab extends TabBase {

    public TravelersBackpackTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.travelersBackpackTabEnabled) {
            try {
                // Use reflection to send the OPEN_SCREEN action packet
                Class<?> actionPacketClass = Class.forName("com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket");
                java.lang.reflect.Method createMethod = actionPacketClass.getDeclaredMethod("create", int.class, Object[].class);

                // OPEN_SCREEN = 1 (from the constants in the packet class)
                createMethod.invoke(null, 1, new Object[0]);
            } catch (Exception e) {
                // Fallback: try the original server-side method if reflection fails
                if (player instanceof ServerPlayer serverPlayer) {
                    try {
                        BackpackContainer.openBackpack(serverPlayer, AttachmentUtils.getWearingBackpack(player), Reference.WEARABLE_SCREEN_ID);
                    } catch (Exception ex) {
                        // Silent fail if both methods don't work
                    }
                }
            }
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.travelersBackpackTabEnabled && hasBackpack(player);
    }

    private boolean hasBackpack(Player player) {
        try {
            Class<?> backpackItemClass = Class.forName("com.tiviacz.travelersbackpack.items.TravelersBackpackItem");

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
                    java.lang.reflect.Method getCuriosInventoryMethod = curiosAPIClass.getDeclaredMethod("getCuriosInventory", net.minecraft.world.entity.LivingEntity.class);
                    Object optionalCuriosInventory = getCuriosInventoryMethod.invoke(null, player);

                    java.lang.reflect.Method isPresentMethod = optionalCuriosInventory.getClass().getDeclaredMethod("isPresent");
                    boolean isPresent = (Boolean) isPresentMethod.invoke(optionalCuriosInventory);

                    if (isPresent) {
                        java.lang.reflect.Method getMethod = optionalCuriosInventory.getClass().getDeclaredMethod("get");
                        Object curiosInventory = getMethod.invoke(optionalCuriosInventory);

                        java.lang.reflect.Method findCuriosMethod = curiosInventory.getClass().getDeclaredMethod("findCurios", java.util.function.Predicate.class);

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

            // Fallback to original AttachmentUtils check
            return AttachmentUtils.isWearingBackpack(player);
        } catch (Exception e) {
            // If reflection fails, fall back to original method
            return AttachmentUtils.isWearingBackpack(player);
        }
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        ItemStack backpackItem = getStandardBackpackItem();
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(backpackItem, 5, 4)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        ItemStack backpackItem = getStandardBackpackItem();
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(backpackItem, 5, 4)
            .render(gui, x, y, hover, true);
    }

    private ItemStack getStandardBackpackItem() {
        try {
            // Use reflection to get the standard travelers backpack item
            Class<?> modItemsClass = Class.forName("com.tiviacz.travelersbackpack.init.ModItems");
            java.lang.reflect.Field standardBackpackField = modItemsClass.getDeclaredField("STANDARD_TRAVELERS_BACKPACK");
            standardBackpackField.setAccessible(true);

            Object deferredItem = standardBackpackField.get(null);
            // DeferredItem.get() returns the actual item
            java.lang.reflect.Method getMethod = deferredItem.getClass().getMethod("get");
            Object item = getMethod.invoke(deferredItem);

            return new ItemStack((net.minecraft.world.item.Item) item);
        } catch (Exception e) {
            // Fallback to a generic backpack-like item if reflection fails
            return new ItemStack(Items.LEATHER_CHESTPLATE);
        }
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return currentScreen instanceof com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.backpack.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register only this tab's own screen - the Travelers Backpack screen
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Screen> backpackScreenClass = (Class<? extends Screen>) Class.forName("com.tiviacz.travelersbackpack.client.screens.BackpackScreen");
            TabsMenu.registerScreenWithAllTabs(backpackScreenClass, IntegrationUtils::getTravelersBackpackWidth, IntegrationUtils::getTravelersBackpackHeight);
        } catch (ClassNotFoundException e) {
            // Mod not available
        }
    }
}
