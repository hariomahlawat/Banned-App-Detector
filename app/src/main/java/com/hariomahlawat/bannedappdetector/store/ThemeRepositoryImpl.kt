package com.hariomahlawat.bannedappdetector.store

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hariomahlawat.bannedappdetector.ThemeSetting
import com.hariomahlawat.bannedappdetector.repository.ThemeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val DS_NAME = "user_prefs"
private val Context.dataStore by preferencesDataStore(DS_NAME)
private val KEY_THEME = stringPreferencesKey("theme")

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ThemeRepository {

    override fun themeFlow(): Flow<ThemeSetting> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_THEME]?.let { ThemeSetting.valueOf(it) } ?: ThemeSetting.SYSTEM
        }

    override suspend fun setTheme(theme: ThemeSetting) {
        context.dataStore.edit { it[KEY_THEME] = theme.name }
    }

    override suspend fun currentTheme(): ThemeSetting {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_THEME]?.let { ThemeSetting.valueOf(it) } ?: ThemeSetting.SYSTEM
    }
}
