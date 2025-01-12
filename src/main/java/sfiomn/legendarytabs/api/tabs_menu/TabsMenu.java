package sfiomn.legendarytabs.api.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ScreenEvent;
import sfiomn.legendarytabs.LegendaryTabs;
import sfiomn.legendarytabs.client.screens.TabButton;
import sfiomn.legendarytabs.config.Config;

import java.util.*;
import java.util.function.Function;

import static sfiomn.legendarytabs.api.tabs_menu.TabBase.TAB_HEIGHT;
import static sfiomn.legendarytabs.api.tabs_menu.TabBase.TAB_WIDTH;

public class TabsMenu {
    private static final Map<Class<? extends Screen>, ScreenInfo> tabsScreens = new HashMap<>();
    private static int leftScreenPos;
    private static int topScreenPos;

    private TabsMenu() {
    }

    public static void updateButtonsPosition(Screen screen, int leftScreenPos, int topScreenPos) {
        if (TabsMenu.leftScreenPos != leftScreenPos || TabsMenu.topScreenPos != topScreenPos) {
            TabsMenu.leftScreenPos = leftScreenPos;
            TabsMenu.topScreenPos = topScreenPos;
            for (GuiEventListener button: screen.children()) {
                if (button instanceof TabButton tabButton) {
                    tabButton.updatePosition(TabsMenu.leftScreenPos, TabsMenu.topScreenPos);
                }
            }
        }
    }

    public static void addTabToScreen(TabBase newTab, Class<? extends Screen> screen, Function<Player, Integer> screenWidth, Function<Player, Integer> screenHeight, int priority) {
        if (tabsScreens.containsKey(screen)) {
            tabsScreens.get(screen).addTab(priority, newTab);
        } else {
            ScreenInfo screenInfo = new ScreenInfo(screenWidth, screenHeight, newTab, priority);
            tabsScreens.put(screen, screenInfo);
        }
    }

    public static void initScreenButtons(ScreenEvent.Init.Post event) {
        if (tabsScreens.containsKey(event.getScreen().getClass())) {
            if (Minecraft.getInstance().player == null)
                return;


            ScreenInfo screenInfo = tabsScreens.get(event.getScreen().getClass());
            TabsMenu.leftScreenPos = (event.getScreen().width - screenInfo.width.apply(Minecraft.getInstance().player)) / 2;
            TabsMenu.topScreenPos = (event.getScreen().height - screenInfo.height.apply(Minecraft.getInstance().player)) / 2;

            if (TabsMenu.topScreenPos - TAB_HEIGHT >= 0) {
                int tabPositionIndex = 0;
                int remainingWidth = screenInfo.width.apply(Minecraft.getInstance().player) - Config.Baked.tabsMenuOffsetX;
                for (List<TabBase> tabBases: screenInfo.tabs.values()) {
                    if (remainingWidth < TAB_WIDTH + 1)
                        break;

                    for (TabBase tabBase: tabBases) {
                        if (!tabBase.isEnabled(Minecraft.getInstance().player))
                            continue;

                        if (tabBase.isCurrentlyUsed(event.getScreen()))
                            event.addListener(new TabButton(tabBase, true, tabPositionIndex, TabsMenu.leftScreenPos, TabsMenu.topScreenPos, button -> {
                            }));
                        else
                            event.addListener(new TabButton(tabBase, false, tabPositionIndex, TabsMenu.leftScreenPos, TabsMenu.topScreenPos, button -> tabBase.openTargetScreen(event.getScreen().getMinecraft().player)));

                        remainingWidth -= TAB_WIDTH + 1;
                        tabPositionIndex++;
                    }
                }
            }
        }
    }

    public static void register(TabBase tabBase) {
        LegendaryTabs.LOGGER.info("Tab " + tabBase.getClass().getName() + " registered");
        tabBase.initTabOnScreens();
    }

    public static class ScreenInfo {
        public Function<Player, Integer> width;
        public Function<Player, Integer> height;
        public Map<Integer, List<TabBase>> tabs;
        public ScreenInfo(Function<Player, Integer> width, Function<Player, Integer> height, TabBase newTab, int priority) {
            this.width = width;
            this.height = height;
            this.tabs = new TreeMap<>();
            this.addTab(priority, newTab);
        }

        public void addTab(int priority, TabBase newTab) {
            if (this.tabs.containsKey(priority))
                this.tabs.get(priority).add(newTab);
            else {
                ArrayList<TabBase> newTabsForPriority = new ArrayList<>();
                newTabsForPriority.add(newTab);
                this.tabs.put(priority, newTabsForPriority);
            }
        }
    }
}
