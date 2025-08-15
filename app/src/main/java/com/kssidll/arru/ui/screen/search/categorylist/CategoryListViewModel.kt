package com.kssidll.arru.ui.screen.search.categorylist


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.repository.CategoryRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoryRepository: CategoryRepositorySource,
): ViewModel() {
    private val _filter = mutableStateOf(String())
    var filter by _filter

    fun items() = categoryRepository.allFlow()
}
