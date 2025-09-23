package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.SimpleItemTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;
import vodmordia.modtabs.utils.IntegrationUtils;

@TabConfig(configKey = "travelersBackpackTab", defaultEnabled = true, defaultOrder = 0)
public class TravelersBackpackTab extends SimpleItemTab {

    public TravelersBackpackTab() {
        super(() -> getStandardBackpackItem());
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
                        Class<?> backpackContainerClass = Class.forName("com.tiviacz.travelersbackpack.inventory.BackpackContainer");
                        Class<?> attachmentUtilsClass = Class.forName("com.tiviacz.travelersbackpack.capability.AttachmentUtils");
                        Class<?> referenceClass = Class.forName("com.tiviacz.travelersbackpack.util.Reference");

                        java.lang.reflect.Method getWearingBackpackMethod = attachmentUtilsClass.getMethod("getWearingBackpack", Player.class);
                        Object wearingBackpack = getWearingBackpackMethod.invoke(null, player);

                        java.lang.reflect.Field wearableScreenIdField = referenceClass.getField("WEARABLE_SCREEN_ID");
                        int wearableScreenId = wearableScreenIdField.getInt(null);

                        java.lang.reflect.Method openBackpackMethod = backpackContainerClass.getMethod("openBackpack", ServerPlayer.class, ItemStack.class, int.class);
                        openBackpackMethod.invoke(null, serverPlayer, wearingBackpack, wearableScreenId);
                    } catch (Exception ex) {
                        // Silent fail if both methods don't work
                    }
                }
            }
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.travelersBackpackTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.TRAVELERS_BACKPACK) && hasBackpack(player);
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
            if (ModIntegrationManager.isModLoaded(ModIntegration.CURIOS)) {
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
            try {
                Class<?> attachmentUtilsClass = Class.forName("com.tiviacz.travelersbackpack.capability.AttachmentUtils");
                java.lang.reflect.Method isWearingBackpackMethod = attachmentUtilsClass.getMethod("isWearingBackpack", Player.class);
                return (Boolean) isWearingBackpackMethod.invoke(null, player);
            } catch (Exception ex) {
                return false;
            }
        } catch (Exception e) {
            // If reflection fails, fall back to original method
            try {
                Class<?> attachmentUtilsClass = Class.forName("com.tiviacz.travelersbackpack.capability.AttachmentUtils");
                java.lang.reflect.Method isWearingBackpackMethod = attachmentUtilsClass.getMethod("isWearingBackpack", Player.class);
                return (Boolean) isWearingBackpackMethod.invoke(null, player);
            } catch (Exception ex) {
                return false;
            }
        }
    }


    private static ItemStack getStandardBackpackItem() {
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
        try {
            Class<?> backpackScreenClass = Class.forName("com.tiviacz.travelersbackpack.client.screens.BackpackScreen");
            return backpackScreenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.backpack.description");
    }

    @Override
    public void initTabOnScreens() {
        try {
            Class<?> backpackScreenClass = Class.forName("com.tiviacz.travelersbackpack.client.screens.BackpackScreen");
            @SuppressWarnings("unchecked")
            Class<? extends Screen> screenClass = (Class<? extends Screen>) backpackScreenClass;
            ScreenRegistry.builder()
                .withDimensions(IntegrationUtils::getTravelersBackpackWidth, IntegrationUtils::getTravelersBackpackHeight)
                .withPositioning(TabPositioning.GUI_RELATIVE)
                .registerAllTabs(screenClass);
        } catch (ClassNotFoundException e) {
            // Traveler's Backpack not present, skip registration
        }
    }
}
