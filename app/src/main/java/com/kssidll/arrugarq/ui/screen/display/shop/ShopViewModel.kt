package com.kssidll.arrugarq.ui.screen.display.shop


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

/**
 * Data representing [ShopScreen] screen state
 */
internal data class ShopScreenState(
    val shop: MutableState<Shop?> = mutableStateOf(null),
    val items: SnapshotStateList<FullItem> = mutableStateListOf(),
    val chartData: MutableState<Flow<List<ItemSpentByTime>>> = mutableStateOf(flowOf()),
    val totalSpentData: MutableState<Flow<Float>> = mutableStateOf(flowOf()),

    val spentByTimePeriod: MutableState<TimePeriodFlowHandler.Periods?> = mutableStateOf(null),

    val columnChartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer(),
)

/**
 * Page fetch size
 */
internal const val fullItemFetchCount = 8

/**
 * Maximum prefetched items
 */
internal const val fullItemMaxPrefetchCount = fullItemFetchCount * 6

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
    private val shopRepository: IShopRepository,
): ViewModel() {
    internal val screenState: ShopScreenState = ShopScreenState()

    private var timePeriodFlowHandler: TimePeriodFlowHandler? = null

    private var fullItemsDataQuery: Job? = null
    private var fullItemOffset: Int = 0

    private var newFullItemFlowJob: Job? = null
    private var newFullItemFlow: (Flow<Item>)? = null

    private var stateTotalSpentDataJob: Job? = null

    /**
     * Switches the state period to [newPeriod]
     * @param newPeriod Period to switch the state to
     */
    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        timePeriodFlowHandler?.switchPeriod(newPeriod)
        screenState.spentByTimePeriod.value = newPeriod

        screenState.chartData.value = timePeriodFlowHandler!!.spentByTimeData
    }

    /**
     * @return True if provided [shopId] was valid, false otherwise
     */
    suspend fun performDataUpdate(shopId: Long) = viewModelScope.async {
        val shop = shopRepository.get(shopId) ?: return@async false

        if (shopId == screenState.shop.value?.id) return@async true

        screenState.shop.value = shop

        stateTotalSpentDataJob?.cancel()
        stateTotalSpentDataJob = launch {
            screenState.totalSpentData.value = itemRepository.getTotalSpentByShopFlow(shopId)
                .map {
                    it.toFloat()
                        .div(100000)
                }
                .distinctUntilChanged()
                .cancellable()
        }

        timePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            dayFlow = {
                itemRepository.getTotalSpentByShopByDayFlow(shopId)
            },
            weekFlow = {
                itemRepository.getTotalSpentByShopByWeekFlow(shopId)
            },
            monthFlow = {
                itemRepository.getTotalSpentByShopByMonthFlow(shopId)
            },
            yearFlow = {
                itemRepository.getTotalSpentByShopByYearFlow(shopId)
            },
        )

        screenState.spentByTimePeriod.value = timePeriodFlowHandler?.currentPeriod

        screenState.chartData.value = timePeriodFlowHandler!!.spentByTimeData

        newFullItemFlowJob?.cancel()
        newFullItemFlowJob = launch {
            newFullItemFlow = itemRepository.getLastFlow()
                .cancellable()

            newFullItemFlow?.collect {
                fullItemOffset = 0
                screenState.items.clear()
                fullItemsDataQuery?.cancel()
                fullItemsDataQuery = performFullItemsQuery()
                fullItemOffset += fullItemFetchCount
            }
        }

        return@async true
    }
        .await()

    /**
     * Requests a query of [fullItemFetchCount] items to be appended to transactions list
     */
    fun queryMoreFullItems() {
        if (fullItemsDataQuery == null) return

        if (fullItemsDataQuery!!.isCompleted && screenState.shop.value != null) {
            fullItemsDataQuery = performFullItemsQuery(fullItemOffset)
            fullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Requires shop value of shopScreenState to be a non null.
     *
     * Doesn't check it itself as it doesn't update the offset
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        screenState.items.addAll(
            itemRepository.getFullItemsByShop(
                offset = queryOffset,
                count = fullItemFetchCount,
                shopId = screenState.shop.value!!.id
            )
        )
    }

}
