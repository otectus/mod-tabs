package sfiomn.legendarytabs.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import sfiomn.legendarytabs.LegendaryTabs;
import sfiomn.legendarytabs.config.Config;

import static sfiomn.legendarytabs.api.tabs_menu.TabBase.TAB_HEIGHT;
import static sfiomn.legendarytabs.api.tabs_menu.TabBase.TAB_WIDTH;

public class NextTabsButton extends Button {
    private final ResourceLocation TAB_ICONS = new ResourceLocation(LegendaryTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    public static final int NEXT_TABS_ICON_TEX_X = 135;
    public static final int NEXT_TABS_ICON_TEX_Y = 0;
    public static final int NEXT_TABS_BUTTON_WIDTH = 12;
    public static final int NEXT_TABS_BUTTON_HEIGHT = 21;
    public int tabPositionIndex;

    public NextTabsButton(int tabPositionIndex, int leftScreenPos, int topScreenPos, net.minecraft.client.gui.components.Button.OnPress press) {
        super(leftScreenPos + tabPositionIndex * (TAB_WIDTH + 1) + Config.Baked.tabsMenuOffsetX, topScreenPos - TAB_HEIGHT + Config.Baked.tabsMenuOffsetY, NEXT_TABS_BUTTON_WIDTH, NEXT_TABS_BUTTON_HEIGHT, Component.literal(""), press, DEFAULT_NARRATION);
        this.tabPositionIndex = tabPositionIndex;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics gui, int mouseX, int mouseY, float partial) {
        int texOffsetX = 0;
        if (this.isMouseOver(mouseX, mouseY))
            texOffsetX = 54;

        gui.blit(TAB_ICONS, this.getX(), this.getY(),NEXT_TABS_ICON_TEX_X + texOffsetX, NEXT_TABS_ICON_TEX_Y, NEXT_TABS_BUTTON_WIDTH, NEXT_TABS_BUTTON_HEIGHT);
    }

    public void updatePosition(int leftScreenPos, int topScreenPos) {
        setX(leftScreenPos + tabPositionIndex * (TAB_WIDTH + 1) + Config.Baked.tabsMenuOffsetX);
        setY(topScreenPos - TAB_HEIGHT + Config.Baked.tabsMenuOffsetY);
    }
}
