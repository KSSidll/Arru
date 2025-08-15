package com.kssidll.arru.ui.screen.display.category


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.repository.CategoryRepositorySource
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepositorySource,
): ViewModel() {
    private val mCategory: MutableState<ProductCategoryEntity?> = mutableStateOf(null)
    val category: ProductCategoryEntity? by mCategory

    private var mCategoryListener: Job? = null

    val chartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler<ImmutableList<ItemSpentByTime>>? = null
    val spentByTimePeriod: TimePeriodFlowHandler.Periods? get() = mTimePeriodFlowHandler?.currentPeriod
    val spentByTimeData: Flow<ImmutableList<ItemSpentByTime>>? get() = mTimePeriodFlowHandler?.spentByTimeData

    fun categoryTotalSpent(): Flow<Float?>? {
        return category?.let { categoryRepository.totalSpent(it) }
    }

    /**
     * @return paging data of full item for current category as flow
     */
    fun transactions(): Flow<PagingData<FullItem>> {
        return category?.let { categoryRepository.fullItemsPaged(it) } ?: emptyFlow()
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
        val category = categoryRepository.get(categoryId).first() ?: return@async false

        // We ignore the possiblity of changing category while one is already loaded
        // as not doing that would increase complexity too much
        // and if it happens somehow, it would be considered a bug
        if (mCategory.value != null || categoryId == mCategory.value?.id) return@async true

        mCategoryListener?.cancel()
        mCategoryListener = viewModelScope.launch {
            categoryRepository.get(categoryId)
                .collectLatest {
                    mCategory.value = it
                }
        }

        mCategory.value = category

        mTimePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            day = {
                categoryRepository.totalSpentByDay(category)
            },
            week = {
                categoryRepository.totalSpentByWeek(category)
            },
            month = {
                categoryRepository.totalSpentByMonth(category)
            },
            year = {
                categoryRepository.totalSpentByYear(category)
            },
        )

        return@async true
    }
        .await()
}
