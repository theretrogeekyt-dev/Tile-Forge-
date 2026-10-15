# ⚒️ Tile Forge Changelog

All notable changes to the **Tile Forge** game will be documented in this file.

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
