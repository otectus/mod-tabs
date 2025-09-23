package vodmordia.modtabs.api.tabs_menu;

import net.minecraft.client.gui.GuiGraphics;
import java.util.function.Consumer;

/**
 * Base class for tabs that need custom icon rendering beyond simple textures or items.
 * Provides the TabRenderer framework with custom icon rendering capability.
 */
public abstract class CustomIconTab extends TabBase {
    private final Consumer<TabRenderer.RenderContext> iconRenderer;

    public CustomIconTab(Consumer<TabRenderer.RenderContext> iconRenderer) {
        super();
        this.iconRenderer = iconRenderer;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withCustomIcon(iconRenderer)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withCustomIcon(iconRenderer)
            .render(gui, x, y, hover, true);
    }
}