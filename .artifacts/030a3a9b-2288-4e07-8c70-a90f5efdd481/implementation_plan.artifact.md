# TimeLine App - Full Upgrade Implementation Plan

This plan outlines the steps to transform the basic **TimeLine** app into a professional-grade task tracker with Firestore cloud sync, local alarms, and the advanced UI design shown in the mockups.

## User Review Required

> [!IMPORTANT]
> **Firebase Setup**: You must create a project in the [Firebase Console](https://console.firebase.google.com/), enable **Firestore** and **Anonymous Authentication**, and add your `google-services.json` file to the `app/` directory. I cannot generate this file for you.

> [!WARNING]
> **Data Migration**: Updating the `TaskEntity` will require a Room database migration or a "destructive migration" (clearing local data). I will use destructive migration for this MVP upgrade to keep it simple, but be aware that local tasks will be reset.

## Proposed Changes

### 1. Dependencies & Configuration
Add Firebase, WorkManager, and Material Icons Extended.

#### [MODIFY] [libs.versions.toml](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/gradle/libs.versions.toml)
- Add Firebase BOM, Firestore, and Auth versions.
- Add WorkManager version.

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/build.gradle.kts)
- Apply Firebase Google Services plugin.
- Add Firebase and WorkManager dependencies.

---

### 2. Data Model & Database
Expand the data layer to support subtasks, categories, and reminders.

#### [MODIFY] [TaskEntity.kt](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/src/main/java/com/example/tasktracker/data/local/TaskEntity.kt)
- Add fields: `category`, `subtasks` (List<String>), `location`, `reminderTime` (Long), `isSynced`.
- Update `TaskType` to include `ACADEMIC`, `TEAM`, `PERSONAL`, etc.

#### [MODIFY] [AppDatabase.kt](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/src/main/java/com/example/tasktracker/data/local/AppDatabase.kt)
- Enable `fallbackToDestructiveMigration()`.

---

### 3. Firebase & Sync
Implement cloud synchronization.

#### [NEW] [FirebaseService.kt](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/src/main/java/com/example/tasktracker/data/remote/FirebaseService.kt)
- Handle Auth (Anonymous login).
- Firestore CRUD operations.

#### [MODIFY] [TaskRepository.kt](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/src/main/java/com/example/tasktracker/data/repository/TaskRepository.kt)
- Coordinate between Room (Local) and Firestore (Remote).

---

### 4. Alarms & Notifications
Implement the "Alarm with ring" feature.

#### [NEW] [AlarmReceiver.kt](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/src/main/java/com/example/tasktracker/util/AlarmReceiver.kt)
- A `BroadcastReceiver` that triggers at the scheduled `reminderTime`.
- Shows a notification with a high-priority channel and a custom sound/ringtone.

#### [NEW] [AlarmScheduler.kt](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/src/main/java/com/example/tasktracker/util/AlarmScheduler.kt)
- Utility to schedule/cancel alarms using `AlarmManager`.

---

### 5. UI Overhaul (Mockup Alignment)
Rebuild the interface to match the high-fidelity mockups.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/src/main/java/com/example/tasktracker/MainActivity.kt)
- Implement `Scaffold` with `BottomNavigationBar` (Timeline, Calendar, Upcoming, Completed).

#### [NEW] [UpcomingScreen.kt](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/src/main/java/com/example/tasktracker/ui/screens/UpcomingScreen.kt)
- Group items by Today, Tomorrow, This Week, Next Month.
- Include the "Upcoming Agenda" dashboard card.

#### [NEW] [CompletedScreen.kt](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/src/main/java/com/example/tasktracker/ui/screens/CompletedScreen.kt)
- "Monthly Velocity" dashboard with stats (Focus time, On-time rate).
- Archive view for finished tasks.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/src/main/java/com/example/tasktracker/ui/screens/HomeScreen.kt)
- Add "Good Morning" dashboard.
- Update timeline items to be more detailed (icons for attendees, subtask progress).

#### [MODIFY] [AddTaskScreen.kt](file:///C:/Users/aakas/Documents/SEM 7/Android CSE226/Android Development Projects/TaskTracker/app/src/main/java/com/example/tasktracker/ui/screens/AddTaskScreen.kt)
- Expanded form with Category, Reminder, Subtasks, and Urgency selection.

---

## Verification Plan

### Automated Tests
- Unit tests for `TaskRepository` to verify Firestore sync logic.
- UI tests to check if the Alarm triggers the notification.

### Manual Verification
1. **Sync Test**: Create a task, check Firestore console for the new document.
2. **Alarm Test**: Set a task for 1 minute in the future with a reminder. Verify the notification rings.
3. **UI Test**: Navigate through all 4 bottom tabs and verify the dashboard cards display correct summaries.
