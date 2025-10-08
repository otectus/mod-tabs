package vodmordia.modtabs.api.tabs_menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import vodmordia.modtabs.utils.IconResolver;

import java.util.function.Supplier;

/**
 * Base class for tabs that render an item icon but support custom texture overrides from config
 */
public abstract class ConfigurableItemTab extends SimpleItemTab {

    private final String customIconConfig;
    private final String tabId;
    private ResourceLocation cachedCustomIcon;
    private boolean iconResolved = false;

    /**
     * Creates a configurable item tab with a dynamic item supplier
     */
    protected ConfigurableItemTab(Supplier<ItemStack> iconItemSupplier, String customIconConfig, String tabId) {
        super(iconItemSupplier);
        this.customIconConfig = customIconConfig;
        this.tabId = tabId;
    }

    /**
     * Creates a configurable item tab with a fixed item
     */
    protected ConfigurableItemTab(ItemStack iconItem, String customIconConfig, String tabId) {
        super(iconItem);
        this.customIconConfig = customIconConfig;
        this.tabId = tabId;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        // Check if custom icon is configured
        ResourceLocation customIcon = getCustomIcon();
        if (customIcon != null) {
            // Render with custom texture
            TabRenderer.builder()
                .withBackground()
                .withTextureIcon(customIcon, 5, 4, 0, 0, 16, 16, 16, 16)
                .render(gui, x, y, hover, false);
        } else {
            // Render with item (default behavior)
            super.render(gui, x, y, hover);
        }
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        // Check if custom icon is configured
        ResourceLocation customIcon = getCustomIcon();
        if (customIcon != null) {
            // Render with custom texture
            TabRenderer.builder()
                .withBackground()
                .withTextureIcon(customIcon, 5, 4, 0, 0, 16, 16, 16, 16)
                .render(gui, x, y, hover, true);
        } else {
            // Render with item (default behavior)
            super.renderInverted(gui, x, y, hover);
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
