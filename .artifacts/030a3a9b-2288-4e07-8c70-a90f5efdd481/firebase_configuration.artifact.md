# Firebase Configuration & Security Rules

To activate the Google Account Sync feature, follow these manual steps in the Firebase Console.

## 1. Firebase Project Setup
1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Select your project: **TimeLine**.
3.  Go to **Project Settings** > **General**.
4.  Download the latest `google-services.json` and place it in your `app/` directory.

## 2. Authentication Configuration
1.  Go to **Build** > **Authentication** > **Sign-in method**.
2.  Enable **Google** as a sign-in provider.
3.  Ensure the **Web SDK configuration** (Client ID and Client Secret) is visible. The `default_web_client_id` used in the code comes from this configuration once the `google-services.json` is added to your project.

## 3. Firestore Database Setup
1.  Go to **Build** > **Firestore Database**.
2.  Click **Create database**.
3.  Choose a location and start in **Production mode**.
4.  Go to the **Rules** tab and publish the following security rules:

```javascript
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    // Each user can only read and write their own tasks
    match /users/{userId}/tasks/{taskId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 4. SHA Fingerprints (Crucial for Google Sign-In)
1.  In your local terminal, run:
    `./gradlew signingReport`
2.  Copy the **SHA-1** fingerprint for the `debug` variant.
3.  Go to **Firebase Console** > **Project Settings** > **Your apps**.
4.  Select the Android app and click **Add fingerprint**.
5.  Paste the SHA-1 fingerprint.
6.  (Optional but recommended) Repeat for the **SHA-256** fingerprint.

## 5. Sync Logic Summary
- **Room** is the single source of truth for the UI.
- **WorkManager** handles background sync every 15 minutes.
- **Last-Write-Wins**: The `updatedAt` timestamp is used to resolve conflicts between local and remote data.
