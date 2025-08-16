package com.kssidll.arru.ui.screen.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.data.data.TransactionSpentByTime
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionRepositorySource
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@Stable
data class HomeUiState(
    val totalSpent: Float = 0f,

    val dashboardSpentByTimeChartData: ImmutableList<TransactionSpentByTime> = persistentListOf(),
    val dashboardSpentByTimeChartCurrentPeriod: TimePeriodFlowHandler.Periods = TimePeriodFlowHandler.Periods.Month,
    val dashboardCategorySpendingRankingData: ImmutableList<ItemSpentByCategory> = persistentListOf(),
    val dashboardShopSpendingRankingData: ImmutableList<TransactionTotalSpentByShop> = persistentListOf(),
    val dashboardTotalChartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val dashboardAverageChartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val dashboardMedianChartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),

    val analysisCurrentDateYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val analysisCurrentDateMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val analysisCurrentDateCategoryData: ImmutableList<ItemSpentByCategory> = persistentListOf(),
    val analysisCurrentDateShopData: ImmutableList<TransactionTotalSpentByShop> = persistentListOf(),
    val analysisPreviousDateCategoryData: ImmutableList<ItemSpentByCategory> = persistentListOf(),
    val analysisPreviousDateShopData: ImmutableList<TransactionTotalSpentByShop> = persistentListOf(),

    val transactions: Flow<PagingData<TransactionBasketDisplayData>> = flowOf(),

    val dashboardScrollState: ScrollState = ScrollState(0),
    val transactionsListState: LazyListState = LazyListState(),
    val currentDestination: HomeDestinations = HomeDestinations.DEFAULT
) {
    val dashboardScreenNothingToDisplayVisible: Boolean =
        dashboardSpentByTimeChartData.isEmpty() && dashboardCategorySpendingRankingData.isEmpty() && dashboardShopSpendingRankingData.isEmpty()
    val dashboardChartSectionVisible: Boolean = dashboardSpentByTimeChartData.isNotEmpty()
    val dashboardCategoryCardVisible: Boolean = dashboardCategorySpendingRankingData.isNotEmpty()
    val dashboardShopCardVisible: Boolean = dashboardShopSpendingRankingData.isNotEmpty()

    val analysisScreenNothingToDisplayVisible: Boolean =
        analysisCurrentDateCategoryData.isEmpty() && analysisCurrentDateShopData.isEmpty() && analysisPreviousDateCategoryData.isEmpty() && analysisPreviousDateShopData.isEmpty()
    val analysisScreenCategoryCardVisible: Boolean =
        analysisCurrentDateCategoryData.isNotEmpty() || analysisPreviousDateCategoryData.isNotEmpty()
    val analysisScreenShopCardVisible: Boolean =
        analysisCurrentDateShopData.isNotEmpty() || analysisPreviousDateShopData.isNotEmpty()
}

@Immutable
sealed class HomeEvent {
    data class ChangeScreenDestination(val newDestination: HomeDestinations): HomeEvent()
    data class ChangeDashboardSpentByTimeChartPeriod(val newPeriod: TimePeriodFlowHandler.Periods):
        HomeEvent()

    data object IncrementCurrentAnalysisDate: HomeEvent()
    data object DecrementCurrentAnalysisDate: HomeEvent()

    data object NavigateSettings: HomeEvent()
    data object NavigateSearch: HomeEvent()
    data class NavigateProduct(val productId: Long): HomeEvent()
    data class NavigateCategory(val categoryId: Long): HomeEvent()
    data class NavigateProducer(val producerId: Long): HomeEvent()
    data class NavigateShop(val shopId: Long): HomeEvent()
    data object NavigateTransactionAdd: HomeEvent()
    data class NavigateTransactionEdit(val transactionId: Long): HomeEvent()
    data class NavigateItemAdd(val transactionId: Long): HomeEvent()
    data class NavigateItemEdit(val itemId: Long): HomeEvent()
    data object NavigateCategoryRanking: HomeEvent()
    data object NavigateShopRanking: HomeEvent()
    data class NavigateCategorySpendingComparison(val year: Int, val month: Int): HomeEvent()
    data class NavigateShopSpendingComparison(val year: Int, val month: Int): HomeEvent()
}

@Stable
data class TransactionBasketDisplayData(
    val basket: TransactionBasketWithItems,
    var itemsVisible: MutableState<Boolean> = mutableStateOf(false)
)

