package com.example.timeline.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskType {
    TASK, EXAM, DEADLINE, MEETING, EVENT
}

enum class Priority {
    LOW, MEDIUM, HIGH
}

enum class SyncStatus {
    SYNCED, PENDING_CREATE, PENDING_UPDATE, PENDING_DELETE
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String?,
    val date: Long, // LocalDate to epoch millis
    val time: Long?, // Start time
    val durationMillis: Long? = null,
    val hasTime: Boolean,
    val type: TaskType,
    val category: String = "General",
    val priority: Priority,
    val isCompleted: Boolean = false,
    val location: String? = null,
    val subtasks: List<String> = emptyList(),
    val isReminderEnabled: Boolean = false,
    val reminderTime: Long? = null,
    val reminderOffsetMinutes: Int = 0,
    val reminderRepeatCount: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val deleted: Boolean = false,
    val isSynced: Boolean = false
)
