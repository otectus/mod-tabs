package vodmordia.modtabs.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.client.screens.NextTabsButton;
import vodmordia.modtabs.client.screens.TabButton;

@EventBusSubscriber(modid = ModTabs.MOD_ID, value = Dist.CLIENT)
public class ClientNeoForgeEvents {

    @SubscribeEvent
    public static void preRenderScreen(ScreenEvent.Render.Pre event) {
        event.getScreen();
        Screen screen = event.getScreen();

        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            TabsMenu.updateButtonsPosition(screen, containerScreen.getGuiLeft(), containerScreen.getGuiTop());
        }
    }

    @SubscribeEvent
    public static void screenInitPost(ScreenEvent.Init.Post event) {
        event.getScreen();
        TabsMenu.initScreenButtons(event);

        // Hide L2 tabs after screen initialization
        if (ModTabs.l2LibraryLoaded || ModTabs.l2HostilityLoaded || ModTabs.l2ArtifactsLoaded || ModTabs.modularGolemsLoaded) {
            // But don't hide tabs when we're in the Modular Golems tracker screens (they need sub-tabs)
            boolean isModularGolemsTrackerScreen = false;
            try {
                Class<?> golemInfoScreenClass = Class.forName("dev.xkmc.modulargolems.content.client.tracker.GolemInfoScreen");
                isModularGolemsTrackerScreen = golemInfoScreenClass.isInstance(event.getScreen());
            } catch (ClassNotFoundException e) {
                // Class not found, not a Modular Golems screen
            }

            if (!isModularGolemsTrackerScreen) {
                hideL2Tabs(event.getScreen());
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (TabsMenu.wasScreenOpenedViaTab()) {
            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.options.keyInventory.matches(event.getKeyCode(), event.getScanCode())) {
                if (minecraft.player != null && minecraft.gameMode != null) {
                    // Before switching to inventory, properly close any open container (like backpack)
                    // This fixes the duplication issue when switching from backpack to inventory via keybind
                    try {
                        if (minecraft.player.containerMenu != null && !minecraft.player.containerMenu.getClass().getSimpleName().equals("InventoryMenu")) {
                            minecraft.player.closeContainer();
                        }
                    } catch (Exception e) {
                        // Silently ignore close container errors
                    }

                    // Mark and open inventory
                    TabsMenu.markScreenOpenedViaTab(event.getScreen());
                    minecraft.setScreen(new InventoryScreen(minecraft.player));
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!TabsMenu.wasScreenOpenedViaTab()) {
            TabsMenu.clearTabScreenTracking();
        }
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Pre event) {
        // Hide L2Library tabs when L2 mods are loaded and we're managing tabs
        if (ModTabs.l2LibraryLoaded || ModTabs.l2HostilityLoaded || ModTabs.l2ArtifactsLoaded || ModTabs.modularGolemsLoaded) {
            Screen screen = event.getScreen();
            if (screen instanceof AbstractContainerScreen<?>) {
                // Cancel L2's tab rendering by removing their tab widgets
                // But don't hide tabs when we're in the Modular Golems tracker screens (they need sub-tabs)
                boolean isModularGolemsTrackerScreen = false;
                try {
                    Class<?> golemInfoScreenClass = Class.forName("dev.xkmc.modulargolems.content.client.tracker.GolemInfoScreen");
                    isModularGolemsTrackerScreen = golemInfoScreenClass.isInstance(screen);
                } catch (ClassNotFoundException e) {
                    // Class not found, not a Modular Golems screen
                }

                if (!isModularGolemsTrackerScreen) {
                    hideL2Tabs(screen);
                }
            }
        }
    }

    private static void hideL2Tabs(Screen screen) {
        if (ModTabs.l2LibraryLoaded || ModTabs.l2HostilityLoaded || ModTabs.l2ArtifactsLoaded || ModTabs.modularGolemsLoaded) {
            try {
                // Remove L2's tab buttons from the screen
                screen.children().removeIf(widget -> {
                    String className = widget.getClass().getName();
                    boolean isL2Tab = className.contains("l2tabs") ||
                                     className.contains("l2library") ||
                                     className.contains("l2hostility") ||
                                     className.contains("l2artifacts") ||
                                     className.contains("modulargolems");
                    boolean isTab = className.toLowerCase().contains("tab");
                    return isL2Tab && isTab;
                });
                
                screen.renderables.removeIf(renderable -> {
                    String className = renderable.getClass().getName();
                    boolean isL2Tab = className.contains("l2tabs") ||
                                     className.contains("l2library") ||
                                     className.contains("l2hostility") ||
                                     className.contains("l2artifacts") ||
                                     className.contains("modulargolems");
                    boolean isTab = className.toLowerCase().contains("tab");
                    return isL2Tab && isTab;
                });
                
            } catch (Exception e) {
                ModTabs.LOGGER.debug("Failed to hide L2 tabs: " + e.getMessage());
            }
        }
    }

    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        // Special handling for screens that don't call super.render() properly
        String screenClassName = event.getScreen().getClass().getName();

        if (screenClassName.equals("net.puffish.skillsmod.client.gui.SkillsScreen") ||
            screenClassName.equals("dev.ftb.mods.ftblibrary.ui.ScreenWrapper") ||
            screenClassName.equals("xaero.map.gui.GuiMap")) {

            // Find and render all TabButton and NextTabsButton widgets for this screen - this renders AFTER the screen content including blur
            for (var child : event.getScreen().children()) {
                if (child instanceof TabButton tabButton) {
                    // Render the tab button on top of everything the screen just rendered
                    tabButton.renderWidget(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
                }
                if (child instanceof NextTabsButton nextTabsButton) {
                    // Render the next tabs button on top of everything the screen just rendered
                    nextTabsButton.renderWidget(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
                }
            }
        }
    }

}
