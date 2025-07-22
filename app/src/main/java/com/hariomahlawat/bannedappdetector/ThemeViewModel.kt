package com.hariomahlawat.bannedappdetector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hariomahlawat.bannedappdetector.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val repo: ThemeRepository
) : ViewModel() {

    val theme: StateFlow<ThemeSetting> =
        repo.themeFlow().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeSetting.SYSTEM
        )

    fun toggleTheme() {
        viewModelScope.launch {
            val newSetting = when (theme.value) {
                ThemeSetting.DARK -> ThemeSetting.LIGHT
                ThemeSetting.LIGHT -> ThemeSetting.DARK
                ThemeSetting.SYSTEM -> ThemeSetting.DARK
            }
            repo.setTheme(newSetting)
        }
    }
}
