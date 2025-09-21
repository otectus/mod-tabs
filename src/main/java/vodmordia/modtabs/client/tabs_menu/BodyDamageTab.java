package vodmordia.modtabs.client.tabs_menu;

import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import sfiomn.legendarysurvivaloverhaul.client.ClientHooks;
import sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;

import static sfiomn.legendarysurvivaloverhaul.config.Config.Baked.localizedBodyDamageEnabled;

public class BodyDamageTab extends TabBase {

    public BodyDamageTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        if (ModTabs.legendarySurvivalOverhaulLoaded && localizedBodyDamageEnabled)
            ClientHooks.openBodyHealthScreen(player);
    }

    @Override
    public boolean isEnabled(Player player) {
        return ModTabs.legendarySurvivalOverhaulLoaded && Config.Baked.bodyDamageTabEnabled && localizedBodyDamageEnabled;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(getFirstAidItem(), 5, 3)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withItemIcon(getFirstAidItem(), 5, 3)
            .render(gui, x, y, hover, true);
    }

    private ItemStack getFirstAidItem() {
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
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return localizedBodyDamageEnabled && currentScreen instanceof BodyHealthScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.body_damage.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register only this tab's own screen - the Body Health screen
        if (ModTabs.legendarySurvivalOverhaulLoaded && Config.Baked.includeOpenedScreenTab)
            TabsMenu.registerScreenWithAllTabs(BodyHealthScreen.class, (player) -> 176, (player) -> 183);
    }
}
