package com.kssidll.arrugarq.ui.screen.shop


import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

internal data class ShopScreenState(
    val shop: MutableState<Shop?> = mutableStateOf(null),
    val items: SnapshotStateList<FullItem> = mutableStateListOf(),
    val chartData: MutableState<Flow<List<ItemSpentByTime>>> = mutableStateOf(flowOf()),
)

internal const val fullItemFetchCount = 8
internal const val fullItemMaxPrefetchCount = 50

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopRepository: IShopRepository,
    private val itemRepository: IItemRepository,
): ViewModel() {
    internal val shopScreenState: ShopScreenState = ShopScreenState()

    private var timePeriodFlowHandlerJob: Job? = null
    private var timePeriodFlowHandler: TimePeriodFlowHandler? = null

    private var fullItemsDataQuery: Job? = null
    private var fullItemOffset: Int = 0

    private var newFullItemFlowJob: Job? = null
    private var newFullItemFlow: (Flow<Item>)? = null

    fun performDataUpdate(shopId: Long) = viewModelScope.launch {
        shopScreenState.shop.value = shopRepository.get(shopId)

        timePeriodFlowHandlerJob?.cancel()

        timePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            cancellableDayFlow = {
                itemRepository.getTotalSpentByShopByDayFlow(shopId)
                    .cancellable()
            },
            cancellableWeekFlow = {
                itemRepository.getTotalSpentByWeekFlow()
                    .cancellable()
            },
            cancellableMonthFlow = {
                itemRepository.getTotalSpentByMonthFlow()
                    .cancellable()
            },
            cancellableYearFlow = {
                itemRepository.getTotalSpentByYearFlow()
                    .cancellable()
            },
            startPeriod = TimePeriodFlowHandler.Periods.Day,
        )

        timePeriodFlowHandlerJob = viewModelScope.launch {
            shopScreenState.chartData.value = timePeriodFlowHandler!!.spentByTimeData
        }


        newFullItemFlowJob?.cancel()
        newFullItemFlowJob = viewModelScope.launch {
            newFullItemFlow = itemRepository.getLastFlow()
                .cancellable()

            newFullItemFlow?.collect {
                fullItemOffset = 0
                shopScreenState.items.clear()
                fullItemsDataQuery?.cancel()
                fullItemsDataQuery = performFullItemsQuery()
                fullItemOffset += fullItemFetchCount
            }
        }

    }

    fun queryMoreFullItems() {
        if (fullItemsDataQuery == null) return

        if (fullItemsDataQuery!!.isCompleted && shopScreenState.shop.value != null) {
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
