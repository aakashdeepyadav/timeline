package com.example.timeline.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.timeline.data.local.AppDatabase
import com.example.timeline.data.remote.GoogleDriveService
import com.example.timeline.data.repository.TaskRepository
import com.example.timeline.util.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val driveService = GoogleDriveService(applicationContext)
        val repository = TaskRepository(database.taskDao(), driveService)
        val preferenceManager = PreferenceManager(applicationContext)

        // Only sync if user is signed in with Google
        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (account == null) return Result.success()

        return try {
            val success = repository.syncWithDrive()
            if (success) {
                preferenceManager.setLastSynced(System.currentTimeMillis())
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
