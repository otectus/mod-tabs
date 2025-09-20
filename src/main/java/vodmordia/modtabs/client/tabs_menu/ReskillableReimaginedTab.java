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
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.utils.IntegrationUtils;
import top.theillusivec4.curios.client.gui.CuriosScreen;

public class ReskillableReimaginedTab extends TabBase {
    private final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(ModTabs.MOD_ID, "textures/gui/tab_menu_buttons.png");
    private final int TAB_ICON_TEX_X = 0; // Empty tab normal state
    private final int TAB_ICON_TEX_Y = 138; // Empty tab background in bottom row

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
        // Render tab background (empty tab style like L2 mods)
        int texOffsetX = 0;
        if (hover)
            texOffsetX = 54; // Hover state is at X=54
        gui.blit(TAB_ICONS, x, y, TAB_ICON_TEX_X + texOffsetX, TAB_ICON_TEX_Y, TAB_WIDTH, TAB_HEIGHT);

        // Render the book icon from Reskillable's skills.png
        try {
            // Book icon coordinates based on exact measurements
            int u = 241; // X position of book icon
            int v = 145; // Y position of book icon

            // Draw the 16x16 book icon, scaled down to 14x14 and moved down 2px
            gui.blit(RESKILLABLE_SKILLS, x + 6, y + 6, u, v, 14, 14, 256, 256);
        } catch (Exception e) {
            // If icon loading fails, draw a fallback book-like appearance
            ModTabs.LOGGER.debug("Failed to load Reskillable book icon, using fallback");
            // Draw a simple book-like colored rectangle as fallback
            gui.fill(x + 7, y + 5, x + 19, y + 17, 0xFF8B4513); // Brown book cover
            gui.fill(x + 9, y + 7, x + 17, y + 15, 0xFFFFF8DC); // Light pages
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
        TabsMenu.addTabToScreen(this, InventoryScreen.class, (player) -> 176, (player) -> 166, 30);

        if (ModTabs.curiosLoaded)
            TabsMenu.addTabToScreen(this, CuriosScreen.class, (player) -> 176, (player) -> 166, 30);

        // if (ModTabs.legendarySurvivalOverhaulLoaded)
        //     TabsMenu.addTabToScreen(this, BodyHealthScreen.class, (player) -> 176, (player) -> 183, 30);

        // if (ModTabs.reskillableLoaded)
        //     TabsMenu.addTabToScreen(this, majik.rereskillable.client.screen.SkillScreen.class, (player) -> 176, (player) -> 166, 30);

        // if (ModTabs.reskillableReimaginedLoaded && Config.Baked.includeOpenedScreenTab)
        //     TabsMenu.addTabToScreen(this, SkillScreen.class, (player) -> 176, (player) -> 166, 30);

        // if (ModTabs.quarkOdditiesLoaded)
        //     TabsMenu.addTabToScreen(this, BackpackInventoryScreen.class, (player) -> 176, (player) -> 224, 30);

        if (ModTabs.cosmeticArmorLoaded)
            TabsMenu.addTabToScreen(this, GuiCosArmorInventory.class, (player) -> 176, (player) -> 166, 30);

        if (ModTabs.backpackedLoaded)
            // Backpacked integration temporarily disabled - mod is in active development
            //TabsMenu.addTabToScreen(this, BackpackScreen.class, (IntegrationUtils::getBackpackWidth), (IntegrationUtils::getBackpackHeight), 30);

        if (ModTabs.travelersBackpackLoaded)
            TabsMenu.addTabToScreen(this, com.tiviacz.travelersbackpack.client.screens.BackpackScreen.class, IntegrationUtils::getTravelersBackpackWidth, IntegrationUtils::getTravelersBackpackHeight, 30);

        // if (ModTabs.dietLoaded)
        //     TabsMenu.addTabToScreen(this, DietScreen.class, (player) -> 248, IntegrationUtils::getDietHeight, 30);
    }
}
