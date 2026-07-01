# Changelog

All notable changes to **Mod Tabs** are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

[0.1.0]: https://github.com/otectus/mod-tabs/releases/tag/v0.1.0
