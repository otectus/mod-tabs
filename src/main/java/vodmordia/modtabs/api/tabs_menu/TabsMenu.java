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
    private static final List<TabBase> allRegisteredTabs = new ArrayList<>();
    private static final List<ScreenRegistration> pendingScreenRegistrations = new ArrayList<>();
    private static int leftScreenPos;
    private static int topScreenPos;
    private static int startTabIndex;
    private static int currentTabsCount;
    private static List<TabBase> enabledTabs;
    private static boolean screenOpenedViaTab = false;
    private static Screen sourceScreen = null;
    private static int preservedStartTabIndex = 0;

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

    public static void registerScreenWithAllTabs(Class<? extends Screen> screen, Function<Player, Integer> screenWidth, Function<Player, Integer> screenHeight) {
        // Store the registration for later processing
        pendingScreenRegistrations.add(new ScreenRegistration(screen, screenWidth, screenHeight));
    }

    public static void registerScreenWithAllTabs(Class<? extends Screen> screen, Function<Player, Integer> screenWidth, Function<Player, Integer> screenHeight, TabDisplayMode displayMode) {
        // Store the registration for later processing
        pendingScreenRegistrations.add(new ScreenRegistration(screen, screenWidth, screenHeight, displayMode));
    }

    public static void registerScreenWithAllTabs(Class<? extends Screen> screen, Function<Player, Integer> screenWidth, Function<Player, Integer> screenHeight, TabDisplayMode displayMode, TabPositioning positioning) {
        // Store the registration for later processing
        pendingScreenRegistrations.add(new ScreenRegistration(screen, screenWidth, screenHeight, displayMode, positioning, null, null, 5));
    }

    public static void registerScreenWithAllTabs(Class<? extends Screen> screen, Function<Player, Integer> screenWidth, Function<Player, Integer> screenHeight, TabDisplayMode displayMode, TabPositioning positioning, int screenEdgeOffset) {
        // Store the registration for later processing
        pendingScreenRegistrations.add(new ScreenRegistration(screen, screenWidth, screenHeight, displayMode, positioning, null, null, screenEdgeOffset));
    }

    public static void forceRegisterScreenWithAllTabs(Class<? extends Screen> screen, Function<Player, Integer> screenWidth, Function<Player, Integer> screenHeight, TabDisplayMode displayMode, TabPositioning positioning) {
        // Force registration - remove any existing registration for this screen class first
        pendingScreenRegistrations.removeIf(reg -> reg.screenClass.equals(screen));
        // Then add our new registration
        pendingScreenRegistrations.add(new ScreenRegistration(screen, screenWidth, screenHeight, displayMode, positioning, null, null, 5));
    }

    public static void registerScreenWithCustomPosition(Class<? extends Screen> screen, Function<Player, Integer> screenWidth, Function<Player, Integer> screenHeight, Function<Screen, Integer> customTabX, Function<Screen, Integer> customTabY) {
        // Store the registration for later processing
        pendingScreenRegistrations.add(new ScreenRegistration(screen, screenWidth, screenHeight, TabDisplayMode.NORMAL, TabPositioning.CUSTOM, customTabX, customTabY, 0));
    }

    public static void registerScreenWithCustomPosition(Class<? extends Screen> screen, Function<Player, Integer> screenWidth, Function<Player, Integer> screenHeight, TabDisplayMode displayMode, Function<Screen, Integer> customTabX, Function<Screen, Integer> customTabY) {
        // Store the registration for later processing
        pendingScreenRegistrations.add(new ScreenRegistration(screen, screenWidth, screenHeight, displayMode, TabPositioning.CUSTOM, customTabX, customTabY, 0));
    }

    public static void finalizePendingRegistrations() {
        // Process all pending screen registrations now that all tabs are registered
        for (ScreenRegistration registration : pendingScreenRegistrations) {
            if (!tabsScreens.containsKey(registration.screenClass)) {
                ScreenInfo screenInfo = new ScreenInfo(registration.screenWidth, registration.screenHeight, registration.displayMode);
                screenInfo.positioning = registration.positioning;
                screenInfo.customTabX = registration.customTabX;
                screenInfo.customTabY = registration.customTabY;
                screenInfo.screenEdgeOffset = registration.screenEdgeOffset;
                tabsScreens.put(registration.screenClass, screenInfo);

                // Debug logging for FTB Quests ScreenWrapper specifically
            } else {
            }

            // Add all registered tabs to this screen with default priority 10
            ScreenInfo screenInfo = tabsScreens.get(registration.screenClass);
            for (TabBase tab : allRegisteredTabs) {
                screenInfo.addTab(10, tab);
            }
        }
        pendingScreenRegistrations.clear();
    }

    public static void initScreenButtons(ScreenEvent.Init.Post event) {

        if (tabsScreens.containsKey(event.getScreen().getClass())) {

            // Clear existing tab buttons to prevent duplicates
            var existingTabButtons = event.getScreen().children().stream()
                .filter(widget -> widget instanceof TabButton || widget instanceof NextTabsButton)
                .toList();
            existingTabButtons.forEach(event.getScreen().children()::remove);

            if (Minecraft.getInstance().player == null)
                return;

            ScreenInfo screenInfo = tabsScreens.get(event.getScreen().getClass());



            // Calculate tab position based on positioning mode
            switch (screenInfo.positioning) {
                case GUI_RELATIVE:
                    // Original behavior - position relative to GUI center
                    TabsMenu.leftScreenPos = (event.getScreen().width - screenInfo.width.apply(Minecraft.getInstance().player)) / 2;
                    TabsMenu.topScreenPos = (event.getScreen().height - screenInfo.height.apply(Minecraft.getInstance().player)) / 2;
                    break;
                case SCREEN_TOP:
                    // Position at top of screen with offset
                    TabsMenu.leftScreenPos = (event.getScreen().width - screenInfo.width.apply(Minecraft.getInstance().player)) / 2;
                    // For inverted tabs, position at absolute top (y=0), otherwise use offset
                    TabsMenu.topScreenPos = screenInfo.displayMode == TabDisplayMode.INVERTED ? 0 : screenInfo.screenEdgeOffset;
                    break;
                case SCREEN_BOTTOM:
                    // Position at bottom of screen with offset
                    TabsMenu.leftScreenPos = (event.getScreen().width - screenInfo.width.apply(Minecraft.getInstance().player)) / 2;
                    // For NORMAL display mode, TabButton will subtract TAB_HEIGHT again, so we need to add it back
                    // For INVERTED display mode, TabButton will use the position as-is
                    if (screenInfo.displayMode == TabDisplayMode.NORMAL) {
                        TabsMenu.topScreenPos = event.getScreen().height - screenInfo.screenEdgeOffset;
                    } else {
                        TabsMenu.topScreenPos = event.getScreen().height - TAB_HEIGHT - screenInfo.screenEdgeOffset;
                    }
                    break;
                case CUSTOM:
                    // Use custom position functions
                    if (screenInfo.customTabX != null && screenInfo.customTabY != null) {
                        TabsMenu.leftScreenPos = screenInfo.customTabX.apply(event.getScreen());
                        TabsMenu.topScreenPos = screenInfo.customTabY.apply(event.getScreen());
                    } else {
                        // Fallback to GUI_RELATIVE if custom functions not provided
                        TabsMenu.leftScreenPos = (event.getScreen().width - screenInfo.width.apply(Minecraft.getInstance().player)) / 2;
                        TabsMenu.topScreenPos = (event.getScreen().height - screenInfo.height.apply(Minecraft.getInstance().player)) / 2;
                    }
                    break;
            }

            // Only check bounds for GUI_RELATIVE positioning
            if (screenInfo.positioning == TabPositioning.GUI_RELATIVE && TabsMenu.topScreenPos - TAB_HEIGHT < 0) {
                ModTabs.LOGGER.warn("TabsMenu: EARLY RETURN - Tab position would be off-screen");
                return;
            }

            startTabIndex = screenOpenedViaTab ? preservedStartTabIndex : 0;
            // Don't clear tracking immediately - let the keybind handler do it after use
            currentTabsCount = 0;
            enabledTabs = new ArrayList<>();
            for (List<TabBase> tabBases: screenInfo.tabs.values()) {
                enabledTabs.addAll(tabBases.stream()
                        .filter(tabBase -> tabBase.isEnabled(Minecraft.getInstance().player))
                        .toList());
            }


            int remainingWidth;
            try {
                remainingWidth = screenInfo.width.apply(Minecraft.getInstance().player);
            } catch (Exception e) {
                ModTabs.LOGGER.error("TabsMenu: Width calculation failed: " + e.getMessage());
                return;
            }


            // First pass: determine how many tabs can fit
            currentTabsCount = 0;
            int tempWidth = remainingWidth;
            for (TabBase tabBase: enabledTabs) {
                if (tempWidth > TAB_WIDTH) {
                    tempWidth -= TAB_WIDTH + 1;
                    currentTabsCount++;
                } else {
                    break;
                }
            }


            // Clear existing tab buttons to prevent duplicates and conflicts
            int removedChildren = 0;

            // Count and log what we're removing
            for (var child : event.getScreen().children()) {
                if (child instanceof TabButton) {
                    TabButton tabButton = (TabButton) child;
                    removedChildren++;
                }
            }

            // Count renderables separately
            int removedRenderables = (int) event.getScreen().renderables.stream()
                .filter(renderable -> renderable instanceof TabButton)
                .count();

            event.getScreen().children().removeIf(child -> child instanceof TabButton);
            event.getScreen().renderables.removeIf(renderable -> renderable instanceof TabButton);


            // Second pass: create buttons for the correct range starting from startTabIndex
            int buttonPosition = 0;
            for (int tabIndex = 0; tabIndex < enabledTabs.size(); tabIndex++) {
                TabBase tabBase = enabledTabs.get(tabIndex);
                int tabIndexToShow = tabIndex - startTabIndex;

                if (tabIndexToShow >= 0 && tabIndexToShow < currentTabsCount) {

                    TabButton newButton = new TabButton(tabBase, Minecraft.getInstance().player, event.getScreen(), buttonPosition, TabsMenu.leftScreenPos, TabsMenu.topScreenPos, screenInfo.displayMode);
                    event.addListener(newButton);
                    buttonPosition++;
                }
            }

            if (enabledTabs.size() > currentTabsCount) {
                // For inverted displays, position the next button correctly
                int nextButtonY = screenInfo.displayMode == TabDisplayMode.INVERTED ?
                    TabsMenu.topScreenPos :
                    TabsMenu.topScreenPos - TAB_HEIGHT;
                event.addListener(new NextTabsButton(currentTabsCount, TabsMenu.leftScreenPos, nextButtonY,
                        button -> nextTabButtons(event.getScreen())));
            }
        } else {
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
        allRegisteredTabs.add(tabBase);
        tabBase.initTabOnScreens();
    }

    public static void markScreenOpenedViaTab(Screen sourceScreen) {
        TabsMenu.screenOpenedViaTab = true;
        TabsMenu.sourceScreen = sourceScreen;
        TabsMenu.preservedStartTabIndex = startTabIndex;
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
        preservedStartTabIndex = 0;
    }

    public static class ScreenInfo {
        public Function<Player, Integer> width;
        public Function<Player, Integer> height;
        public Map<Integer, List<TabBase>> tabs;
        public TabDisplayMode displayMode;
        public TabPositioning positioning;
        public Function<Screen, Integer> customTabX;
        public Function<Screen, Integer> customTabY;
        public int screenEdgeOffset;
        public ScreenInfo(Function<Player, Integer> width, Function<Player, Integer> height, TabBase newTab, int priority) {
            this.width = width;
            this.height = height;
            this.tabs = new TreeMap<>();
            this.displayMode = TabDisplayMode.NORMAL; // Default to normal
            this.positioning = TabPositioning.GUI_RELATIVE; // Default to GUI relative
            this.customTabX = null;
            this.customTabY = null;
            this.screenEdgeOffset = 5; // Default offset from screen edges
            this.addTab(priority, newTab);
        }

        public ScreenInfo(Function<Player, Integer> width, Function<Player, Integer> height) {
            this.width = width;
            this.height = height;
            this.tabs = new TreeMap<>();
            this.displayMode = TabDisplayMode.NORMAL; // Default to normal
            this.positioning = TabPositioning.GUI_RELATIVE; // Default to GUI relative
            this.customTabX = null;
            this.customTabY = null;
            this.screenEdgeOffset = 5; // Default offset from screen edges
        }

        public ScreenInfo(Function<Player, Integer> width, Function<Player, Integer> height, TabDisplayMode displayMode) {
            this.width = width;
            this.height = height;
            this.tabs = new TreeMap<>();
            this.displayMode = displayMode;
            this.positioning = TabPositioning.GUI_RELATIVE; // Default to GUI relative
            this.customTabX = null;
            this.customTabY = null;
            this.screenEdgeOffset = 5; // Default offset from screen edges
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

    private static class ScreenRegistration {
        public final Class<? extends Screen> screenClass;
        public final Function<Player, Integer> screenWidth;
        public final Function<Player, Integer> screenHeight;
        public final TabDisplayMode displayMode;
        public final TabPositioning positioning;
        public final Function<Screen, Integer> customTabX;
        public final Function<Screen, Integer> customTabY;
        public final int screenEdgeOffset;

        public ScreenRegistration(Class<? extends Screen> screenClass, Function<Player, Integer> screenWidth, Function<Player, Integer> screenHeight) {
            this.screenClass = screenClass;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.displayMode = TabDisplayMode.NORMAL;
            this.positioning = TabPositioning.GUI_RELATIVE;
            this.customTabX = null;
            this.customTabY = null;
            this.screenEdgeOffset = 5;
        }

        public ScreenRegistration(Class<? extends Screen> screenClass, Function<Player, Integer> screenWidth, Function<Player, Integer> screenHeight, TabDisplayMode displayMode) {
            this.screenClass = screenClass;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.displayMode = displayMode;
            this.positioning = TabPositioning.GUI_RELATIVE;
            this.customTabX = null;
            this.customTabY = null;
            this.screenEdgeOffset = 5;
        }

        public ScreenRegistration(Class<? extends Screen> screenClass, Function<Player, Integer> screenWidth, Function<Player, Integer> screenHeight, TabDisplayMode displayMode, TabPositioning positioning, Function<Screen, Integer> customTabX, Function<Screen, Integer> customTabY, int screenEdgeOffset) {
            this.screenClass = screenClass;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.displayMode = displayMode;
            this.positioning = positioning;
            this.customTabX = customTabX;
            this.customTabY = customTabY;
            this.screenEdgeOffset = screenEdgeOffset;
        }
    }
}
