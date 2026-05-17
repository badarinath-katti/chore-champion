# Chore Champion - Setup Instructions

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

### Required Software

1. **Android Studio** (Hedgehog 2023.1.1 or later)
   - Download: https://developer.android.com/studio
   - Includes Android SDK, SDK Manager, and AVD Manager

2. **Java Development Kit (JDK) 17**
   - Bundled with Android Studio, or
   - Download: https://adoptium.net/
   - Verify: `java -version` (should show version 17.x.x)

3. **Git** (for cloning the repository)
   - macOS: Pre-installed or via Homebrew (`brew install git`)
   - Windows: https://git-scm.com/download/win
   - Verify: `git --version`

### System Requirements

- **macOS**: 10.14 (Mojave) or later
- **Windows**: Windows 10 64-bit or later
- **Linux**: Any 64-bit distribution (Ubuntu, Fedora, etc.)
- **RAM**: Minimum 8GB, 16GB recommended
- **Disk Space**: At least 8GB free for Android Studio + SDK + emulator

---

## 🚀 Quick Start (5 Minutes)

### Step 1: Clone the Repository

```bash
# Navigate to your projects directory
cd ~/Desktop/CurrentWork/Apps

# Clone the repository (or use your path)
cd ChoreChampion
```

### Step 2: Firebase Configuration (REQUIRED)

**Firebase is required for authentication.** Follow these steps:

#### A. Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" or use existing project
3. Name: `Chore Champion` (or your choice)
4. Disable Google Analytics (optional for development)
5. Click "Create project"

#### B. Add Android App

1. In Firebase Console, click "Add app" → Select Android icon
2. Enter package name: **`com.chorechampion.app`** (must match exactly)
3. App nickname: `Chore Champion` (optional)
4. Debug signing certificate SHA-1: (optional for now)
5. Click "Register app"

#### C. Download Configuration File

1. Download `google-services.json` file
2. Place it in: `ChoreChampion/app/google-services.json`
3. **IMPORTANT**: The file must be exactly in the `app/` folder

#### D. Enable Email/Password Authentication

1. In Firebase Console, go to "Authentication" → "Sign-in method"
2. Click "Email/Password"
3. Enable "Email/Password" (first toggle)
4. Click "Save"

### Step 3: Configure Local Environment

#### Update SDK Path (if needed)

The `local.properties` file should already exist with:
```properties
sdk.dir=/Users/D065085/Library/Android/sdk
```

**Update this path** if your SDK is in a different location:

**macOS/Linux:**
```bash
# Find your SDK location
echo $ANDROID_HOME
# or check: ~/Library/Android/sdk
```

**Windows:**
```bash
# Usually: C:\Users\YOUR_USERNAME\AppData\Local\Android\Sdk
```

Edit `local.properties`:
```properties
sdk.dir=/path/to/your/Android/sdk
```

### Step 4: Open in Android Studio

1. Launch Android Studio
2. Click "Open" (not "New Project")
3. Navigate to `ChoreChampion` folder and select it
4. Click "Open"
5. Wait for Gradle sync to complete (this may take 2-5 minutes first time)

**If prompted:**
- "Trust Project": Click "Trust Project"
- "Gradle Sync": Let it complete automatically
- Download any missing SDK components if prompted

### Step 5: Run the Application

#### Option A: Using Android Studio (Recommended)

1. **Setup Emulator** (first time only):
   - Click "Device Manager" in top right
   - Click "Create Device"
   - Select "Pixel 6" or similar (API 34 recommended)
   - Click "Next" → Download system image if needed
   - Click "Finish"

2. **Run the App**:
   - Click green "Run" button (▶️) in toolbar
   - Or: Run → Run 'app'
   - Or: Keyboard shortcut: `Ctrl+R` (Mac) / `Shift+F10` (Windows)
   - Select your emulator/device from dropdown
   - Wait for build and installation (~30-60 seconds first time)

#### Option B: Using Physical Device

