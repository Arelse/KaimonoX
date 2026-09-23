package com.kaimono.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kaimono_prefs")

class Prefs(private val context: Context) {
    private val KEY_THEME = stringPreferencesKey("theme")
    private val KEY_USER = stringPreferencesKey("user")
    private val KEY_SEEDED = booleanPreferencesKey("seeded")

    val themeFlow: Flow<String> = context.dataStore.data.map { it[KEY_THEME] ?: "system" }
    val userFlow: Flow<String?> = context.dataStore.data.map { it[KEY_USER] }
    val seededFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_SEEDED] ?: false }

    suspend fun setTheme(value: String) = context.dataStore.edit { it[KEY_THEME] = value }
    suspend fun setUser(value: String?) = context.dataStore.edit {
        if (value == null) it.remove(KEY_USER) else it[KEY_USER] = value
    }
    suspend fun setSeeded(value: Boolean) = context.dataStore.edit { it[KEY_SEEDED] = value }
}

