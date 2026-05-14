package vodmordia.modtabs.utils;

/**
 * Central registry of fully-qualified class names for screens (and a few items) from every
 * integrated mod. When a mod renames or restructures a class, this file is the only place
 * that needs to change — instead of grepping across 35 tab classes plus the TabsMenu switch.
 *
 * <p>Constants are grouped by mod and named so the call site reads naturally:
 * {@code ScreenClasses.FTB_LIBRARY_WRAPPER} rather than a raw string literal.
 *
 * <p>All values are {@code public static final String} so they remain valid as
 * {@code switch} case labels and as compile-time constants.
 */
public final class ScreenClasses {
    private ScreenClasses() {}

    // -- Vanilla ----------------------------------------------------------
    public static final String VANILLA_INVENTORY =
            "net.minecraft.client.gui.screens.inventory.InventoryScreen";
    public static final String VANILLA_ADVANCEMENTS =
            "net.minecraft.client.gui.screens.advancements.AdvancementsScreen";
    public static final String VANILLA_CONTAINER =
            "net.minecraft.client.gui.screens.inventory.AbstractContainerScreen";

    // -- AppleSkin --------------------------------------------------------
    public static final String APPLESKIN_FOOD_STATS =
            "squeek.appleskin.client.gui.screen.FoodStatsScreen";

    // -- Ars Nouveau ------------------------------------------------------
    public static final String ARS_NOUVEAU_SPELLBOOK_GUI =
            "com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook";
    public static final String ARS_NOUVEAU_SPELLBOOK_ITEM =
            "com.hollingsworth.arsnouveau.common.items.SpellBook";
    // Legacy / alternate names also present in TabsMenu switch
    public static final String ARS_NOUVEAU_SPELLBOOK_GUI_LEGACY1 =
            "com.dhanantry.arsnouveau.client.gui.SpellBookGUI";
    public static final String ARS_NOUVEAU_SPELLBOOK_GUI_LEGACY2 =
            "com.dhanantry.arsnouveau.client.gui.SpellBookScreen";

    // -- Backpacked (mrcrayfish) ------------------------------------------
    public static final String BACKPACKED_SCREEN =
            "com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen";
    public static final String BACKPACKED_ITEM =
            "com.mrcrayfish.backpacked.item.BackpackItem";
    public static final String BACKPACKED_SCREEN_ALT =
            "net.backpacked.client.screen.BackpackScreen";
    public static final String BACKPACKED_FLYWHEEL_TRANSFORM =
            "com.jozufozu.flywheel.util.transform.TransformStack";

    // -- Better Advancements ----------------------------------------------
    public static final String BETTER_ADVANCEMENTS =
            "betteradvancements.common.gui.BetterAdvancementsScreen";

    // -- Body Damage (Legendary Survival Overhaul) ------------------------
    public static final String LSO_BODY_HEALTH =
            "sfiomn.legendarysurvivaloverhaul.client.gui.BodyHealthScreen";

    // -- Brassworks Missions ----------------------------------------------
    public static final String BRASSWORKS_MISSIONS_UI =
            "net.swzo.brassworksmissions.client.gui.UiScreen";

    // -- Cobblemon --------------------------------------------------------
    public static final String COBBLEMON_PARTY =
            "com.cobblemon.mod.common.client.gui.party.PartyGUI";
    public static final String COBBLEMON_PARTY_LEGACY =
            "com.cobblemon.mod.common.client.gui.PartyGUI";

    // -- Cosmetic Armor Reworked ------------------------------------------
    public static final String COSMETIC_ARMOR_GUI =
            "lain.mods.cos.impl.client.gui.GuiCosArmorInventory";

    // -- Draconic Evolution / SCGuns ---------------------------------------
    public static final String DRACONIC_EVOLUTION_GUI =
            "com.brandon3055.draconicevolution.client.gui.GuiDraconicEvolution";
    public static final String SCGUNS_ATTACHMENT =
            "com.dhanantry.scguns.client.screen.AttachmentScreen";
    public static final String SCGUNS_PASSIVE_SKILL =
            "com.dhanantry.scguns.client.screen.PassiveSkillScreen";

    // -- Eccentric Tome ---------------------------------------------------
    public static final String ECCENTRIC_TOME_SCREEN =
            "website.eccentric.tome.client.TomeScreen";
    public static final String ECCENTRIC_TOME_ITEM =
            "website.eccentric.tome.TomeItem";

