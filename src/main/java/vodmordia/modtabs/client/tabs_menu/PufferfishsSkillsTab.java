package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.puffish.skillsmod.client.SkillsClientMod;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.ConfigurableIconTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;

import java.util.Optional;

@TabConfig(configKey = "pufferfishSkillsTab", defaultEnabled = true, defaultOrder = 0)
public class PufferfishsSkillsTab extends ConfigurableIconTab {
    private static final ResourceLocation PUFFER_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/puffer.png");

    public PufferfishsSkillsTab() {
        super(PUFFER_ICON, Config.Baked.pufferfishSkillsTabCustomIcon, "pufferfishSkills");
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.pufferfishSkillsTabEnabled && player.level().isClientSide) {
            SkillsClientMod.getInstance().openScreen(Optional.empty());
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.pufferfishSkillsTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.PUFFERFISHS_SKILLS);
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        // Always return false so this tab is never disabled - we want it visible on all screens including SkillsScreen
        return false;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.pufferfish_skills.description");
    }

    @Override
    public void initTabOnScreens() {
        ScreenRegistry.builder()
            .withStandardDimensions()
            .inverted()
            .atTop()
            .registerAllTabs("net.puffish.skillsmod.client.gui.SkillsScreen");
    }
}
