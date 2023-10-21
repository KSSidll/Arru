package com.kssidll.arrugarq.ui.screen.shop.shopranking

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class ShopRankingViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
): ViewModel() {
    fun getSpentByShop(): Flow<List<ItemSpentByShop>> {
        return itemRepository.getShopTotalSpentFlow()
    }
}