package com.example.timeline.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timeline.data.remote.FirebaseService
import com.example.timeline.util.DataMode
import com.example.timeline.util.PreferenceManager
import com.example.timeline.util.ThemeMode
import com.example.timeline.data.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val firebaseService: FirebaseService,
    private val preferenceManager: PreferenceManager,
    private val repository: TaskRepository
) : ViewModel() {

    val dataMode: StateFlow<DataMode?> = preferenceManager.dataMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    val lastSynced: StateFlow<Long> = preferenceManager.lastSynced.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0L
    )

    val themeMode: StateFlow<ThemeMode> = preferenceManager.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.LIGHT
    )

    private val _currentUser = MutableStateFlow<FirebaseUser?>(firebaseService.getCurrentUser())
    val currentUser = _currentUser.asStateFlow()

    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
        }
    }

    fun setOfflineMode() = viewModelScope.launch {
        preferenceManager.setDataMode(DataMode.LOCAL)
    }

    suspend fun getLocalTasksCount(): Int {
        return repository.getLocalTasksCount()
    }

    fun onGoogleSignInSuccess() = viewModelScope.launch {
        preferenceManager.setDataMode(DataMode.GOOGLE)
    }

    fun syncWithDrive() = viewModelScope.launch {
        val success = repository.syncWithDrive()
        if (success) {
            preferenceManager.setLastSynced(System.currentTimeMillis())
        }
    }

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch {
        preferenceManager.setThemeMode(mode)
    }

    fun signOut() = viewModelScope.launch {
        firebaseService.signOut()
        preferenceManager.setDataMode(DataMode.LOCAL)
        preferenceManager.setLastSynced(0L)
    }
}
