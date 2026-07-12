package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.ConfigurableIconTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;

@TabConfig(configKey = "inventoryTab", defaultEnabled = true, defaultOrder = 0)
public class InventoryTab extends ConfigurableIconTab {
    private static final Identifier INVENTORY_ICON = Identifier.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/inventory.png");

    public InventoryTab() {
        super(INVENTORY_ICON, Config.Baked.inventoryTabCustomIcon, "inventory");
    }

    @Override
    public void openTargetScreen(Player player) {
        Minecraft mc = Minecraft.getInstance();
        // If a non-inventory menu is open (e.g. the player clicked this tab from a chest
        // screen mounted via ScreenRegistry.registerStandardScreens(ContainerScreen.class)),
        // close it with full vanilla semantics first — a plain setScreen(InventoryScreen)
        // only changes the client view and leaves the server still holding the container
        // menu, which desyncs the next interaction.
        TabsMenu.closeCurrentContainer();
        mc.setScreen(new InventoryScreen(player));
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.inventoryTabEnabled;
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        // InventoryTab should always be visible as it represents the "home" screen
        return false;
    }

    @Override
    public boolean isHomeTab(Screen currentScreen) {
        return currentScreen instanceof InventoryScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.inventory.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register common screens with all tabs using the new ScreenRegistry
        ScreenRegistry.registerStandardScreens(InventoryScreen.class);

        // Curios screen integration dropped from the minimal core (Curios is not a dependency here).
    }
}
