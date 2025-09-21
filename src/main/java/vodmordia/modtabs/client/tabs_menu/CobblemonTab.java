package vodmordia.modtabs.client.tabs_menu;

import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.api.tabs_menu.TabDisplayMode;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;

import java.lang.reflect.Method;

public class CobblemonTab extends TabBase {
    private final ResourceLocation POKEBALL_ICON = ResourceLocation.fromNamespaceAndPath("cobblemon", "textures/item/poke_balls/poke_ball.png");

    public CobblemonTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.cobblemonTabEnabled && player.level().isClientSide) {
            try {
                // Get CobblemonClient storage
                Class<?> cobblemonClientClass = Class.forName("com.cobblemon.mod.common.client.CobblemonClient");
                java.lang.reflect.Field storageField = cobblemonClientClass.getDeclaredField("storage");
                storageField.setAccessible(true);
                Object storage = storageField.get(null);

                // Get party from storage
                java.lang.reflect.Field partyField = storage.getClass().getDeclaredField("myParty");
                partyField.setAccessible(true);
                Object party = partyField.get(storage);

                // Get slots from party
                java.lang.reflect.Field slotsField = party.getClass().getDeclaredField("slots");
                slotsField.setAccessible(true);
                Object slots = slotsField.get(party);

                // Get selected slot
                int selectedSlot = 0;
                try {
                    java.lang.reflect.Field selectedSlotField = storage.getClass().getDeclaredField("selectedSlot");
                    selectedSlotField.setAccessible(true);
                    Object selectedSlotObj = selectedSlotField.get(storage);
                    if (selectedSlotObj instanceof Integer && (Integer) selectedSlotObj >= 0) {
                        selectedSlot = (Integer) selectedSlotObj;
                    }
                } catch (Exception ignored) {}

                // Check if player has any Pokemon in their party
                boolean hasAnyPokemon = false;
                if (slots instanceof java.util.Collection) {
                    java.util.Collection<?> collection = (java.util.Collection<?>) slots;
                    hasAnyPokemon = collection.stream().anyMatch(pokemon -> pokemon != null);
                }

                if (hasAnyPokemon) {
                    // Open party summary if player has Pokemon
                    Class<?> companionClass = Class.forName("com.cobblemon.mod.common.client.gui.summary.Summary$Companion");
                    Class<?> summaryClass = Class.forName("com.cobblemon.mod.common.client.gui.summary.Summary");
                    java.lang.reflect.Field companionField = summaryClass.getField("Companion");
                    Object companionInstance = companionField.get(null);
                    Method openMethod = companionClass.getMethod("open", java.util.Collection.class, boolean.class, int.class);
                    openMethod.invoke(companionInstance, slots, true, selectedSlot);
                } else {
                    // Request starter screen if player has no Pokemon
                    Class<?> packetClass = Class.forName("com.cobblemon.mod.common.net.messages.server.starter.RequestStarterScreenPacket");
                    Object packet = packetClass.getDeclaredConstructor().newInstance();

                    Class<?> networkClass = Class.forName("com.cobblemon.mod.common.CobblemonNetwork");
                    Class<?> networkPacketClass = Class.forName("com.cobblemon.mod.common.api.net.NetworkPacket");
                    Object networkInstance = networkClass.getField("INSTANCE").get(null);

                    Method sendToServerMethod = networkClass.getMethod("sendToServer", networkPacketClass);
                    sendToServerMethod.invoke(networkInstance, packet);
                }

            } catch (Exception e) {
            }
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.cobblemonTabEnabled && ModTabs.cobblemonLoaded;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(POKEBALL_ICON, 5, 3, 16, 16)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(POKEBALL_ICON, 5, 3, 16, 16)
            .render(gui, x, y, hover, true);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            // Check for both Summary screen and Starter Selection screen
            Class<?> summaryScreenClass = Class.forName("com.cobblemon.mod.common.client.gui.summary.Summary");
            Class<?> starterScreenClass = Class.forName("com.cobblemon.mod.common.client.gui.startselection.StarterSelectionScreen");
            return summaryScreenClass.isInstance(currentScreen) || starterScreenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.cobblemon.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register both Cobblemon screens - Summary and Starter Selection with inverted display
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Screen> summaryScreenClass = (Class<? extends Screen>) Class.forName("com.cobblemon.mod.common.client.gui.summary.Summary");
            TabsMenu.registerScreenWithAllTabs(summaryScreenClass, (player) -> 176, (player) -> 166, TabDisplayMode.INVERTED, TabPositioning.SCREEN_TOP);
        } catch (ClassNotFoundException e) {
            // Summary screen not available
        }

        try {
            @SuppressWarnings("unchecked")
            Class<? extends Screen> starterScreenClass = (Class<? extends Screen>) Class.forName("com.cobblemon.mod.common.client.gui.startselection.StarterSelectionScreen");
            TabsMenu.registerScreenWithAllTabs(starterScreenClass, (player) -> 176, (player) -> 166, TabDisplayMode.INVERTED, TabPositioning.SCREEN_TOP);
        } catch (ClassNotFoundException e) {
            // Starter selection screen not available
        }
    }
}