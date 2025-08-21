package com.kssidll.arru.ui.screen.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionRepositorySource
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.kssidll.arru.domain.data.data.Transaction
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.emptyImmutableSet
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.ui.component.SpendingSummaryPeriod
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@Immutable
data class HomeUiState(
    val totalSpent: Float = 0f,
    val dashboardSpentByTimeChartData: ImmutableList<ChartSource> = emptyImmutableList(),
    val dashboardSpentByTimeChartCurrentPeriod: SpendingSummaryPeriod = SpendingSummaryPeriod.Month,
    val dashboardCategorySpendingRankingData: ImmutableList<ItemSpentByCategory> =
        emptyImmutableList(),
    val dashboardShopSpendingRankingData: ImmutableList<TransactionTotalSpentByShop> =
        emptyImmutableList(),
    val dashboardTotalChartEntryModelProducer: CartesianChartModelProducer =
        CartesianChartModelProducer(),
    val dashboardAverageChartEntryModelProducer: CartesianChartModelProducer =
        CartesianChartModelProducer(),
    val dashboardMedianChartEntryModelProducer: CartesianChartModelProducer =
        CartesianChartModelProducer(),
    val analysisCurrentDateYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val analysisCurrentDateMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val analysisCurrentDateCategoryData: ImmutableList<ItemSpentByCategory> = emptyImmutableList(),
    val analysisCurrentDateShopData: ImmutableList<TransactionTotalSpentByShop> =
        emptyImmutableList(),
    val analysisPreviousDateCategoryData: ImmutableList<ItemSpentByCategory> = emptyImmutableList(),
    val analysisPreviousDateShopData: ImmutableList<TransactionTotalSpentByShop> =
        emptyImmutableList(),
    val transactions: Flow<PagingData<Transaction>> = flowOf(),
    val transactionWithVisibleItems: ImmutableSet<Long> = emptyImmutableSet(),
    val dashboardScrollState: ScrollState = ScrollState(0),
    val transactionsListState: LazyListState = LazyListState(),
    val currentDestination: HomeDestinations = HomeDestinations.DEFAULT,
) {
    val dashboardScreenNothingToDisplayVisible: Boolean =
        dashboardSpentByTimeChartData.isEmpty() &&
            dashboardCategorySpendingRankingData.isEmpty() &&
            dashboardShopSpendingRankingData.isEmpty()
    val dashboardChartSectionVisible: Boolean = dashboardSpentByTimeChartData.isNotEmpty()
    val dashboardCategoryCardVisible: Boolean = dashboardCategorySpendingRankingData.isNotEmpty()
    val dashboardShopCardVisible: Boolean = dashboardShopSpendingRankingData.isNotEmpty()

    val analysisScreenNothingToDisplayVisible: Boolean =
        analysisCurrentDateCategoryData.isEmpty() &&
            analysisCurrentDateShopData.isEmpty() &&
            analysisPreviousDateCategoryData.isEmpty() &&
            analysisPreviousDateShopData.isEmpty()
    val analysisScreenCategoryCardVisible: Boolean =
        analysisCurrentDateCategoryData.isNotEmpty() ||
            analysisPreviousDateCategoryData.isNotEmpty()
    val analysisScreenShopCardVisible: Boolean =
        analysisCurrentDateShopData.isNotEmpty() || analysisPreviousDateShopData.isNotEmpty()
}

@Immutable
sealed class HomeEvent {
    data class ChangeScreenDestination(val newDestination: HomeDestinations) : HomeEvent()

    data class ChangeDashboardSpentByTimeChartPeriod(val newPeriod: SpendingSummaryPeriod) :
        HomeEvent()

    data object IncrementCurrentAnalysisDate : HomeEvent()

    data object DecrementCurrentAnalysisDate : HomeEvent()

    data object NavigateSettings : HomeEvent()

    data object NavigateSearch : HomeEvent()

    data class NavigateDisplayProduct(val productId: Long) : HomeEvent()

    data class NavigateDisplayProductCategory(val categoryId: Long) : HomeEvent()

    data class NavigateDisplayProductProducer(val producerId: Long) : HomeEvent()

    data class NavigateDisplayShop(val shopId: Long) : HomeEvent()

    data object NavigateAddTransaction : HomeEvent()

    data class NavigateEditTransaction(val transactionId: Long) : HomeEvent()

    data class NavigateAddItem(val transactionId: Long) : HomeEvent()

