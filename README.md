# TimeLine - Minimalistic Task Tracker

<p align="center">
  <img src="app/src/main/res/drawable/icon_logo.png" alt="TimeLine logo" width="180" />
</p>

TimeLine is a modern, premium Android application designed to help you manage your tasks, deadlines, and events in a beautiful, chronological interface. Built with **Kotlin** and **Jetpack Compose**, it follows a strictly minimalistic philosophy and the latest Android development best practices.

## 🚀 TimeLine v1.0.1 - UI and Branding Improvements

TimeLine v1.0.1 improves the visual experience, branding, and usability while preserving the offline-first architecture and Google Drive synchronization.

### ✨ Key Features

- **Chronological Timeline**: Track tasks, exams, deadlines, meetings, and events in a visual vertical timeline.
- **Privacy-First Cloud Sync**: Back up data to your private Google Drive App Data folder.
- **Premium Dark Mode**: Use the custom dark theme for a comfortable, modern experience.
- **Intelligent Reminders**: Receive high-priority alarms and notifications for important tasks.
- **Real-time Search**: Filter tasks by title, description, or category.
- **Google Integration**: Sign in with Google to use profile details and cross-device syncing.

### 🎨 UI and Branding Improvements

- Updated the shared productivity palette with blue, slate, teal, and navy brand colors.
- Added a white splash screen with the full `logo_animation.gif` preserved at its original aspect ratio.
- Added the multicolor Google logo to the Google sign-in button.
- Standardized the branded `+` floating action button across Home, Calendar, Upcoming, and Completed.
- Improved button contrast, spacing, elevation, and accessibility labels.

### 🛠️ Technical Highlights

- Built with Jetpack Compose and Material 3.
- MVVM architecture with repository-based data access.
- Room as the local source of truth.
- WorkManager for background synchronization.
- Firebase Authentication for secure Google login.

### 📦 Installation

1. Download the `timeline-v1.0.1.apk` release artifact.
2. Enable installation from unknown sources if required by your Android device.
3. Install the APK and start planning your timeline.

When building from source, provide a valid `google-services.json` file in the `app/` directory.

See [TimeLine v1.0.0](https://github.com/aakashdeepyadav/timeline/releases/tag/v1.0.0) for the initial stable release.

## ✨ Features

- **Chronological Timeline**: View your life at a glance with a clean, vertical timeline.
- **Modern Productivity Design**: A focused Material 3 interface inspired by the supplied design system, with primary blue, slate secondary, teal accents, navy neutrals, and clean sans-serif typography.
- **Privacy-First (Google Drive Sync)**: Securely back up and sync your data across devices using your own Google Drive storage—zero server costs for the developer, maximum privacy and ownership for you.
- **Offline-First**: All data is stored locally in a Room database, ensuring the app works 100% offline.
- **Smart Filtering & Search**: Find exactly what you need with real-time search across all views and category-based filtering.
- **Reliable Reminders**: Set precise alarms for your most important tasks with high-priority notifications that persist even after device reboots.
- **Customizable Appearance**: Choose between Light, Dark, or System Default themes from the settings while retaining the shared brand palette.
- **Consistent Add Actions**: A clearly visible, branded `+` floating action button is available in the Home, Calendar, Upcoming, and Completed sections.
- **Animated Splash Screen**: The app opens with `logo_animation.gif` on a white background in both light and dark device themes.
- **Clean Slate**: No predefined data—start your productivity journey with a minimalistic, production-ready interface.

## 🛠️ Technical Stack

- **UI**: Jetpack Compose, Material 3 (Rounded Style)
- **Architecture**: MVVM (Model-View-ViewModel), Repository Pattern
- **Persistence**: Room Database, DataStore (Preferences)
- **Sync**: Google Drive REST API (App Data Folder), WorkManager
- **Auth**: Firebase Authentication (Google Sign-In)
- **Image Loading**: Coil
- **Concurrency**: Kotlin Coroutines & Flow

## 🎨 Design System

The interface uses a shared productivity-focused palette:

- **Primary**: Blue `#2B50ED`
- **Secondary**: Slate `#475569`
- **Tertiary**: Teal `#0D9488`
- **Neutral**: Navy `#111827`

The add-task floating action button uses the primary blue with a high-contrast white add icon and consistent rounded elevation across task sections.

## 🚀 Getting Started

### Prerequisites

- Android Studio Koala or newer
- A Firebase project (for Authentication)
- Google Drive API enabled in the Google Cloud Console

### Setup

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/TimeLine.git
    ```
2.  **Add Firebase**:
    - Create a project in the [Firebase Console](https://console.firebase.google.com/).
    - Add an Android app with the package name `com.example.timeline`.
    - Download `google-services.json` and place it in the `app/` directory.
    - Enable **Google Sign-In** in the Authentication section.
3.  **Enable Google Drive API**:
    - Go to the [Google Cloud Console](https://console.cloud.google.com/).
    - Select your project and enable the **Google Drive API**.
    - Add the `https://www.googleapis.com/auth/drive.appdata` scope to your OAuth consent screen.
4.  **Add SHA-1 Fingerprint**:
    - Run `./gradlew signingReport` and add your SHA-1 to the Firebase project settings.
5.  **Build and Run**: Open the project in Android Studio and hit **Run**.

## 🛡️ Privacy

TimeLine is built with privacy as a core value. Your task data never touches our servers. When you choose to sync, your data is stored in a hidden, private folder on **your own Google Drive** (`appDataFolder`), which only this application can access. You own your data.

## 📧 Support

For help, feedback, or business inquiries, please reach out to us at:
**ady.playground@gmail.com**

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
