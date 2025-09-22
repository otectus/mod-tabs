package vodmordia.modtabs.client.tabs_menu;

//import com.illusivesoulworks.diet.client.screen.DietScreen;
//import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import net.bandit.reskillable.client.screen.SkillScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
//import org.violetmoon.quark.addons.oddities.client.screen.BackpackInventoryScreen;
//import sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.api.tabs_menu.TabRenderer;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;

public class ReskillableReimaginedTab extends TabBase {
    // Direct reference to Reskillable skills spritesheet for the book icon
    private final ResourceLocation RESKILLABLE_SKILLS = ResourceLocation.fromNamespaceAndPath("reskillable", "textures/gui/skills.png");

    public ReskillableReimaginedTab() {
        super();
    }

    @Override
    public void openTargetScreen(Player player) {
        if (ModTabs.reskillableReimaginedLoaded)
            Minecraft.getInstance().setScreen(new SkillScreen());
    }

    @Override
    public boolean isEnabled(Player player) {
        return Config.Baked.reskillableTabEnabled && ModTabs.reskillableReimaginedLoaded;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withCustomIcon(this::renderSkillBookIcon)
            .render(gui, x, y, hover, false);
    }

    @Override
    protected void renderInverted(GuiGraphics gui, int x, int y, boolean hover) {
        TabRenderer.builder()
            .withBackground()
            .withCustomIcon(this::renderSkillBookIcon)
            .render(gui, x, y, hover, true);
    }

    private void renderSkillBookIcon(TabRenderer.RenderContext context) {
        // Render the book icon from Reskillable's skills.png
        try {
            // Book icon coordinates based on exact measurements
            int u = 241; // X position of book icon
            int v = 145; // Y position of book icon

            // Draw the 16x16 book icon, scaled down to 14x14 and moved down 2px
            context.gui.blit(RESKILLABLE_SKILLS, context.x + 6, context.y + 6, u, v, 14, 14, 256, 256);
        } catch (Exception e) {
            context.gui.fill(context.x + 7, context.y + 5, context.x + 19, context.y + 17, 0xFF8B4513);
            context.gui.fill(context.x + 9, context.y + 7, context.x + 17, context.y + 15, 0xFFFFF8DC);
        }
    }

    @Override
    public boolean isCurrentlyUsed(Screen currentScreen) {
        return ModTabs.reskillableReimaginedLoaded && currentScreen instanceof SkillScreen;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip." + ModTabs.MOD_ID + ".tab.reskillable_reimagined.description");
    }

    @Override
    public void initTabOnScreens() {
        // Register only this tab's own screen - the Reskillable Reimagined skill screen
        if (ModTabs.reskillableReimaginedLoaded)
            TabsMenu.registerScreenWithAllTabs(SkillScreen.class, (player) -> 176, (player) -> 166);
    }
}
