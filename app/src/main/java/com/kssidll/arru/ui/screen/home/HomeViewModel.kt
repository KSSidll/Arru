package com.kssidll.arru.ui.screen.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Immutable
data class HomeUiState(val currentDestination: HomeDestinations = HomeDestinations.DEFAULT)

@Immutable
sealed class HomeEvent {
    data class ChangeScreenDestination(val newDestination: HomeDestinations) : HomeEvent()

    data object NavigateAddTransaction : HomeEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ChangeScreenDestination -> {
                _uiState.update { currentState ->
                    currentState.copy(currentDestination = event.newDestination)
                }
            }

            is HomeEvent.NavigateAddTransaction -> {}
        }
    }
}
