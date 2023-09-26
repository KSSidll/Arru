package com.kssidll.arrugarq.presentation.screen.home

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class HomeViewModel @Inject constructor(
    itemRepository: IItemRepository,
    shopRepository: IShopRepository,
): ViewModel() {
    private val shopRepository: IShopRepository
    private val itemRepository: IItemRepository

    init {
        this.shopRepository = shopRepository
        this.itemRepository = itemRepository
    }

    fun getItemTotalSpentByMonth(): Flow<List<ItemMonthlyTotal>> {
        return itemRepository.getTotalSpentByMonthFlow()
    }
}
