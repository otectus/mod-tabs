package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.ConfigurableIconTab;
import vodmordia.modtabs.api.tabs_menu.TabConfig;
import vodmordia.modtabs.api.tabs_menu.ScreenRegistry;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;


@TabConfig(configKey = "passiveSkillTreeTab", defaultEnabled = true, defaultOrder = 0)
public class PassiveSkillTreeTab extends ConfigurableIconTab {
    private static final ResourceLocation SKILL_TREE_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/skill_tree.png");

    public PassiveSkillTreeTab() {
        super(SKILL_TREE_ICON, Config.Baked.passiveSkillTreeTabCustomIcon, "passiveSkillTree");
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.passiveSkillTreeTabEnabled && player.level().isClientSide) {
            try {
                Class<?> skillTreeScreenClass = Class.forName("daripher.skilltree.client.screen.SkillTreeScreen");
                ResourceLocation mainTreeLocation = ResourceLocation.fromNamespaceAndPath("skilltree", "main_tree");
                Screen skillTreeScreen = (Screen) skillTreeScreenClass.getDeclaredConstructor(ResourceLocation.class)
                    .newInstance(mainTreeLocation);
                Minecraft.getInstance().setScreen(skillTreeScreen);
            } catch (Exception e) {
                // Passive Skill Tree mod not present or failed to open screen
            }
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.passiveSkillTreeTabEnabled && ModIntegrationManager.isModLoaded(ModIntegration.PASSIVE_SKILL_TREE);
    }


    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        try {
            Class<?> skillTreeScreenClass = Class.forName("daripher.skilltree.client.screen.SkillTreeScreen");
            return skillTreeScreenClass.isInstance(currentScreen);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.passive_skill_tree.description");
    }

    @Override
    public void initTabOnScreens() {
        try {
            Class<?> skillTreeScreenClass = Class.forName("daripher.skilltree.client.screen.SkillTreeScreen");
            @SuppressWarnings("unchecked")
            Class<? extends Screen> screenClass = (Class<? extends Screen>) skillTreeScreenClass;
            ScreenRegistry.builder()
                .withStandardDimensions()
                .withPositioning(vodmordia.modtabs.api.tabs_menu.TabPositioning.GUI_RELATIVE)
                .registerAllTabs(screenClass);
        } catch (ClassNotFoundException e) {
            // Passive Skill Tree mod not present, skip registration
        }
    }
}

