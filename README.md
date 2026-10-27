# ZAVRANGO BOOST — Native Android System Optimization App

[![Build & Release Debug APK](https://github.com/zavrango889-oss/Zavrangovip/actions/workflows/build-apk.yml/badge.svg)](https://github.com/zavrango889-oss/Zavrangovip/actions/workflows/build-apk.yml)
[![Latest Release](https://img.shields.io/github/v/release/zavrango889-oss/Zavrangovip?label=Download%20APK&color=FF1E44)](https://github.com/zavrango889-oss/Zavrangovip/releases)

Real native Android system optimization and gaming control center, designed specifically for **Samsung Galaxy M15 5G (6GB RAM, Android 16)** and modern Android devices.

---

## ⚡ Direct Download & Automated GitHub Releases

Every push to this repository triggers the **GitHub Actions CI/CD workflow** (`.github/workflows/build-apk.yml`) to compile and publish the debug APK automatically.

### 📥 Direct Download Links

* 📲 **[Download Latest APK from Releases](https://github.com/zavrango889-oss/Zavrangovip/releases)**
  * Navigate to the latest release and click **`ZAVRANGO-BOOST-debug.apk`** under **Assets**.
* ⚙️ **[View Build Status in GitHub Actions](https://github.com/zavrango889-oss/Zavrangovip/actions)**
  * View build progress or download previous build artifacts under **Artifacts** (`zavrango-boost-debug-apk`).

---

## 🚀 How to Push from AI Studio to GitHub

If you are pushing this project from your machine or terminal to **`zavrango889-oss/Zavrangovip`**:

```bash
# 1. Add all project files
git add .

# 2. Commit the initial release
git commit -m "feat: ZAVRANGO BOOST with GitHub Actions automated APK workflow"

# 3. Ensure branch is main
git branch -M main

# 4. Push to your GitHub repository
git push -u origin main
```

*(If pushing with a GitHub Personal Access Token or SSH key, enter your credentials when prompted).*

Once pushed, GitHub Actions will immediately build the APK and publish it to [GitHub Releases](https://github.com/zavrango889-oss/Zavrangovip/releases).

---

## 📲 How to Install on Samsung Galaxy M15 5G

1. Open **[Releases](https://github.com/zavrango889-oss/Zavrangovip/releases)** on your phone's browser (Chrome or Samsung Internet).
2. Download **`ZAVRANGO-BOOST-debug.apk`**.
3. Tap the downloaded file in the browser downloads bar or inside the **My Files** app (`Internal storage/Download`).
4. If Android displays: *"For your security, your phone is not allowed to install unknown apps from this source"*:
   - Tap **Settings**.
   - Turn **ON** the switch for **Allow from this source**.
5. Tap **Install**, then tap **Open**.

---

## 🎮 Features Included

* **Gamer Control Center:** Premium cyber dark background (`#07070A`) with neon-red accents and 5 active optimization switches (Performance Mode, Touch Optimization, Precision Mode, Protection System, FPS Booster).
* **Game Launcher (`ABRIR FREE FIRE`):** Direct launch shortcut for Free Fire MAX and Free Fire with automated gaming focus profile.
* **Smart Performance Center:** Live hardware telemetry using Android system APIs for 6GB RAM, internal flash storage, and battery temperature (°C).
* **FPS & Game Performance:** Real-time display render frame meter using Android `Choreographer` and Samsung GPUWatch / Perf Z guide.
* **Notification Assistant:** Gaming Do Not Disturb (DND) filter to block heads-up notifications while preserving emergency alerts.
* **Touch & Smoothness:** Interactive millisecond reaction-time reflex test, 5-second rapid tap challenge (CPS), and Samsung 90Hz / touch sensitivity checklist.
* **One-Tap SMART OPTIMIZE:** Diagnostic engine evaluating 6 device parameters with verified system shortcuts.

---

## 🛠 Local Build Instructions

To build the APK locally on your machine using Gradle:

```bash
# Make gradlew executable
chmod +x gradlew

# Build the debug APK
./gradlew assembleDebug
```

The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`
