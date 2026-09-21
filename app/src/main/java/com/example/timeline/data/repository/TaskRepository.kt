package com.example.timeline.data.repository

import com.example.timeline.data.local.SyncStatus
import com.example.timeline.data.local.TaskDao
import com.example.timeline.data.local.TaskEntity
import com.example.timeline.data.remote.GoogleDriveService
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val googleDriveService: GoogleDriveService
) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun getTaskById(id: Int): TaskEntity? {
        return taskDao.getTaskById(id)
    }

    suspend fun insert(task: TaskEntity): Int {
        val id = taskDao.insertTask(task.copy(updatedAt = System.currentTimeMillis())).toInt()
        return id
    }

    suspend fun update(task: TaskEntity) {
        taskDao.updateTask(task.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun delete(task: TaskEntity) {
        // Soft delete: mark as deleted and update timestamp
        // This ensures the deletion syncs to other devices via Drive
        taskDao.updateTask(task.copy(
            deleted = true, 
            updatedAt = System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING_DELETE
        ))
    }
    
    suspend fun syncWithDrive(): Boolean {
        try {
            // 1. Pull from Drive (Merge Remote into Local)
            val driveTasks = googleDriveService.fetchTasksFromDrive()
            if (driveTasks.isNotEmpty()) {
                driveTasks.forEach { remoteTask ->
                    val localTask = taskDao.getTaskById(remoteTask.id)
                    // Conflict Resolution: Latest updatedAt wins
                    if (localTask == null || remoteTask.updatedAt > localTask.updatedAt) {
                        taskDao.insertTask(remoteTask.copy(syncStatus = SyncStatus.SYNCED))
                    }
                }
            }

            // 2. Push to Drive (Backup all Local state including soft-deleted)
            val allLocalTasks = taskDao.getAllTasksWithDeleted()
            val success = googleDriveService.saveTasksToDrive(allLocalTasks)
            
            if (success) {
                // Mark all local tasks as synced
                allLocalTasks.forEach { task ->
                    if (task.syncStatus != SyncStatus.SYNCED) {
                        taskDao.updateTask(task.copy(syncStatus = SyncStatus.SYNCED))
                    }
                }
            }
            return success
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun getLocalTasksCount(): Int {
        return taskDao.getAllTasksSync().size
    }
}
