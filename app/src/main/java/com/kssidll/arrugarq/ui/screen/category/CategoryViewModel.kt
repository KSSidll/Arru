package com.kssidll.arrugarq.ui.screen.category


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

internal data class CategoryScreenState(
    val category: MutableState<ProductCategory?> = mutableStateOf(null),
    val items: SnapshotStateList<FullItem> = mutableStateListOf(),
    val chartData: SnapshotStateList<ItemSpentByTime> = mutableStateListOf(),
    val totalSpentData: MutableFloatState = mutableFloatStateOf(0F),

    val spentByTimePeriod: MutableState<TimePeriodFlowHandler.Periods?> = mutableStateOf(null),

    val columnChartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer(),
    var finishedChartAnimation: Boolean = false,
)

internal const val fullItemFetchCount = 8
internal const val fullItemMaxPrefetchCount = 50

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
    private val categoryRepository: IProductCategoryRepository,
): ViewModel() {
    internal val categoryScreenState: CategoryScreenState = CategoryScreenState()

    private var timePeriodFlowHandlerJob: Job? = null
    private var timePeriodFlowHandler: TimePeriodFlowHandler? = null

    private var fullItemsDataQuery: Job? = null
    private var fullItemOffset: Int = 0

    private var newFullItemFlowJob: Job? = null
    private var newFullItemFlow: (Flow<Item>)? = null

    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        timePeriodFlowHandler?.switchPeriod(newPeriod)
        categoryScreenState.spentByTimePeriod.value = newPeriod

        timePeriodFlowHandlerJob?.cancel()
        timePeriodFlowHandlerJob = viewModelScope.launch {
            timePeriodFlowHandler!!.spentByTimeData.collect {
                categoryScreenState.chartData.clear()
                categoryScreenState.chartData.addAll(it)
            }
        }
    }

    fun performDataUpdate(categoryId: Long) = viewModelScope.launch {
        categoryScreenState.category.value = categoryRepository.get(categoryId)

        viewModelScope.launch {
            categoryScreenState.totalSpentData.floatValue =
                itemRepository.getTotalSpentByCategory(categoryId)
                    .toFloat()
                    .div(100000)
        }

        timePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            cancellableDayFlow = {
                itemRepository.getTotalSpentByCategoryByDayFlow(categoryId)
                    .cancellable()
            },
            cancellableWeekFlow = {
                itemRepository.getTotalSpentByCategoryByWeekFlow(categoryId)
                    .cancellable()
            },
            cancellableMonthFlow = {
                itemRepository.getTotalSpentByCategoryByMonthFlow(categoryId)
                    .cancellable()
            },
            cancellableYearFlow = {
                itemRepository.getTotalSpentByCategoryByYearFlow(categoryId)
                    .cancellable()
            },
        )

        categoryScreenState.spentByTimePeriod.value = timePeriodFlowHandler?.currentPeriod

        timePeriodFlowHandlerJob?.cancel()
        timePeriodFlowHandlerJob = viewModelScope.launch {
            categoryScreenState.chartData.clear()
            timePeriodFlowHandler!!.spentByTimeData.collect {
                categoryScreenState.chartData.addAll(it)
            }
        }

        newFullItemFlowJob?.cancel()
        newFullItemFlowJob = viewModelScope.launch {
            newFullItemFlow = itemRepository.getLastFlow()
                .cancellable()

            newFullItemFlow?.collect {
                fullItemOffset = 0
                categoryScreenState.items.clear()
                fullItemsDataQuery?.cancel()
                fullItemsDataQuery = performFullItemsQuery()
                fullItemOffset += fullItemFetchCount
            }
        }

    }

    fun queryMoreFullItems() {
        if (fullItemsDataQuery == null) return

        if (fullItemsDataQuery!!.isCompleted && categoryScreenState.category.value != null) {
            fullItemsDataQuery = performFullItemsQuery(fullItemOffset)
            fullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Requires category value of categoryScreenState to be a non null.
     * Doesn't check it itself as it doesn't update the offset
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        categoryScreenState.items.addAll(
            itemRepository.getFullItemsByCategory(
                offset = queryOffset,
                count = fullItemFetchCount,
                categoryId = categoryScreenState.category.value!!.id,
            )
        )
    }
}
