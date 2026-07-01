package vodmordia.modtabs.client.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabDisplayMode;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;


public class NextTabsButton extends Button {
    private final Identifier TAB_ICONS = Identifier.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    public static final int NEXT_TABS_ICON_TEX_X = 135;
    public static final int NEXT_TABS_ICON_TEX_Y = 0;
    public static final int NEXT_TABS_BUTTON_WIDTH = 12;
    public static final int NEXT_TABS_BUTTON_HEIGHT = 21;
    public int tabPositionIndex;
    public TabDisplayMode displayMode;
    private int barLeft;
    private int barTop;

    public NextTabsButton(int tabPositionIndex, int leftScreenPos, int topScreenPos, net.minecraft.client.gui.components.Button.OnPress press) {
        this(tabPositionIndex, leftScreenPos, topScreenPos, TabDisplayMode.NORMAL, press);
    }

    public NextTabsButton(int tabPositionIndex, int leftScreenPos, int topScreenPos, TabDisplayMode displayMode, net.minecraft.client.gui.components.Button.OnPress press) {
        super(calculateX(leftScreenPos, tabPositionIndex),
              calculateY(topScreenPos, tabPositionIndex, displayMode),
              NEXT_TABS_BUTTON_WIDTH, NEXT_TABS_BUTTON_HEIGHT, Component.literal(""), press, DEFAULT_NARRATION);
        this.tabPositionIndex = tabPositionIndex;
        this.displayMode = displayMode;
        this.barLeft = leftScreenPos;
        this.barTop = topScreenPos;
    }

    private static int calculateX(int leftScreenPos, int tabPositionIndex) {
        return leftScreenPos + tabPositionIndex * TabsMenu.primaryAxisStep();
    }

    private static int calculateY(int topScreenPos, int tabPositionIndex, TabDisplayMode displayMode) {
        return displayMode == TabDisplayMode.INVERTED ?
            topScreenPos :
            topScreenPos - TabsMenu.effectiveTabHeight();
    }

    /** Natural anchor X (top-left in unrotated bar frame, including bar drag offset). */
    public int getAnimatedAnchorX() {
        int offX = TabsMenu.getAnimatedXOffset();
        Screen current = net.minecraft.client.Minecraft.getInstance().screen;
        if (current != null && TabsMenu.isEditing(current)) {
            offX += TabsMenu.getDragOffsetX();
        }
        return this.getX() + offX;
    }

    public int getAnimatedAnchorY() {
        int offY = TabsMenu.getAnimatedYOffset();
        Screen current = net.minecraft.client.Minecraft.getInstance().screen;
        if (current != null && TabsMenu.isEditing(current)) {
            offY += TabsMenu.getDragOffsetY();
        }
        return this.getY() + offY;
    }

