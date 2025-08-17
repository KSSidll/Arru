package com.kssidll.arru.ui.screen.display.category


import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.interfaces.ChartSource
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
import kotlinx.collections.immutable.ImmutableList
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
import javax.inject.Inject

@Immutable
data class DisplayCategoryUiState(
    val chartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val spentByTime: ImmutableList<ChartSource> = emptyImmutableList(),
    val spentByTimePeriod: SpendingSummaryPeriod = SpendingSummaryPeriod.Month,
    val categoryName: String = String(),
    val totalSpent: Float = 0f,
    val items: Flow<PagingData<Item>> = emptyFlow(),
)

@Immutable
sealed class DisplayCategoryEvent {
    data object NavigateBack: DisplayCategoryEvent()
    data object NavigateCategoryEdit: DisplayCategoryEvent()
    data class NavigateProduct(val productId: Long): DisplayCategoryEvent()
    data class NavigateItemEdit(val itemId: Long): DisplayCategoryEvent()
    data class NavigateProducer(val productProducerId: Long): DisplayCategoryEvent()
    data class NavigateShop(val shopId: Long): DisplayCategoryEvent()

    data class SetSpentByTimePeriod(val newPeriod: SpendingSummaryPeriod): DisplayCategoryEvent()
}

@HiltViewModel
class DisplayCategoryViewModel @Inject constructor(
    private val getProductCategoryEntityUseCase: GetProductCategoryEntityUseCase,
    private val getTotalSpentForProductCategoryUseCase: GetTotalSpentForProductCategoryUseCase,
    private val getItemsForProductCategoryUseCase: GetItemsForProductCategoryUseCase,
    private val getTotalSpentByDayForProductCategoryUseCase: GetTotalSpentByDayForProductCategoryUseCase,
    private val getTotalSpentByWeekForProductCategoryUseCase: GetTotalSpentByWeekForProductCategoryUseCase,
    private val getTotalSpentByMonthForProductCategoryUseCase: GetTotalSpentByMonthForProductCategoryUseCase,
    private val getTotalSpentByYearForProductCategoryUseCase: GetTotalSpentByYearForProductCategoryUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow(DisplayCategoryUiState())
    val uiState = _uiState.asStateFlow()

    private var job: Job? = null
    private var chartJob: Job? = null

    private var _categoryId: Long? = null

    /**
     * @return true if provided [categoryId] was valid, false otherwise
     */
    suspend fun performDataUpdate(categoryId: Long) = viewModelScope.async {
        val category = getProductCategoryEntityUseCase(categoryId).first() ?: return@async false
        _categoryId = categoryId

        job?.cancel()
        job = viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    categoryName = category.name,
                    items = getItemsForProductCategoryUseCase(categoryId)
                )
            }

            viewModelScope.launch {
                getTotalSpentForProductCategoryUseCase(categoryId).collectLatest {
                    _uiState.update { currentState ->
                        currentState.copy(
                            totalSpent = it ?: 0f
                        )
                    }
                }
            }
        }

        updateChartJob(_uiState.value.spentByTimePeriod, categoryId)

        return@async true
    }.await()

    fun handleEvent(event: DisplayCategoryEvent) {
        when (event) {
            is DisplayCategoryEvent.NavigateBack -> {}
            is DisplayCategoryEvent.NavigateCategoryEdit -> {}
            is DisplayCategoryEvent.NavigateProduct -> {}
            is DisplayCategoryEvent.NavigateItemEdit -> {}
            is DisplayCategoryEvent.NavigateProducer -> {}
            is DisplayCategoryEvent.NavigateShop -> {}
            is DisplayCategoryEvent.SetSpentByTimePeriod -> setSpentByTimePeriod(event.newPeriod)
        }
    }

    private fun setSpentByTimePeriod(newPeriod: SpendingSummaryPeriod) {
        _uiState.update { currentState ->
            currentState.copy(
                spentByTimePeriod = newPeriod
            )
        }

        _categoryId?.let { updateChartJob(newPeriod, it) }
    }

    private fun updateChartJob(period: SpendingSummaryPeriod, categoryId: Long) {
        chartJob?.cancel()
        chartJob = viewModelScope.launch {
            when (period) {
                SpendingSummaryPeriod.Day   -> {
                    getTotalSpentByDayForProductCategoryUseCase(categoryId).collectLatest {
                        _uiState.update { currentState ->
                            currentState.copy(
                                spentByTime = it
                            )
                        }
                    }
                }

                SpendingSummaryPeriod.Week  -> {
                    getTotalSpentByWeekForProductCategoryUseCase(categoryId).collectLatest {
                        _uiState.update { currentState ->
                            currentState.copy(
                                spentByTime = it
                            )
                        }
                    }
                }

                SpendingSummaryPeriod.Month -> {
                    getTotalSpentByMonthForProductCategoryUseCase(categoryId).collectLatest {
                        _uiState.update { currentState ->
                            currentState.copy(
                                spentByTime = it
                            )
                        }
                    }
                }

                SpendingSummaryPeriod.Year  -> {
                    getTotalSpentByYearForProductCategoryUseCase(categoryId).collectLatest {
                        _uiState.update { currentState ->
                            currentState.copy(
                                spentByTime = it
                            )
                        }
                    }
                }
            }
        }
    }
}
