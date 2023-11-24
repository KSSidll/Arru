package com.kssidll.arrugarq.ui.screen.search.shoplist


import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.search.shared.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class ShopListViewModel @Inject constructor(
    private val shopRepository: IShopRepository,
): ViewModel() {
    internal val screenState: ListScreenState<Shop> = ListScreenState()

    init {
        fillStateItems()
    }

    /**
     * Fetches new data to screen state
     */
    private fun fillStateItems() {
        screenState.items.value = shopRepository.getAllFlow()
    }
}
