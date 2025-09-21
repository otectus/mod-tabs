package vodmordia.modtabs.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.api.tabs_menu.TabDisplayMode;
import vodmordia.modtabs.config.Config;

import static vodmordia.modtabs.api.tabs_menu.TabBase.TAB_HEIGHT;
import static vodmordia.modtabs.api.tabs_menu.TabBase.TAB_WIDTH;


public class TabButton extends Button {
    public int tabPositionIndex;
    public TabBase tabBase;
    public Player player;
    public Screen screen;
    public boolean isDisabled;
    public TabDisplayMode displayMode;

    public TabButton(TabBase tabBase, Player player, Screen screen, int tabPositionIndex, int leftScreenPos, int topScreenPos, TabDisplayMode displayMode) {
        super(leftScreenPos + tabPositionIndex * (TAB_WIDTH + 1) + Config.Baked.tabsMenuOffsetX,
              calculateYPosition(topScreenPos, displayMode),
              TAB_WIDTH, TAB_HEIGHT, Component.literal(""), button -> {}, DEFAULT_NARRATION);


        this.tabPositionIndex = tabPositionIndex;
        this.player = player;
        this.screen = screen;
        this.displayMode = displayMode;
        this.setTabBase(tabBase);
    }

    private static int calculateYPosition(int topScreenPos, TabDisplayMode displayMode) {
        // Adjust Y position for inverted tabs - they hang down instead of up
        return displayMode == TabDisplayMode.INVERTED ?
            topScreenPos + Config.Baked.tabsMenuOffsetY :
            topScreenPos - TAB_HEIGHT + Config.Baked.tabsMenuOffsetY;
    }

    @Override
    public void onPress() {
        super.onPress();
        if (!this.isDisabled) {
            TabsMenu.markScreenOpenedViaTab(this.screen);
            tabBase.openTargetScreen(this.player);
        }
    }

    public void setTabBase(TabBase tabBase) {
        this.tabBase = tabBase;
        this.isDisabled = this.tabBase.isCurrentlyUsed(this.screen);
        this.setTooltip(Tooltip.create(this.tabBase.getTooltip()));
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics gui, int mouseX, int mouseY, float partial) {
        // Debug for FTB Quests tab specifically
        if (this.tabBase.getClass().getSimpleName().equals("FtbQuestsTab")) {
            ModTabs.LOGGER.info("TabButton: FtbQuestsTab renderWidget - displayMode: {}, position: ({}, {})",
                this.displayMode, this.getX(), this.getY());
        }

        // Standard tab rendering
        this.tabBase.render(gui, this.getX(), this.getY(), this.isDisabled || this.isMouseOver(mouseX, mouseY), this.displayMode);
    }

    public void updatePosition(int leftScreenPos, int topScreenPos) {
        setX(leftScreenPos + tabPositionIndex * (TAB_WIDTH + 1) + Config.Baked.tabsMenuOffsetX);
        setY(calculateYPosition(topScreenPos, displayMode));
    }
}
