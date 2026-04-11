package com.neuro notes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neuro notes.data.DashboardStats
import com.neuronotes.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Dashboard screen following MVVM architecture.
 * Fetches dashboard stats using Retrofit and exposes state via StateFlow.
 */
class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        fetchDashboardStats()
    }

    private fun fetchDashboardStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val stats = RetrofitClient.api.getDashboardStats()
                _uiState.value = _uiState.value.copy(
                    stats = stats,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load dashboard: ${e.message}"
                )
            }
        }
    }
}

/**
 * UI State for Dashboard screen.
 */
data class DashboardUiState(
    val stats: DashboardStats? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

