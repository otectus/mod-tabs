package sfiomn.legendarytabs.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import sfiomn.legendarytabs.LegendaryTabs;
import sfiomn.legendarytabs.api.tabs_menu.TabBase;
import sfiomn.legendarytabs.api.tabs_menu.TabsMenu;
import sfiomn.legendarytabs.config.Config;

import static sfiomn.legendarytabs.api.tabs_menu.TabBase.TAB_HEIGHT;
import static sfiomn.legendarytabs.api.tabs_menu.TabBase.TAB_WIDTH;


public class TabButton extends Button {
    public int tabPositionIndex;
    public TabBase tabBase;
    public Player player;
    public Screen screen;
    public boolean isDisabled ;

    public TabButton(TabBase tabBase, Player player, Screen screen, int tabPositionIndex, int leftScreenPos, int topScreenPos) {
        super(leftScreenPos + tabPositionIndex * (TAB_WIDTH + 1) + Config.Baked.tabsMenuOffsetX, topScreenPos - TAB_HEIGHT + Config.Baked.tabsMenuOffsetY, TAB_WIDTH, TAB_HEIGHT, Component.literal(""), button -> {}, DEFAULT_NARRATION);
        this.tabPositionIndex = tabPositionIndex;
        this.player = player;
        this.screen = screen;
        this.setTabBase(tabBase);
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
        this.tabBase.render(gui, this.getX(), this.getY(), this.isDisabled || this.isMouseOver(mouseX, mouseY));
    }

    public void updatePosition(int leftScreenPos, int topScreenPos) {
        setX(leftScreenPos + tabPositionIndex * (TAB_WIDTH + 1) + Config.Baked.tabsMenuOffsetX);
        setY(topScreenPos - TAB_HEIGHT + Config.Baked.tabsMenuOffsetY);
    }
}
