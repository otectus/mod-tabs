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
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;

import java.lang.reflect.Method;

public class CobblemonTab extends TabBase {
    private final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final ResourceLocation POKEBALL_ICON = ResourceLocation.fromNamespaceAndPath("cobblemon", "textures/item/poke_balls/poke_ball.png");
    private final int TAB_ICON_TEX_X = 0;
    private final int TAB_ICON_TEX_Y = 138;

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
                ModTabs.LOGGER.error("Failed to open Cobblemon GUI: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.cobblemonTabEnabled && ModTabs.cobblemonLoaded;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        int texOffsetX = 0;
        if (hover)
            texOffsetX = 54;

        gui.blit(TAB_ICONS, x, y, TAB_ICON_TEX_X + texOffsetX, TAB_ICON_TEX_Y, TAB_WIDTH, TAB_HEIGHT);

        int scale = 16;
        gui.blit(POKEBALL_ICON, x + 5, y + 3, 0, 0, scale, scale, scale, scale);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> summaryScreenClass = Class.forName("com.cobblemon.mod.common.client.gui.summary.Summary");
            return summaryScreenClass.isInstance(currentScreen);
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
        TabsMenu.addTabToScreen(this, InventoryScreen.class, (player) -> 176, (player) -> 166, 65);

        if (ModTabs.curiosLoaded)
            TabsMenu.addTabToScreen(this, CuriosScreen.class, (player) -> 176, (player) -> 166, 65);

        if (ModTabs.cosmeticArmorLoaded)
            TabsMenu.addTabToScreen(this, GuiCosArmorInventory.class, (player) -> 176, (player) -> 166, 65);

        if (ModTabs.travelersBackpackLoaded)
            TabsMenu.addTabToScreen(this, com.tiviacz.travelersbackpack.client.screens.BackpackScreen.class, IntegrationUtils::getTravelersBackpackWidth, IntegrationUtils::getTravelersBackpackHeight, 65);
    }
}