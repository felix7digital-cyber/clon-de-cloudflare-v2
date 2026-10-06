package com.cfadmin.pro.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cf_prefs")

class UserPrefs(private val context: Context) {
    private val KEY_DARK = booleanPreferencesKey("dark_theme")
    private val KEY_RECENT = stringPreferencesKey("recent")
    private val KEY_PINNED = stringPreferencesKey("pinned")
    private val KEY_DOMAIN = stringPreferencesKey("domain")

    val darkTheme: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK] ?: true }

    val recent: Flow<List<String>> = context.dataStore.data.map {
        it[KEY_RECENT]?.split(",")?.filter { s -> s.isNotBlank() } ?: emptyList()
    }

    val pinned: Flow<List<String>> = context.dataStore.data.map {
        it[KEY_PINNED]?.split(",")?.filter { s -> s.isNotBlank() }
            ?: listOf("overview", "pages")
    }

    val domain: Flow<String> = context.dataStore.data.map {
        it[KEY_DOMAIN] ?: "midominio.com"
    }

    suspend fun setDarkTheme(dark: Boolean) {
        context.dataStore.edit { it[KEY_DARK] = dark }
    }
    suspend fun setRecent(list: List<String>) {
        context.dataStore.edit { it[KEY_RECENT] = list.joinToString(",") }
    }
    suspend fun setPinned(list: List<String>) {
        context.dataStore.edit { it[KEY_PINNED] = list.joinToString(",") }
    }
    suspend fun setDomain(domain: String) {
        context.dataStore.edit { it[KEY_DOMAIN] = domain }
    }
}
