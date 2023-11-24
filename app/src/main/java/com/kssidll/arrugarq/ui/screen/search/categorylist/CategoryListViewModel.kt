package com.kssidll.arrugarq.ui.screen.search.categorylist


import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.search.shared.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoryRepository: ICategoryRepository,
): ViewModel() {
    internal val screenState: ListScreenState<ProductCategoryWithAltNames> = ListScreenState()

    init {
        fillStateItems()
    }

    /**
     * Fetches new data to screen state
     */
    private fun fillStateItems() {
        screenState.items.value = categoryRepository.getAllWithAltNamesFlow()
    }
}
