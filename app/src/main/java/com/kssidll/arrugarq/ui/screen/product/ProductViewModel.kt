package com.kssidll.arrugarq.ui.screen.product


import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.shop.fullItemFetchCount
import com.patrykandpatrick.vico.core.entry.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

internal data class ProductScreenState(
    val product: MutableState<Product?> = mutableStateOf(null),
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
class ProductViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
    private val productRepository: IProductRepository,
): ViewModel() {
    internal val productScreenState: ProductScreenState = ProductScreenState()

    private var timePeriodFlowHandlerJob: Job? = null
    private var timePeriodFlowHandler: TimePeriodFlowHandler? = null

    private var fullItemsDataQuery: Job? = null
    private var fullItemOffset: Int = 0

    private var newFullItemFlowJob: Job? = null
    private var newFullItemFlow: (Flow<Item>)? = null

    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        timePeriodFlowHandler?.switchPeriod(newPeriod)
        productScreenState.spentByTimePeriod.value = newPeriod

        timePeriodFlowHandlerJob?.cancel()
        timePeriodFlowHandlerJob = viewModelScope.launch {
            timePeriodFlowHandler!!.spentByTimeData.collect {
                productScreenState.chartData.clear()
                productScreenState.chartData.addAll(it)
            }
        }
    }

    fun performDataUpdate(productId: Long) = viewModelScope.launch {
        productScreenState.product.value = productRepository.get(productId)

        viewModelScope.launch {
            productScreenState.totalSpentData.floatValue =
                itemRepository.getTotalSpentByProduct(productId)
                    .toFloat()
                    .div(100000)
        }

        timePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            cancellableDayFlow = {
                itemRepository.getTotalSpentByProductByDayFlow(productId)
                    .cancellable()
            },
            cancellableWeekFlow = {
                itemRepository.getTotalSpentByProductByWeekFlow(productId)
                    .cancellable()
            },
            cancellableMonthFlow = {
                itemRepository.getTotalSpentByProductByMonthFlow(productId)
                    .cancellable()
            },
            cancellableYearFlow = {
                itemRepository.getTotalSpentByProductByYearFlow(productId)
                    .cancellable()
            },
        )

        productScreenState.spentByTimePeriod.value = timePeriodFlowHandler?.currentPeriod

        timePeriodFlowHandlerJob?.cancel()
        timePeriodFlowHandlerJob = viewModelScope.launch {
            productScreenState.chartData.clear()
            timePeriodFlowHandler!!.spentByTimeData.collect {
                productScreenState.chartData.addAll(it)
            }
        }

        newFullItemFlowJob?.cancel()
        newFullItemFlowJob = viewModelScope.launch {
            newFullItemFlow = itemRepository.getLastFlow()
                .cancellable()

            newFullItemFlow?.collect {
                fullItemOffset = 0
                productScreenState.items.clear()
                fullItemsDataQuery?.cancel()
                fullItemsDataQuery = performFullItemsQuery()
                fullItemOffset += fullItemFetchCount
            }
        }

    }

    fun queryMoreFullItems() {
        if (fullItemsDataQuery == null) return

        if (fullItemsDataQuery!!.isCompleted && productScreenState.product.value != null) {
            fullItemsDataQuery = performFullItemsQuery(fullItemOffset)
            fullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Requires product value of productScreenState to be a non null.
     * Doesn't check it itself as it doesn't update the offset
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        productScreenState.items.addAll(
            itemRepository.getFullItemsByProduct(
                offset = queryOffset,
                count = fullItemFetchCount,
                productId = productScreenState.product.value!!.id,
            )
        )
    }
}