    // -- FTB Library / Quests / Teams -------------------------------------
    public static final String FTB_LIBRARY_WRAPPER =
            "dev.ftb.mods.ftblibrary.ui.ScreenWrapper";
    public static final String FTB_QUESTS_QUEST_SCREEN =
            "dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen";
    public static final String FTB_QUESTS_QUESTION_SCREEN =
            "dev.ftb.mods.ftbquests.client.gui.QuestionScreen";
    public static final String FTB_QUESTS_QUESTS_SCREEN =
            "dev.ftb.mods.ftbquests.client.gui.QuestsScreen";
    public static final String FTB_QUESTS_QUEST_SCREEN_ALT1 =
            "dev.ftb.mods.ftbquests.client.screens.QuestScreen";
    public static final String FTB_QUESTS_QUEST_SCREEN_ALT2 =
            "dev.ftb.mods.ftbquests.client.QuestScreen";
    public static final String FTB_TEAMS_SCREEN =
            "dev.ftb.mods.ftbteams.client.gui.TeamsScreen";
    public static final String FTB_TEAMS_SCREEN_ALT1 =
            "dev.ftb.mods.ftbteams.client.screens.TeamsScreen";
    public static final String FTB_TEAMS_SCREEN_ALT2 =
            "dev.ftb.mods.ftbteams.client.TeamsScreen";

    // -- JourneyMap -------------------------------------------------------
    public static final String JOURNEYMAP_FULLSCREEN =
            "journeymap.client.ui.fullscreen.Fullscreen";

    // -- Map Atlases ------------------------------------------------------
    public static final String MAP_ATLASES_OVERVIEW =
            "pepjebs.mapatlases.client.screen.AtlasOverviewScreen";
    public static final String MAP_ATLASES_ACCESS_UTILS =
            "pepjebs.mapatlases.client.ui.MapAtlasesAccessUtils";
    public static final String MAP_ATLASES_ITEM =
            "pepjebs.mapatlases.item.MapAtlasItem";

    // -- Pufferfish Skills ------------------------------------------------
    public static final String PUFFERFISH_SKILLS =
            "net.puffish.skillsmod.client.gui.SkillsScreen";
    public static final String PUFFERFISH_SKILLS_ALT1 =
            "puffish.skillsmod.client.screen.SkillScreen";
    public static final String PUFFERFISH_SKILLS_ALT2 =
            "net.puffish.skillsmod.client.screen.SkillScreen";

    // -- Xaero's World Map ------------------------------------------------
    public static final String XAEROS_MAP =
            "xaero.map.gui.GuiMap";

    // -- Cobblemon (additional screens for tab registration) --------------
    public static final String COBBLEMON_SUMMARY =
            "com.cobblemon.mod.common.client.gui.summary.Summary";
    public static final String COBBLEMON_STARTER_SELECTION =
            "com.cobblemon.mod.common.client.gui.startselection.StarterSelectionScreen";

    // -- FTB Chunks -------------------------------------------------------
    public static final String FTB_CHUNKS_MAP =
            "dev.ftb.mods.ftbchunks.client.map.MapScreen";
    public static final String FTB_CHUNKS_MAP_ALT1 =
            "dev.ftb.mods.ftbchunks.client.gui.MapScreen";
    public static final String FTB_CHUNKS_MAP_ALT2 =
            "dev.ftb.mods.ftbchunks.client.screens.MapScreen";

    // -- Memory of the Past (MOTP) ----------------------------------------
    public static final String MOTP_PLAYER_STATS =
            "tn.mbs.memory.client.gui.PlayerStatsGUIScreen";

    // -- Passive Skill Tree (daripher) ------------------------------------
    public static final String PASSIVE_SKILL_TREE =
            "daripher.skilltree.client.screen.SkillTreeScreen";

    // -- L2 family --------------------------------------------------------
    public static final String L2_ARTIFACTS_SET_EFFECT =
            "dev.xkmc.l2artifacts.content.client.tab.SetEffectScreen";
    public static final String L2_HOSTILITY_DIFFICULTY =
            "dev.xkmc.l2hostility.content.menu.tab.DifficultyScreen";

    // -- Modular Golems ---------------------------------------------------
    public static final String MODULAR_GOLEMS_INFO =
            "dev.xkmc.modulargolems.content.client.tracker.GolemInfoScreen";

    // -- RPG Crafting -----------------------------------------------------
    public static final String RPG_CRAFTING_HAND =
            "com.github.theredbrain.rpgcrafting.gui.screen.ingame.HandCraftingScreen";

    // -- Body Damage / LSO (modern path) ----------------------------------
    public static final String LSO_BODY_HEALTH_SCREENS =
            "sfiomn.legendarysurvivaloverhaul.client.screens.BodyHealthScreen";

