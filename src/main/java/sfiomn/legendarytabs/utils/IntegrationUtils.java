package sfiomn.legendarytabs.utils;

import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sfiomn.legendarytabs.LegendaryTabs;

public class IntegrationUtils {
    public IntegrationUtils(){}

    public static int getBackpackWidth(Player player) {
        if (!LegendaryTabs.backpackedLoaded)
            return 0;

        ItemStack backpack = Services.BACKPACK.getBackpackStack(player);
        BackpackItem backpackItem = (BackpackItem)backpack.getItem();
        return 14 + Math.max(backpackItem.getColumnCount(), 9) * 18;
    }

    public static int getBackpackHeight(Player player) {
        if (!LegendaryTabs.backpackedLoaded)
            return 0;

        ItemStack backpack = Services.BACKPACK.getBackpackStack(player);
        BackpackItem backpackItem = (BackpackItem)backpack.getItem();
        return 114 + backpackItem.getRowCount() * 18;
    }
}
