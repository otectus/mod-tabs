package sfiomn.legendarytabs.api.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ScreenEvent;
import sfiomn.legendarytabs.LegendaryTabs;
import sfiomn.legendarytabs.client.screens.NextTabsButton;
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
    private static int startTabIndex;
    private static int tabsCount;

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
                if (button instanceof NextTabsButton tabButton) {
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
            startTabIndex = 0;

            if (TabsMenu.topScreenPos - TAB_HEIGHT >= 0) {
                tabsCount = 0;
                int remainingWidth = screenInfo.width.apply(Minecraft.getInstance().player) - Config.Baked.tabsMenuOffsetX;
                for (List<TabBase> tabBases: screenInfo.tabs.values()) {
                    if (remainingWidth < TAB_WIDTH + 1) {
                        if (screenInfo.totalTabs > tabsCount)
                            event.addListener(new NextTabsButton(tabsCount, TabsMenu.leftScreenPos, TabsMenu.topScreenPos,
                                    button -> nextTabButtons(event.getScreen())));
                        break;
                    }

                    for (TabBase tabBase: tabBases) {
                        if (!tabBase.isEnabled(Minecraft.getInstance().player))
                            continue;

                        event.addListener(new TabButton(tabBase, Minecraft.getInstance().player, event.getScreen(), tabsCount, TabsMenu.leftScreenPos, TabsMenu.topScreenPos));

                        remainingWidth -= TAB_WIDTH + 1;
                        tabsCount++;
                    }
                }
            }
        }
    }

    public static void nextTabButtons(Screen screen) {
        List<? extends GuiEventListener> tabButtons = screen.children().stream().filter(button -> button instanceof TabButton).toList();

        ScreenInfo screenInfo = tabsScreens.get(screen.getClass());

        if (startTabIndex + tabsCount >= screenInfo.totalTabs)
            startTabIndex = 0;
        else
            startTabIndex += tabsCount + Math.min(screenInfo.totalTabs - tabsCount * 2 - startTabIndex, 0);

        int currentTabIndex = 0;
        for (List<TabBase> tabBases: screenInfo.tabs.values()) {
            for (TabBase tabBase: tabBases) {
                int tabIndexToUpdate = currentTabIndex - startTabIndex;
                if (tabIndexToUpdate >= tabsCount)
                    break;

                if (tabIndexToUpdate >= 0) {
                    ((TabButton) tabButtons.get(tabIndexToUpdate)).setTabBase(tabBase);
                }

                currentTabIndex++;
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
        public int totalTabs;
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
            totalTabs++;
        }
    }
}
