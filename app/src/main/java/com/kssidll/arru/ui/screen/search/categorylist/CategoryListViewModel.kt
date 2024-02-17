package com.kssidll.arru.ui.screen.search.categorylist


import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.ui.screen.search.shared.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoryRepository: CategoryRepositorySource,
): ViewModel() {
    internal val screenState: ListScreenState<ProductCategoryWithAltNames> = ListScreenState()

    init {
        fillStateItems()
    }

    /**
     * Fetches new data to screen state
     */
    private fun fillStateItems() {
        screenState.items.value = categoryRepository.allWithAltNamesFlow()
    }
}
