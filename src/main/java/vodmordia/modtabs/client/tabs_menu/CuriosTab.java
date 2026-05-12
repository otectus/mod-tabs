package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.IntegrationItemTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.TabSpec;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.utils.ClassCache;
import vodmordia.modtabs.utils.ScreenClasses;

import java.lang.reflect.Constructor;

/**
 * Tab for Curios. Reuses the mod's own packet path
 * ({@code CPacketOpenCurios}): the client sends an empty-carry packet to the
 * server, which opens the curios menu — same flow as clicking the mod's own
 * CuriosButton on the inventory screen.
 *
 * <p>The packet class is resolved through {@link ClassCache} so this class
 * compiles and loads without a hard runtime dependency on Curios.
 */
@TabConfig(configKey = "curiosTab", defaultEnabled = true, defaultOrder = 0)
public class CuriosTab extends IntegrationItemTab {

    private static final TabSpec SPEC = new TabSpec(
            "curiosTab",
            ModIntegration.CURIOS,
            () -> Config.Baked.curiosTabEnabled,
            "curios",
            "curios",
            TabSpec.Layout.guiRelative(),
            new String[] { ScreenClasses.CURIOS_SCREEN },
            new String[] { ScreenClasses.CURIOS_SCREEN }
    );

    public CuriosTab() {
        super(SPEC, () -> new ItemStack(Items.NETHER_STAR), Config.Baked.curiosTabCustomIcon);
    }

    @Override
    public void openTargetScreen(Player player) {
        if (!Config.Baked.curiosTabEnabled || !player.level().isClientSide) return;
        try {
            Class<?> packetClass = ClassCache.resolve(ScreenClasses.CURIOS_OPEN_PACKET);
            if (packetClass == null) return;
            Constructor<?> ctor = packetClass.getConstructor(ItemStack.class);
            Object packet = ctor.newInstance(ItemStack.EMPTY);
            PacketDistributor.sendToServer((CustomPacketPayload) packet);
        } catch (Exception e) {
            ModTabs.LOGGER.debug("Error opening Curios screen: " + e.getMessage());
        }
    }
}
