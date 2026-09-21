# TimeLine - Minimalistic Task Tracker

TimeLine is a modern, premium Android application designed to help you manage your tasks, deadlines, and events in a beautiful, chronological interface. Built with **Kotlin** and **Jetpack Compose**, it follows a strictly minimalistic philosophy and the latest Android development best practices.

## ✨ Features

- **Chronological Timeline**: View your life at a glance with a clean, vertical timeline.
- **Minimalistic Premium Design**: A high-end UI focused on content, featuring a bespoke "Deep Night" dark mode and modern **Rounded Icons**.
- **Privacy-First (Google Drive Sync)**: Securely back up and sync your data across devices using your own Google Drive storage—zero server costs for the developer, maximum privacy and ownership for you.
- **Offline-First**: All data is stored locally in a Room database, ensuring the app works 100% offline.
- **Smart Filtering & Search**: Find exactly what you need with real-time search across all views and category-based filtering.
- **Reliable Reminders**: Set precise alarms for your most important tasks with high-priority notifications that persist even after device reboots.
- **Customizable Appearance**: Choose between Light, Dark, or System Default themes from the settings.
- **Clean Slate**: No predefined data—start your productivity journey with a minimalistic, production-ready interface.

## 🛠️ Technical Stack

- **UI**: Jetpack Compose, Material 3 (Rounded Style)
- **Architecture**: MVVM (Model-View-ViewModel), Repository Pattern
- **Persistence**: Room Database, DataStore (Preferences)
- **Sync**: Google Drive REST API (App Data Folder), WorkManager
- **Auth**: Firebase Authentication (Google Sign-In)
- **Image Loading**: Coil
- **Concurrency**: Kotlin Coroutines & Flow

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
