# ZAVRANGOFFZ - Gaming Hub & Sensitivity Suite for Free Fire MAX

ZAVRANGOFFZ is a native Android gaming companion engineered specifically for Samsung Galaxy M15 5G (6GB RAM, Android 16) and competitive Free Fire MAX players. It combines a military-cyber dark aesthetic with genuine system telemetry, a compliant 0–200 sensitivity generator, reflex/aim trainers, notification silencing tools, and local game profile persistence.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The app is built with 100% genuine Android APIs and adheres strictly to platform sandboxing policies:
> - **Sensitivity Generator:** Free Fire MAX sliders accept values between 0 and 200. All values generated, saved, and adjusted are strictly constrained within `0..200`.
> - **FPS Monitoring:** Real-time display refresh rate and Compose frame-rendering times are calculated via `Choreographer.postFrameCallback`. Transparent guidance and shortcuts are provided for Samsung Game Booster / Perf Z for in-game overlay telemetry.
> - **System Controls:** Android does not allow third-party unrooted apps to silently alter system-level touch drivers or kernel frequencies. The app implements direct system intents to Samsung Display (90Hz Motion Smoothness), Battery, Developer Options, and Do-Not-Disturb policies.

- **Confirmed Aesthetic**: Premium dark navy/black (`#080C14` / `#0D1321`) with neon-blue (`#00F0FF`) and high-impact neon-red (`#FF1E44`) accents, tactical HUD reticles, and cyber cards inspired by the reference designs.
- **Navigation Structure**: 5-tab modern bottom navigation bar (`Control`, `Sensitivity`, `Aim Trainer`, `Telemetry & Tools`, `Profiles & Settings`) ensuring fast access to all 10 features without clutter.
- **Local Persistence**: Offline Room database storing custom sensitivity presets, aim training high scores, and hardware preference snapshots.

---

## 1. Overview & Core Concept

- **What It Does**: Empowers Free Fire MAX players on the Samsung Galaxy M15 5G to optimize their gameplay setup: generating tailored 0–200 sensitivity configs, launching the game with instant memory refresh, practicing headshot reflexes on a moving-target trainer, monitoring real battery/RAM/storage telemetry, and managing gaming profiles.
- **Target Audience**: Mobile esports players and Free Fire MAX enthusiasts playing on Samsung One UI devices with 90Hz refresh rate displays.
- **Key Value**: Replaces sketchy, misleading "booster" mockups with a real, high-performance, beautiful native Android tool where every button works and genuine device telemetry is displayed.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Launch & Quick Boost**:
   - The user opens ZAVRANGOFFZ and sees live device telemetry (available RAM out of 6GB, battery health/temp, storage, and 90Hz display state).
   - Tapping "ABRIR FREE FIRE MAX" checks package availability (`com.dts.freefiremax` / `com.dts.freefireth`), triggers an explicit memory trim hint, checks network ping, and launches the game directly (or opens Google Play if not installed).

2. **Sensitivity Calibration (0–200)**:
   - User navigates to the **Sensitivity** tab.
   - Selects a playstyle preset (`One-Tap Headshot`, `Rush / Shotgun`, `Sniper Support`, `Balanced 90Hz`) or taps "GERAR SENSIBILIDADE" to compute calibrated values.
   - Sliders allow precise adjustment between `0` and `200` for General, Red Dot, 2x Scope, 4x Scope, Sniper Scope, and Free Look, plus recommended DPI and Fire Button size (%).
   - Tapping "Save Profile" stores the values locally; tapping "Copy Settings" copies a clean formatted text to clipboard.

3. **Aim & Reflex Training**:
   - In the **Aim Trainer** screen, players practice target acquisition with moving red targets.
   - Hitting the center red reticle triggers a critical "HEADSHOT" (+100 points, red damage feedback), while outer hits count as body shots (+25 points).
   - A dedicated **Touch Tester** measures tap reaction latency in milliseconds and calculates click speed (CPS).

4. **Gaming Assistance & System Tuning**:
   - Quick toggles to activate Zen Gaming Mode (checking Do Not Disturb policy access).
   - One-tap shortcuts directly launch Samsung One UI settings: Display (90Hz Motion Smoothness), Battery Saver, Storage manager, Developer Options (Force 4x MSAA, Pointer Location), and Samsung Game Booster.

5. **Local Profiles & Configuration**:
   - Save custom configurations with names and weapon tags.
   - Load previously saved configs with a single tap, instant preview, and delete/reset controls.

### Visual Identity & Theme Tokens

```kotlin
// Theme Colors
val DarkNavyBg        = Color(0xFF070B12) // Deep space navy background
val DarkNavySurface   = Color(0xFF0F172A) // Elevated card surface
val DarkNavyBorder    = Color(0xFF1E293B) // Card border strokes
val CyberBlueNeon     = Color(0xFF00F0FF) // Electric cyan accent
val CyberBlueGlow     = Color(0x3300F0FF) // Translucent glow for borders
val NeonRedBright     = Color(0xFFFF1E44) // Striking Free Fire red
val NeonRedGlow       = Color(0x33FF1E44) // Red glow highlight
val TextPrimary       = Color(0xFFF8FAFC) // Crisp white
val TextMuted         = Color(0xFF94A3B8) // Tactical slate gray
val SuccessGreen      = Color(0xFF10B981) // Online / Ready green
```

