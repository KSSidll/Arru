package com.kssidll.arru.ui.screen.search.categorylist


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arru.data.repository.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoryRepository: CategoryRepositorySource,
): ViewModel() {
    private val _filter = mutableStateOf(String())
    var filter by _filter

    fun items() = categoryRepository.allWithAltNamesFlow()
}
