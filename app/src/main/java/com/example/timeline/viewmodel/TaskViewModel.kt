package com.example.timeline.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.timeline.data.local.TaskEntity
import com.example.timeline.data.repository.TaskRepository
import com.example.timeline.util.AlarmScheduler
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application, private val repository: TaskRepository) : AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .combine(_searchQuery) { tasks, query ->
            if (query.isBlank()) {
                tasks
            } else {
                tasks.filter { it.title.contains(query, ignoreCase = true) || it.description?.contains(query, ignoreCase = true) == true }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun insert(task: TaskEntity) = viewModelScope.launch {
        val id = repository.insert(task)
        val insertedTask = repository.getTaskById(id)
        insertedTask?.let {
            AlarmScheduler.scheduleAlarm(getApplication(), it)
        }
    }

    fun update(task: TaskEntity) = viewModelScope.launch {
        repository.update(task)
        AlarmScheduler.scheduleAlarm(getApplication(), task)
    }

    fun delete(task: TaskEntity) = viewModelScope.launch {
        repository.delete(task)
        AlarmScheduler.cancelAlarm(getApplication(), task)
    }

    fun toggleCompletion(task: TaskEntity) = viewModelScope.launch {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        repository.update(updatedTask)
        if (updatedTask.isCompleted) {
            AlarmScheduler.cancelAlarm(getApplication(), updatedTask)
        } else {
            AlarmScheduler.scheduleAlarm(getApplication(), updatedTask)
        }
    }

    fun syncFromRemote() = viewModelScope.launch {
        repository.syncWithDrive()
    }
}
