package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.puffish.skillsmod.client.SkillsClientMod;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabDisplayMode;
import vodmordia.modtabs.api.tabs_menu.TabPositioning;
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;

import java.util.Optional;


public class PufferfishsSkillsTab extends TabBase {
    private final ResourceLocation PUFFER_ICON = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/puffer.png");

    public PufferfishsSkillsTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        if (Config.Baked.pufferfishSkillsTabEnabled && player.level().isClientSide) {
            SkillsClientMod.getInstance().openScreen(Optional.empty());
        }
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.pufferfishSkillsTabEnabled;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(PUFFER_ICON, 4, 4, 16, 16)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withTextureIcon(PUFFER_ICON, 4, 4, 16, 16)
            .render(gui, x, y, hover, true);
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
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Screen> skillsScreenClass = (Class<? extends Screen>) Class.forName("net.puffish.skillsmod.client.gui.SkillsScreen");
            TabsMenu.registerScreenWithAllTabs(skillsScreenClass, (player) -> 400, (player) -> 300, TabDisplayMode.INVERTED, TabPositioning.SCREEN_TOP);
        } catch (ClassNotFoundException e) {
            // Pufferfish Skills mod not available
        }
    }
}
