# Google Drive Sync Configuration

To enable zero-cost, privacy-first synchronization using the user's own Google Drive, follow these steps in the Google Cloud Console and Firebase Console.

## 1. Enable Drive API in Google Cloud Console
1.  Go to the [Google Cloud Console](https://console.cloud.google.com/).
2.  Select the project associated with your Firebase app (e.g., **com-example-tasktracker-d3c65**).
3.  Go to **APIs & Services** > **Library**.
4.  Search for **"Google Drive API"** and click **Enable**.

## 2. Configure OAuth Consent Screen
1.  Go to **APIs & Services** > **OAuth consent screen**.
2.  Ensure your app is in **Production** mode or add your test emails to the **Test users** list.
3.  Add the following scope:
    - `https://www.googleapis.com/auth/drive.appdata` (View and manage its own configuration data in your Google Drive)

## 3. Firebase Console Check
1.  Ensure **Google Sign-In** is enabled in **Authentication** > **Sign-in method**.
2.  Ensure you have added your **SHA-1** fingerprint to the project settings.

## 4. How Sync Works
- **Hidden Storage**: Your tasks are stored in a file named `tasks_backup.json` inside the hidden **App Data Folder** on the user's Google Drive.
- **Privacy**: No other apps can see this folder, and it doesn't clutter the user's main Drive view.
- **Zero Cost**: Storage and API calls count against the user's Google account quota, not yours.
- **Conflict Resolution**: The app uses a "Last-Write-Wins" strategy based on the `updatedAt` timestamp.