- **HUD Cyber Cards**: Subtle 1dp neon border gradients, rounded corners (16dp), dark semi-transparent backgrounds with tactile elevation.
- **Tactical Reticles**: Custom drawn Vector & Canvas crosshairs inspired by military headtracking HUDs.
- **Accessibility & Touch**: Enforced `minimumInteractiveComponentSize` (48dp) and clear status indicators.

---

## 3. Key Product Decisions & Trade-Offs

| Decision | Chosen Approach | Rationale & Trade-offs |
|---|---|---|
| **Free Fire Sensitivity Range** | Strict `0..200` for all 6 scope categories | Matches modern Free Fire MAX in-game settings. Older versions used 0–100, but current FF MAX extends to 200. |
| **FPS Measurement Strategy** | In-app `Choreographer` display cadence + hardware refresh rate display + Samsung Game Booster Perf Z direct guide | Android 14+ sandboxing prevents third-party apps from snooping on other app surfaces without root/adb. Honest telemetry prevents fake claims while providing real display metrics. |
| **Data Persistence** | Room Database (local SQLite) | 100% offline, zero latency, no account or API keys required, completely durable across app launches. |
| **Device Specifics** | Tailored for Samsung Galaxy M15 5G (Dimensity 6100+, 6GB RAM, 90Hz, One UI) | Tailors RAM gauges to 6GB, checks for 90Hz refresh rate capability, and provides direct intents for Samsung's One UI settings. |

---

## 4. Technical Architecture & Data Strategy

### System Layout & Flow

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MainActivity                              │
│                    Scaffold + Bottom Navigation Bar                    │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
    ┌───────────────────────────────┼───────────────────────────────┐
    ▼                               ▼                               ▼
┌──────────────────┐    ┌──────────────────────┐    ┌──────────────────┐
│   ControlHub     │    │ SensitivityGenerator │    │   Aim & Touch    │
│  - FF MAX Launch │    │  - Sliders (0-200)   │    │  - Moving Target │
│  - RAM/Bat/Temp  │    │  - Style Presets     │    │  - Touch Latency │
│  - Ping & Boost  │    │  - Copy & Save       │    │  - Reflex Test   │
└────────┬─────────┘    └──────────┬───────────┘    └─────────┬────────┘
         │                         │                          │
         │              ┌──────────┴──────────┐               │
         ▼              ▼                     ▼               ▼
┌──────────────────┐ ┌───────────────────────────────────────────────┐
│ Performance & DND│ │             Profiles & Settings               │
│ - Samsung Direct │ │ - Room Database (Local Game Profiles)         │
│   Settings Links │ │ - Permission status & Reset controls          │
│ - Game Booster   │ │ - Device Specs (Galaxy M15 5G, Android 16)    │
└──────────────────┘ └───────────────────────────────────────────────┘
                                    ▲
                                    │
                    ┌───────────────┴───────────────┐
                    │       BoosterViewModel        │
                    │  - SystemTelemetryRepository  │
                    │  - ProfileRepository (Room)   │
                    │  - FpsCalculator (Real Cadence)
                    └───────────────────────────────┘
```

### Data Model & Persistence (Room)

```kotlin
@Entity(tableName = "game_profiles")
data class GameProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileName: String,
    val playstyleTag: String, // e.g., "One-Tap Headshot", "Rush SMG"
    val general: Int,         // 0..200
    val redDot: Int,         // 0..200
    val scope2x: Int,         // 0..200
    val scope4x: Int,         // 0..200
    val scopeSniper: Int,     // 0..200
    val freeLook: Int,        // 0..200
    val dpi: Int,             // Recommended DPI (e.g. 400..960)
    val fireButtonSize: Int,  // Button % (10..100)
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

### Verification & Testing Plan

1. **Compilation Check**: Run `compile_applet` to ensure Kotlin 2.0+ and Jetpack Compose 1.7+ build cleanly with KSP Room generation.
2. **Telemetry Validation**: Test MemoryInfo (reporting available RAM out of 6GB), BatteryManager receiver (temperature in °C, level %, voltage), and Storage stats.
3. **Sensitivity Bounds**: Enforce that every slider and generated number is strictly within `0..200`.
4. **Interactive Tester Validation**: Verify that the Aim Trainer detects touches, calculates accuracy, increments score, and handles restarts without memory leaks.
5. **Intent Safety**: Ensure every system settings intent (Display, Battery, Storage, DND, Free Fire launch) is protected with `intent.resolveActivity()` or `try/catch ActivityNotFoundException`.
