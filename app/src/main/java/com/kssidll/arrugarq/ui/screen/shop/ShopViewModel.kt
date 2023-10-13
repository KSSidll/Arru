package com.kssidll.arrugarq.ui.screen.shop


import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

internal data class ShopScreenState(
    val shop: MutableState<Shop?> = mutableStateOf(null),
    val items: SnapshotStateList<FullItem> = mutableStateListOf(),
    val chartData: MutableState<Flow<List<ItemSpentByTime>>> = mutableStateOf(flowOf<List<ItemSpentByTime>>().cancellable())
)

internal const val fullItemFetchCount = 8
internal const val fullItemMaxPrefetchCount = 50

@HiltViewModel
class ShopViewModel @Inject constructor(
    shopRepository: IShopRepository,
    itemRepository: IItemRepository,
): ViewModel() {
    internal val shopScreenState: ShopScreenState = ShopScreenState()

    private val shopRepository: IShopRepository
    private val itemRepository: IItemRepository

    private var chartDataJob: Job = Job()

    private var fullItemsDataQuery: Job = Job()
    private var newFullItemFlow: Flow<List<FullItem>> = flowOf()
    private var fullItemOffset: Int = 0

    init {
        this.shopRepository = shopRepository
        this.itemRepository = itemRepository
    }

    fun performDataUpdate(shopId: Long) = viewModelScope.launch {
        shopScreenState.shop.value = shopRepository.get(shopId)

        chartDataJob.cancel()
        chartDataJob = viewModelScope.launch {
            shopScreenState.chartData.value = itemRepository.getTotalSpentByShopByDayFlow(shopId)
        }

        newFullItemFlow = itemRepository.getFullItemsFlow(
            0,
            1
        )

        viewModelScope.launch {
            newFullItemFlow.collect {
                fullItemOffset = 0
                fullItemsDataQuery.cancel()
                shopScreenState.items.clear()
                fullItemsDataQuery = performFullItemsQuery()
                fullItemOffset += fullItemFetchCount
            }
        }
    }

    fun queryMoreFullItems() {
        if (fullItemsDataQuery.isCompleted && shopScreenState.shop.value != null) {
            fullItemsDataQuery = performFullItemsQuery(fullItemOffset)
            fullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Requires shop value of shopScreenState to be a non null.
     * Doesn't check it itself as it doesn't update the offset
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        shopScreenState.items.addAll(
            itemRepository.getFullItemsByShop(
                offset = queryOffset,
                count = fullItemFetchCount,
                shopId = shopScreenState.shop.value!!.id
            )
        )
    }

}
