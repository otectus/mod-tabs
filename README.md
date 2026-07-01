# Mod Tabs

A NeoForge mod that adds a configurable **tab bar** to your inventory and container
screens, letting you jump between screens without closing your inventory. It also
surfaces **nearby chests, barrels, shulker boxes and modded containers** as tabs so you
can open them straight from the inventory screen.

This repository is a **fork** and **minimal core port to Minecraft 26.1.2 / NeoForge** of
**[Mod Tabs](https://www.curseforge.com/minecraft/mc-mods/mod-tabs)** by **Vodmordia**
([source](https://github.com/morelandjo/ModTabs)), which itself is a rewrite of
**[Legendary Tabs](https://www.curseforge.com/minecraft/mc-mods/legendary-tabs)** by
**Sfiomn** ([source](https://github.com/sfiomn/LegendaryTabs)). Version `0.1.0` ships the
tab framework, the inventory (home) tab and the nearby-container tabs; additional per-mod
integrations are planned on top of this base. Distributed under the **GNU LGPL v2.1** — the
same license as the upstream *Mod Tabs* project.

---

## Requirements

| | |
|---|---|
| **Minecraft** | 26.1.2 |
| **Loader** | NeoForge `26.1.2.76` (range `[26.1.0,)`), FML `[4,)` |
| **Java** | 25 |
| **Required dependency** | [MidnightLib](https://modrinth.com/mod/midnightlib) (`1.9.3+26.1-neoforge`) — powers the config screen |
| **Side** | Client (safe to install client-side only) |

---

## Features

- **Inventory / home tab** — a tab bar anchored to the inventory screen and the vanilla
  container screens (chest, shulker box, dispenser, hopper). The home tab always takes
  you back to your inventory.
- **Nearby container tabs** — scans a radius around the player and shows **one tab per
  openable block**. Detection is driven by the block's menu provider, so vanilla *and*
  modded containers are picked up automatically without a hardcoded list. Double chests
  collapse into a single tab, blocked chests are skipped, and the bar refreshes live when
  a container is placed or broken while your screen is open. *(Disabled by default.)*
- **Per-screen layout editor** — reposition, scale, rotate and space out the bar, move the
  paging button, choose a tuck direction and anchoring mode, and set a tabs-per-page cap.
  Layouts are saved per screen type.
- **Tuck & sticky tabs** — tuck the bar out of the way and reveal it on hover, pin tabs to
  the leading edge so they survive pagination, or hide the bar entirely per screen.
- **Pagination** — cap how many tabs show per page; a chevron button flips through the rest.
- **Modpack lock** — pack authors can disable layout editing to ship a fixed arrangement.

---

## Controls

| Action | Default |
|---|---|
| Cycle to the next tab | **Shift + Tab** *(rebindable — "Tab Cycle" under the *Mod Tabs* controls category)* |
| Return to the inventory from a tab-opened screen | your **inventory key** (default **E**) |
| Open the layout editor | **Shift + Z**, or **long-press the inventory tab** (~1.5 s) |
| Click a tab | left-click to open its target screen |

---

## Configuration

Settings live in the in-game **MidnightLib** config screen (and in
`config/modtabs.json`). The most useful options:

| Option | Default | Description |
|---|---|---|
| **Allow Layout Editing** | `true` | When off, every layout-editor entry point is locked. Intended for modpacks shipping a fixed layout. |
| **Enable Nearby Container Tabs** | `false` | Show one tab per nearby chest / barrel / shulker box / modded container. |
| **Nearby Container Range** | `5` | Radius in blocks to scan for openable containers (`1`–`16`). |
| **Require Line of Sight** | `false` | If on, containers behind walls won't show a tab. |
| **Custom Tabs** | `true` | Toggles for the data-driven custom-tab subsystem and its debug logging. |

Per-tab visibility (`Yes` / `No` / `Tuck`), order, sticky state and custom icons are
edited through the in-game layout tools rather than the raw config file.

---

## Building

The project uses NeoForge ModDevGradle and requires a **full JDK 25** (with `javac`).

```bash
./gradlew build
```

The built jar lands in `build/libs/`. To launch the dev client:

```bash
./gradlew runClient
```

> The JDK 25 path is pinned in `gradle.properties`
> (`org.gradle.java.installations.paths`). Adjust it to your local install if needed.

---

## Credits & License

- Original *[Legendary Tabs](https://www.curseforge.com/minecraft/mc-mods/legendary-tabs)*
  concept and implementation by **Sfiomn**
  ([source](https://github.com/sfiomn/LegendaryTabs)).
- *[Mod Tabs](https://www.curseforge.com/minecraft/mc-mods/mod-tabs)* rewrite and NeoForge
  port by **Vodmordia** ([source](https://github.com/morelandjo/ModTabs)) — the direct
  parent this fork is based on.
- 26.1.2 core port (this fork) by **otectus**
  ([source](https://github.com/otectus/mod-tabs)).
- The nearby-container feature is a clean-room implementation inspired by the design of
  **[InventoryTabs](https://github.com/dhyces/InventoryTabs)** by dhyces / Kavin Phan — see
  [`CREDITS.txt`](CREDITS.txt) for the full attribution.

Licensed under the **GNU LGPL v2.1** — see [`LICENSE`](LICENSE). This matches the license of
the upstream *Mod Tabs* project. See
[`CREDITS.txt`](CREDITS.txt) for third-party attributions.