1. **Enable Developer Options** on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - Go back to Settings → Developer Options
   - Enable "USB Debugging"

2. **Connect Device**:
   - Connect via USB cable
   - Accept "Allow USB debugging" popup on device
   - Device should appear in Android Studio's device dropdown

3. **Run**: Click green "Run" button and select your device

#### Option C: Using Command Line

```bash
# Make gradlew executable (Unix-based systems)
chmod +x gradlew

# Clean build (optional)
./gradlew clean

# Build the app
./gradlew build

# Install on connected device/emulator
./gradlew installDebug

# Or build and install in one command
./gradlew installDebug

# Run tests (optional)
./gradlew test
```

**Windows:**
```cmd
gradlew.bat installDebug
```

---

## ✅ Testing the Application

Once the app launches, test the complete flow:

### 1. Welcome Screen
- You should see "Welcome to Chore Champion" 
- Two buttons: "Sign In" and "Sign Up"

### 2. Create Account
- Tap "Sign Up"
- Enter:
  - Name: `Test User`
  - Email: `test@example.com`
  - Password: `password123`
- Tap "Sign Up"
- Should auto-navigate to Home screen

### 3. Create Chores
- Tap "Manage Your Chores" or bottom nav "Chores"
- Tap + button (FAB)
- Create a chore:
  - Title: `Wash Dishes`
  - Description: `Clean all dishes and counters`
  - Category: `Cleaning`
  - Weightage: `30`
- Tap "Save"
- Repeat to create 3-5 chores

### 4. Assign Weekly Tasks
- Go back to Home → "This Week's Assignments"
- Tap + button
- Select a chore from dropdown
- Set weightage (or keep default)
- Tap "Assign"
- Repeat for 2-3 chores

### 5. Complete a Chore
- In "My Tasks" tab, tap "Mark as Complete" on an assignment
- Adjust completion percentage (try 90%)
- (Optional) Tap "Take Photo" → Grant camera permission → Take photo
- Add notes: `Completed thoroughly`
- Tap "Mark as Complete"
- Points should be calculated and displayed

### 6. View Results
- Go to Home → "History" button
- View past evaluations (if any)
- Check points are calculated correctly

---

## 🔧 Troubleshooting

### "google-services.json is missing"

**Error Message:** `File google-services.json is missing`

**Solution:**
1. Verify file location: `ChoreChampion/app/google-services.json`
2. Check filename is exactly `google-services.json` (no spaces, correct extension)
3. Re-download from Firebase Console if corrupted
4. After placing file, sync Gradle: File → Sync Project with Gradle Files

### "SDK location not found"

**Error Message:** `SDK location not found. Define location with sdk.dir in local.properties`

**Solution:**
1. Create or edit `local.properties` in project root
2. Add SDK path:
   - **macOS**: `sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk`
   - **Windows**: `sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk`
   - **Linux**: `sdk.dir=/home/YOUR_USERNAME/Android/Sdk`
3. Use `\\` for Windows paths (escape backslashes)
4. Restart Android Studio

### Build Fails with Gradle Error

**Error Message:** Various Gradle errors

**Solution:**
```bash
# Clean project
./gradlew clean

# Invalidate caches in Android Studio
File → Invalidate Caches → Invalidate and Restart

# Or delete build folders manually
rm -rf .gradle build app/build

# Then rebuild
./gradlew build
```

### App Crashes on Launch

**Possible Causes:**
1. Firebase not configured correctly
2. Missing google-services.json
3. Wrong package name in Firebase

**Solution:**
1. Check Logcat in Android Studio (View → Tool Windows → Logcat)
2. Look for error messages (usually red text)
3. Common fixes:
   - Ensure Firebase Authentication is enabled
   - Verify package name matches: `com.chorechampion.app`
   - Re-download google-services.json
   - Clean and rebuild project

### Camera Not Working

**Issue:** Camera won't open or crashes

