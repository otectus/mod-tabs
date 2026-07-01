package vodmordia.modtabs.api.tabs_menu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Base class for tabs that simply render a texture icon
 */
@OnlyIn(Dist.CLIENT)
public abstract class SimpleTextureTab extends TabBase {

    private final Identifier iconTexture;
    private final int iconWidth;
    private final int iconHeight;

    /**
     * Creates a simple texture tab with 16x16 icon at position (5, 4)
     */
    protected SimpleTextureTab(Identifier iconTexture) {
        this(iconTexture, 16, 16);
    }

    /**
     * Creates a simple texture tab with custom icon size at position (5, 4)
     */
    protected SimpleTextureTab(Identifier iconTexture, int iconWidth, int iconHeight) {
        this.iconTexture = iconTexture;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    public void render(GuiGraphicsExtractor gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(iconTexture, 5, 4, iconWidth, iconHeight)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphicsExtractor gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(iconTexture, 5, 4, iconWidth, iconHeight)
            .render(gui, x, y, hover, true);
    }

    /**
     * Get the icon texture for this tab
     */
    protected Identifier getIconTexture() {
        return iconTexture;
    }
}