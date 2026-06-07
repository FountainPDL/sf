# 🌊 Surf Fountain Browser

A production-ready Android browser app built with pure Java and Android Studio.

## Features
- Multi-tab browsing engine (WebView-based)
- Ad blocker & tracker blocker (Surf Shield)
- AI Assistant (on-device / API-based)
- Password Manager with biometric lock
- Crypto Wallet
- Built-in VPN support
- Download manager
- Reader Mode with TTS
- QR code scanner & generator
- Dev Tools (console, inspector)
- Bookmarks, History, Speed Dial
- Dark/Light theme
- Home & Search widgets

---

## Quick Start

### Option A — Android Studio (Recommended)
1. Download the project ZIP / clone the repo
2. Open **Android Studio → Open** → select `surf-fountain-android/`
3. Let Gradle sync (internet required first time)
4. Hit **Run ▶** or **Build → Build APK**

### Option B — Termux (build on-device)
```bash
# 1. Clone the repo in Termux
pkg install git -y
git clone https://github.com/YOUR_USERNAME/surf-fountain-browser.git
cd surf-fountain-browser

# 2. Run the build script
bash termux-build.sh
```

### Option C — GitHub Actions (CI/CD)
Push to GitHub → Actions tab → APK is built & attached automatically.

---

## Push to GitHub
```bash
cd surf-fountain-android/
bash github-push.sh YOUR_GITHUB_USERNAME surf-fountain-browser
```

---

## Fix `Permission denied` on gradlew
This is the most common issue. Always run this first:
```bash
chmod +x gradlew
./gradlew assembleDebug
```

---

## Project Structure
```
surf-fountain-android/
├── app/
│   ├── src/main/
│   │   ├── java/com/surffountain/browser/
│   │   │   ├── activities/       # All screen activities
│   │   │   ├── adapters/         # RecyclerView adapters
│   │   │   ├── database/         # Room entities, DAOs, AppDatabase
│   │   │   ├── fragments/        # Home, AddressBar, Tabs fragments
│   │   │   ├── models/           # Plain Java models
│   │   │   ├── privacy/          # AdBlocker, SurfShield
│   │   │   ├── security/         # PasswordManager, BiometricHelper
│   │   │   ├── services/         # VPN, Download, Media, Sync services
│   │   │   ├── settings/         # SettingsManager
│   │   │   ├── utils/            # URL, Theme, Reader, Crash utils
│   │   │   ├── viewmodels/       # All ViewModels
│   │   │   ├── webview/          # SurfWebView, TabManager
│   │   │   ├── widgets/          # Search & Bookmark widgets
│   │   │   └── workers/          # WorkManager workers
│   │   └── res/
│   │       ├── drawable/         # All vector icons & backgrounds
│   │       ├── layout/           # All XML layouts
│   │       ├── values/           # strings, colors, themes, dimens
│   │       └── xml/              # Prefs, network config, etc.
│   └── build.gradle
├── gradle/wrapper/
│   └── gradle-wrapper.properties
├── gradle.properties
├── gradlew                       # Run: chmod +x gradlew first!
├── gradlew.bat                   # Windows
├── settings.gradle
├── build.gradle
├── termux-build.sh               # Build on-device in Termux
├── github-push.sh                # Push to GitHub helper
└── .github/workflows/build-apk.yml
```

---

## Tech Stack
| Layer | Library |
|---|---|
| Language | Java 17 |
| Min SDK | 26 (Android 8) |
| Target SDK | 36 |
| UI | Material Design 3 |
| Database | Room + Drizzle |
| HTTP | OkHttp + Retrofit |
| Images | Glide |
| Biometrics | AndroidX Biometric |
| Crypto | Security-crypto |
| Background | WorkManager |
| QR | ZXing |

---

## Signing a Release APK
Set these GitHub Secrets for automatic signed builds:
- `KEYSTORE_BASE64` — base64 of your `.jks` keystore
- `KEY_ALIAS` — key alias
- `KEY_PASSWORD` — key password
- `STORE_PASSWORD` — keystore password

Generate a keystore:
```bash
keytool -genkey -v -keystore surf-fountain.jks \
  -alias surf-fountain -keyalg RSA -keysize 2048 -validity 10000
base64 surf-fountain.jks | pbcopy   # macOS — paste into GitHub secret
```
