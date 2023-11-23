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
    private val mCategory: MutableState<ProductCategory?> = mutableStateOf(null)
    val category: ProductCategory? by mCategory

    val chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()

    private val mTransactionItems: SnapshotStateList<FullItem> = mutableStateListOf()
    val transactionItems get() = mTransactionItems.toList()

    private var timePeriodFlowHandler: TimePeriodFlowHandler? = null
    val spentByTimePeriod: TimePeriodFlowHandler.Periods? get() = timePeriodFlowHandler?.currentPeriod
    val spentByTimeData: Flow<List<ItemSpentByTime>>? get() = timePeriodFlowHandler?.spentByTimeData

    private var fullItemsDataQuery: Job? = null
    private var fullItemOffset: Int = 0

    private var newFullItemFlowJob: Job? = null
    private var newFullItemFlow: (Flow<Item>)? = null

    fun categoryTotalSpent(): Flow<Float>? {
        if (category == null) return null

        return itemRepository.getTotalSpentByCategoryFlow(category!!.id)
            .map {
                it.toFloat()
                    .div(100000)
            }
            .distinctUntilChanged()
            .cancellable()
    }

    /**
     * Switches the state period to [newPeriod]
     * @param newPeriod Period to switch the state to
     */
    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        timePeriodFlowHandler?.switchPeriod(newPeriod)
    }

    /**
     * @return true if provided [categoryId] was valid, false otherwise
     */
    suspend fun performDataUpdate(categoryId: Long) = viewModelScope.async {
        val category = categoryRepository.get(categoryId) ?: return@async false

        // We ignore the possiblity of changing category while one is already loaded
        // as not doing that would increase complexity too much
        // and if it happens somehow, it would be considered a bug
        if (mCategory.value != null || categoryId == mCategory.value?.id) return@async true

        mCategory.value = category

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

        newFullItemFlowJob?.cancel()
        newFullItemFlowJob = launch {
            newFullItemFlow = itemRepository.getLastFlow()
                .cancellable()

            newFullItemFlow?.collect {
                fullItemOffset = 0
                mTransactionItems.clear()
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
        if (category == null || fullItemsDataQuery == null) return

        if (fullItemsDataQuery!!.isCompleted) {
            fullItemsDataQuery = performFullItemsQuery(fullItemOffset)
            fullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Requires category value of categoryScreenState to be a non null.
     * Doesn't check it itself as it doesn't update the offset
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        if (category == null) return@launch

        mTransactionItems.addAll(
            itemRepository.getFullItemsByCategory(
                offset = queryOffset,
                count = fullItemFetchCount,
                categoryId = category!!.id,
            )
        )
    }
}
