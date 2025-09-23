package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.SimpleTextureTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import net.minecraft.client.gui.GuiGraphics;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

@TabConfig(configKey = "draconicEvolutionTab", defaultEnabled = true, defaultOrder = 0)
public class DraconicEvolutionTab extends SimpleTextureTab {
    private static final ResourceLocation INFO_TABLET_ICON = ResourceLocation.fromNamespaceAndPath("draconicevolution", "textures/item/info_tablet.png");

    public DraconicEvolutionTab() {
        super(INFO_TABLET_ICON);
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.draconicEvolutionTabEnabled && player.level().isClientSide) {
            try {
                // Use reflection to access DraconicNetwork.sendOpenItemConfig method
                Class<?> draconicNetworkClass = Class.forName("com.brandon3055.draconicevolution.network.DraconicNetwork");
                java.lang.reflect.Method sendOpenItemConfigMethod = draconicNetworkClass.getMethod("sendOpenItemConfig", net.minecraft.core.RegistryAccess.class, boolean.class);

                // Send the network packet to open Tool Config GUI (false = not modules mode)
                sendOpenItemConfigMethod.invoke(null, player.registryAccess(), false);

            } catch (Exception e) {
                // Silently handle any reflection errors
                
            }
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.draconicEvolutionTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.DRACONIC_EVOLUTION) && hasConfigurableItem(player);
    }

    private boolean hasConfigurableItem(Player player) {
        try {
            // Use reflection to check for DECapabilities.Host.ITEM capability
            Class<?> deCapabilitiesClass = Class.forName("com.brandon3055.draconicevolution.api.capability.DECapabilities");
            Class<?> hostClass = Class.forName("com.brandon3055.draconicevolution.api.capability.DECapabilities$Host");
            java.lang.reflect.Field itemField = hostClass.getDeclaredField("ITEM");
            Object itemCapability = itemField.get(null);

            // Check main inventory
            for (ItemStack stack : player.getInventory().items) {
                if (!stack.isEmpty() && hasCapability(stack, itemCapability)) {
                    return true;
                }
            }

            // Check armor slots
            for (ItemStack stack : player.getInventory().armor) {
                if (!stack.isEmpty() && hasCapability(stack, itemCapability)) {
                    return true;
                }
            }

            // Check offhand
            ItemStack offhandStack = player.getOffhandItem();
            if (!offhandStack.isEmpty() && hasCapability(offhandStack, itemCapability)) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasCapability(ItemStack stack, Object capability) {
        try {
            Class<?> itemCapabilityClass = Class.forName("net.neoforged.neoforge.capabilities.ItemCapability");
            java.lang.reflect.Method getCapabilityMethod = ItemStack.class.getMethod("getCapability", itemCapabilityClass);
            Object result = getCapabilityMethod.invoke(stack, capability);
            return result != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> screenClass = Class.forName("com.brandon3055.draconicevolution.client.gui.modular.itemconfig.ConfigurableItemGui$Screen");
            return screenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.draconic_evolution.description");
    }

    @Override
    public void initTabOnScreens() {
        ScreenRegistry.builder()
            .withDimensions((player) -> 218, (player) -> 230)
            .inverted()
            .atTop()
            .forceRegisterAllTabs("com.brandon3055.draconicevolution.client.gui.modular.itemconfig.ConfigurableItemGui$Screen");
    }
}