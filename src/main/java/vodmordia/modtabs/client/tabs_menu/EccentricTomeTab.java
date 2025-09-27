package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@TabConfig(configKey = "eccentricTomeTab", defaultEnabled = true, defaultOrder = 0)
public class EccentricTomeTab extends SimpleItemTab {

    public EccentricTomeTab() {
        super(() -> getTomeItem());
    }

    @Override
    public void openTargetScreen(Player player) {
        try {
            // Find any tome in player's inventory
            ItemStack tomeStack = findTomeInInventory(player);
            if (tomeStack.isEmpty()) {
                return; // No tome found
            }

            // Create original TomeScreen
            Class<?> tomeScreenClass = Class.forName("website.eccentric.tome.client.TomeScreen");
            Object originalTomeScreen = tomeScreenClass.getDeclaredConstructor(ItemStack.class)
                .newInstance(tomeStack);

            // Initialize the original screen properly (set minecraft instance)
            Method initMethod = Screen.class.getDeclaredMethod("init", net.minecraft.client.Minecraft.class, int.class, int.class);
            initMethod.setAccessible(true);
            var minecraft = Minecraft.getInstance();
            initMethod.invoke(originalTomeScreen, minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());

            // Create a wrapper that delegates to original but overrides mouseClicked
            TomeScreenWrapper wrapper = new TomeScreenWrapper((Screen) originalTomeScreen, player);

            // Open the GUI
            Minecraft.getInstance().setScreen(wrapper);

        } catch (Exception e) {
            ModTabs.LOGGER.error("Error opening TomeScreen", e);
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        boolean configEnabled = Config.Baked.eccentricTomeTabEnabled;
        boolean modLoaded = ModIntegrationManager.isModLoaded(ModIntegration.ECCENTRIC_TOME);
        boolean hasTheTome = hasTome(player);

        return configEnabled && modLoaded && hasTheTome;
    }

    private boolean hasTome(Player player) {
        try {
            // Check main inventory and hotbar
            for (ItemStack stack : player.getInventory().items) {
                if (!stack.isEmpty() && isTome(stack)) {
                    return true;
                }
            }

            // Check offhand
            ItemStack offhandStack = player.getOffhandItem();
            if (!offhandStack.isEmpty() && isTome(offhandStack)) {
                return true;
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

                        java.util.function.Predicate<ItemStack> tomePredicate = stack ->
                            !stack.isEmpty() && isTome(stack);

                        @SuppressWarnings("unchecked")
                        java.util.List<Object> curioResults = (java.util.List<Object>) findCuriosMethod.invoke(curiosInventory, tomePredicate);

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

    private ItemStack findTomeInInventory(Player player) {
        // Check hands first
        if (isTome(player.getMainHandItem())) {
            return player.getMainHandItem();
        }
        if (isTome(player.getOffhandItem())) {
            return player.getOffhandItem();
        }

        // Check inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && isTome(stack)) {
                return stack;
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

                    java.util.function.Predicate<ItemStack> tomePredicate = stack ->
                        !stack.isEmpty() && isTome(stack);

                    @SuppressWarnings("unchecked")
                    java.util.List<Object> curioResults = (java.util.List<Object>) findCuriosMethod.invoke(curiosInventory, tomePredicate);

                    if (!curioResults.isEmpty()) {
                        // Get the first result and extract the ItemStack
                        Object curioResult = curioResults.get(0);
                        Method getStackMethod = curioResult.getClass().getDeclaredMethod("stack");
                        return (ItemStack) getStackMethod.invoke(curioResult);
                    }
                }
            } catch (Exception e) {
                // Silently ignore Curios errors
            }
        }

        return ItemStack.EMPTY;
    }

    private boolean isTome(ItemStack stack) {
        try {
            Class<?> tomeItemClass = Class.forName("website.eccentric.tome.TomeItem");
            return tomeItemClass.isInstance(stack.getItem());
        } catch (Exception e) {
            return false;
        }
    }

    private static ItemStack getTomeItem() {
        // Try to get Eccentric Tome's tome item via reflection, fallback to vanilla item
        try {
            Class<?> eccentricTomeClass = Class.forName("website.eccentric.tome.EccentricTome");
            Field tomeField = eccentricTomeClass.getField("TOME");
            Object registryObject = tomeField.get(null);
            Method getMethod = registryObject.getClass().getMethod("get");
            Item tomeItem = (Item) getMethod.invoke(registryObject);
            return new ItemStack(tomeItem);
        } catch (Exception e) {
            // Fallback to vanilla item
            return new ItemStack(Items.BOOK);
        }
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> tomeScreenClass = Class.forName("website.eccentric.tome.client.TomeScreen");
            return tomeScreenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.eccentric_tome.description");
    }

    @Override
    public void initTabOnScreens() {
        ScreenRegistry.builder()
            .withStandardDimensions()
            .inverted()
            .atTop()
            .registerAllTabs("website.eccentric.tome.client.TomeScreen");
    }

    // Wrapper that delegates to original TomeScreen but overrides mouseClicked for custom conversion
    private static class TomeScreenWrapper extends Screen {
        private final Screen originalTomeScreen;
        private final Player player;

        public TomeScreenWrapper(Screen originalTomeScreen, Player player) {
            super(Component.empty());
            this.originalTomeScreen = originalTomeScreen;
            this.player = player;
        }

        @Override
        protected void init() {
            super.init();
            // Make sure the original screen is also initialized
            try {
                Method initMethod = originalTomeScreen.getClass().getMethod("init");
                initMethod.invoke(originalTomeScreen);
            } catch (Exception e) {
                ModTabs.LOGGER.error("Error initializing original screen", e);
            }
        }

        @Override
        public boolean mouseClicked(double x, double y, int button) {
            if (button == 0) { // LEFT_CLICK
                try {
                    // Get the selected book by calling mouseClicked and checking state
                    boolean originalResult = originalTomeScreen.mouseClicked(x, y, button);

                    Field bookField = originalTomeScreen.getClass().getDeclaredField("book");
                    bookField.setAccessible(true);
                    ItemStack selectedBook = (ItemStack) bookField.get(originalTomeScreen);

                    if (selectedBook != null) {
                        // Convert the tome to the selected book
                        sendConvertMessage(selectedBook);

                        this.onClose();
                        return true;
                    }

                    return originalResult;
                } catch (Exception e) {
                    ModTabs.LOGGER.error("Error in custom mouseClicked", e);
                }
            }

            return originalTomeScreen.mouseClicked(x, y, button);
        }

        private void sendConvertMessage(ItemStack selectedBook) {
            try {
                // Find the tome in the player's inventory
                EccentricTomeTab tab = new EccentricTomeTab();
                ItemStack tomeStack = tab.findTomeInInventory(player);
                if (tomeStack.isEmpty()) {
                    return;
                }

                // Use the TomeUtils.convert method
                Class<?> tomeUtilsClass = Class.forName("website.eccentric.tome.TomeUtils");
                Method convertMethod = tomeUtilsClass.getMethod("convert", net.minecraft.world.item.ItemStack.class, net.minecraft.world.item.ItemStack.class);

                // Call the convert method with the tome and selected book
                ItemStack convertedStack = (ItemStack) convertMethod.invoke(null, tomeStack, selectedBook);

                // Find the slot where the tome was and replace it with the converted stack
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack slotStack = player.getInventory().getItem(i);
                    if (!slotStack.isEmpty() && tab.isTome(slotStack)) {
                        player.getInventory().setItem(i, convertedStack);
                        break;
                    }
                }

                // Also check offhand
                if (tab.isTome(player.getOffhandItem())) {
                    player.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, convertedStack);
                }

            } catch (Exception e) {
                ModTabs.LOGGER.error("Failed to convert tome", e);
            }
        }

        private boolean isTome(ItemStack stack) {
            try {
                Class<?> tomeItemClass = Class.forName("website.eccentric.tome.TomeItem");
                return tomeItemClass.isInstance(stack.getItem());
            } catch (Exception e) {
                return false;
            }
        }

        // Delegate all other methods to the original screen
        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            try {
                originalTomeScreen.render(guiGraphics, mouseX, mouseY, partialTick);
            } catch (Exception e) {
                ModTabs.LOGGER.error("Error in original screen render", e);
            }
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return originalTomeScreen.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean isPauseScreen() {
            return originalTomeScreen.isPauseScreen();
        }

        @Override
        public void onClose() {
            originalTomeScreen.onClose();
            super.onClose();
        }
    }
}