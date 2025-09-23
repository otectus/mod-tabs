package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.SimpleTextureTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

import java.lang.reflect.Method;

@TabConfig(configKey = "cobblemonTab", defaultEnabled = true, defaultOrder = 0)
public class CobblemonTab extends SimpleTextureTab {
    private static final ResourceLocation POKEBALL_ICON = ResourceLocation.fromNamespaceAndPath("cobblemon", "textures/item/poke_balls/poke_ball.png");

    public CobblemonTab() {
        super(POKEBALL_ICON);
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
        return Config.Baked.cobblemonTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.COBBLEMON);
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
        ScreenRegistry.registerInvertedScreens(
            "com.cobblemon.mod.common.client.gui.summary.Summary",
            "com.cobblemon.mod.common.client.gui.startselection.StarterSelectionScreen"
        );
    }
}