**Solution:**
1. Check permissions in AndroidManifest.xml (already included)
2. Grant camera permission when prompted
3. On emulator: Ensure emulator has camera enabled (Virtual Device Settings)
4. On device: Go to Settings → Apps → Chore Champion → Permissions → Enable Camera

### "Duplicate class" Error

**Error Message:** `Duplicate class found in modules...`

**Solution:**
1. This usually means version conflicts
2. Check `app/build.gradle.kts` for duplicate dependencies
3. Clean project: `./gradlew clean`
4. Sync Gradle files

### Emulator is Slow

**Issue:** Emulator runs slowly or freezes

**Solution:**
1. Allocate more RAM to emulator:
   - Device Manager → Edit device → Show Advanced Settings
   - Increase RAM to 2048 MB or higher
2. Enable Hardware Acceleration:
   - macOS/Linux: Install HAXM
   - Windows: Enable Hyper-V
3. Use a device with lower resolution (e.g., Pixel 4 instead of Pixel 6)
4. Close other applications to free up system resources

### "Unable to connect to Firebase"

**Issue:** Authentication not working

**Solution:**
1. Check internet connection
2. Verify Firebase project is active (not deleted)
3. Ensure google-services.json is from correct project
4. Check Firebase Console for any service outages
5. Try re-enabling Email/Password authentication

---

## 📱 Emulator vs Physical Device

### Using Emulator (Recommended for Development)

**Pros:**
- Easy to reset and test fresh installs
- Can simulate various devices and Android versions
- No need for physical hardware
- Easy screenshot and screen recording

**Cons:**
- Slower than physical device
- Camera simulation may be limited
- Requires significant system resources

**Recommended Setup:**
- Device: Pixel 6 or Pixel 6 Pro
- API Level: 34 (Android 14)
- RAM: 2048 MB
- Internal Storage: 4096 MB

### Using Physical Device

**Pros:**
- Faster performance
- Real camera and sensors
- True user experience testing
- Better for demo purposes

**Cons:**
- Requires USB cable (or wireless debugging)
- Developer options needed
- Device-specific issues possible

**Best For:**
- Final testing before release
- Camera feature testing
- Performance validation
- Demo to stakeholders

---

## 🔐 Development Configuration

### Debug vs Release Builds

**Debug Build** (default):
- Used during development
- Includes debugging symbols
- Larger APK size
- Can attach debugger

**Release Build** (for production):
```bash
# Build release APK
./gradlew assembleRelease

# Build signed release bundle
./gradlew bundleRelease
```

### Environment Variables (Optional)

You can create different Firebase projects for dev/staging/prod:

1. Create `app/src/debug/google-services.json` (dev)
2. Create `app/src/release/google-services.json` (prod)
3. Gradle will automatically use correct config per build type

---

## 🧪 Running Tests

### Unit Tests

```bash
# Run all unit tests
./gradlew test

# Run tests for specific module
./gradlew app:testDebugUnitTest

# Generate test coverage report
./gradlew jacocoTestReport
```

### Instrumented Tests (requires emulator/device)

```bash
# Run all instrumented tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.chorechampion.app.ExampleTest
```

---

## 📊 Performance Monitoring

### Logcat Filtering

In Android Studio Logcat:
```
# Filter by app package
package:com.chorechampion.app

# Filter by tag
tag:ChoreChampion

# Filter by log level
level:ERROR
```

### Profiling

1. Run → Profile 'app'
2. Select profiling type:
   - CPU profiler
   - Memory profiler
   - Network profiler
   - Energy profiler

---

## 🌐 Network Configuration

### Local API Testing (if applicable)

If you add a backend API later:

1. Update `local.properties`:
```properties
api.base.url=http://10.0.2.2:8080/api
# 10.0.2.2 is emulator's localhost
```

2. For physical device, use your computer's IP:
```properties
api.base.url=http://192.168.1.100:8080/api
```

---

## 📝 Project Structure Reference

