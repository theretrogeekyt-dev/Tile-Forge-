# ⚒️ Tile Forge Changelog

All notable changes to the **Tile Forge** game will be documented in this file.

---

## [v10052.0] - 2026-10-29

### 🚀 Release
- **Post-Publishing Version Bump**:
  - Incremented `versionCode` to `10052` and updated `versionName` to `"10052.0"`.
  - Prepared release build targets for subsequent Google Play production updates.

---

## [v10051.0] - 2026-10-29

### ✨ Added
- **Re-Added Quotes to Main Menu Screen**:
  - Re-integrated the "Words of the Forge" quote card into `MainMenuScreen`.
  - Displays inspirational quotes from fantasy and anime lore (Lord of the Rings, Harry Potter, My Hero Academia, etc.) along with speaker and source.
  - Added dedicated refresh button (`testTag("refresh_quote_btn")`) to shuffle and discover new quotes with a tap.
  - Added smooth vertical scrolling to ensure all quotes, action cards, and utilities fit comfortably on any screen size.

---

## [v10050.0] - 2026-10-29

### 🎮 Re-Added Home Screen
- **Re-Added Game Home Screen (Main Menu)**:
  - Re-enabled `MainMenuScreen` as the opening landing screen on app launch.
  - Reconnected game navigation: tapping **Play Forge**, **Challenge Stages**, or **Daily Quest** transitions seamlessly into the gameplay screen.
  - Restored header back button in `HeaderBar` to smoothly return from active gameplay back to the Home Screen.
  - Preserved quick-access utility controls (Trophies, Themes, Tutorial, Backup, and Credits) and top-bar audio/fullscreen toggles.

---

## [v10049.0] - 2026-10-29

### 🔄 Restored
- **Restored Original Direct Game Board Layout**:
  - Returned app startup screen directly to the original `GameScreen` board layout, eliminating the separate menu screen.
  - Placed the player immediately into the 4x4 sliding-tile puzzle on launch, matching the web companion and original game architecture.
  - Updated the header bar with the glowing Forge emblem to quickly access game modes and challenge levels without a separate menu screen.
  - Set default Android back navigation to dismiss active dialogs or minimize the app cleanly.

---

## [v10048.0] - 2026-10-29

### 🔄 Reverted & Streamlined
- **Reverted Heavy Menu Structure to Clean Classic Game Menu**:
  - Removed bloated multi-card layout, oversized quote card banner, and 2x2 utility widget grid that cluttered the screen and forced vertical scrolling.
  - Restored a classic, punchy arcade game start menu with a radiant glowing Forge emblem, high score pill, and bold primary action buttons (**PLAY FORGE**, **CHALLENGE STAGES**, and **DAILY QUEST**).
  - Streamlined utility navigation (Trophies, Themes, Tutorial, Backup, and Credits) into an elegant, non-intrusive bottom toolbar.
  - Optimized viewport density so all menu controls fit on any screen without scrolling or awkward multi-column splits.

---

## [v10047.0] - 2026-10-28

### 🚀 Release
- **Post-Publishing Version Bump**:
  - Incremented `versionCode` to `10047` and `versionName` to `"10047.0"`.
  - Prepared release manifest and build targets for subsequent Google Play production updates.

---

## [v10046.0] - 2026-10-28

### 🛠️ Fixed & Optimized
- **GitHub Actions Build Pipeline Fix**:
  - Upgraded workflow build environment from Gradle 8.11.1 to **Gradle 9.3.1**, resolving the AGP 9.1.1 version requirement (`Minimum supported Gradle version is 9.3.1`).
  - Upgraded build runner JDK from Temurin 17 to **Temurin 21** to align with modern AGP 9+ compilation standards.
  - Bundled verified Gradle 9.3.1 wrapper distribution (`gradle/wrapper/gradle-wrapper.properties` and `gradle-wrapper.jar`).
  - Added resilient dual-build execution logic with automatic fallback to ensure signed distributable APK output.
- **Codebase Cleanup & APK Optimization**:
  - Commented out unused network, serialization, and cloud dependencies (`Retrofit`, `Moshi`, `OkHttp`, and unused Firebase SDKs) in `app/build.gradle.kts`.
  - Significantly reduced build overhead, compilation memory footprint, and final APK bundle size.

---

## [v10045.0] - 2026-10-15
### 🤖 Added
- **GitHub Automated Actions Workflow (`.github/workflows/build-apk.yml`)**:
  - Automatically compiles, signs, and packages ready-to-distribute APKs on push, pull request, tag creation, or manual workflow dispatch.
  - Dynamically extracts `versionName`, `versionCode`, and `applicationId` directly from `app/build.gradle.kts`.
  - Generates an automated, formatted release changelog with git commit history, build timestamps, and SHA-256 checksums.
  - Automatically publishes GitHub Releases with attached signed APKs and checksums.
  - Uploads build artifacts to workflow runs with 30-day retention.
  - Supports zero-config signing fallback and custom production signing via GitHub Secrets (`KEYSTORE_BASE64`, `STORE_PASSWORD`, etc.).

---

## [v10044.0] - 2026-10-15
### 💥 Added & Polish
- **My Hero Academia (Boku no Hero Academia) Integration**:
  - Added iconic quotes from All Might, Izuku Midoriya (Deku), Katsuki Bakugo, Shoto Todoroki, Lemillion (Mirio Togata), Eraser Head (Aizawa), and Red Riot (Kirishima) to lore cards.
  - Added **"Plus Ultra (U.A. High)"** Theme with One For All emerald lightning green (`#00E676`), All Might gold (`#FFD700`), and crimson flame sparks (`#FF1744`).
  - Added Pro Hero Mastery Ranks in Score Tracker and Game Over screens: *Symbol of Peace • Plus Ultra*, *No. 1 Pro Hero*, *Class 1-A Hero Elite*, *Full Cowling Artisan*, and *U.A. Hero Trainee*.
  - Added MHA achievements: *"Go Beyond! PLUS ULTRA!"* and *"One For All: Full Cowling"*.
  - Added Stage 11 (*"U.A. Hero Exam"*) and Stage 12 (*"All Might's Legacy"*).

---

## [v10041.0] - 2026-10-15
### 📱 Optimized
- **Samsung Phone & Tablet Adaptive Screen Scaling**:
  - Resolved bottom button clipping on Samsung Galaxy devices by adding smooth `verticalScroll(rememberScrollState())` to `MainMenuScreen`.
  - Added dynamic compact mode scaling for hero emblem, title typography, and card padding.
  - Added two-pane tablet and foldable layouts for screens with width $\ge$ 600dp (Galaxy Tab, Galaxy Fold).
  - Added `20.dp` bottom safety clearance spacer above navigation bars and gesture pill indicators.

---

## [v10040.0] - 2026-10-15
### ⚡ Core & Troubleshooting
- **Build & Preview Reliability**:
  - Added `package.json` and Node dev server runtime handling for smooth platform compilation.
  - Built comprehensive web companion with live Web Audio API sound synthesis and direct Android APK download endpoint.
  - Fixed 502 warmup timeouts in preview environment.

---

## [v10039.0] - Initial Release
### 🎮 Launch Features
- 4x4 sliding tile puzzle mechanics with energy crystal harvesting and forge combinations.
- Arcane Powers: *Shatter*, *Duplicate*, *Transmute*, and *Ignis Burn*.
- 3 Rescue Hearts mistake recovery system.
- Offline SQLite Room database for local progress, stats, and achievements.
- Lightweight `.zip` profile backup and restore.
- Themes: Obsidian, Cyber Rune, Celestial Gold, Frost Spire.
