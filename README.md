# Chore Champion - Android App

A gamified chores management app for couples that tracks weekly household tasks, awards points based on completion, and determines a weekly winner.

## Features

- 🎯 **Gamified Chore Tracking**: Turn household tasks into a fun competition
- 📊 **Points-Based Scoring**: Earn points based on completion percentage and chore weightage
- 📸 **Photo Proof**: Attach photos to completed chores
- 🏆 **Weekly Evaluation**: Automatic weekly winner determination
- 📈 **History & Statistics**: Track performance over time
- 🎁 **Rewards System**: Claim rewards for accumulated points
- 🔔 **Notifications**: Daily reminders and weekly evaluation alerts
- 📱 **Modern UI**: Built with Jetpack Compose and Material 3

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **Database**: Room (SQLite)
- **Authentication**: Firebase Authentication
- **Dependency Injection**: Hilt
- **Asynchronous**: Kotlin Coroutines + Flow
- **Background Tasks**: WorkManager
- **Image Loading**: Coil
- **Camera**: CameraX

## Project Structure

```
app/src/main/java/com/chorechampion/app/
├── ChoreChampionApplication.kt
├── di/                          # Hilt dependency injection modules
├── data/
│   ├── local/                   # Room database
│   │   ├── dao/                 # Data Access Objects
│   │   ├── entity/              # Room entities
│   │   └── ChoreDatabase.kt
│   └── repository/              # Repository implementations
├── domain/
│   ├── model/                   # Domain models
│   ├── repository/              # Repository interfaces
│   └── usecase/                 # Business logic use cases
├── presentation/
│   ├── auth/                    # Authentication screens
│   ├── home/                    # Home/Dashboard screen
│   ├── navigation/              # Navigation setup
│   └── theme/                   # Compose theme
└── util/                        # Utilities & helpers
```

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK (API 26+)
- Firebase account

### Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing one
3. Add an Android app with package name: `com.chorechampion.app`
4. Download the `google-services.json` file
5. Place it in `app/` directory (replace the template file)
6. Enable **Email/Password** authentication in Firebase Console:
   - Go to Authentication → Sign-in method
   - Enable Email/Password provider

### Local Setup

1. Clone the repository
2. Copy `local.properties.template` to `local.properties`
3. Update SDK path in `local.properties`:
   ```properties
   sdk.dir=/path/to/your/Android/sdk
   ```
4. Copy `app/google-services.json.template` to `app/google-services.json`
5. Replace template values with your Firebase configuration
6. Open project in Android Studio
7. Sync Gradle files
8. Run the app on emulator or physical device (API 26+)

## Build & Run

### Debug Build

```bash
./gradlew assembleDebug
```

### Release Build

```bash
./gradlew assembleRelease
```

### Install on Device

```bash
./gradlew installDebug
```

## Architecture

### MVVM + Clean Architecture

The app follows Clean Architecture principles with three main layers:

1. **Presentation Layer**: Jetpack Compose UI + ViewModels
2. **Domain Layer**: Use cases and business logic
3. **Data Layer**: Repository pattern with Room DB + Firebase Auth

### Database Schema

- **User**: User accounts and preferences
- **ChoreCategory**: Chore categories (Cleaning, Cooking, etc.)
- **Chore**: Individual chore definitions
- **WeeklyAssignment**: Chores assigned for a specific week
- **ChoreCompletion**: Completion records with points
- **WeeklyEvaluation**: Weekly summary and winner
- **Reward**: Rewards that can be claimed

### Scoring Algorithm

```kotlin
// Base points from completion percentage (1-5 scale)
0-20%   = 1 point
21-40%  = 2 points
41-60%  = 3 points
61-80%  = 4 points
81-100% = 5 points

// Final points with weightage multiplier
finalPoints = basePoints * (weightage / 10)

// Example: 80% completion with weightage 30
// basePoints = 4, finalPoints = 4 * 3.0 = 12.0 points
```

## Current Implementation Status

### ✅ Completed

- [x] Project structure and Gradle configuration
- [x] Hilt dependency injection setup
- [x] Room database entities and DAOs
- [x] Repository pattern implementation
- [x] Domain models and use cases
- [x] Firebase Authentication integration
- [x] Jetpack Compose theme and navigation
- [x] Welcome screen and basic UI structure
- [x] Scoring calculation utilities

### 🚧 In Progress

- [ ] Complete authentication flow (Sign In/Sign Up screens)
- [ ] Home dashboard with real-time data
- [ ] Chore management screens
- [ ] Weekly assignment system
- [ ] Photo capture and completion flow
- [ ] Weekly evaluation automation
- [ ] Notifications implementation
- [ ] History and statistics screens
- [ ] Rewards system UI

### 📋 Planned

- [ ] Default chore templates
- [ ] Partner pairing flow
- [ ] Week start day configuration
- [ ] Image compression and storage management
- [ ] WorkManager for scheduled tasks
- [ ] Unit and integration tests
- [ ] UI/UX polish and animations

## Testing

### Run Unit Tests

```bash
./gradlew test
```

### Run Instrumentation Tests

```bash
./gradlew connectedAndroidTest
```

## Dependencies

Key dependencies and their versions:

- **Jetpack Compose**: 2024.02.00 BOM
- **Firebase**: 32.7.0 BOM
- **Room**: 2.6.1
- **Hilt**: 2.50
- **CameraX**: 1.3.1
- **Coil**: 2.5.0
- **WorkManager**: 2.9.0

See [app/build.gradle.kts](app/build.gradle.kts) for complete list.

## Contributing

This is a personal/educational project. Contributions, issues, and feature requests are welcome!

## License

[Add your license here]

## Support

For questions or issues:
- Create an issue in the repository
- Contact: [Your contact information]

---

**Built with ❤️ using Kotlin and Jetpack Compose**
