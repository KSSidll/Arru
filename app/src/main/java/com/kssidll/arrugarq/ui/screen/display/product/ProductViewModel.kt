package com.kssidll.arrugarq.ui.screen.display.product


import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.display.shop.fullItemFetchCount
import com.patrykandpatrick.vico.core.entry.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

/**
 * Data representing [ProductScreen] screen state
 */
internal data class ProductScreenState(
    val product: MutableState<Product?> = mutableStateOf(null),
    val items: SnapshotStateList<FullItem> = mutableStateListOf(),
    val productPriceByShopByTimeItems: SnapshotStateList<ProductPriceByShopByTime> = mutableStateListOf(),
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
class ProductViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
    private val productRepository: IProductRepository,
): ViewModel() {
    internal val screenState: ProductScreenState = ProductScreenState()

    private var timePeriodFlowHandler: TimePeriodFlowHandler? = null

    private var fullItemsDataQuery: Job? = null
    private var fullItemOffset: Int = 0

    private var newFullItemFlowJob: Job? = null
    private var newFullItemFlow: (Flow<Item>)? = null

    private var productPriceByShopByTimeJob: Job? = null
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
     * @return True if provided [productId] was valid, false otherwise
     */
    suspend fun performDataUpdate(productId: Long) = viewModelScope.async {
        val product = productRepository.get(productId) ?: return@async false

        if (productId == screenState.product.value?.id) return@async true

        screenState.product.value = product

        stateTotalSpentDataJob?.cancel()
        stateTotalSpentDataJob = launch {
            screenState.totalSpentData.value = itemRepository.getTotalSpentByProductFlow(productId)
                .map {
                    it.toFloat()
                        .div(100000)
                }
                .distinctUntilChanged()
                .cancellable()
        }

        productPriceByShopByTimeJob?.cancel()
        productPriceByShopByTimeJob = launch {
            val itemFlow = itemRepository.getProductsAveragePriceByShopByMonthSortedFlow(productId)

            itemFlow.collect {
                screenState.productPriceByShopByTimeItems.clear()
                screenState.productPriceByShopByTimeItems.addAll(it)
            }
        }

        timePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            dayFlow = {
                itemRepository.getTotalSpentByProductByDayFlow(productId)
            },
            weekFlow = {
                itemRepository.getTotalSpentByProductByWeekFlow(productId)
            },
            monthFlow = {
                itemRepository.getTotalSpentByProductByMonthFlow(productId)
            },
            yearFlow = {
                itemRepository.getTotalSpentByProductByYearFlow(productId)
            },
        )

        screenState.spentByTimePeriod.value = timePeriodFlowHandler?.currentPeriod

        screenState.chartData.value = timePeriodFlowHandler!!.spentByTimeData

        newFullItemFlowJob?.cancel()
        newFullItemFlowJob = viewModelScope.launch {
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

        if (fullItemsDataQuery!!.isCompleted && screenState.product.value != null) {
            fullItemsDataQuery = performFullItemsQuery(fullItemOffset)
            fullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Requires product value of productScreenState to be a non null.
     * Doesn't check it itself as it doesn't update the offset
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        screenState.items.addAll(
            itemRepository.getFullItemsByProduct(
                offset = queryOffset,
                count = fullItemFetchCount,
                productId = screenState.product.value!!.id,
            )
        )
    }
}
