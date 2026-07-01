package vodmordia.modtabs.client.keybinds;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

public class ModKeybinds {

    // 26.1: key categories are KeyMapping.Category objects, not String translation keys.
    public static final KeyMapping TAB_CYCLE = new KeyMapping(
        "key.modtabs.tab_cycle",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_TAB,
        KeyMapping.Category.INVENTORY
    );
}