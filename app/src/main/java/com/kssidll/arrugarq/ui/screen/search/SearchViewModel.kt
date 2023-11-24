package com.kssidll.arrugarq.ui.screen.search


import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class SearchViewModel @Inject constructor(

): ViewModel() {
    internal val screenState: SearchScreenState = SearchScreenState()
}