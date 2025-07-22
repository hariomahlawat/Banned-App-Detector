package com.hariomahlawat.bannedappdetector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hariomahlawat.bannedappdetector.usecase.CheckForAppUpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val checkUpdate: CheckForAppUpdateUseCase
) : ViewModel() {

    private val _updateAvailable = MutableStateFlow(false)
    val updateAvailable: StateFlow<Boolean> = _updateAvailable.asStateFlow()

    init {
        viewModelScope.launch {
            _updateAvailable.value = checkUpdate()
        }
    }
}
