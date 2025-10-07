package vodmordia.modtabs.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import vodmordia.modtabs.ModTabs;

import java.lang.reflect.Method;

public class TomeConvertHandler {

    public static void handle(TomeConvertPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                try {
                    int tomeSlot = payload.tomeSlot();
                    ItemStack selectedBook = payload.selectedBook();

                    ModTabs.LOGGER.info("Server received TomeConvertPayload: slot={}, book={}", tomeSlot, selectedBook);

                    // Get the tome stack from the player's inventory
                    ItemStack tomeStack = serverPlayer.getInventory().getItem(tomeSlot);

                    if (tomeStack.isEmpty()) {
                        ModTabs.LOGGER.warn("Tome stack is empty at slot {}", tomeSlot);
                        return;
                    }

                    // Use TomeUtils.convert to convert the tome
                    Class<?> tomeUtilsClass = Class.forName("website.eccentric.tome.TomeUtils");
                    Method convertMethod = tomeUtilsClass.getMethod("convert", ItemStack.class, ItemStack.class);
                    ItemStack convertedBook = (ItemStack) convertMethod.invoke(null, tomeStack, selectedBook);

                    ModTabs.LOGGER.info("Server converted tome to: {}", convertedBook);

                    // Replace the tome in the player's inventory
                    serverPlayer.getInventory().setItem(tomeSlot, convertedBook);

                    // Notify the client that the inventory has changed
                    serverPlayer.containerMenu.broadcastChanges();

                    ModTabs.LOGGER.info("Successfully converted tome in slot {} to book {}", tomeSlot, convertedBook.getItem());

                } catch (Exception e) {
                    ModTabs.LOGGER.error("Failed to handle tome convert on server", e);
                }
            }
        });
    }
}
