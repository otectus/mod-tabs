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
    }

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (TabsMenu.wasScreenOpenedViaTab()) {
            Minecraft minecraft = Minecraft.getInstance();
            
            if (minecraft.options.keyInventory.matches(event.getKeyCode(), event.getScanCode())) {
                Screen sourceScreen = TabsMenu.getSourceScreen();
                
                if (sourceScreen instanceof InventoryScreen) {
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
}
