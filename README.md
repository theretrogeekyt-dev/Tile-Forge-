# <align="center">🔥 Tile Forge

<p align="center">
  <strong>Slide • Merge • Gather Energy • Forge Relics</strong><br>
  <em>A modern, fantasy-infused puzzle strategy game built with Jetpack Compose & Kotlin</em>
</p>

---

## 🌟 Overview

**Tile Forge** takes classic sliding-tile puzzle mechanics and elevates them with strategic RPG elements: harness arcane energy, trigger powerful forging abilities, survive obstacle-laden boards, and craft legendary artifacts. 

Whether competing in the seeded **Daily Crucible**, testing your endurance in **Endless Forge**, or clearing hand-crafted **Challenge Levels**, Tile Forge delivers responsive haptic feedback, dynamic themes, immersive audio, and a mistake-forgiving **Rescue Health system**.

---

## 🎮 Game Modes

### 1. 📅 Daily Crucible (Daily Quest)
* **Synchronized Seeded Board**: Every player worldwide tackles the exact same starting board and tile generation sequence each day.
* **Pure Strategy**: Maximize your daily high score without random variation advantages.
* **Persistent Daily Records**: Compete against your personal bests with daily performance tracking.

### 2. ♾️ Endless Forge
* **Infinite Progression**: Merge tiles from **2** up to **2048**, **4096**, and beyond.
* **Energy Accumulation**: Build your mana pool to unleash board-clearing tactical powers.
* **High Score Chase**: Push your strategic limits with real-time score and best-record tracking.

### 3. 🏆 Hand-Crafted Challenge Stages
* **10+ Progressive Levels**: Each level introduces distinct clear conditions (target tile tier, energy goals, move limits, or clearing stubborn rocky obstacles).
* **Star & Mastery System**: Earn up to 3 stars per stage and unlock new challenge tiers as you progress.

---

## ⚡ Core Mechanics & Special Tiles

### 🧩 Tile Types
* **Standard Numbered Tiles**: Primary building blocks ($2, 4, 8, 16 \dots 2048+$).
* **⚡ Energy Crystals**: Merging these harvests Arcane Energy used to cast active spells.
* **⚒️ Forge Anvils**: Supercharges adjacent merges and boosts scoring.
* **🔥 Forge Flames**: High-temperature catalyst tiles that trigger bonus multipliers.
* **🪨 Obstacles**: Immovable stones found in challenge stages; clear them using *Shatter* or strategic adjacent merges.
* **✨ Legendary Relics & Artifacts**:
  * **Aegis Shield**: Automatically saves you from game over when the board has no valid moves.
  * **Ignis Ember**: Incinerates all low-value (2 & 4) clutter tiles across the board.
  * **Midas Ring**: Doubles all points earned for the next 5 moves.
  * **Chronos Relic**: Grants bonus moves in challenge stages.

### 🔮 Arcane Power Abilities
When energy builds up, tap any power ability in the active control bar to modify the board:
* **💥 Shatter (20⚡)**: Select and destroy any single tile or obstacle immediately.
* **✨ Duplicate (40⚡)**: Duplicate a high-value tile to set up instant chain merges.
* **🪄 Transmute (50⚡)**: Upgrade any target tile to its next power-of-two level.
* **🔥 Ignis Burn (FREE / Cooldown)**: Clear low-tier clutter to free up breathing room on a crowded board.

### ❤️ Health & Rescue System
* Start each run with **3 Rescue Hearts**.
* When no valid moves remain, a heart is consumed to automatically shuffle/clear lower-tier clutter and restore game flow, preventing frustrating sudden losses.

---

## 🎨 Themes & Customization

Tile Forge features high-contrast, eye-friendly themes tailored for any device display:
* 🌋 **Classic Obsidian**: Volcanic dark slate with warm ember glows.
* 🌌 **Cyber Rune Neon**: Glowing grid illuminated with vibrant cyan and electric magenta.
* ✨ **Celestial Astral**: Deep cosmic violet canvas with golden starlight accents.
* 🌿 **Emerald Sanctuary**: Soothing forest greens and jade crystal highlights.
* ☀️ **Solar Flare**: Radiant amber, orange, and gold palette.

---

## 🛠️ Key Features

- **Adaptive & Responsive Layout**: Fluidly scales across all screen sizes—compact phones, foldables, and landscape tablets—with dedicated two-pane layouts and zero content clipping.
- **Offline-First & Local Room Database**: High scores, statistics, level completions, and trophies are stored safely on-device with SQLite/Room.
- **💾 .ZIP Save Backup & Restore**: Export and import your entire player profile and progress as a lightweight `.zip` archive without requiring external servers.
- **Synthesized Audio Engine**: Built-in sound synthesis for tile slides, mergers, power casting, and fantasy quote chimes with zero bulky media dependencies.
- **Fullscreen Immersive Mode**: Seamlessly toggle system UI bars for distraction-free tactile play.
- **Inspirational Lore Cards**: Features refreshing fantasy proverbs and artisan quotes with every session.

---

## 🏗️ Architecture & Tech Stack

* **Language**: Kotlin 2.0+
* **UI Framework**: Jetpack Compose (Material Design 3)
* **Architecture**: MVVM + Clean Architecture with Kotlin Coroutines & StateFlow
* **Database**: Room Persistence Library with KSP
* **Navigation**: Jetpack Navigation Compose with type-safe state routing
* **Testing**: Local JVM Robolectric testing & Roborazzi screenshot verification suite

---

## 🚀 Building and Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/<your-username>/<repo-name>.git
   cd <repo-name>
   ```

2. **Open in Android Studio**:
   * Open the project folder in **Android Studio Ladybug (or newer)**.
   * Allow Gradle to sync dependencies.

3. **Build & Run**:
   * Select your target device or emulator (Android 7.0+ / API 24+).
   * Run the `:app` configuration (`Shift + F10` or click **Run**).

4. **Run Tests**:
   ```bash
   ./gradlew :app:testDebugUnitTest
   ```

---

<p align="center">
  Crafted with passion for puzzle enthusiasts everywhere. ⚔️🛡️
</p>
