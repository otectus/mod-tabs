package vodmordia.modtabs.client.tabs_menu;

import net.bandit.reskillable.client.screen.SkillScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.CustomIconTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

@TabConfig(configKey = "reskillableReimaginedTab", defaultEnabled = true, defaultOrder = 0)
public class ReskillableReimaginedTab extends CustomIconTab {

    public ReskillableReimaginedTab() {
        super((context) -> {
            // Render the book icon from Reskillable's skills.png
            try {
                // Book icon coordinates based on exact measurements
                int u = 241; // X position of book icon
                int v = 145; // Y position of book icon

                // Draw the 16x16 book icon, scaled down to 14x14 and moved down 2px
                context.gui.blit(ResourceLocation.fromNamespaceAndPath("reskillable", "textures/gui/skills.png"),
                    context.x + 6, context.y + 6, u, v, 14, 14, 256, 256);
            } catch (Exception e) {
                context.gui.fill(context.x + 7, context.y + 5, context.x + 19, context.y + 17, 0xFF8B4513);
                context.gui.fill(context.x + 9, context.y + 7, context.x + 17, context.y + 15, 0xFFFFF8DC);
            }
        });
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.reskillableReimaginedTabEnabled && player.level().isClientSide) {
            Minecraft.getInstance().setScreen(new SkillScreen());
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.reskillableReimaginedTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.RESKILLABLE_REIMAGINED);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return currentScreen instanceof SkillScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.reskillable_reimagined.description");
    }

    @Override
    public void initTabOnScreens() {
        ScreenRegistry.builder()
            .withStandardDimensions()
            .withPositioning(TabPositioning.GUI_RELATIVE)
            .registerAllTabs(SkillScreen.class);
    }
}
