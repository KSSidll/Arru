package com.kssidll.arru.ui.screen.display.shop

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.domain.usecase.data.GetItemsForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForShopUseCase
import com.kssidll.arru.ui.component.SpendingSummaryPeriod
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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

@Immutable
data class DisplayShopUiState(
    val chartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val spentByTime: ImmutableList<ChartSource> = emptyImmutableList(),
    val spentByTimePeriod: SpendingSummaryPeriod = SpendingSummaryPeriod.Month,
    val shopName: String = String(),
    val totalSpent: Float = 0f,
    val items: Flow<PagingData<Item>> = emptyFlow(),
)

@Immutable
sealed class DisplayShopEvent {
    data object NavigateBack : DisplayShopEvent()

    data class NavigateDisplayProduct(val productId: Long) : DisplayShopEvent()

    data class NavigateDisplayProductCategory(val productCategoryId: Long) : DisplayShopEvent()

    data class NavigateDisplayProductProducer(val productProducerId: Long) : DisplayShopEvent()

    data class NavigateEditItem(val itemId: Long) : DisplayShopEvent()

    data object NavigateEditShop : DisplayShopEvent()

    data class SetSpentByTimePeriod(val newPeriod: SpendingSummaryPeriod) : DisplayShopEvent()
}

@HiltViewModel
class DisplayShopViewModel
@Inject
constructor(
    private val getShopEntityUseCase: GetShopEntityUseCase,
    private val getItemsForShopUseCase: GetItemsForShopUseCase,
    private val getTotalSpentForShopUseCase: GetTotalSpentForShopUseCase,
    private val getTotalSpentByDayForShopUseCase: GetTotalSpentByDayForShopUseCase,
    private val getTotalSpentByWeekForShopUseCase: GetTotalSpentByWeekForShopUseCase,
    private val getTotalSpentByMonthForShopUseCase: GetTotalSpentByMonthForShopUseCase,
    private val getTotalSpentByYearForShopUseCase: GetTotalSpentByYearForShopUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DisplayShopUiState())
    val uiState = _uiState.asStateFlow()

    private var job: Job? = null
    private var chartJob: Job? = null

    private var _shopId: Long? = null

    suspend fun checkExists(id: Long) =
        viewModelScope
            .async {
                return@async getShopEntityUseCase(id).first() != null
            }
            .await()

    fun updateState(shopId: Long) =
        viewModelScope.launch {
            val shop = getShopEntityUseCase(shopId).first() ?: return@launch
            _shopId = shop.id

            job?.cancel()
            job =
                viewModelScope.launch {
                    _uiState.update { currentState ->
                        currentState.copy(
                            shopName = shop.name,
                            items = getItemsForShopUseCase(shopId),
                        )
                    }
                }

            viewModelScope.launch {
                getTotalSpentForShopUseCase(shopId).collectLatest {
                    _uiState.update { currentState -> currentState.copy(totalSpent = it ?: 0f) }
                }
            }

            updateChartJob(_uiState.value.spentByTimePeriod, shopId)
        }

    fun handleEvent(event: DisplayShopEvent) {
        when (event) {
            is DisplayShopEvent.NavigateBack -> {}
            is DisplayShopEvent.NavigateDisplayProduct -> {}
            is DisplayShopEvent.NavigateDisplayProductCategory -> {}
            is DisplayShopEvent.NavigateDisplayProductProducer -> {}
            is DisplayShopEvent.NavigateEditItem -> {}
            is DisplayShopEvent.NavigateEditShop -> {}
            is DisplayShopEvent.SetSpentByTimePeriod -> setSpentByTimePeriod((event.newPeriod))
        }
    }

    private fun setSpentByTimePeriod(newPeriod: SpendingSummaryPeriod) {
        _uiState.update { currentState -> currentState.copy(spentByTimePeriod = newPeriod) }

        _shopId?.let { updateChartJob(newPeriod, it) }
    }

    private fun updateChartJob(period: SpendingSummaryPeriod, productId: Long) {
        chartJob?.cancel()
        chartJob =
            viewModelScope.launch {
                when (period) {
                    SpendingSummaryPeriod.Day -> {
                        getTotalSpentByDayForShopUseCase(productId).collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }

                    SpendingSummaryPeriod.Week -> {
                        getTotalSpentByWeekForShopUseCase(productId).collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }

                    SpendingSummaryPeriod.Month -> {
                        getTotalSpentByMonthForShopUseCase(productId).collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }

                    SpendingSummaryPeriod.Year -> {
                        getTotalSpentByYearForShopUseCase(productId).collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }
                }
            }
    }
}
