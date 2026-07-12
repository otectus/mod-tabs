# Changelog

All notable changes to **Mod Tabs** are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0] - 2026-07-12

The **trustworthy core** release: every documented feature now actually works, and the
internals were hardened before any new feature work. If you only read one line: the
layout editor is back (fully — it was broken in 0.1.0 and could soft-lock your tab bar),
and config changes now apply without restarting the game.

### Fixed
- **The layout editor works again.** In 0.1.0 the editor could be *entered* (long-press)
  but none of its input handling survived the port — no handle responded, and there was
  no way to save or leave, soft-locking that screen's tab UI for the session. The full
  editor is restored: drag to move, corner handles to scale, spacing and rotation
  handles, arrow-key nudging (Shift for 8px steps), ESC to cancel, and save/cancel/reset
  buttons. If a different screen opens mid-edit, the editor now exits instead of
  silently swallowing all input.
- **Config screen changes apply immediately.** Settings were snapshotted once at launch,
  so enabling Nearby Container Tabs in-game appeared to do nothing until a relaunch.
- **Container-switch desyncs.** All three menu-close paths now use vanilla's own close
  semantics; previously the client could be left pointing at a dead menu, breaking slot
  interactions after switching containers via tabs.
- **Out-of-reach container tabs.** The scan radius could exceed the server's interaction
  range, producing tabs whose click closed your current screen and then failed, leaving
  a desynced view. The scan is now capped at your actual block-interaction reach and the
  open action re-checks range before touching anything.
- **Stale pagination.** Returning to a page that no longer exists (chests broken while
  you were on page 2+) showed an empty bar until you paged manually; the index now snaps
  to the last full page.
- **Custom-icon texture leak.** Every icon file you experimented with registered a GPU
  texture that was never released; superseded textures are now evicted and F3+T clears
  the cache.

### Added
- **Options panel and global settings modal** in the layout editor: per-tab visibility
  (`Yes`/`No`/`Tuck`), custom icon (with dropdown, folder shortcut and refresh), icon
  scale and nudge, anchoring, tab order, tabs-per-page — plus the cogwheel modal for
  per-tab visibility/sticky/order and global icon offsets. These were documented in
  0.1.0 but had no working UI.
- **Tab Cycle (Backward)** keybind (default **Ctrl+Tab**).
- **Fail-soft tab opens**: a tab whose target screen crashes on open is disabled for the
  session with a toast and a logged stack trace instead of taking the game down.
- First **unit tests** (pagination math) — run with `./gradlew test`.

### Changed
- **The Shift in Shift+Tab is now part of the keybinding** (NeoForge KeyModifier) instead
  of being hardcoded in the handler, so rebinding actually works and controller mods can
  map it. *Migration note:* a `key.modtabs.tab_cycle` entry saved by 0.1.0 in
  `options.txt` loads without the modifier (bare Tab cycles) — reset the bind to default
  in Controls if that happens.
- **Nearby-container scanning is ~100× cheaper**: instead of checking every block in a
  cube twice a second, it walks the chunk block-entity maps. *Behavior change:* blocks
  with a menu but no block entity — crafting tables, grindstones, anvils and the like —
  no longer get tabs. They are stateless workstations, not containers.
- Double-chest deduplication is deterministic across chunk borders (no more phantom
  refreshes for a pair straddling two chunks).

### Removed
- The dead scaffolding of the stripped 1.21.1 features: ~44 inert per-mod config groups
  (and the two **Custom Tabs** config toggles that gated a subsystem that doesn't exist
  in this build), nine unused tab classes, 16 bundled layouts and 6 icon textures for
  unregistered screens, and ~400 orphaned translation keys. Existing `config/modtabs.json`
  values for those keys are dropped on next save; they had no effect in 0.1.0. The
  custom-tab subsystem returns in a future release as data-driven JSON definitions.

## [0.1.0] - 2026-06-30

Initial release of the **minimal core port** to Minecraft 26.1.2 / NeoForge. This is a
ground-up rewrite of Vodmordia's *[Mod Tabs](https://www.curseforge.com/minecraft/mc-mods/mod-tabs)*
([source](https://github.com/morelandjo/ModTabs)) — itself a rewrite of Sfiomn's
*[Legendary Tabs](https://www.curseforge.com/minecraft/mc-mods/legendary-tabs)* — reduced to
the tab framework plus the two always-available tabs. Per-mod
integrations from the older 1.21.1 line are not part of this build.

### Added
- Tab-bar framework mounted on the inventory screen and the vanilla container screens
  (chest, shulker box, dispenser, hopper).
- **Inventory / home tab** that returns you to your inventory, cleanly closing any open
  container menu on the server first to avoid desyncs.
- **Nearby container tabs**: one tab per openable block within a configurable radius,
  discovered from each block's menu provider so vanilla and modded containers are both
  detected. Includes double-chest deduplication, blocked-chest skipping, an optional
  line-of-sight check, and a live refresh when containers change while a screen is open.
  Disabled by default; range defaults to 5 (1–16).
- **Per-screen layout editor** (open with Shift+Z or a ~1.5 s long-press on the inventory
  tab): move, scale, rotate and re-space the bar, reposition/rotate the paging button,
  choose a tuck direction, switch between GUI-relative and screen-absolute anchoring, and
  set a tabs-per-page cap. Layouts persist per screen type.
- **Sticky tabs**, **tuck mode** with hover reveal, and per-screen visibility (`Yes` /
  `No` / `Tuck`).
- **Pagination** with a chevron paging button when tabs exceed the per-page cap.
- **Shift+Tab** keybind to cycle tabs, and inventory-key handling to return home from a
  tab-opened screen.
- **Modpack lock** (`allowEditing`) to ship a fixed, non-editable layout.
- MidnightLib-backed configuration screen.

### Changed
- Rebased onto Minecraft 26.1.2, NeoForge `26.1.2.76` and Java 25.
- Migrated configuration entirely to MidnightConfig; the legacy NeoForge config system was
  removed.
- Container detection is now menu-provider driven rather than relying on a hardcoded
  block list.

### Removed
- The ~45 third-party per-mod integration tabs, the integration network packets, and the
  standalone global-settings panel from the older 1.21.1 line are not included in this
  core build.

[0.2.0]: https://github.com/otectus/mod-tabs/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/otectus/mod-tabs/releases/tag/v0.1.0