fun Flow<PagingData<TransactionBasketWithItems>>.toDisplayData(): Flow<PagingData<TransactionBasketDisplayData>> {
    return map { pagingData -> pagingData.map { TransactionBasketDisplayData(it) } }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepositorySource,
    private val categoryRepository: ProductCategoryRepositorySource,
    private val shopRepository: ShopRepositorySource,
): ViewModel() {
    private val _uiState = MutableStateFlow(
        HomeUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val mTimePeriodFlowHandler: TimePeriodFlowHandler<List<TransactionSpentByTime>> =
        TimePeriodFlowHandler(
            scope = viewModelScope,
            day = {
                transactionRepository.totalSpentByDay()
            },
            week = {
                transactionRepository.totalSpentByWeek()
            },
            month = {
                transactionRepository.totalSpentByMonth()
            },
            year = {
                transactionRepository.totalSpentByYear()
            },
        )
    private var dashboardSpentByTimeChartDataCollectJob: Job? = null
    private var analysisCurrentDateCategoryDataCollectJob: Job? = null
    private var analysisCurrentDateShopDataCollectJob: Job? = null
    private var analysisPreviousDateCategoryDataCollectJob: Job? = null
    private var analysisPreviousDateShopDataCollectJob: Job? = null

    init {
        _uiState.update { currentState ->
            currentState.copy(
                transactions = transactionRepository.transactionBasketsPaged()
                    .toDisplayData()
                    .cachedIn(viewModelScope)
            )
        }

        viewModelScope.launch {
            transactionRepository.totalSpent().collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        totalSpent = it ?: 0f,
                    )
                }
            }
        }

        viewModelScope.launch {
            categoryRepository.totalSpentByCategory()
                .collect {
                    _uiState.update { currentState ->
                        currentState.copy(
                            dashboardCategorySpendingRankingData = it
                        )
                    }
                }
        }

        viewModelScope.launch {
            shopRepository.totalSpentByShop().collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        dashboardShopSpendingRankingData = it
                    )
                }
            }
        }

        updateDashboardSpentByTimeChartDataCollectJob()
        updateAnalysisDataCollectionJobs()
    }

    fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ChangeScreenDestination -> {
                changeScreenDestination(event.newDestination)
            }

            is HomeEvent.ChangeDashboardSpentByTimeChartPeriod -> {
                changeDashboardSpentByTimeChartPeriod(event.newPeriod)
            }

            is HomeEvent.IncrementCurrentAnalysisDate -> {
                incrementCurrentAnalysisDate()
            }

            is HomeEvent.DecrementCurrentAnalysisDate -> {
                decrementCurrentAnalysisDate()
            }

            is HomeEvent.NavigateSettings -> {}

            is HomeEvent.NavigateSearch -> {}

            is HomeEvent.NavigateProduct -> {}

            is HomeEvent.NavigateCategory -> {}

            is HomeEvent.NavigateProducer -> {}

            is HomeEvent.NavigateShop -> {}

            is HomeEvent.NavigateItemAdd -> {}

            is HomeEvent.NavigateItemEdit -> {}

            is HomeEvent.NavigateTransactionAdd -> {}

            is HomeEvent.NavigateTransactionEdit -> {}

            is HomeEvent.NavigateCategoryRanking -> {}

            is HomeEvent.NavigateShopRanking -> {}

            is HomeEvent.NavigateCategorySpendingComparison -> {}

            is HomeEvent.NavigateShopSpendingComparison -> {}
        }
    }

    private fun changeScreenDestination(newDestination: HomeDestinations) {
        _uiState.update { currentState ->
            currentState.copy(
                currentDestination = newDestination
            )
        }
    }

    private fun changeDashboardSpentByTimeChartPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        mTimePeriodFlowHandler.switchPeriod(newPeriod)

        _uiState.update { currentState ->
            currentState.copy(
                dashboardSpentByTimeChartCurrentPeriod = newPeriod
            )
        }

        updateDashboardSpentByTimeChartDataCollectJob()
    }

    private fun updateDashboardSpentByTimeChartDataCollectJob() {
        dashboardSpentByTimeChartDataCollectJob?.cancel()
        dashboardSpentByTimeChartDataCollectJob = viewModelScope.launch {
            mTimePeriodFlowHandler.spentByTimeData.collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        dashboardSpentByTimeChartData = it.toImmutableList()
                    )
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
                analysisCurrentDateMonth = newMonth
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
                analysisCurrentDateMonth = newMonth
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
        analysisCurrentDateCategoryDataCollectJob = viewModelScope.launch {
            categoryRepository.totalSpentByCategoryByMonth(
                localUiState.analysisCurrentDateYear,
                localUiState.analysisCurrentDateMonth
            ).collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        analysisCurrentDateCategoryData = it
                    )
                }
            }
        }

        analysisCurrentDateShopDataCollectJob?.cancel()
        analysisCurrentDateShopDataCollectJob = viewModelScope.launch {
            shopRepository.totalSpentByShopByMonth(
                localUiState.analysisCurrentDateYear,
                localUiState.analysisCurrentDateMonth
            ).collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        analysisCurrentDateShopData = it
                    )
                }
            }
        }

        analysisPreviousDateCategoryDataCollectJob?.cancel()
        analysisPreviousDateCategoryDataCollectJob = viewModelScope.launch {
            categoryRepository.totalSpentByCategoryByMonth(previousDateYear, previousDateMonth)
                .collect {
                    _uiState.update { currentState ->
                        currentState.copy(
                            analysisPreviousDateCategoryData = it
                        )
                    }
                }
        }

        analysisPreviousDateShopDataCollectJob?.cancel()
        analysisPreviousDateShopDataCollectJob = viewModelScope.launch {
            shopRepository.totalSpentByShopByMonth(previousDateYear, previousDateMonth)
                .collect {
                    _uiState.update { currentState ->
                        currentState.copy(
                            analysisPreviousDateShopData = it
                        )
                    }
                }
        }
    }
}