```
ChoreChampion/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/chorechampion/app/
│   │   │   │   ├── data/          # Repository implementations, DAOs
│   │   │   │   ├── domain/        # Use cases, models
│   │   │   │   ├── presentation/  # ViewModels, Compose UI
│   │   │   │   ├── di/           # Hilt modules
│   │   │   │   ├── util/         # Utilities, helpers
│   │   │   │   └── worker/       # WorkManager workers
│   │   │   ├── res/              # Resources (strings, colors, icons)
│   │   │   └── AndroidManifest.xml
│   │   └── test/                 # Unit tests
│   ├── build.gradle.kts          # App-level Gradle config
│   └── google-services.json      # Firebase config (REQUIRED)
├── gradle/
│   └── libs.versions.toml        # Dependency versions
├── build.gradle.kts              # Project-level Gradle config
├── settings.gradle.kts           # Project settings
├── local.properties              # SDK path (local only)
└── README.md                     # Project documentation
```

---

## 🚀 Next Steps After Setup

Once the app is running successfully:

1. **Explore Features**: Test all screens and functionality
2. **Check Documentation**:
   - `README.md` - Project overview
   - `README_ARCHITECTURE.md` - Architecture details
   - `STATUS.md` - Current implementation status
   - `COMPLETION_FLOW.md` - Completion feature docs
   - `EVALUATION_SYSTEM.md` - Evaluation system docs
3. **Customize**: Modify UI, add features, adjust scoring
4. **Deploy**: Follow Android deployment guide for production

---

## 💡 Tips for Development

### Hot Reload
- Compose Preview: See UI changes without running app
- Live Edit: Minor changes apply without rebuild (Android Studio Flamingo+)

### Debugging
- Add breakpoints: Click line number gutter
- Step through code: Debug → Step Over/Into/Out
- Inspect variables: Variables panel during debug

### Keyboard Shortcuts
- **Run app**: `Ctrl+R` (Mac) / `Shift+F10` (Win)
- **Debug app**: `Ctrl+D` (Mac) / `Shift+F9` (Win)
- **Find in files**: `Cmd+Shift+F` (Mac) / `Ctrl+Shift+F` (Win)
- **Refactor/Rename**: `Shift+F6`

### Best Practices
- Sync Gradle after changing dependencies
- Clean build if seeing weird errors
- Check Logcat for runtime errors
- Use emulator snapshots for faster startup

---

## 🆘 Getting Help

### Resources
- **Android Docs**: https://developer.android.com
- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Firebase Docs**: https://firebase.google.com/docs
- **Kotlin Docs**: https://kotlinlang.org/docs

### Common Commands Reference

```bash
# Build
./gradlew build                    # Build project
./gradlew clean build              # Clean then build
./gradlew assembleDebug            # Build debug APK

# Install
./gradlew installDebug             # Install debug version
./gradlew uninstallDebug           # Uninstall debug version

# Test
./gradlew test                     # Run unit tests
./gradlew connectedAndroidTest     # Run instrumented tests

# Check
./gradlew lint                     # Run lint checks
./gradlew dependencies             # Show dependency tree

# Info
./gradlew tasks                    # List all available tasks
./gradlew properties               # Show project properties
```

---

## ✅ Quick Verification Checklist

Before considering setup complete:

- [ ] Android Studio opens project without errors
- [ ] Gradle sync completes successfully
- [ ] `google-services.json` is in `app/` folder
- [ ] Firebase Authentication is enabled
- [ ] App builds without errors (`./gradlew build`)
- [ ] App installs on emulator/device
- [ ] Welcome screen displays correctly
- [ ] Can sign up a new user
- [ ] Can sign in with created user
- [ ] Home screen loads with data
- [ ] Can create chores
- [ ] Can assign weekly tasks
- [ ] Can complete tasks with photo
- [ ] Points are calculated correctly

**If all items checked ✅ → You're ready to develop!**

---

**Need help?** Check the troubleshooting section above or review the error in Logcat.

**Need help?** Check the troubleshooting section above or review the error in Logcat.
