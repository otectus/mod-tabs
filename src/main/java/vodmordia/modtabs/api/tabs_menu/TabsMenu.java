package vodmordia.modtabs.api.tabs_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ScreenEvent;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.client.screens.NextTabsButton;
import vodmordia.modtabs.client.screens.TabButton;
import vodmordia.modtabs.config.Config;

import java.util.*;
import java.util.function.Function;

import static vodmordia.modtabs.api.tabs_menu.TabBase.TAB_HEIGHT;
import static vodmordia.modtabs.api.tabs_menu.TabBase.TAB_WIDTH;

public class TabsMenu {
    private static final Map<Class<? extends Screen>, ScreenInfo> tabsScreens = new HashMap<>();
    private static int leftScreenPos;
    private static int topScreenPos;
    private static int startTabIndex;
    private static int currentTabsCount;
    private static List<TabBase> enabledTabs;
    private static boolean screenOpenedViaTab = false;
    private static Screen sourceScreen = null;

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

            if (TabsMenu.topScreenPos - TAB_HEIGHT < 0)
                return;

            startTabIndex = 0;
            currentTabsCount = 0;
            enabledTabs = new ArrayList<>();
            for (List<TabBase> tabBases: screenInfo.tabs.values()) {
                enabledTabs.addAll(tabBases.stream().filter(tabBase -> tabBase.isEnabled(Minecraft.getInstance().player)).toList());
            }

            int remainingWidth = screenInfo.width.apply(Minecraft.getInstance().player) - Config.Baked.tabsMenuOffsetX;
            for (TabBase tabBase: enabledTabs) {
                if (remainingWidth > TAB_WIDTH) {
                    event.addListener(new TabButton(tabBase, Minecraft.getInstance().player, event.getScreen(), currentTabsCount, TabsMenu.leftScreenPos, TabsMenu.topScreenPos));

                    remainingWidth -= TAB_WIDTH + 1;
                    currentTabsCount++;
                }
            }

            if (enabledTabs.size() > currentTabsCount)
                event.addListener(new NextTabsButton(currentTabsCount, TabsMenu.leftScreenPos, TabsMenu.topScreenPos,
                        button -> nextTabButtons(event.getScreen())));
        }
    }

    public static void nextTabButtons(Screen screen) {
        List<? extends GuiEventListener> tabButtons = screen.children().stream().filter(button -> button instanceof TabButton).toList();

        if (startTabIndex + currentTabsCount >= enabledTabs.size())
            startTabIndex = 0;
        else
            startTabIndex += currentTabsCount + Math.min(enabledTabs.size() - currentTabsCount * 2 - startTabIndex, 0);

        int currentTabIndex = 0;
        for (TabBase tabBase: enabledTabs) {
            int tabIndexToUpdate = currentTabIndex - startTabIndex;
            if (tabIndexToUpdate >= currentTabsCount)
                break;

            if (tabIndexToUpdate >= 0)
                ((TabButton) tabButtons.get(tabIndexToUpdate)).setTabBase(tabBase);

            currentTabIndex++;
        }
    }

    public static void register(TabBase tabBase) {
        ModTabs.LOGGER.info("Tab " + tabBase.getClass().getName() + " registered");
        tabBase.initTabOnScreens();
    }

    public static void markScreenOpenedViaTab(Screen sourceScreen) {
        TabsMenu.screenOpenedViaTab = true;
        TabsMenu.sourceScreen = sourceScreen;
    }

    public static boolean wasScreenOpenedViaTab() {
        return screenOpenedViaTab;
    }

    public static Screen getSourceScreen() {
        return sourceScreen;
    }

    public static void clearTabScreenTracking() {
        screenOpenedViaTab = false;
        sourceScreen = null;
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