    data class NavigateEditItem(val itemId: Long) : HomeEvent()

    data object NavigateCategoryRanking : HomeEvent()

    data object NavigateShopRanking : HomeEvent()

    data class NavigateCategorySpendingComparison(val year: Int, val month: Int) : HomeEvent()

    data class NavigateShopSpendingComparison(val year: Int, val month: Int) : HomeEvent()

    data class ToggleTransactionItemVisibility(val transactionId: Long) : HomeEvent()
}

// TODO refactor UseCase

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val transactionRepository: TransactionRepositorySource,
    private val categoryRepository: ProductCategoryRepositorySource,
    private val shopRepository: ShopRepositorySource,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val mTimePeriodFlowHandler: TimePeriodFlowHandler<ImmutableList<ChartSource>> =
        TimePeriodFlowHandler(
            scope = viewModelScope,
            day = { transactionRepository.totalSpentByDay() },
            week = { transactionRepository.totalSpentByWeek() },
            month = { transactionRepository.totalSpentByMonth() },
            year = { transactionRepository.totalSpentByYear() },
        )
    private var dashboardSpentByTimeChartDataCollectJob: Job? = null
    private var analysisCurrentDateCategoryDataCollectJob: Job? = null
    private var analysisCurrentDateShopDataCollectJob: Job? = null
    private var analysisPreviousDateCategoryDataCollectJob: Job? = null
    private var analysisPreviousDateShopDataCollectJob: Job? = null

    init {
        _uiState.update { currentState ->
            currentState.copy(
                transactions =
                    transactionRepository
                        .transactionBasketsPaged()
                        .cachedIn(viewModelScope)
            )
        }

        viewModelScope.launch {
            transactionRepository.totalSpent().collect {
                _uiState.update { currentState -> currentState.copy(totalSpent = it ?: 0f) }
            }
        }

        viewModelScope.launch {
            categoryRepository.totalSpentByCategory().collect {
                _uiState.update { currentState ->
                    currentState.copy(dashboardCategorySpendingRankingData = it)
                }
            }
        }

        viewModelScope.launch {
            shopRepository.totalSpentByShop().collect {
                _uiState.update { currentState ->
                    currentState.copy(dashboardShopSpendingRankingData = it)
                }
            }
        }

        updateDashboardSpentByTimeChartDataCollectJob()
        updateAnalysisDataCollectionJobs()
    }

    fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ChangeScreenDestination -> changeScreenDestination(event.newDestination)

            is HomeEvent.ChangeDashboardSpentByTimeChartPeriod -> changeDashboardSpentByTimeChartPeriod(event.newPeriod)

            is HomeEvent.IncrementCurrentAnalysisDate -> incrementCurrentAnalysisDate()

            is HomeEvent.DecrementCurrentAnalysisDate -> decrementCurrentAnalysisDate()

            is HomeEvent.NavigateSettings -> {}

            is HomeEvent.NavigateSearch -> {}

            is HomeEvent.NavigateDisplayProduct -> {}

            is HomeEvent.NavigateDisplayProductCategory -> {}

            is HomeEvent.NavigateDisplayProductProducer -> {}

            is HomeEvent.NavigateDisplayShop -> {}

            is HomeEvent.NavigateAddItem -> {}

            is HomeEvent.NavigateEditItem -> {}

            is HomeEvent.NavigateAddTransaction -> {}

            is HomeEvent.NavigateEditTransaction -> {}

            is HomeEvent.NavigateCategoryRanking -> {}

            is HomeEvent.NavigateShopRanking -> {}

            is HomeEvent.NavigateCategorySpendingComparison -> {}

            is HomeEvent.NavigateShopSpendingComparison -> {}

            is HomeEvent.ToggleTransactionItemVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        transactionWithVisibleItems =
                            if (
                                currentState.transactionWithVisibleItems.contains(
                                    event.transactionId
                                )
                            ) {
                                (currentState.transactionWithVisibleItems - event.transactionId)
                                    .toImmutableSet()
                            } else {
                                (currentState.transactionWithVisibleItems + event.transactionId)
                                    .toImmutableSet()
                            }
                    )
                }
            }
        }
    }

    private fun changeScreenDestination(newDestination: HomeDestinations) {
        _uiState.update { currentState -> currentState.copy(currentDestination = newDestination) }
    }

    private fun changeDashboardSpentByTimeChartPeriod(newPeriod: SpendingSummaryPeriod) {
        val nPeriod = TimePeriodFlowHandler.Periods.valueOf(newPeriod.name)
        mTimePeriodFlowHandler.switchPeriod(nPeriod)

        _uiState.update { currentState ->
            currentState.copy(dashboardSpentByTimeChartCurrentPeriod = newPeriod)
        }

        updateDashboardSpentByTimeChartDataCollectJob()
    }

    private fun updateDashboardSpentByTimeChartDataCollectJob() {
        dashboardSpentByTimeChartDataCollectJob?.cancel()
        dashboardSpentByTimeChartDataCollectJob =
            viewModelScope.launch {
                mTimePeriodFlowHandler.spentByTimeData.collect {
                    _uiState.update { currentState ->
                        currentState.copy(dashboardSpentByTimeChartData = it)
                    }
                }
            }
    }

    private fun incrementCurrentAnalysisDate() {
        val localUiState = uiState.value
        val newMonth: Int
        val newYear: Int

        if (localUiState.analysisCurrentDateMonth == 12) {
            newYear = localUiState.analysisCurrentDateYear + 1
            newMonth = 1
        } else {
            newYear = localUiState.analysisCurrentDateYear
            newMonth = localUiState.analysisCurrentDateMonth + 1
        }

        _uiState.update { currentState ->
            currentState.copy(
                analysisCurrentDateYear = newYear,
                analysisCurrentDateMonth = newMonth,
            )
        }

        updateAnalysisDataCollectionJobs()
    }

    private fun decrementCurrentAnalysisDate() {
        val localUiState = uiState.value
        val newMonth: Int
        val newYear: Int

        if (localUiState.analysisCurrentDateMonth == 1) {
            newYear = localUiState.analysisCurrentDateYear - 1
            newMonth = 12
        } else {
            newYear = localUiState.analysisCurrentDateYear
            newMonth = localUiState.analysisCurrentDateMonth - 1
        }

        _uiState.update { currentState ->
            currentState.copy(
                analysisCurrentDateYear = newYear,
                analysisCurrentDateMonth = newMonth,
            )
        }

        updateAnalysisDataCollectionJobs()
    }

    private fun updateAnalysisDataCollectionJobs() {
        val localUiState = uiState.value

        var previousDateYear: Int = localUiState.analysisCurrentDateYear
        var previousDateMonth: Int = localUiState.analysisCurrentDateMonth

        if (previousDateMonth == 1) {
            previousDateYear -= 1
            previousDateMonth = 12
        } else {
            previousDateMonth -= 1
        }

        analysisCurrentDateCategoryDataCollectJob?.cancel()
        analysisCurrentDateCategoryDataCollectJob =
            viewModelScope.launch {
                categoryRepository
                    .totalSpentByCategoryByMonth(
                        localUiState.analysisCurrentDateYear,
                        localUiState.analysisCurrentDateMonth,
                    )
                    .collect {
                        _uiState.update { currentState ->
                            currentState.copy(analysisCurrentDateCategoryData = it)
                        }
                    }
            }

        analysisCurrentDateShopDataCollectJob?.cancel()
        analysisCurrentDateShopDataCollectJob =
            viewModelScope.launch {
                shopRepository
                    .totalSpentByShopByMonth(
                        localUiState.analysisCurrentDateYear,
                        localUiState.analysisCurrentDateMonth,
                    )
                    .collect {
                        _uiState.update { currentState ->
                            currentState.copy(analysisCurrentDateShopData = it)
                        }
                    }
            }

        analysisPreviousDateCategoryDataCollectJob?.cancel()
        analysisPreviousDateCategoryDataCollectJob =
            viewModelScope.launch {
                categoryRepository
                    .totalSpentByCategoryByMonth(previousDateYear, previousDateMonth)
                    .collect {
                        _uiState.update { currentState ->
                            currentState.copy(analysisPreviousDateCategoryData = it)
                        }
                    }
            }

        analysisPreviousDateShopDataCollectJob?.cancel()
        analysisPreviousDateShopDataCollectJob =
            viewModelScope.launch {
                shopRepository
                    .totalSpentByShopByMonth(previousDateYear, previousDateMonth)
                    .collect {
                        _uiState.update { currentState ->
                            currentState.copy(analysisPreviousDateShopData = it)
                        }
                    }
            }
    }
}
