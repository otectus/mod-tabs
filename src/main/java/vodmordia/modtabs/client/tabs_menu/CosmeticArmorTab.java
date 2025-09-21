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
import vodmordia.modtabs.config.Config;

public class CosmeticArmorTab extends TabBase {

    public CosmeticArmorTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.inventoryTabEnabled) { // Using inventoryTabEnabled as there's no specific config for cosmetic armor
            try {
                // Use reflection to send the PayloadOpenCosArmorInventory packet
                Class<?> packetDistributorClass = Class.forName("net.neoforged.neoforge.network.PacketDistributor");
                Class<?> payloadClass = Class.forName("lain.mods.cos.impl.network.payload.PayloadOpenCosArmorInventory");

                // Create the payload using default constructor
                Object payload = payloadClass.getDeclaredConstructor().newInstance();

                // Find the sendToServer method
                for (java.lang.reflect.Method method : packetDistributorClass.getDeclaredMethods()) {
                    if (method.getName().equals("sendToServer") && method.getParameterCount() == 2) {
                        Class<?>[] paramTypes = method.getParameterTypes();
                        // Create empty array for the second parameter
                        Object[] emptyArray = (Object[]) java.lang.reflect.Array.newInstance(paramTypes[1].getComponentType(), 0);
                        method.invoke(null, payload, emptyArray);
                        return;
                    }
                }
            } catch (Exception e) {
                // Silent fail if packet sending doesn't work
            }
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.inventoryTabEnabled; // Always enabled when inventory tab is enabled
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        ResourceLocation customIcon = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/cosmeticarmor.png");
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(customIcon, 5, 4, 16, 16)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        ResourceLocation customIcon = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/cosmeticarmor.png");
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(customIcon, 5, 4, 16, 16)
            .render(gui, x, y, hover, true);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return currentScreen instanceof GuiCosArmorInventory;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.cosmetic_armor.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register only this tab's own screen - the Cosmetic Armor screen
        TabsMenu.registerScreenWithAllTabs(GuiCosArmorInventory.class, (player) -> 176, (player) -> 166);
    }
}