    // -- Draconic Evolution (configurable item GUI) -----------------------
    public static final String DRACONIC_EVOLUTION_CONFIGURABLE_ITEM =
            "com.brandon3055.draconicevolution.client.gui.modular.itemconfig.ConfigurableItemGui$Screen";

    // -- Travelers Backpack -----------------------------------------------
    public static final String TRAVELERS_BACKPACK_SCREEN =
            "com.tiviacz.travelersbackpack.client.screens.BackpackScreen";

    // -- Biology Dictionary -----------------------------------------------
    public static final String BIOLOGY_DICTIONARY_HOME_SCREEN =
            "io.github.xienaoban.biologydictionary.gui.screen.BdHomeScreen";
    public static final String BIOLOGY_DICTIONARY_ABOUT_SCREEN =
            "io.github.xienaoban.biologydictionary.gui.screen.BdAboutScreen";
    public static final String BIOLOGY_DICTIONARY_CONFIG_SCREEN =
            "io.github.xienaoban.biologydictionary.gui.screen.BdConfigScreen";
    public static final String BIOLOGY_DICTIONARY_ENTITY_OVERVIEW_SCREEN =
            "io.github.xienaoban.biologydictionary.gui.screen.BdEntityOverviewScreen";
    public static final String BIOLOGY_DICTIONARY_ENTITY_DETAIL_SCREEN =
            "io.github.xienaoban.biologydictionary.gui.screen.BdEntityDetailScreen";
    /** {@code BiologyDictionaryItem.createBook()} / {@code isBook(ItemStack)}. */
    public static final String BIOLOGY_DICTIONARY_ITEM =
            "io.github.xienaoban.biologydictionary.core.BiologyDictionaryItem";
    /** Static {@code openBookScreen(Minecraft)} entry point used by the keybind and right-click. */
    public static final String BIOLOGY_DICTIONARY_EVENT =
            "io.github.xienaoban.biologydictionary.client.BiologyDictionaryEvent";
    /** {@code ConfigsManager.getServer()} returns {@code Configs.ServerConfigs} which exposes {@code isBookItemRequired()}. */
    public static final String BIOLOGY_DICTIONARY_CONFIGS_MANAGER =
            "io.github.xienaoban.biologydictionary.config.ConfigsManager";

    // -- Reliable Backpacks -----------------------------------------------
    public static final String RELIABLE_BACKPACKS_ITEM =
            "com.evandev.reliable_backpacks.common.items.BackpackItem";
    public static final String RELIABLE_BACKPACKS_CONTAINER =
            "com.evandev.reliable_backpacks.common.items.BackpackItemContainer";

    // -- Wildex Bestiary --------------------------------------------------
    public static final String WILDEX_SCREEN =
            "de.coldfang.wildex.client.screen.WildexScreen";
    /** Static {@code open()} entry point, same path the mod's own keybind uses. */
    public static final String WILDEX_SCREEN_OPENER =
            "de.coldfang.wildex.client.WildexScreenOpener";
    /** {@code WildexClientConfigView.requireBookForKeybind()} respects synced server config. */
    public static final String WILDEX_CLIENT_CONFIG_VIEW =
            "de.coldfang.wildex.client.WildexClientConfigView";

    // -- Apothic Attributes -----------------------------------------------
    /** Renderable overlay attached to {@link InventoryScreen} via the mod's own ScreenEvent.Init.Post listener.
     *  Holds the static {@code wasOpen} flag we flip before opening the inventory. */
    public static final String APOTHIC_ATTRIBUTES_GUI =
            "dev.shadowsoffire.apothic_attributes.client.AttributesGui";
    /** Static {@code enableAttributesGui} field — server admins can disable the panel entirely. */
    public static final String APOTHIC_ATTRIBUTES_CONFIG =
            "dev.shadowsoffire.apothic_attributes.ALConfig";

    // -- Curios -----------------------------------------------------------
    /** Legacy curios inventory (V1). Used when {@code CuriosConfig.SERVER.enableLegacyMenu = true}. */
    public static final String CURIOS_SCREEN =
            "top.theillusivec4.curios.client.gui.CuriosScreen";
    /** Revamped curios inventory (V2). Default on modern Curios — {@code CuriosContainerProvider}
     *  picks V2 unless the legacy-menu config flag is on, so this is what most players see. */
    public static final String CURIOS_SCREEN_V2 =
            "top.theillusivec4.curios.client.gui.CuriosScreenV2";
    /** Serverbound packet with a single {@code ItemStack carried} field; the server's handler
     *  opens the curios menu for the player. Same path the mod's CuriosButton uses. */
    public static final String CURIOS_OPEN_PACKET =
            "top.theillusivec4.curios.common.network.client.CPacketOpenCurios";

