package sfiomn.legendarytabs.utils;

import com.illusivesoulworks.diet.api.type.IDietSuite;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sfiomn.legendarytabs.LegendaryTabs;

import java.util.Collection;
import java.util.Set;

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

    public static int getDietHeight(Player player) {
        if (Minecraft.getInstance().level == null)
            return 0;
        return ((Collection<?>) com.illusivesoulworks.diet.platform.Services.CAPABILITY.get(player)
                .map(
                        (tracker) -> (Set) DietSuites.getSuite(Minecraft.getInstance().level, tracker.getSuite()).map(IDietSuite::getGroups).orElse(Set.of()))
                .orElse(Set.of())).size() * 20 + 60;
    }
}
