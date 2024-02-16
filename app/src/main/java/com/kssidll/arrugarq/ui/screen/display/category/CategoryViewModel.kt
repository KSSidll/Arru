package com.kssidll.arrugarq.ui.screen.display.category


import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.paging.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.data.*
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

    val chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler? = null
    val spentByTimePeriod: TimePeriodFlowHandler.Periods? get() = mTimePeriodFlowHandler?.currentPeriod
    val spentByTimeData: Flow<List<ChartSource>>? get() = mTimePeriodFlowHandler?.spentByTimeData

    fun categoryTotalSpent(): Flow<Float>? {
        if (category == null) return null

        return categoryRepository.totalSpentFlow(category!!)
            .map {
                it.toFloat()
                    .div(Item.PRICE_DIVISOR * Item.QUANTITY_DIVISOR)
            }
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
