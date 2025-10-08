package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import sfiomn.legendarysurvivaloverhaul.client.ClientHooks;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.ConfigurableItemTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

import static sfiomn.legendarysurvivaloverhaul.config.Config.Baked.localizedBodyDamageEnabled;

@TabConfig(configKey = "bodyDamageTab", defaultEnabled = true, defaultOrder = 0)
public class BodyDamageTab extends ConfigurableItemTab {

    public BodyDamageTab() {
        super(() -> getFirstAidItem(), Config.Baked.bodyDamageTabCustomIcon, "bodyDamage");
    }

    private static ItemStack getFirstAidItem() {
        // Try to get LSO's First Aid Supplies item via reflection, fallback to vanilla item
        try {
            Class<?> itemsClass = Class.forName("sfiomn.legendarysurvivaloverhaul.registry.ItemRegistry");
            Field firstAidField = itemsClass.getField("FIRST_AID_SUPPLIES");
            Object registryObject = firstAidField.get(null);
            Method getMethod = registryObject.getClass().getMethod("get");
            Item firstAidItem = (Item) getMethod.invoke(registryObject);
            return new ItemStack(firstAidItem);
        } catch (Exception e) {
            // Fallback to vanilla item (heart)
            return new ItemStack(Items.GLISTERING_MELON_SLICE);
        }
    }

    @Override
    public void openTargetScreen(Player player) {
        if (ModIntegrationManager.isModLoaded(ModIntegration.LEGENDARY_SURVIVAL_OVERHAUL) && localizedBodyDamageEnabled)
            ClientHooks.openBodyHealthScreen(player);
    }

    @Override
    public boolean isEnabled(Player player) {
        return ModIntegrationManager.isModLoaded(ModIntegration.LEGENDARY_SURVIVAL_OVERHAUL) && Config.Baked.bodyDamageTabEnabled && localizedBodyDamageEnabled;
    }


    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        if (!localizedBodyDamageEnabled) return false;
        try {
            Class<?> bodyHealthScreenClass = Class.forName("sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen");
            return bodyHealthScreenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.body_damage.description");
    }

    @Override
    public void initTabOnScreens() {
        try {
            Class<?> bodyHealthScreenClass = Class.forName("sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen");
            @SuppressWarnings("unchecked")
            Class<? extends Screen> screenClass = (Class<? extends Screen>) bodyHealthScreenClass;
            ScreenRegistry.builder()
                .withBodyHealthDimensions()
                .withPositioning(TabPositioning.GUI_RELATIVE)
                .registerAllTabs(screenClass);
        } catch (ClassNotFoundException e) {
            // Legendary Survival Overhaul not present, skip registration
        }
    }
}
