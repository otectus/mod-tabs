# Mod Tabs

**Stop closing your inventory just to open a chest.** Mod Tabs adds a clean, configurable
**tab bar** to your inventory and container screens — click a tab to jump straight to
another screen, then hit your inventory key to snap right back.

This is the **Minecraft 26.1.2 / NeoForge** line (`0.2.0`): a ground-up rewrite of
the classic *Legendary Tabs* / *Mod Tabs* concept, rebuilt on a lean, flexible core.

> **This is a fork.** Mod Tabs (Forked) is a fork of
> **[Mod Tabs](https://www.curseforge.com/minecraft/mc-mods/mod-tabs)** by **Vodmordia**
> ([source](https://github.com/morelandjo/ModTabs)), which itself descends from
> **[Legendary Tabs](https://www.curseforge.com/minecraft/mc-mods/legendary-tabs)** by
> **Sfiomn**. It is distributed under the **GNU LGPL v2.1** (see *How this fork differs* and
> *Credits* below). Fork source: <https://github.com/otectus/mod-tabs>

---

## ✨ What it does

### 🎒 Nearby containers, one click away
Enable **Nearby Container Tabs** and Mod Tabs quietly scans the blocks around you and
adds a tab for every openable one — **chests, barrels, shulker boxes, hoppers, dispensers,
and modded containers alike**. No hardcoded list: if a block opens a menu when you
right-click it, it gets a tab.

- Double chests show as a **single** tab — no clutter.
- Blocked chests (something on top) are skipped, just like vanilla.
- The bar **updates live** if a chest is placed or broken while you're browsing.
- Optional **line-of-sight** filter, and an adjustable **scan radius** (1–16 blocks) —
  capped at your actual **block-interaction reach**, so every tab you see is one the
  server will actually let you open.
- Detection covers **block entities** — every real container. Stateless workstations
  (crafting tables, anvils, grindstones) don't get tabs.

### 🏠 Always-home inventory tab
A dedicated inventory tab sits on your inventory screen and on vanilla container screens,
so getting back home is always one click — and it closes any open container properly so
your items never desync.

### 🎛️ Make the bar yours
A built-in **per-screen layout editor** (Shift+Z, or long-press the inventory tab) lets
you dial in exactly how the bar looks:

- **Move, scale, rotate** and re-space the tabs; arrow keys nudge pixel-by-pixel.
- Reposition and rotate the **paging button**.
- An **options panel** for per-tab settings: visibility (`Yes`/`No`/`Tuck`), **custom
  icons** (drop a PNG in `config/modtabs/icons/`), icon scale and nudge, anchoring,
  tab order, and a **tabs-per-page** cap.
- A **global settings modal** (the cogwheel) for per-tab visibility, sticky state,
  ordering and global icon offsets in one place.
- **Tuck** the bar out of the way and have it slide back in on hover — in any direction.
- Pin **sticky tabs** to the leading edge so they never scroll off the page.
- **ESC cancels**, the save button persists, reset restores the curated default.

Every layout is saved **per screen**, so each menu can look exactly how you want. And a
tab that misbehaves (a modded screen that crashes on open) is quietly disabled for the
session with a toast — never a crash.

### 📦 Modpack-friendly
Pack authors can flip a single **"Allow Layout Editing"** switch to lock the layout, ship a
curated arrangement, and keep it consistent for every player.

---

## 🎮 Controls

- **Shift + Tab** — cycle to the next tab *(fully rebindable, modifier included)*
- **Ctrl + Tab** — cycle to the previous tab *(rebindable)*
- **Your inventory key (E)** — return to the inventory from a tab-opened screen
- **Shift + Z** *or* **long-press the inventory tab** — open the layout editor
- **ESC** — leave the layout editor
- **Left-click a tab** — open its screen

---

## 📋 Requirements

- **Minecraft 26.1.2**
- **NeoForge** (`26.1.2.76` or compatible)
- **Java 25**
- **[MidnightLib](https://modrinth.com/mod/midnightlib)** — required (powers the config screen)

Client-side friendly — you can install it on the client alone.

---

## ⚙️ Configuration

All options live in the in-game **MidnightLib** config screen and **apply the moment you
close it** — no restart needed. Toggle nearby-container tabs, set the scan radius and
line-of-sight, lock layout editing for modpacks, and more. Fine-grained per-tab tweaks
(visibility, order, sticky, custom icons) are handled right in the layout editor.

---

## 🔀 How this fork differs from the original

Mod Tabs (Forked) is a **ground-up port to Minecraft 26.1.2 / NeoForge**, deliberately
reduced to a lean core rather than a feature-for-feature copy of the upstream 1.21.1 line:

- **Ships** the tab-bar framework, the inventory / home tab, the nearby-container tabs,
  and the full per-screen layout editor (including the options panel and the
  global-settings modal).
- **Not included (yet):** the ~45 third-party per-mod integration tabs and the
  integration network packets from the older *Mod Tabs* / *Legendary Tabs* builds. They
  are planned to return as **data-driven JSON definitions** rather than hand-written
  code, so new integrations become contributions instead of feature requests.
- **Config migrated** entirely to the in-game **MidnightLib** config screen, with changes
  applying live.
- **Container detection is menu-provider-driven** (no hardcoded block list), so vanilla
  and modded containers are picked up automatically — and the scan walks chunk
  block-entity maps, so it stays effectively free even at max radius.

---

## 🙏 Credits

- Original *[Legendary Tabs](https://www.curseforge.com/minecraft/mc-mods/legendary-tabs)*
  concept and implementation by **Sfiomn**
  ([source](https://github.com/sfiomn/LegendaryTabs)).
- *[Mod Tabs](https://www.curseforge.com/minecraft/mc-mods/mod-tabs)* rewrite and NeoForge
  port by **Vodmordia** ([source](https://github.com/morelandjo/ModTabs)) — the direct
  parent of this fork.
- 26.1.2 core port (this fork) by **otectus**
  ([source](https://github.com/otectus/mod-tabs)).
- The nearby-container feature is a clean-room implementation inspired by the design of
  **[InventoryTabs](https://github.com/dhyces/InventoryTabs)** by dhyces / Kavin Phan. No
  source code was copied — the shared ideas are credited in full in the project's
  `CREDITS.txt`.

Released under the **GNU LGPL v2.1** — the same license as the upstream *Mod Tabs* project.

---

> **Note:** `0.2.0` is the *trustworthy core* release — every documented feature works,
> the internals were hardened, and the layout editor is back in full. Per-mod
> integrations return next as data-driven definitions. Feedback and bug reports are very
> welcome!
>
> **Upgrading from 0.1.0?** If tab cycling fires on bare **Tab**, reset the "Tab Cycle"
> bind to default in Controls once — the Shift modifier moved into the keybinding itself.
