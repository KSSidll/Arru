package com.kssidll.arrugarq.ui.screen.category.category


import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.producer.producer.*
import com.kssidll.arrugarq.ui.screen.product.product.*
import com.kssidll.arrugarq.ui.screen.shop.shop.fullItemFetchCount
import com.patrykandpatrick.vico.core.entry.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

/**
 * Data representing [CategoryScreen] screen state
 */
internal data class CategoryScreenState(
    val category: MutableState<ProductCategory?> = mutableStateOf(null),
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
class CategoryViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
    private val categoryRepository: ICategoryRepository,
): ViewModel() {
    internal val screenState: CategoryScreenState = CategoryScreenState()

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
     * @return true if provided [categoryId] was valid, false otherwise
     */
    suspend fun performDataUpdate(categoryId: Long) = viewModelScope.async {
        val category = categoryRepository.get(categoryId) ?: return@async false

        if (categoryId == screenState.category.value?.id) return@async true

        screenState.category.value = category

        stateTotalSpentDataJob?.cancel()
        stateTotalSpentDataJob = launch {
            screenState.totalSpentData.value =
                itemRepository.getTotalSpentByCategoryFlow(categoryId)
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
                itemRepository.getTotalSpentByCategoryByDayFlow(categoryId)
            },
            weekFlow = {
                itemRepository.getTotalSpentByCategoryByWeekFlow(categoryId)
            },
            monthFlow = {
                itemRepository.getTotalSpentByCategoryByMonthFlow(categoryId)
            },
            yearFlow = {
                itemRepository.getTotalSpentByCategoryByYearFlow(categoryId)
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

        if (fullItemsDataQuery!!.isCompleted && screenState.category.value != null) {
            fullItemsDataQuery = performFullItemsQuery(fullItemOffset)
            fullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Requires category value of categoryScreenState to be a non null.
     * Doesn't check it itself as it doesn't update the offset
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        screenState.items.addAll(
            itemRepository.getFullItemsByCategory(
                offset = queryOffset,
                count = fullItemFetchCount,
                categoryId = screenState.category.value!!.id,
            )
        )
    }
}