    // -- The Aether ------------------------------------------------------
    /** {@code EffectRenderingInventoryScreen<AetherAccessoriesMenu>} — the accessory inventory. */
    public static final String AETHER_ACCESSORIES_SCREEN =
            "com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen";
    /** Serverbound packet with a single {@code ItemStack carryStack} field; the server's handler
     *  calls {@code serverPlayer.openMenu(...)} with an {@code AetherAccessoriesMenu}. Same path
     *  the mod's own "I" keybind uses in {@code GuiHooks.openAccessoryMenu()}. */
    public static final String AETHER_OPEN_ACCESSORIES_PACKET =
            "com.aetherteam.aether.network.packet.serverbound.OpenAccessoriesPacket";

    // -- Epic Fight ------------------------------------------------------
    /** {@code Screen} (not a container screen) opened by Epic Fight's "K" keybind via
     *  {@code ControlEngine.openSkillEditor()}. Constructor takes ({@link net.minecraft.world.entity.player.Player},
     *  {@code PlayerSkills}). */
    public static final String EPIC_FIGHT_SKILL_EDIT_SCREEN =
            "yesman.epicfight.client.gui.screen.SkillEditScreen";
    /** Static helpers: {@code getLocalPlayerPatch(LocalPlayer)} returns the patch attached
     *  via {@code EpicFightAttachmentTypes.ENTITY_PATCH}; the patch's {@code getPlayerSkills()}
     *  supplies the second {@link #EPIC_FIGHT_SKILL_EDIT_SCREEN} constructor argument. */
    public static final String EPIC_FIGHT_CAPABILITIES =
            "yesman.epicfight.world.capabilities.EpicFightCapabilities";

    // -- Epic Fight Skill Tree (epicskills addon) ------------------------
    /** {@code Screen} (not a container screen) opened by epicskills' keybind. Constructor
     *  takes a single {@code LocalPlayerPatch}. Overrides {@code render()} without calling
     *  {@code super.render()} → same plain-Screen-override quirk as
     *  {@link #EPIC_FIGHT_SKILL_EDIT_SCREEN}; add this FQN to the manual-renderables
     *  list in {@code ClientNeoForgeEvents.onScreenRenderPost} or tabs won't draw. */
    public static final String EPIC_SKILLS_SKILL_TREE_SCREEN =
            "com.yesman.epicskills.client.gui.screen.SkillTreeScreen";
    /** Epic Fight's local-player patch class — second {@link #EPIC_SKILLS_SKILL_TREE_SCREEN}
     *  constructor argument type. Note the {@code capabilites} typo (sic) in the package. */
    public static final String EPIC_FIGHT_LOCAL_PLAYER_PATCH =
            "yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch";

    // -- Completionist's Index (Fuzs, NeoForge 1.21.1) -------------------
    /** Abstract parent of {@code ModsIndexViewScreen} / {@code ItemsIndexViewScreen}.
     *  On 1.21.1 it extends {@code StatsUpdateListener} → {@code StatsScreen} (vanilla),
     *  and its {@code render()} calls {@code super.render()} so tabs added as children
     *  DO draw without needing the manual-renderables list in
     *  {@code ClientNeoForgeEvents.onScreenRenderPost}. */
    public static final String COMPLETIONISTS_INDEX_VIEW_SCREEN =
            "fuzs.completionistsindex.client.gui.screens.inventory.IndexViewScreen";
    /** Top-level index screen — constructor is {@code (Screen lastScreen, boolean fromInventory)}.
     *  Note the {@code boolean} arg is new on 1.21.1 (1.20.1 Forge only took the {@code Screen}). */
    public static final String COMPLETIONISTS_INDEX_MODS_SCREEN =
            "fuzs.completionistsindex.client.gui.screens.inventory.ModsIndexViewScreen";
    /** Drill-down screen (per-mod item list). Constructor is {@code (Screen, boolean, List<ItemStack>)};
     *  we never open it directly but track it for the "currently used" check so cycling skips
     *  the tab when the player is browsing a mod's items. */
    public static final String COMPLETIONISTS_INDEX_ITEMS_SCREEN =
            "fuzs.completionistsindex.client.gui.screens.inventory.ItemsIndexViewScreen";
    /** Static handler that adds the inventory + pause-menu buttons. On NeoForge 1.21.1 the
     *  method names are clean ({@code onAfterInventoryScreenInit} / {@code onAfterPauseScreenInit})
     *  — different from the 1.20.1 Forge build's {@code onScreenInit$Post$1/2}. */
    public static final String COMPLETIONISTS_INDEX_BUTTON_HANDLER =
            "fuzs.completionistsindex.client.handler.IndexButtonHandler";
}
