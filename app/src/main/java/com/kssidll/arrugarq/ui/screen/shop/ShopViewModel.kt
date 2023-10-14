package com.kssidll.arrugarq.ui.screen.shop


import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.repository.*
import com.patrykandpatrick.vico.core.entry.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

internal data class ShopScreenState(
    val shop: MutableState<Shop?> = mutableStateOf(null),
    val items: SnapshotStateList<FullItem> = mutableStateListOf(),
    val chartData: SnapshotStateList<ItemSpentByTime> = mutableStateListOf(),
    val totalSpentData: MutableFloatState = mutableFloatStateOf(0F),

    val spentByTimePeriod: MutableState<TimePeriodFlowHandler.Periods?> = mutableStateOf(null),

    val columnChartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer(),
    val smaChartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer(),
    var finishedChartAnimation: Boolean = false,
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

    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        timePeriodFlowHandler?.switchPeriod(newPeriod)
        shopScreenState.spentByTimePeriod.value = newPeriod

        timePeriodFlowHandlerJob?.cancel()
        timePeriodFlowHandlerJob = viewModelScope.launch {
            timePeriodFlowHandler!!.spentByTimeData.collect {
                shopScreenState.chartData.clear()
                shopScreenState.chartData.addAll(it)
            }
        }
    }

    fun performDataUpdate(shopId: Long) = viewModelScope.launch {
        shopScreenState.shop.value = shopRepository.get(shopId)

        viewModelScope.launch {
            shopScreenState.totalSpentData.floatValue = itemRepository.getTotalSpentByShop(shopId)
                .toFloat()
                .div(100000)
        }


        timePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            cancellableDayFlow = {
                itemRepository.getTotalSpentByShopByDayFlow(shopId)
                    .cancellable()
            },
            cancellableWeekFlow = {
                itemRepository.getTotalSpentByShopByWeekFlow(shopId)
                    .cancellable()
            },
            cancellableMonthFlow = {
                itemRepository.getTotalSpentByShopByMonthFlow(shopId)
                    .cancellable()
            },
            cancellableYearFlow = {
                itemRepository.getTotalSpentByShopByYearFlow(shopId)
                    .cancellable()
            },
        )

        shopScreenState.spentByTimePeriod.value = timePeriodFlowHandler?.currentPeriod

        timePeriodFlowHandlerJob?.cancel()
        timePeriodFlowHandlerJob = viewModelScope.launch {
            shopScreenState.chartData.clear()
            timePeriodFlowHandler!!.spentByTimeData.collect {
                shopScreenState.chartData.addAll(it)
            }
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
