package vodmordia.modtabs.client.keybinds;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

public class ModKeybinds {

    // The Shift/Ctrl requirement lives in the KeyMapping itself (NeoForge KeyModifier)
    // rather than a hardcoded GLFW mask in the handler, so the whole combination is
    // rebindable from the Controls screen and controller mods can map it cleanly.
    // 26.1: key categories are KeyMapping.Category objects, not String translation keys.
    public static final KeyMapping TAB_CYCLE = new KeyMapping(
        "key.modtabs.tab_cycle",
        KeyConflictContext.GUI,
        KeyModifier.SHIFT,
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_TAB,
        KeyMapping.Category.INVENTORY
    );

    public static final KeyMapping TAB_CYCLE_BACK = new KeyMapping(
        "key.modtabs.tab_cycle_back",
        KeyConflictContext.GUI,
        KeyModifier.CONTROL,
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_TAB,
        KeyMapping.Category.INVENTORY
    );
}
