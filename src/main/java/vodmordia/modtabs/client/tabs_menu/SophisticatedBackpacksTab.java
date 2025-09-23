package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.SimpleItemTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.config.BackpackSlot;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@TabConfig(configKey = "sophisticatedBackpacksTab", defaultEnabled = true, defaultOrder = 0)
public class SophisticatedBackpacksTab extends SimpleItemTab {

    public SophisticatedBackpacksTab() {
        super(() -> getBackpackItem());
    }

    @Override
    public void openTargetScreen(Player player) {
        try {
            Class<?> packetDistributorClass = Class.forName("net.neoforged.neoforge.network.PacketDistributor");
            Class<?> payloadClass = Class.forName("net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackOpenPayload");
            Class<?> backpackItemClass = Class.forName("net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem");

            Object payload;

            // DEBUG: Log current preference setting
            BackpackSlot currentPreference = Config.Baked.sophisticatedBackpacksPreferredSlot;
            ModTabs.LOGGER.info("DEBUG: Sophisticated Backpacks preferred slot setting: {}", currentPreference);

            // DEBUG: Show all inventory slots with backpacks
            ModTabs.LOGGER.info("DEBUG: === INVENTORY SCAN ===");
            for (int i = 0; i < 9; i++) {
                ItemStack stack = player.getInventory().items.get(i);
                if (!stack.isEmpty() && backpackItemClass.isInstance(stack.getItem())) {
                    ModTabs.LOGGER.info("DEBUG: Hotbar slot {} contains backpack: {}", i, stack.getDisplayName().getString());
                }
            }
            for (int i = 9; i < player.getInventory().items.size(); i++) {
                ItemStack stack = player.getInventory().items.get(i);
                if (!stack.isEmpty() && backpackItemClass.isInstance(stack.getItem())) {
                    ModTabs.LOGGER.info("DEBUG: Main inventory slot {} contains backpack: {}", i, stack.getDisplayName().getString());
                }
            }
            ItemStack offhandStack = player.getOffhandItem();
            if (!offhandStack.isEmpty() && backpackItemClass.isInstance(offhandStack.getItem())) {
                ModTabs.LOGGER.info("DEBUG: Offhand contains backpack: {}", offhandStack.getDisplayName().getString());
            }
            ModTabs.LOGGER.info("DEBUG: === END INVENTORY SCAN ===");

            // Check if user has a preferred slot and if it contains a backpack
            ItemStack preferredBackpack = getBackpackFromPreferredSlot(player, backpackItemClass);

            if (preferredBackpack != null && !preferredBackpack.isEmpty()) {
                ModTabs.LOGGER.info("DEBUG: Found preferred backpack: {} (display name: {})", preferredBackpack.getItem(), preferredBackpack.getDisplayName().getString());

                // Try to create payload with specific backpack slot/position
                // First try to find the constructor that takes slot parameters
                try {
                    // Get the slot index for the preferred backpack
                    int slotIndex = getSlotIndexForBackpack(player, preferredBackpack);
                    ModTabs.LOGGER.info("DEBUG: Slot index for preferred backpack: {}", slotIndex);

                    // Let's check what constructors are available
                    ModTabs.LOGGER.info("DEBUG: Available constructors for BackpackOpenPayload:");
                    for (var constructor : payloadClass.getDeclaredConstructors()) {
                        Class<?>[] paramTypes = constructor.getParameterTypes();
                        StringBuilder paramInfo = new StringBuilder();
                        for (int i = 0; i < paramTypes.length; i++) {
                            if (i > 0) paramInfo.append(", ");
                            paramInfo.append(paramTypes[i].getSimpleName());
                        }
                        ModTabs.LOGGER.info("DEBUG: Constructor with parameters: [{}]", paramInfo.toString());
                    }

                    if (slotIndex != -1) {
                        // Try constructor with slot parameter
                        try {
                            // Maybe the mod uses a different slot numbering system?
                            // Let's try to correlate our slot index with what the mod expects

                            // For hotbar slots (0-8), try different mappings
                            int modSlotIndex = slotIndex;
                            if (slotIndex <= 8) {
                                // If it's a hotbar slot, maybe the mod expects it differently
                                // Some mods use 36-44 for hotbar slots instead of 0-8
                                int alternativeIndex = slotIndex + 36;
                                ModTabs.LOGGER.info("DEBUG: Trying alternative hotbar slot mapping: {} -> {}", slotIndex, alternativeIndex);
                                modSlotIndex = alternativeIndex;
                            }

                            payload = payloadClass.getDeclaredConstructor(int.class).newInstance(modSlotIndex);
                            ModTabs.LOGGER.info("DEBUG: Created payload with slot parameter: {} (original: {})", modSlotIndex, slotIndex);
                        } catch (NoSuchMethodException e) {
                            ModTabs.LOGGER.info("DEBUG: No constructor with int parameter found, trying other constructors");

                            // Try other possible constructor patterns
                            try {
                                // Maybe it takes a boolean and int?
                                payload = payloadClass.getDeclaredConstructor(boolean.class, int.class).newInstance(true, slotIndex);
                                ModTabs.LOGGER.info("DEBUG: Created payload with boolean+int parameters: true, {}", slotIndex);
                            } catch (Exception e2) {
                                // Try just boolean
                                try {
                                    payload = payloadClass.getDeclaredConstructor(boolean.class).newInstance(true);
                                    ModTabs.LOGGER.info("DEBUG: Created payload with boolean parameter: true");
                                } catch (Exception e3) {
                                    // Fall back to default
                                    payload = payloadClass.getDeclaredConstructor().newInstance();
                                    ModTabs.LOGGER.info("DEBUG: All specific constructors failed, using default constructor");
                                }
                            }
                        }
                    } else {
                        // Fall back to default constructor
                        payload = payloadClass.getDeclaredConstructor().newInstance();
                        ModTabs.LOGGER.info("DEBUG: Failed to find slot index, using default payload constructor");
                    }
                } catch (Exception e) {
                    // Fall back to default constructor if specific constructor fails
                    payload = payloadClass.getDeclaredConstructor().newInstance();
                    ModTabs.LOGGER.info("DEBUG: Exception creating payload with slot parameter, using default constructor: {}", e.getMessage());
                }
            } else {
                // Use the default constructor - let Sophisticated Backpacks handle all the logic
                payload = payloadClass.getDeclaredConstructor().newInstance();
                ModTabs.LOGGER.info("DEBUG: No preferred backpack found or preference is DEFAULT, using default payload constructor");
            }

            // Find the sendToServer method with 2 parameters
            for (Method method : packetDistributorClass.getDeclaredMethods()) {
                if (method.getName().equals("sendToServer") && method.getParameterCount() == 2) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    // Create empty array for the second parameter
                    Object[] emptyArray = (Object[]) java.lang.reflect.Array.newInstance(paramTypes[1].getComponentType(), 0);
                    method.invoke(null, payload, emptyArray);
                    ModTabs.LOGGER.info("DEBUG: Sent backpack open payload to server");
                    return;
                }
            }

        } catch (Exception e) {
            ModTabs.LOGGER.error("DEBUG: Exception in openTargetScreen: ", e);
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.sophisticatedBackpacksTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.SOPHISTICATED_BACKPACKS) && hasBackpack(player);
    }

    private ItemStack getBackpackFromPreferredSlot(Player player, Class<?> backpackItemClass) {
        BackpackSlot preferredSlot = Config.Baked.sophisticatedBackpacksPreferredSlot;
        ModTabs.LOGGER.info("DEBUG: getBackpackFromPreferredSlot called with preference: {}", preferredSlot);

        if (preferredSlot == BackpackSlot.DEFAULT) {
            ModTabs.LOGGER.info("DEBUG: Preference is DEFAULT, returning null");
            return null; // Use default behavior
        }

        try {
            switch (preferredSlot) {
                case QUICKBAR_1:
                case QUICKBAR_2:
                case QUICKBAR_3:
                case QUICKBAR_4:
                case QUICKBAR_5:
                case QUICKBAR_6:
                case QUICKBAR_7:
                case QUICKBAR_8:
                case QUICKBAR_9:
                    // QUICKBAR_1 has ordinal 1, but should map to inventory slot 0
                    // QUICKBAR_9 has ordinal 9, but should map to inventory slot 8
                    int hotbarIndex = preferredSlot.ordinal() - 1; // Convert QUICKBAR_1 (ordinal 1) to slot 0
                    ModTabs.LOGGER.info("DEBUG: Checking quickbar slot {} (ordinal: {}, calculated index: {})", preferredSlot, preferredSlot.ordinal(), hotbarIndex);

                    if (hotbarIndex < 0 || hotbarIndex >= 9) {
                        ModTabs.LOGGER.warn("DEBUG: Invalid hotbar index calculated: {}", hotbarIndex);
                        break;
                    }

                    ItemStack hotbarStack = player.getInventory().items.get(hotbarIndex);
                    ModTabs.LOGGER.info("DEBUG: Item in slot {}: {} (isEmpty: {})", hotbarIndex, hotbarStack.getItem(), hotbarStack.isEmpty());

                    if (!hotbarStack.isEmpty() && backpackItemClass.isInstance(hotbarStack.getItem())) {
                        ModTabs.LOGGER.info("DEBUG: Found backpack in preferred quickbar slot {}", hotbarIndex);
                        return hotbarStack;
                    } else {
                        ModTabs.LOGGER.info("DEBUG: No backpack found in preferred quickbar slot {}", hotbarIndex);
                    }
                    break;

                case OFFHAND:
                    ItemStack offhandStack = player.getOffhandItem();
                    if (!offhandStack.isEmpty() && backpackItemClass.isInstance(offhandStack.getItem())) {
                        return offhandStack;
                    }
                    break;

                case MAIN_INVENTORY:
                    for (ItemStack stack : player.getInventory().items) {
                        if (!stack.isEmpty() && backpackItemClass.isInstance(stack.getItem())) {
                            return stack;
                        }
                    }
                    break;

                case ARMOR:
                    for (ItemStack stack : player.getInventory().armor) {
                        if (!stack.isEmpty() && backpackItemClass.isInstance(stack.getItem())) {
                            return stack;
                        }
                    }
                    break;

                case CURIOS:
                    if (ModIntegrationManager.isModLoaded(ModIntegration.CURIOS)) {
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
                                    // Get the first curio result and extract the ItemStack
                                    Object curioResult = curioResults.get(0);
                                    Method getStackMethod = curioResult.getClass().getDeclaredMethod("stack");
                                    return (ItemStack) getStackMethod.invoke(curioResult);
                                }
                            }
                        } catch (Exception e) {
                            // Silently ignore Curios errors
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            // Silently ignore errors
        }

        return null;
    }

    private int getSlotIndexForBackpack(Player player, ItemStack targetBackpack) {
        // Try to find the slot index of the target backpack
        try {
            // Check hotbar first (slots 0-8)
            for (int i = 0; i < 9; i++) {
                if (player.getInventory().items.get(i) == targetBackpack) {
                    return i;
                }
            }

            // Check main inventory (slots 9-35)
            for (int i = 9; i < player.getInventory().items.size(); i++) {
                if (player.getInventory().items.get(i) == targetBackpack) {
                    return i;
                }
            }

            // Check armor slots (slots 36-39)
            for (int i = 0; i < player.getInventory().armor.size(); i++) {
                if (player.getInventory().armor.get(i) == targetBackpack) {
                    return 36 + i;
                }
            }

            // Check offhand (slot 40)
            if (player.getOffhandItem() == targetBackpack) {
                return 40;
            }

        } catch (Exception e) {
            // Silently ignore errors
        }

        return -1; // Not found
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
            if (ModIntegrationManager.isModLoaded(ModIntegration.CURIOS)) {
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


    private static ItemStack getBackpackItem() {
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
        ScreenRegistry.builder()
            .withStandardDimensions()
            .withPositioning(TabPositioning.GUI_RELATIVE)
            .registerAllTabs("net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen");
    }

}