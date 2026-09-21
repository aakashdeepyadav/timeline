package com.example.timeline.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.timeline.data.remote.FirebaseService
import com.example.timeline.data.repository.TaskRepository
import com.example.timeline.util.PreferenceManager

class TaskViewModelFactory(
    private val application: Application,
    private val repository: TaskRepository,
    private val firebaseService: FirebaseService,
    private val preferenceManager: PreferenceManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(application, repository) as T
        }
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(firebaseService, preferenceManager, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
