package vodmordia.modtabs.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import eu.midnightdust.lib.config.MidnightConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import vodmordia.modtabs.ModTabs;
import vodmordia.modtabs.config.Config;
import vodmordia.modtabs.api.tabs_menu.TabsMenu;
import vodmordia.modtabs.client.screens.NextTabsButton;
import vodmordia.modtabs.client.screens.TabButton;
import vodmordia.modtabs.client.tabs_menu.NearbyContainersProvider;
import vodmordia.modtabs.client.keybinds.ModKeybinds;
import vodmordia.modtabs.integration.ModIntegration;
import vodmordia.modtabs.integration.ModIntegrationManager;
import vodmordia.modtabs.utils.ScreenClasses;

@EventBusSubscriber(modid = ModTabs.MOD_ID, value = Dist.CLIENT)
public class ClientNeoForgeEvents {

    @SubscribeEvent
    public static void preRenderScreen(ScreenEvent.Render.Pre event) {
        event.getScreen();
        Screen screen = event.getScreen();

        // Update mouse position for tuck mode hover detection
        TabsMenu.onMouseMove(event.getMouseX(), event.getMouseY(), screen);

        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            TabsMenu.updateButtonsPosition(screen, containerScreen.getGuiLeft(), containerScreen.getGuiTop());
        }

    }


    @SubscribeEvent
    public static void renderEditModeOverlay(ScreenEvent.Render.Post event) {
        if (TabsMenu.isEditing(event.getScreen())) {
            TabsMenu.renderEditModeOverlay(event.getGuiGraphics(), event.getScreen(), event.getMouseX(), event.getMouseY());
        }
    }

    // Throttle counter for the nearby-container live refresh: only checks every
    // NEARBY_REFRESH_TICKS ticks so the cube scan cost is amortized to ~twice per second.
    private static int nearbyRefreshTickCounter = 0;
    private static final int NEARBY_REFRESH_TICKS = 10;

    // Set when our MidnightLib config screen closes; the actual re-bake is deferred to
    // the next client tick so we never depend on MidnightConfig's internal ordering of
    // loadValuesFromJson() vs setScreen() inside onClose().
    private static boolean pendingConfigRebake = false;

    /**
     * MidnightLib (1.9.3) has no save/close callback API, and {@code Config.Baked} is a
     * one-shot snapshot — without this hook, config-screen changes silently do nothing
     * until the game is relaunched. The {@code modid} public field is unofficial API;
     * pinned against midnightlib 1.9.3.
     */
    @SubscribeEvent
    public static void onScreenClosing(ScreenEvent.Closing event) {
        if (event.getScreen() instanceof MidnightConfigScreen configScreen
                && ModTabs.MOD_ID.equals(configScreen.modid)) {
            pendingConfigRebake = true;
        }
    }

    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event) {
        if (pendingConfigRebake) {
            pendingConfigRebake = false;
            Config.rebake();
            // If the config was opened from a tabbed screen, that screen never refires
            // ScreenEvent.Init.Post on return — rebuild its tab bar so the new values
            // (enabled tabs, nearby-container options) show up immediately.
            Minecraft rebakeMc = Minecraft.getInstance();
            if (rebakeMc.screen != null && TabsMenu.hasTabsForScreen(rebakeMc.screen.getClass())) {
                TabsMenu.reinitCurrentScreen();
            }
        }

        // Without this, a chest broken (or placed) while the player has an inventory /
        // container screen open leaves a stale tab in the bar until they close + reopen.
        // We poll the provider's change-detection helper and trigger a screen re-init when
        // the world's container set has drifted from what's currently rendered.
        if (++nearbyRefreshTickCounter < NEARBY_REFRESH_TICKS) return;
        nearbyRefreshTickCounter = 0;

        Minecraft mc = Minecraft.getInstance();
        Screen screen = mc.screen;
        if (screen == null || mc.player == null) return;
        // Re-init would clobber drag-in-progress state in the layout editor.
        if (TabsMenu.isEditing(screen)) return;
        // Only refresh on screens that actually host the tab bar.
        if (!TabsMenu.hasTabsForScreen(screen.getClass())) return;
        if (NearbyContainersProvider.hasContainerSetChanged(mc.player)) {
            TabsMenu.reinitCurrentScreen();
        }
    }

    @SubscribeEvent
    public static void screenInitPost(ScreenEvent.Init.Post event) {
        event.getScreen();
        TabsMenu.initScreenButtons(event);

        // Wildex's bestiary screen renders its children inside a scaled PoseStack,
        // which would draw our tab widgets microscopic in the corner. Strip them from
        // `renderables` so vanilla's render path skips them; the post-render handler
        // below redraws them at correct scale, and the mouse-click handler forwards
        // input manually since the buttons remain in `children()`.
        if (event.getScreen().getClass().getName().equals("de.coldfang.wildex.client.screen.WildexScreen")) {
            event.getScreen().renderables.removeIf(r ->
                r instanceof TabButton || r instanceof NextTabsButton);
        }

        // Hide L2 tabs after screen initialization
        if (ModIntegrationManager.isModLoaded(ModIntegration.L2_LIBRARY) ||
            ModIntegrationManager.isModLoaded(ModIntegration.L2_HOSTILITY) ||
            ModIntegrationManager.isModLoaded(ModIntegration.L2_ARTIFACTS) ||
            ModIntegrationManager.isModLoaded(ModIntegration.MODULAR_GOLEMS)) {
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
        Minecraft minecraft = Minecraft.getInstance();

        // Handle tab cycling keybind
        if (ModKeybinds.TAB_CYCLE.matches(event.getKeyEvent()) && (event.getModifiers() & org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT) != 0) {
            Screen currentScreen = event.getScreen();
            if (currentScreen != null && TabsMenu.hasTabsForScreen(currentScreen.getClass())) {
                TabsMenu.cycleToNextTab(currentScreen);
                event.setCanceled(true);
                return;
            }
        }

        // Only handle inventory keybind if screen was opened via tab AND current screen has tab integration
        if (TabsMenu.wasScreenOpenedViaTab() && TabsMenu.hasTabsForScreen(event.getScreen().getClass())) {
            if (minecraft.options.keyInventory.matches(event.getKeyEvent())) {
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

                    // Open inventory and clear tab tracking so normal E key behavior works
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
        if (ModIntegrationManager.isModLoaded(ModIntegration.L2_LIBRARY) ||
            ModIntegrationManager.isModLoaded(ModIntegration.L2_HOSTILITY) ||
            ModIntegrationManager.isModLoaded(ModIntegration.L2_ARTIFACTS) ||
            ModIntegrationManager.isModLoaded(ModIntegration.MODULAR_GOLEMS)) {
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
        if (ModIntegrationManager.isModLoaded(ModIntegration.L2_LIBRARY) ||
            ModIntegrationManager.isModLoaded(ModIntegration.L2_HOSTILITY) ||
            ModIntegrationManager.isModLoaded(ModIntegration.L2_ARTIFACTS) ||
            ModIntegrationManager.isModLoaded(ModIntegration.MODULAR_GOLEMS)) {
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
                
            }
        }
    }

    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        // Update mouse position for tuck mode hover detection (for screens that need special handling)
        TabsMenu.onMouseMove(event.getMouseX(), event.getMouseY(), event.getScreen());

        // Special handling for screens that don't call super.render() properly
        String screenClassName = event.getScreen().getClass().getName();

        if (screenClassName.equals("net.puffish.skillsmod.client.gui.SkillsScreen") ||
            screenClassName.equals("dev.ftb.mods.ftblibrary.ui.ScreenWrapper") ||
            screenClassName.equals("xaero.map.gui.GuiMap") ||
            screenClassName.equals("pepjebs.mapatlases.client.screen.AtlasOverviewScreen") ||
            screenClassName.equals("betteradvancements.common.gui.BetterAdvancementsScreen") ||
            screenClassName.equals("de.coldfang.wildex.client.screen.WildexScreen") ||
            screenClassName.equals(ScreenClasses.EPIC_FIGHT_SKILL_EDIT_SCREEN) ||
            screenClassName.equals(ScreenClasses.EPIC_SKILLS_SKILL_TREE_SCREEN)) {

            // These screens don't iterate renderables in their render method, so we
            // manually render TabButton/NextTabsButton at Z=0 (above any blur the screen
            // applied). Editor widgets are NOT rendered here — they go through
            // renderEditModeOverlay's Z=400/Z=700 passes, which correctly layer them
            // ABOVE the panel background drawn at Z=600. Rendering them here at Z=0
            // would put them behind the panel BG and they'd be invisible.
            for (var child : event.getScreen().children()) {
                if (child instanceof TabButton tabButton) {
                    tabButton.extractContents(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
                }
                if (child instanceof NextTabsButton nextTabsButton) {
                    nextTabsButton.extractContents(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
                }
            }
        }
    }

    /**
     * Give our tab buttons first dibs on every screen, not just the custom-routing ones.
     * Without this, host screens with many widgets (e.g. MOTP's PlayerStatsGUIScreen has 13
     * ImageButtons) iterate their own buttons first; if any has raw clicked() bounds that
     * overlap our tab area (common at small scale, since vanilla uses unscaled width=32px),
     * the wrong target screen opens. Forwarding only when isMouseOver matches our scaled
     * bounds preserves slot interaction on AbstractContainerScreens — we don't cancel
     * unless the cursor is actually on a tab.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenMouseClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (TabsMenu.isEditing(event.getScreen())) return; // edit-mode handler runs at HIGHEST
        for (var child : event.getScreen().children()) {
            if (child instanceof TabButton tabButton) {
                if (tabButton.isMouseOver(event.getMouseX(), event.getMouseY())) {
                    tabButton.mouseClicked(new MouseButtonEvent(event.getMouseX(), event.getMouseY(), new MouseButtonInfo(event.getButton(), 0)), false);
                    event.setCanceled(true);
                    return;
                }
            }
            if (child instanceof NextTabsButton nextTabsButton) {
                if (nextTabsButton.isMouseOver(event.getMouseX(), event.getMouseY())) {
                    nextTabsButton.mouseClicked(new MouseButtonEvent(event.getMouseX(), event.getMouseY(), new MouseButtonInfo(event.getButton(), 0)), false);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    /**
     * Companion to {@link #onScreenMouseClick}: dispatches releases to whichever tab button
     * has {@code pressStartMs > 0} (set in {@code mouseClicked}). Runs at HIGH priority for
     * every screen so the short-click open-target path fires regardless of the host screen
     * doing custom mouse routing or eating the release through some focused widget.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenMouseReleasedSpecial(ScreenEvent.MouseButtonReleased.Pre event) {
        Screen screen = event.getScreen();
        if (TabsMenu.isEditing(screen)) return; // edit-mode handler in onMouseReleased takes over
        // Only forward (and cancel) when a tab actually owns this release — i.e. it had a
        // pending press from mouseClicked. Without that gate we'd cancel every left-button
        // release on every screen with tabs, since AbstractWidget.mouseReleased returns true
        // for any left release regardless of state — that breaks slot drag-drop in the vanilla
        // inventory. NextTabsButton doesn't track press state (it fires onPress synchronously
        // from mouseClicked) so it never needs forwarding here.
        // Iterate a snapshot: tabButton.mouseReleased -> openTargetScreen -> Minecraft.setScreen,
        // which can synchronously mutate the live children list (mod mixins on Screen lifecycle)
        // and trigger ConcurrentModificationException on the next iterator step.
        boolean dispatched = false;
        for (var child : new java.util.ArrayList<>(screen.children())) {
            if (child instanceof TabButton tabButton && tabButton.hasPendingPress()) {
                tabButton.mouseReleased(new MouseButtonEvent(event.getMouseX(), event.getMouseY(), new MouseButtonInfo(event.getButton(), 0)));
                dispatched = true;
                break;
            }
        }
        if (dispatched) event.setCanceled(true);
    }
}
