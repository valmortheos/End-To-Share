package com.valmortheos.endtoshare.feature.home

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Idle : HomeUiState
    data class Error(val message: String) : HomeUiState
}
