package com.alanstallwood.nasswitch.ui.model

data class NasUiState(
    val isOnline: Boolean? = null,   // null = unknown/loading
    val isLoading: Boolean = false,
    val error: String? = null
)