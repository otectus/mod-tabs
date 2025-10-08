package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
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

@TabConfig(configKey = "motpTab", defaultEnabled = true, defaultOrder = 0)
public class MotpTab extends SimpleTextureTab {
    private static final ResourceLocation MOTP_ICON = ResourceLocation.fromNamespaceAndPath("memory_of_the_past", "textures/item/book_02f.png");

    public MotpTab() {
        super(MOTP_ICON);
    }

    @Override
    public void openTargetScreen(Player player) {
        try {
            // MOTP uses NeoForge 1.21+ CustomPacketPayload system
            Class<?> messageClass = Class.forName("tn.mbs.memory.network.OpenStatsMenuKeybindMessage");
            Object message = messageClass.getConstructor(int.class, int.class).newInstance(0, 0);

            // Get the client's connection and send the packet
            net.minecraft.client.multiplayer.ClientPacketListener connection = Minecraft.getInstance().getConnection();

            if (connection != null) {
                // Use reflection to call send(CustomPacketPayload)
                java.lang.reflect.Method sendMethod = connection.getClass().getMethod("send",
                    Class.forName("net.minecraft.network.protocol.common.custom.CustomPacketPayload"));
                sendMethod.invoke(connection, message);
            }
        } catch (Exception e) {
            ModTabs.LOGGER.error("Error opening MOTP stats screen", e);
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.motpTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.MOTP);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> screenClass = Class.forName("tn.mbs.memory.client.gui.PlayerStatsGUIScreen");
            return screenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.motp.description");
    }

    @Override
    public void initTabOnScreens() {
        ScreenRegistry.builder()
            .withStandardDimensions()
            .inverted()
            .atTop()
            .registerAllTabs("tn.mbs.memory.client.gui.PlayerStatsGUIScreen");
    }
}
