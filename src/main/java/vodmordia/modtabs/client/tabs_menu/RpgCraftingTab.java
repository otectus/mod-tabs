package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.SimpleItemTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

@TabConfig(configKey = "rpgCraftingTab", defaultEnabled = true, defaultOrder = 0)
public class RpgCraftingTab extends SimpleItemTab {

    public RpgCraftingTab() {
        super(new ItemStack(Items.CRAFTING_TABLE));
    }

    @Override
    public void openTargetScreen(Player player) {
        try {
            // Use reflection to call RPGCraftingClient.openHandCraftingScreen()
            Class<?> rpgCraftingClientClass = Class.forName("com.github.theredbrain.rpgcrafting.RPGCraftingClient");
            java.lang.reflect.Method openScreenMethod = rpgCraftingClientClass.getMethod("openHandCraftingScreen", net.minecraft.client.Minecraft.class);
            openScreenMethod.invoke(null, Minecraft.getInstance());
        } catch (Exception e) {
            ModTabs.LOGGER.error("Failed to open RPG Crafting screen", e);
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.rpgCraftingTabEnabled &&
               ModIntegrationManager.isModLoaded(ModIntegration.RPG_CRAFTING);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        // Check if the current screen is HandCraftingScreen
        try {
            Class<?> handCraftingScreenClass = Class.forName("com.github.theredbrain.rpgcrafting.gui.screen.ingame.HandCraftingScreen");
            return handCraftingScreenClass.isInstance(currentScreen);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.rpgcrafting.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register the RPG Crafting screen with tabs using string-based registration
        ScreenRegistry.registerStandardScreens("com.github.theredbrain.rpgcrafting.gui.screen.ingame.HandCraftingScreen");
    }
}