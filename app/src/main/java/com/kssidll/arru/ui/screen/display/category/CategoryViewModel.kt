package com.kssidll.arru.ui.screen.display.category


import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.paging.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.domain.*
import com.kssidll.arru.domain.data.*
import com.patrykandpatrick.vico.core.entry.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepositorySource,
): ViewModel() {
    private val mCategory: MutableState<ProductCategory?> = mutableStateOf(null)
    val category: ProductCategory? by mCategory

    private var mCategoryListener: Job? = null

    val chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler<Data<List<ItemSpentByTime>>>? = null
    val spentByTimePeriod: TimePeriodFlowHandler.Periods? get() = mTimePeriodFlowHandler?.currentPeriod

    // TODO this should probably take from transaction spending instead of items
    // this being items means that if there's a transaction but no items are registered
    // the displayed data would be inaccurate
    val spentByTimeData: Flow<Data<List<ItemSpentByTime>>>? get() = mTimePeriodFlowHandler?.spentByTimeData

    fun categoryTotalSpent(): Flow<Data<Float?>>? {
        if (category == null) return null

        return categoryRepository.totalSpentFlow(category!!)
    }

    /**
     * @return paging data of full item for current category as flow
     */
    fun transactions(): Flow<PagingData<FullItem>> {
        if (category == null) return emptyFlow()
        return categoryRepository.fullItemsPagedFlow(category!!)
    }

    /**
     * Switches the state period to [newPeriod]
     * @param newPeriod Period to switch the state to
     */
    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        mTimePeriodFlowHandler?.switchPeriod(newPeriod)
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

        mCategoryListener?.cancel()
        mCategoryListener = viewModelScope.launch {
            categoryRepository.getFlow(categoryId)
                .collectLatest {
                    if (it is Data.Loaded) {
                        mCategory.value = it.data
                    } else {
                        mCategory.value = null
                    }
                }
        }

        mCategory.value = category

        mTimePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            dayFlow = {
                categoryRepository.totalSpentByDayFlow(category)
            },
            weekFlow = {
                categoryRepository.totalSpentByWeekFlow(category)
            },
            monthFlow = {
                categoryRepository.totalSpentByMonthFlow(category)
            },
            yearFlow = {
                categoryRepository.totalSpentByYearFlow(category)
            },
        )

        return@async true
    }
        .await()
}
