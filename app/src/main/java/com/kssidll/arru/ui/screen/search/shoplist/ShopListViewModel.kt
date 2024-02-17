package com.kssidll.arru.ui.screen.search.shoplist


import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.ui.screen.search.shared.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class ShopListViewModel @Inject constructor(
    private val shopRepository: ShopRepositorySource,
): ViewModel() {
    internal val screenState: ListScreenState<Shop> = ListScreenState()

    init {
        fillStateItems()
    }

    /**
     * Fetches new data to screen state
     */
    private fun fillStateItems() {
        screenState.items.value = shopRepository.allFlow()
    }
}
