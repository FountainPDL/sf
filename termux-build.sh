#!/data/data/com.termux/files/usr/bin/bash
# ============================================================
# Surf Fountain Browser — Termux Build Script
# Run this script inside Termux on your Android device.
# ============================================================

set -e

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
log()  { echo -e "${GREEN}[surf]${NC} $*"; }
warn() { echo -e "${YELLOW}[warn]${NC} $*"; }
fail() { echo -e "${RED}[fail]${NC} $*"; exit 1; }

# ── 1. Install dependencies ──────────────────────────────────
log "Installing required Termux packages..."
pkg update -y && pkg upgrade -y
pkg install -y git openjdk-17 wget

# ── 2. Accept Android SDK licenses (needed for sdkmanager) ──
log "Setting up Android SDK path..."
export ANDROID_HOME="$HOME/android-sdk"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"

if [ ! -d "$ANDROID_HOME/cmdline-tools" ]; then
  warn "Android SDK not found. Downloading command-line tools..."
  mkdir -p "$ANDROID_HOME/cmdline-tools"
  SDK_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
  wget -q --show-progress -O /tmp/sdk-tools.zip "$SDK_URL"
  unzip -q /tmp/sdk-tools.zip -d /tmp/cmdline-tools-tmp
  mv /tmp/cmdline-tools-tmp/cmdline-tools "$ANDROID_HOME/cmdline-tools/latest"
  rm /tmp/sdk-tools.zip
  log "SDK tools downloaded."
fi

log "Accepting SDK licenses..."
yes | sdkmanager --licenses > /dev/null 2>&1 || true
sdkmanager "platforms;android-36" "build-tools;36.0.0" > /dev/null 2>&1 || true

# ── 3. Get gradle-wrapper.jar ────────────────────────────────
JAR_PATH="gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$JAR_PATH" ]; then
  log "Downloading gradle-wrapper.jar..."
  mkdir -p gradle/wrapper
  wget -q --show-progress \
    "https://github.com/gradle/gradle/raw/v8.6.0/gradle/wrapper/gradle-wrapper.jar" \
    -O "$JAR_PATH" || \
  wget -q --show-progress \
    "https://raw.githubusercontent.com/gradle/gradle/v8.6.0/gradle/wrapper/gradle-wrapper.jar" \
    -O "$JAR_PATH"
fi

# ── 4. Fix gradlew permissions ───────────────────────────────
chmod +x gradlew
log "gradlew is now executable."

# ── 5. Build ─────────────────────────────────────────────────
log "Building debug APK..."
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
./gradlew assembleDebug --no-daemon --stacktrace

# ── 6. Done ──────────────────────────────────────────────────
APK=$(find app/build/outputs/apk/debug -name "*.apk" 2>/dev/null | head -1)
if [ -n "$APK" ]; then
  log "✅  Build successful!"
  log "APK location: $APK"
  log "To install directly: adb install $APK"
  log "Or copy to Downloads: cp '$APK' /sdcard/Download/"
else
  fail "Build may have failed — APK not found."
fi
