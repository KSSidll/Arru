package com.kssidll.arru.ui.screen.search.productlist


import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.ui.screen.search.shared.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepositorySource,
): ViewModel() {
    internal val screenState: ListScreenState<ProductWithAltNames> = ListScreenState()

    init {
        fillStateItems()
    }

    /**
     * Fetches new data to screen state
     */
    private fun fillStateItems() {
        screenState.items.value = productRepository.allWithAltNamesFlow()
    }
}
