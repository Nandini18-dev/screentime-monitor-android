# Screen Time Monitor

A personal Android app (like Digital Wellbeing) that tracks, entirely on your device:

- Total & per-app **screen time**
- Total & per-app **Wi-Fi data usage**
- How many times the device was **locked / unlocked**
- How many **notifications** you received

All data stays on the device (local database) — nothing is sent anywhere.

---

## Easiest way to run it: Android Studio

This is the recommended path if you just want to see the app running.

### 1. Install Android Studio

Download it from [developer.android.com/studio](https://developer.android.com/studio) and install it (this includes everything needed: JDK, Android SDK, emulator).

### 2. Open the project

- Launch Android Studio
- Choose **Open**, and select this folder (`Mobile Info`)
- Wait for it to finish "Gradle Sync" in the bottom status bar (first time may take a few minutes — it downloads dependencies)

### 3. Run it

- At the top toolbar, pick a device from the dropdown:
  - **A virtual device (emulator)**: click the dropdown → *Device Manager* → *Create device* if none exists → pick any modern phone (e.g. Pixel 6) → pick a system image (Android 10 / API 29 or newer) → Finish. Then select it and hit the green ▶️ **Run** button.
  - **Your own phone**: enable Developer Options and USB debugging on it (Settings → About phone → tap "Build number" 7 times → Settings → Developer options → enable USB debugging), plug it in via USB, allow the debugging prompt on the phone, select it from the device dropdown, hit ▶️ **Run**.
- The app installs and launches automatically.

### 4. First-run setup (one-time, required)

The app needs a few permissions that Android requires you to grant manually in Settings (this is normal for any screen-time app — Android doesn't allow a simple pop-up for these):

1. **Usage access** → tap "Grant" → find "Screen Time Monitor" in the list → toggle it on → go back to the app.
2. **Notification access** → tap "Grant" → find "Screen Time Monitor" → toggle it on → confirm → go back.
3. **Allow notifications** → tap "Grant" → allow the system permission dialog.
4. **Ignore battery optimizations** (recommended, not required) → tap "Grant" → allow, so monitoring doesn't get killed in the background.

Once the required ones are granted, tap **Continue**. The app starts monitoring immediately and takes you to the Dashboard.

---

## What you'll see

- **Dashboard**: today's totals (screen time, Wi-Fi usage, locks, unlocks, notifications) and your top apps by screen time — updates live.
- **Apps**: every app's screen time + Wi-Fi usage today; tap one for details.
- **History**: bar charts of the last 7 days.
- **Settings**: change how long data is kept, reset all data, or re-check permissions.

---

## Advanced: running from the command line (no Android Studio)

If you'd rather not install Android Studio, the app can be built and run with just command-line tools. This is more setup but works entirely from a terminal.

### 1. Install the tools

```bash
# Gradle + a JDK (Android tooling needs JDK 17)
brew install gradle
brew install --cask temurin@17

# Android SDK command-line tools
brew install --cask android-commandlinetools
```

### 2. Set up the Android SDK

```bash
export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
yes | sdkmanager --sdk_root="$ANDROID_HOME" --licenses
sdkmanager --sdk_root="$ANDROID_HOME" \
  "platform-tools" "platforms;android-34" "build-tools;34.0.0" \
  "emulator" "system-images;android-34;google_apis;arm64-v8a"
```

(Use `system-images;android-34;google_apis;x86_64` instead if you're on an Intel Mac.)

### 3. Point the project at the SDK

Create `local.properties` in the project root (already present in this project) with:

```
sdk.dir=/opt/homebrew/share/android-commandlinetools
```

### 4. Build the APK

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
gradle assembleDebug
```

The APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

### 5. Create and start an emulator

```bash
avdmanager create avd -n ScreenTimeTest \
  -k "system-images;android-34;google_apis;arm64-v8a" -d pixel_6

emulator -avd ScreenTimeTest
```

Wait for it to fully boot (a real window opens on your screen).

### 6. Install and launch

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.nandini.screentime/.ui.OnboardingActivity
```

Then follow the same **first-run setup** steps above (usage access, notification access, etc.) inside the emulator window.

---

## Project structure (for developers)

```
app/src/main/java/com/nandini/screentime/
  service/     foreground service, lock/unlock + notification listeners, boot receiver
  data/        Room database, DAOs, entities, repositories
  util/        UsageStatsManager/NetworkStatsManager helpers, permission checks, formatting
  viewmodel/   ViewModels backing each screen
  ui/          Activities, Fragments, adapters
  worker/      WorkManager job for daily data retention cleanup
app/src/main/res/  layouts, strings, icons
```

Min Android version supported: **Android 8.0 (API 26)**. Written in Java with the traditional Android View system (no Jetpack Compose).
