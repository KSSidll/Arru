package com.kssidll.arru.ui.screen.home.dashboard

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.TotalSpentByCategory
import com.kssidll.arru.data.data.TotalSpentByShop
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentUseCase
import com.kssidll.arru.ui.component.SpendingSummaryPeriod
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class DashboardUiState(
    val scrollState: ScrollState = ScrollState(0),
    val totalSpent: Float = 0f,
    val chartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val spentByTime: ImmutableList<ChartSource> = emptyImmutableList(),
    val spentByTimePeriod: SpendingSummaryPeriod = SpendingSummaryPeriod.Month,
    val categorySpendingRankingData: ImmutableList<TotalSpentByCategory> = emptyImmutableList(),
    val shopSpendingRankingData: ImmutableList<TotalSpentByShop> = emptyImmutableList(),
) {
    val nothingToDisplayVisible: Boolean =
        spentByTime.isEmpty() &&
            categorySpendingRankingData.isEmpty() &&
            shopSpendingRankingData.isEmpty()
    val chartSectionVisible: Boolean = spentByTime.isNotEmpty()
    val categoryCardVisible: Boolean = categorySpendingRankingData.isNotEmpty()
    val shopCardVisible: Boolean = shopSpendingRankingData.isNotEmpty()
}

@Immutable
sealed class DashboardEvent {
    data class ChangeSpentByTimePeriod(val newPeriod: SpendingSummaryPeriod) : DashboardEvent()

    data object NavigateSettings : DashboardEvent()

    data object NavigateCategoryRanking : DashboardEvent()

    data object NavigateShopRanking : DashboardEvent()
}

@HiltViewModel
class DashboardViewModel
@Inject
constructor(
    private val getTotalSpentByDayUseCase: GetTotalSpentByDayUseCase,
    private val getTotalSpentByWeekUseCase: GetTotalSpentByWeekUseCase,
    private val getTotalSpentByMonthUseCase: GetTotalSpentByMonthUseCase,
    private val getTotalSpentByYearUseCase: GetTotalSpentByYearUseCase,
    private val getTotalSpentUseCase: GetTotalSpentUseCase,
    private val getTotalSpentByProductCategoryUseCase: GetTotalSpentByProductCategoryUseCase,
    private val getTotalSpentByShopUseCase: GetTotalSpentByShopUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    private var chartJob: Job? = null

    init {
        viewModelScope.launch {
            getTotalSpentUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(totalSpent = it ?: 0f) }
            }
        }

        viewModelScope.launch {
            getTotalSpentByProductCategoryUseCase().collectLatest {
                _uiState.update { currentState ->
                    currentState.copy(
                        categorySpendingRankingData =
                            it.sortedByDescending { spending -> spending.sortValue() }
                                .take(6)
                                .toImmutableList()
                    )
                }
            }
        }

        viewModelScope.launch {
            getTotalSpentByShopUseCase().collectLatest {
                _uiState.update { currentState ->
                    currentState.copy(
                        shopSpendingRankingData =
                            it.sortedByDescending { spending -> spending.sortValue() }
                                .take(6)
                                .toImmutableList()
                    )
                }
            }
        }

        updateChartJob(_uiState.value.spentByTimePeriod)
    }

    fun handleEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.NavigateSettings -> {}

            is DashboardEvent.NavigateCategoryRanking -> {}

            is DashboardEvent.NavigateShopRanking -> {}

            is DashboardEvent.ChangeSpentByTimePeriod -> setSpentByTimePeriod(event.newPeriod)
        }
    }

    private fun setSpentByTimePeriod(newPeriod: SpendingSummaryPeriod) {
        _uiState.update { currentState -> currentState.copy(spentByTimePeriod = newPeriod) }

        updateChartJob(newPeriod)
    }

    private fun updateChartJob(period: SpendingSummaryPeriod) {
        chartJob?.cancel()
        chartJob =
            viewModelScope.launch {
                when (period) {
                    SpendingSummaryPeriod.Day -> {
                        getTotalSpentByDayUseCase().collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }

                    SpendingSummaryPeriod.Week -> {
                        getTotalSpentByWeekUseCase().collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }

                    SpendingSummaryPeriod.Month -> {
                        getTotalSpentByMonthUseCase().collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }

                    SpendingSummaryPeriod.Year -> {
                        getTotalSpentByYearUseCase().collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }
                }
            }
    }
}
