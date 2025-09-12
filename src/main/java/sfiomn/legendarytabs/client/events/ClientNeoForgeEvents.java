package sfiomn.legendarytabs.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;
import sfiomn.legendarytabs.LegendaryTabs;
import sfiomn.legendarytabs.api.tabs_menu.TabsMenu;

@EventBusSubscriber(modid = LegendaryTabs.MOD_ID, value = Dist.CLIENT)
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
        if (LegendaryTabs.l2LibraryLoaded || LegendaryTabs.l2HostilityLoaded || LegendaryTabs.l2ArtifactsLoaded) {
            hideL2Tabs(event.getScreen());
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (TabsMenu.wasScreenOpenedViaTab()) {
            Minecraft minecraft = Minecraft.getInstance();
            
            if (minecraft.options.keyInventory.matches(event.getKeyCode(), event.getScanCode())) {
                Screen sourceScreen = TabsMenu.getSourceScreen();
                
                if (sourceScreen instanceof InventoryScreen && minecraft.player != null && minecraft.gameMode != null) {
                    TabsMenu.clearTabScreenTracking();
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
        if (LegendaryTabs.l2LibraryLoaded || LegendaryTabs.l2HostilityLoaded || LegendaryTabs.l2ArtifactsLoaded) {
            Screen screen = event.getScreen();
            if (screen instanceof AbstractContainerScreen<?>) {
                // Cancel L2's tab rendering by removing their tab widgets
                hideL2Tabs(screen);
            }
        }
    }

    private static void hideL2Tabs(Screen screen) {
        if (LegendaryTabs.l2LibraryLoaded || LegendaryTabs.l2HostilityLoaded || LegendaryTabs.l2ArtifactsLoaded) {
            try {
                // Remove L2's tab buttons from the screen
                screen.children().removeIf(widget -> {
                    String className = widget.getClass().getName();
                    boolean isL2Tab = className.contains("l2tabs") || 
                                     className.contains("l2library") ||
                                     className.contains("l2hostility") ||
                                     className.contains("l2artifacts");
                    boolean isTab = className.toLowerCase().contains("tab");
                    return isL2Tab && isTab;
                });
                
                screen.renderables.removeIf(renderable -> {
                    String className = renderable.getClass().getName();
                    boolean isL2Tab = className.contains("l2tabs") || 
                                     className.contains("l2library") ||
                                     className.contains("l2hostility") ||
                                     className.contains("l2artifacts");
                    boolean isTab = className.toLowerCase().contains("tab");
                    return isL2Tab && isTab;
                });
                
            } catch (Exception e) {
                LegendaryTabs.LOGGER.debug("Failed to hide L2 tabs: " + e.getMessage());
            }
        }
    }
}