    @Override
    public void extractContents(@NotNull GuiGraphicsExtractor gui, int mouseX, int mouseY, float partial) {
        // 26.1: behind-panel render gate removed (see TabButton) — always draw on top.
        // Recompute natural-frame position each render. Without this, live scale / spacing
        // edits don't shift the chevron's anchor — the user compensates with tempNextOffset,
        // and on save the natural anchor jumps to its correct (recomputed) spot while the
        // baked-in offset still applies, making the chevron visibly shift.
        setX(calculateX(this.barLeft, this.tabPositionIndex));
        setY(calculateY(this.barTop, this.tabPositionIndex, this.displayMode));
        int animatedX = getAnimatedAnchorX();
        int animatedY = getAnimatedAnchorY();
        float barRotation = TabsMenu.currentEffectiveRotation();
        int nextOffX = TabsMenu.currentNextOffsetX();
        int nextOffY = TabsMenu.currentNextOffsetY();
        float nextRotation = TabsMenu.currentNextEffectiveRotation();

        // Hit-test inverse-transforms mouse: undo screen offset, undo bar rotation, undo next rotation.
        int btnCenterX = animatedX + this.width / 2;
        int btnCenterY = animatedY + this.height / 2;
        double mx = mouseX - nextOffX;
        double my = mouseY - nextOffY;
        if (barRotation != 0f) {
            double[] center = TabsMenu.barCenter();
            double[] m = TabsMenu.inverseRotatePoint(mx, my, center[0], center[1], barRotation);
            mx = m[0]; my = m[1];
        }
        if (nextRotation != 0f) {
            double[] m = TabsMenu.inverseRotatePoint(mx, my, btnCenterX, btnCenterY, nextRotation);
            mx = m[0]; my = m[1];
        }
        boolean isMouseOverAnimated = mx >= animatedX && mx < animatedX + this.width &&
                                     my >= animatedY && my < animatedY + this.height;

        int texOffsetX = isMouseOverAnimated ? 54 : 0;

        // Apply outer transforms: next-button screen offset, then bar rotation around bar
        // center, then next-button's own rotation around its (natural-frame) center.
        boolean needsPose = nextOffX != 0 || nextOffY != 0 || barRotation != 0f || nextRotation != 0f;
        if (needsPose) {
            gui.pose().pushMatrix();
            if (nextOffX != 0 || nextOffY != 0) {
                gui.pose().translate((float) nextOffX, (float) nextOffY);
            }
            if (barRotation != 0f) {
                double[] center = TabsMenu.barCenter();
                gui.pose().translate((float) center[0], (float) center[1]);
                gui.pose().rotate((float) Math.toRadians(barRotation));
                gui.pose().translate((float) -center[0], (float) -center[1]);
            }
            if (nextRotation != 0f) {
                gui.pose().translate((float) btnCenterX, (float) btnCenterY);
                gui.pose().rotate((float) Math.toRadians(nextRotation));
                gui.pose().translate((float) -btnCenterX, (float) -btnCenterY);
            }
        }

        gui.blit(RenderPipelines.GUI_TEXTURED, TAB_ICONS, animatedX, animatedY, (float) (NEXT_TABS_ICON_TEX_X + texOffsetX), (float) NEXT_TABS_ICON_TEX_Y, NEXT_TABS_BUTTON_WIDTH, NEXT_TABS_BUTTON_HEIGHT, 256, 256);
        if (needsPose) {
            gui.pose().popMatrix();
        }
    }

    public void updatePosition(int leftScreenPos, int topScreenPos) {
        this.barLeft = leftScreenPos;
        this.barTop = topScreenPos;
        setX(calculateX(leftScreenPos, tabPositionIndex));
        setY(calculateY(topScreenPos, tabPositionIndex, displayMode));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        // In edit mode, don't consume clicks — let editor widgets registered after us
        // in screen.children() receive them. Without this, vanilla Button.mouseClicked
        // would swallow any click in our bounds (calling our no-op edit-mode onPress).
        net.minecraft.client.gui.screens.Screen current = net.minecraft.client.Minecraft.getInstance().screen;
        if (current != null && TabsMenu.isEditing(current)) {
            return false;
        }
        // Custom hit-test path: vanilla Button.mouseClicked uses clicked() which compares
        // against raw getX()/getY() bounds and ignores rotation. With a rotated bar (e.g.
        // 180°), the natural-frame bounds don't line up with the on-screen position, so
        // clicked() always missed and the next-page button was unclickable. isMouseOver()
        // already inverse-rotates the cursor; route through that instead.
        if (button == 0 && this.active && this.visible && this.isMouseOver(mouseX, mouseY)) {
            this.playDownSound(net.minecraft.client.Minecraft.getInstance().getSoundManager());
            this.onPress(new MouseButtonInfo(button, 0));
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        int animatedX = getAnimatedAnchorX();
        int animatedY = getAnimatedAnchorY();
        int btnCenterX = animatedX + this.width / 2;
        int btnCenterY = animatedY + this.height / 2;
        float barRotation = TabsMenu.currentEffectiveRotation();
        int nextOffX = TabsMenu.currentNextOffsetX();
        int nextOffY = TabsMenu.currentNextOffsetY();
        float nextRotation = TabsMenu.currentNextEffectiveRotation();

        double mx = mouseX - nextOffX;
        double my = mouseY - nextOffY;
        if (barRotation != 0f) {
            double[] center = TabsMenu.barCenter();
            double[] m = TabsMenu.inverseRotatePoint(mx, my, center[0], center[1], barRotation);
            mx = m[0]; my = m[1];
        }
        if (nextRotation != 0f) {
            double[] m = TabsMenu.inverseRotatePoint(mx, my, btnCenterX, btnCenterY, nextRotation);
            mx = m[0]; my = m[1];
        }
        return mx >= animatedX && mx < animatedX + this.width &&
               my >= animatedY && my < animatedY + this.height;
    }
}
