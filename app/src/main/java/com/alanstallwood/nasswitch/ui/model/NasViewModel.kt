package com.alanstallwood.nasswitch.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alanstallwood.nasswitch.domain.usecase.GetNasStatus
import com.alanstallwood.nasswitch.domain.usecase.ShutdownNas
import com.alanstallwood.nasswitch.domain.usecase.WakeNas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NasViewModel(
    private val wakeNas: WakeNas,
    private val shutdownNas: ShutdownNas,
    private val getNasStatus: GetNasStatus
) : ViewModel() {

    private val _uiState = MutableStateFlow(NasUiState())
    val uiState: StateFlow<NasUiState> = _uiState.asStateFlow()

    fun refreshStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val online = getNasStatus()
                _uiState.value = NasUiState(
                    isOnline = online,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = NasUiState(
                    isOnline = null,
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun wake() {
        viewModelScope.launch {
            try {
                wakeNas()
                refreshStatus()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun shutdown() {
        viewModelScope.launch {
            try {
                shutdownNas()
                refreshStatus()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}