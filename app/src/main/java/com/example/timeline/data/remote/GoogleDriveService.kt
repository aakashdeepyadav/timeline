package com.example.timeline.data.remote

import android.content.Context
import com.example.timeline.data.local.TaskEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class GoogleDriveService(private val context: Context) {
    private val gson = Gson()
    private val driveService: Drive? by lazy {
        val account = GoogleSignIn.getLastSignedInAccount(context) ?: return@lazy null
        val credential = GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(DriveScopes.DRIVE_APPDATA)
        ).apply {
            selectedAccount = account.account
        }
        
        Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("TimeLine").build()
    }

    private val FILE_NAME = "tasks_backup.json"

    suspend fun saveTasksToDrive(tasks: List<TaskEntity>): Boolean = withContext(Dispatchers.IO) {
        val service = driveService ?: return@withContext false
        try {
            val json = gson.toJson(tasks)
            val tempFile = java.io.File(context.cacheDir, FILE_NAME)
            tempFile.writeText(json)

            val metadata = File().apply {
                name = FILE_NAME
                parents = Collections.singletonList("appDataFolder")
            }
            
            val content = FileContent("application/json", tempFile)

            // Check if file exists
            val existingFileId = getExistingFileId()
            if (existingFileId != null) {
                service.files().update(existingFileId, null, content).execute()
            } else {
                service.files().create(metadata, content).execute()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchTasksFromDrive(): List<TaskEntity> = withContext(Dispatchers.IO) {
        val service = driveService ?: return@withContext emptyList()
        try {
            val fileId = getExistingFileId() ?: return@withContext emptyList()
            val outputStream = java.io.ByteArrayOutputStream()
            service.files().get(fileId).executeMediaAndDownloadTo(outputStream)
            
            val json = outputStream.toString()
            val type = object : TypeToken<List<TaskEntity>>() {}.type
            gson.fromJson<List<TaskEntity>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun getExistingFileId(): String? {
        val service = driveService ?: return null
        val result = service.files().list()
            .setSpaces("appDataFolder")
            .setQ("name = '$FILE_NAME'")
            .setFields("files(id)")
            .execute()
        
        return result.files?.firstOrNull()?.id
    }
}
