package com.example.timeline.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class DataMode {
    UNSET, LOCAL, GOOGLE
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {
    private val DATA_MODE_KEY = stringPreferencesKey("data_mode")
    private val LAST_SYNCED_KEY = androidx.datastore.preferences.core.longPreferencesKey("last_synced")
    private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

    val dataMode: Flow<DataMode> = context.dataStore.data.map { preferences ->
        val modeStr = preferences[DATA_MODE_KEY] ?: DataMode.UNSET.name
        DataMode.valueOf(modeStr)
    }
    
    val lastSynced: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[LAST_SYNCED_KEY] ?: 0L
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val themeStr = preferences[THEME_MODE_KEY] ?: ThemeMode.LIGHT.name
        ThemeMode.valueOf(themeStr)
    }

    suspend fun setDataMode(mode: DataMode) {
        context.dataStore.edit { preferences ->
            preferences[DATA_MODE_KEY] = mode.name
        }
    }
    
    suspend fun setLastSynced(time: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SYNCED_KEY] = time
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }
}
