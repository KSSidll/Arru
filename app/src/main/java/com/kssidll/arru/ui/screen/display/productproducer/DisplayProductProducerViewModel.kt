package com.kssidll.arru.ui.screen.display.productproducer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.domain.usecase.data.GetItemsForProductProducerUseCase
import com.kssidll.arru.domain.usecase.data.GetProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForProductProducerUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForProductProducerUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForProductProducerUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForProductProducerUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForProductProducerUseCase
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
data class DisplayProductProducerUiState(
    val listState: LazyListState = LazyListState(),
    val chartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val spentByTime: ImmutableList<ChartSource> = emptyImmutableList(),
    val spentByTimePeriod: SpendingSummaryPeriod = SpendingSummaryPeriod.Month,
    val productProducerName: String = String(),
    val totalSpent: Float = 0f,
    val items: Flow<PagingData<Item>> = emptyFlow(),
)

@Immutable
sealed class DisplayProductProducerEvent {
    data object NavigateBack : DisplayProductProducerEvent()

    data object NavigateEditProductProducer : DisplayProductProducerEvent()

    data class NavigateDisplayProduct(val productId: Long) : DisplayProductProducerEvent()

    data class NavigateEditItem(val itemId: Long) : DisplayProductProducerEvent()

    data class NavigateDisplayProductCategory(val productCategoryId: Long) :
        DisplayProductProducerEvent()

    data class NavigateEditProductCategory(val productCategoryId: Long) :
        DisplayProductProducerEvent()

    data class NavigateDisplayShop(val shopId: Long) : DisplayProductProducerEvent()

    data class NavigateEditShop(val shopId: Long) : DisplayProductProducerEvent()

    data class SetSpentByTimePeriod(val newPeriod: SpendingSummaryPeriod) :
        DisplayProductProducerEvent()
}

@HiltViewModel
class DisplayProductProducerViewModel
@Inject
constructor(
    private val getProductProducerEntityUseCase: GetProductProducerEntityUseCase,
    private val getItemsForProductProducerUseCase: GetItemsForProductProducerUseCase,
    private val getTotalSpentForProductProducerUseCase: GetTotalSpentForProductProducerUseCase,
    private val getTotalSpentByDayForProductProducerUseCase:
        GetTotalSpentByDayForProductProducerUseCase,
    private val getTotalSpentByWeekForProductProducerUseCase:
        GetTotalSpentByWeekForProductProducerUseCase,
    private val getTotalSpentByMonthForProductProducerUseCase:
        GetTotalSpentByMonthForProductProducerUseCase,
    private val getTotalSpentByYearForProductProducerUseCase:
        GetTotalSpentByYearForProductProducerUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DisplayProductProducerUiState())
    val uiState = _uiState.asStateFlow()

    private var job: Job? = null
    private var chartJob: Job? = null

    private var _productProducerId: Long? = null

    suspend fun checkExists(id: Long?) =
        viewModelScope
            .async {
                return@async id?.let { getProductProducerEntityUseCase(it).first() } != null
            }
            .await()

    fun updateState(productProducerId: Long?) =
        viewModelScope.launch {
            if (productProducerId == null || _productProducerId == productProducerId) return@launch
            val productProducer =
                getProductProducerEntityUseCase(productProducerId).first() ?: return@launch
            _productProducerId = productProducer.id

            job?.cancel()
            job =
                viewModelScope.launch {
                    viewModelScope.launch {
                        _uiState.update { currentState ->
                            currentState.copy(
                                productProducerName = productProducer.name,
                                items =
                                    getItemsForProductProducerUseCase(productProducerId)
                                        .cachedIn(this),
                            )
                        }
                    }

                    viewModelScope.launch {
                        getTotalSpentForProductProducerUseCase(productProducerId).collectLatest {
                            _uiState.update { currentState ->
                                currentState.copy(totalSpent = it ?: 0f)
                            }
                        }
                    }
                }

            updateChartJob(_uiState.value.spentByTimePeriod, productProducerId)
        }

    fun handleEvent(event: DisplayProductProducerEvent) {
        when (event) {
            is DisplayProductProducerEvent.NavigateBack -> {}
            is DisplayProductProducerEvent.NavigateDisplayProduct -> {}
            is DisplayProductProducerEvent.NavigateDisplayProductCategory -> {}
            is DisplayProductProducerEvent.NavigateEditProductCategory -> {}
            is DisplayProductProducerEvent.NavigateDisplayShop -> {}
            is DisplayProductProducerEvent.NavigateEditShop -> {}
            is DisplayProductProducerEvent.NavigateEditItem -> {}
            is DisplayProductProducerEvent.NavigateEditProductProducer -> {}
            is DisplayProductProducerEvent.SetSpentByTimePeriod ->
                setSpentByTimePeriod(event.newPeriod)
        }
    }

    private fun setSpentByTimePeriod(newPeriod: SpendingSummaryPeriod) {
        _uiState.update { currentState -> currentState.copy(spentByTimePeriod = newPeriod) }

        _productProducerId?.let { updateChartJob(newPeriod, it) }
    }

    private fun updateChartJob(period: SpendingSummaryPeriod, productId: Long) {
        chartJob?.cancel()
        chartJob =
            viewModelScope.launch {
                when (period) {
                    SpendingSummaryPeriod.Day -> {
                        getTotalSpentByDayForProductProducerUseCase(productId).collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }

                    SpendingSummaryPeriod.Week -> {
                        getTotalSpentByWeekForProductProducerUseCase(productId).collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }

                    SpendingSummaryPeriod.Month -> {
                        getTotalSpentByMonthForProductProducerUseCase(productId).collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }

                    SpendingSummaryPeriod.Year -> {
                        getTotalSpentByYearForProductProducerUseCase(productId).collectLatest {
                            _uiState.update { currentState -> currentState.copy(spentByTime = it) }
                        }
                    }
                }
            }
    }
}
