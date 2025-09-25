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
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;


@TabConfig(configKey = "mapAtlasesTab", defaultEnabled = true, defaultOrder = 0)
public class MapAtlasesTab extends SimpleTextureTab {
    private static final ResourceLocation MAP_ATLAS_ICON = ResourceLocation.fromNamespaceAndPath("map_atlases", "textures/item/atlas_generic.png");

    public MapAtlasesTab() {
        super(MAP_ATLAS_ICON);
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.mapAtlasesTabEnabled && player.level().isClientSide) {
            try {
                Class<?> mapAtlasesAccessUtilsClass = Class.forName("pepjebs.mapatlases.utils.MapAtlasesAccessUtils");
                java.lang.reflect.Method getAtlasMethod = mapAtlasesAccessUtilsClass.getMethod("getAtlasFromPlayerByConfig", Player.class);
                ItemStack atlas = (ItemStack) getAtlasMethod.invoke(null, player);

                Class<?> mapAtlasItemClass = Class.forName("pepjebs.mapatlases.item.MapAtlasItem");
                if (mapAtlasItemClass.isInstance(atlas.getItem())) {
                    Class<?> networkHelperClass = Class.forName("net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper");
                    Class<?> packetClass = Class.forName("pepjebs.mapatlases.networking.C2S2COpenAtlasScreenPacket");
                    Class<?> customPacketPayloadClass = Class.forName("net.minecraft.network.protocol.common.custom.CustomPacketPayload");

                    Object packet = packetClass.getDeclaredConstructor().newInstance();
                    java.lang.reflect.Method sendToServerMethod = networkHelperClass.getMethod("sendToServer", customPacketPayloadClass);
                    sendToServerMethod.invoke(null, packet);
                }
            } catch (Exception e) {
                // Map Atlases not present or failed to open atlas
            }
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        if (!Config.Baked.mapAtlasesTabEnabled || !ModIntegrationManager.isModLoaded(ModIntegration.MAP_ATLASES)) {
            return false;
        }
        try {
            Class<?> mapAtlasesAccessUtilsClass = Class.forName("pepjebs.mapatlases.utils.MapAtlasesAccessUtils");
            java.lang.reflect.Method getAtlasMethod = mapAtlasesAccessUtilsClass.getMethod("getAtlasFromPlayerByConfig", Player.class);
            ItemStack atlas = (ItemStack) getAtlasMethod.invoke(null, player);

            Class<?> mapAtlasItemClass = Class.forName("pepjebs.mapatlases.item.MapAtlasItem");
            return mapAtlasItemClass.isInstance(atlas.getItem());
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return false;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.map_atlases.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register Map Atlases screen with inverted tabs at the top
        ScreenRegistry.registerInvertedScreens("pepjebs.mapatlases.client.screen.AtlasOverviewScreen");
    }
}
