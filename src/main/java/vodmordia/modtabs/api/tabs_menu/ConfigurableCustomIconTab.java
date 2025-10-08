package vodmordia.modtabs.api.tabs_menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import vodmordia.modtabs.utils.IconResolver;

import java.util.function.Consumer;

/**
 * A tab that supports both custom icon configuration and custom rendering logic.
 * If a custom icon is configured, it will be used. Otherwise, the custom rendering logic will be used.
 */
public abstract class ConfigurableCustomIconTab extends CustomIconTab {
    private final String customIconConfig;
    private final String tabId;
    private ResourceLocation cachedCustomIcon;
    private boolean iconResolved = false;
    private final Consumer<TabRenderer.RenderContext> customRenderer;

    protected ConfigurableCustomIconTab(Consumer<TabRenderer.RenderContext> customRenderer, String customIconConfig, String tabId) {
        super(customRenderer);
        this.customRenderer = customRenderer;
        this.customIconConfig = customIconConfig;
        this.tabId = tabId;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        ResourceLocation customIcon = getCustomIcon();
        if (customIcon != null) {
            // Render with custom texture from config
            TabRenderer.builder()
                .withBackground()
                .withTextureIcon(customIcon, 5, 4, 0, 0, 16, 16, 16, 16)
                .render(gui, x, y, hover, false);
        } else {
            // Render with custom rendering logic (fallback)
            super.render(gui, x, y, hover);
        }
    }

    private ResourceLocation getCustomIcon() {
        if (!iconResolved) {
            cachedCustomIcon = IconResolver.resolveIcon(customIconConfig, tabId);
            iconResolved = true;
        }
        return cachedCustomIcon;
    }
}
