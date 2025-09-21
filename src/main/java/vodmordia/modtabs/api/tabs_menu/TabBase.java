package vodmordia.modtabs.api.tabs_menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;


public abstract class TabBase {
    public static final int TAB_HEIGHT = 22;
    public static final int TAB_WIDTH = 26;

    public TabBase() {
    }

    public abstract void openTargetScreen(Player player);

    public abstract boolean isEnabled(Player player);

    public abstract void initTabOnScreens();

    public abstract void render(GuiGraphics gui, int x, int y, boolean hover);

    public void render(GuiGraphics gui, int x, int y, boolean hover, TabDisplayMode displayMode) {
        // Debug logging for FTB Quests tab specifically
        if (this.getClass().getSimpleName().equals("FtbQuestsTab")) {
            ModTabs.LOGGER.info("TabBase: FtbQuestsTab rendering - displayMode: {}, isInverted: {}, position: ({}, {})",
                displayMode, displayMode == TabDisplayMode.INVERTED, x, y);
        }

        if (displayMode == TabDisplayMode.INVERTED) {
            renderInverted(gui, x, y, hover);
        } else {
            render(gui, x, y, hover);
        }
    }

    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        // Default: subclasses should override for custom inverted rendering
        // Most tabs can use TabRenderer which handles inversion automatically
        render(gui, x, y, hover);
    }


    public abstract boolean isCurrentlyUsed(Screen currentScreen);

    public abstract Component getTooltip();
}
