package com.kssidll.arru.ui.screen.display.productcategory

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.domain.data.interfaces.avg
import com.kssidll.arru.domain.data.interfaces.median
import com.kssidll.arru.domain.data.interfaces.runMovingAverageChartDataTransaction
import com.kssidll.arru.domain.data.interfaces.runMovingMedianChartDataTransaction
import com.kssidll.arru.domain.data.interfaces.runMovingTotalChartDataTransaction
import com.kssidll.arru.domain.usecase.data.GetItemsForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForProductCategoryUseCase
import com.kssidll.arru.ui.component.SpendingSummaryPeriod
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
data class DisplayProductCategoryUiState(
    val listState: LazyListState = LazyListState(),
    val chartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val spentByTime: ImmutableList<ChartSource> = emptyImmutableList(),
    val spentByTimePeriod: SpendingSummaryPeriod = SpendingSummaryPeriod.Month,
    val categoryName: String = String(),
    val items: Flow<PagingData<Item>> = emptyFlow(),
    val totalChartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val totalSpentValue: Float = 0f,
    val averageChartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val averageSpentValue: Float = 0f,
    val medianChartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val medianSpentValue: Float = 0f,
)

@Immutable
sealed class DisplayProductCategoryEvent {
    data object NavigateBack : DisplayProductCategoryEvent()

    data object NavigateEditProductCategory : DisplayProductCategoryEvent()

    data class NavigateDisplayProduct(val productId: Long) : DisplayProductCategoryEvent()

    data class NavigateEditItem(val itemId: Long) : DisplayProductCategoryEvent()

    data class NavigateDisplayProductProducer(val productProducerId: Long) :
        DisplayProductCategoryEvent()

    data class NavigateEditProductProducer(val productProducerId: Long) :
        DisplayProductCategoryEvent()

    data class NavigateDisplayShop(val shopId: Long) : DisplayProductCategoryEvent()

    data class NavigateEditShop(val shopId: Long) : DisplayProductCategoryEvent()

    data class SetSpentByTimePeriod(val newPeriod: SpendingSummaryPeriod) :
        DisplayProductCategoryEvent()
}

@HiltViewModel
class DisplayProductCategoryViewModel
@Inject
constructor(
    private val getProductCategoryEntityUseCase: GetProductCategoryEntityUseCase,
    private val getTotalSpentForProductCategoryUseCase: GetTotalSpentForProductCategoryUseCase,
    private val getItemsForProductCategoryUseCase: GetItemsForProductCategoryUseCase,
    private val getTotalSpentByDayForProductCategoryUseCase:
        GetTotalSpentByDayForProductCategoryUseCase,
    private val getTotalSpentByWeekForProductCategoryUseCase:
        GetTotalSpentByWeekForProductCategoryUseCase,
    private val getTotalSpentByMonthForProductCategoryUseCase:
        GetTotalSpentByMonthForProductCategoryUseCase,
    private val getTotalSpentByYearForProductCategoryUseCase:
        GetTotalSpentByYearForProductCategoryUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DisplayProductCategoryUiState())
    val uiState = _uiState.asStateFlow()

    private var job: Job? = null
    private var chartJob: Job? = null

    private var _categoryId: Long? = null

    suspend fun checkExists(id: Long?) =
        viewModelScope
            .async {
                return@async id?.let { getProductCategoryEntityUseCase(it).first() } != null
            }
            .await()

    fun updateState(categoryId: Long?) =
        viewModelScope.launch {
            if (categoryId == null || _categoryId == categoryId) return@launch
            val category = getProductCategoryEntityUseCase(categoryId).first() ?: return@launch
            _categoryId = categoryId

            job?.cancel()
            job =
                viewModelScope.launch {
                    viewModelScope.launch {
                        _uiState.update { currentState ->
                            currentState.copy(
                                categoryName = category.name,
                                items = getItemsForProductCategoryUseCase(categoryId).cachedIn(this),
                            )
                        }

                        viewModelScope.launch {
                            getTotalSpentForProductCategoryUseCase(categoryId).collectLatest {
                                _uiState.update { currentState ->
                                    currentState.copy(totalSpentValue = it ?: 0f)
                                }
                            }
                        }
                    }
                }

            updateChartJob(_uiState.value.spentByTimePeriod, categoryId)
        }

    fun handleEvent(event: DisplayProductCategoryEvent) {
        when (event) {
            is DisplayProductCategoryEvent.NavigateBack -> {}
            is DisplayProductCategoryEvent.NavigateEditProductCategory -> {}
            is DisplayProductCategoryEvent.NavigateDisplayProduct -> {}
            is DisplayProductCategoryEvent.NavigateEditItem -> {}
            is DisplayProductCategoryEvent.NavigateDisplayProductProducer -> {}
            is DisplayProductCategoryEvent.NavigateEditProductProducer -> {}
            is DisplayProductCategoryEvent.NavigateDisplayShop -> {}
            is DisplayProductCategoryEvent.NavigateEditShop -> {}
            is DisplayProductCategoryEvent.SetSpentByTimePeriod ->
                setSpentByTimePeriod(event.newPeriod)
        }
    }

    private fun setSpentByTimePeriod(newPeriod: SpendingSummaryPeriod) {
        _uiState.update { currentState -> currentState.copy(spentByTimePeriod = newPeriod) }

        _categoryId?.let { updateChartJob(newPeriod, it) }
    }

    private fun updateChartJob(period: SpendingSummaryPeriod, categoryId: Long) {
        chartJob?.cancel()
        chartJob =
            viewModelScope.launch {
                when (period) {
                    SpendingSummaryPeriod.Day -> {
                        getTotalSpentByDayForProductCategoryUseCase(categoryId).collectLatest {
                            updateChartUiState(it)
                        }
                    }

                    SpendingSummaryPeriod.Week -> {
                        getTotalSpentByWeekForProductCategoryUseCase(categoryId).collectLatest {
                            updateChartUiState(it)
                        }
                    }

                    SpendingSummaryPeriod.Month -> {
                        getTotalSpentByMonthForProductCategoryUseCase(categoryId).collectLatest {
                            updateChartUiState(it)
                        }
                    }

                    SpendingSummaryPeriod.Year -> {
                        getTotalSpentByYearForProductCategoryUseCase(categoryId).collectLatest {
                            updateChartUiState(it)
                        }
                    }
                }
            }
    }

    private suspend fun updateChartUiState(spentByTime: ImmutableList<ItemSpentChartData>) {
        withContext(Dispatchers.Default) {
            _uiState.update { currentState ->
                currentState.copy(
                    spentByTime = spentByTime,
                    averageSpentValue = spentByTime.avg(),
                    medianSpentValue = spentByTime.median(),
                )
            }

            val currentUiState = _uiState.value
            currentUiState.totalChartEntryModelProducer.runMovingTotalChartDataTransaction(spentByTime)
            currentUiState.averageChartEntryModelProducer.runMovingAverageChartDataTransaction(
                spentByTime
            )
            currentUiState.medianChartEntryModelProducer.runMovingMedianChartDataTransaction(
                spentByTime
            )
        }
    }
}
