package sfiomn.legendarytabs.client.events;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sfiomn.legendarytabs.LegendaryTabs;
import sfiomn.legendarytabs.api.tabs_menu.TabsMenu;

@Mod.EventBusSubscriber(modid = LegendaryTabs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

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
}
