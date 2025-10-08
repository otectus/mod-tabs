package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.resources.ResourceLocation;
// import dev.xkmc.l2artifacts.content.client.tab.SetEffectScreen; // Available at runtime only
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.ConfigurableItemTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@TabConfig(configKey = "l2ArtifactsTab", defaultEnabled = true, defaultOrder = 0)
public class L2ArtifactsTab extends ConfigurableItemTab {

    public L2ArtifactsTab() {
        super(() -> getSelectItem(), Config.Baked.l2ArtifactsTabCustomIcon, "l2Artifacts");
    }

    private static ItemStack getSelectItem() {
        // Try to get L2 Artifacts' SELECT item via reflection, fallback to vanilla item
        try {
            Class<?> itemsClass = Class.forName("dev.xkmc.l2artifacts.init.registrate.items.ArtifactItems");
            Field selectField = itemsClass.getField("SELECT");
            Object registryObject = selectField.get(null);
            Method getMethod = registryObject.getClass().getMethod("get");
            Item selectItem = (Item) getMethod.invoke(registryObject);
            return new ItemStack(selectItem);
        } catch (Exception e) {
            // Fallback to vanilla item
            return new ItemStack(Items.NETHER_STAR);
        }
    }

    @Override
    public void openTargetScreen(Player player) {
        try {
            // Use reflection to access the constructor
            Class<?> screenClass = Class.forName("dev.xkmc.l2artifacts.content.client.tab.SetEffectScreen");
            
            // Try no-arg constructor first
            try {
                java.lang.reflect.Constructor<?> constructor = screenClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object screen = constructor.newInstance();
                Minecraft.getInstance().setScreen((Screen) screen);
                return;
            } catch (NoSuchMethodException e) {
                // Fall through to try other constructors
            }
            
            // If no-arg fails, try all available constructors
            java.lang.reflect.Constructor<?>[] constructors = screenClass.getDeclaredConstructors();
            for (java.lang.reflect.Constructor<?> constructor : constructors) {
                constructor.setAccessible(true);
                Class<?>[] paramTypes = constructor.getParameterTypes();
                
                if (paramTypes.length == 0) {
                    Object screen = constructor.newInstance();
                    Minecraft.getInstance().setScreen((Screen) screen);
                    return;
                } else if (paramTypes.length == 1 && paramTypes[0].equals(Component.class)) {
                    Object screen = constructor.newInstance(Component.translatable("l2artifacts.gui.set_effects"));
                    Minecraft.getInstance().setScreen((Screen) screen);
                    return;
                } else if (paramTypes.length == 2 && paramTypes[0].equals(Component.class) && paramTypes[1].equals(int.class)) {
                    // Constructor(Component, int) - try with 0 as the int parameter (page number)
                    Object screen = constructor.newInstance(Component.translatable("l2artifacts.gui.set_effects"), 0);
                    Minecraft.getInstance().setScreen((Screen) screen);
                    return;
                }
            }
            
            
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.l2ArtifactsTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.L2_ARTIFACTS);
    }


    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> screenClass = Class.forName("dev.xkmc.l2artifacts.content.client.tab.SetEffectScreen");
            return screenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.l2_artifacts.description");
    }

    @Override
    public void initTabOnScreens() {
        ScreenRegistry.builder()
            .withStandardDimensions()
            .withPositioning(TabPositioning.GUI_RELATIVE)
            .registerAllTabs("dev.xkmc.l2artifacts.content.client.tab.SetEffectScreen");
    }
}