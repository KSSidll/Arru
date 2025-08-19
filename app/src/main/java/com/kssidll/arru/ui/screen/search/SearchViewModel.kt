package com.kssidll.arru.ui.screen.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// TODO refactor uiState Event UseCase

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    internal val screenState: SearchScreenState = SearchScreenState()
}
