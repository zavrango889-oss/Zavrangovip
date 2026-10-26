# ZAVRANGO BOOST — Native Android System Optimization App

Real native Android system optimization and gaming control center, designed specifically for **Samsung Galaxy M15 5G (6GB RAM, Android 16)** and modern Android devices.

---

## ⚡ Automated APK Builds via GitHub Actions

This repository is pre-configured with a **GitHub Actions CI/CD workflow** (`.github/workflows/build-apk.yml`) that builds the debug APK automatically.

### 📥 How to Download the APK

#### Option 1: GitHub Releases (Recommended — Direct Download)
1. Go to the **Releases** section on the right side of your GitHub repository (or navigate to `https://github.com/<your-username>/<your-repo>/releases`).
2. Under the latest release (e.g., `v1.0.1`), expand **Assets**.
3. Click on **`ZAVRANGO-BOOST-debug.apk`** to download it directly to your phone or computer.

#### Option 2: GitHub Actions Artifacts
1. Go to the **Actions** tab in your repository.
2. Click on the latest workflow run: **"Build & Release Debug APK"**.
3. Scroll down to the **Artifacts** section at the bottom of the summary page.
4. Click on **`zavrango-boost-debug-apk`** to download the ZIP file containing the APK.

#### Option 3: Trigger a Manual Build Anytime
1. Go to the **Actions** tab in your GitHub repository.
2. Select **"Build & Release Debug APK"** from the left sidebar.
3. Click the **"Run workflow"** button.
4. Choose whether to publish a GitHub Release and enter an optional custom tag (e.g., `v1.0.0`), then click **"Run workflow"**.

---

## 📲 How to Install on Samsung Galaxy M15 5G

1. Download **`ZAVRANGO-BOOST-debug.apk`** directly on your phone using Chrome/Samsung Internet, or transfer it from your PC via USB / Quick Share.
2. Tap the downloaded `.apk` file from **My Files** or the browser notification bar.
3. If prompted: **"For your security, your phone is not allowed to install unknown apps from this source"**:
   - Tap **Settings**.
   - Enable the toggle for **Allow from this source**.
4. Tap **Install**, then tap **Open**.

---

## 🎮 Key Features

* **Gamer Control Center:** Premium cyber dark background with neon-red accents and 5 active optimization switches (Performance Mode, Touch Optimization, Precision Mode, Protection System, FPS Booster).
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
