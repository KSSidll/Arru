package com.kssidll.arru.ui.screen.display.product

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import com.kssidll.arru.domain.data.data.ProductPriceByShopByVariantByProducerByTime
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.domain.data.interfaces.avg
import com.kssidll.arru.domain.data.interfaces.median
import com.kssidll.arru.domain.data.interfaces.runMovingAverageChartDataTransaction
import com.kssidll.arru.domain.data.interfaces.runMovingMedianChartDataTransaction
import com.kssidll.arru.domain.data.interfaces.runMovingTotalChartDataTransaction
import com.kssidll.arru.domain.usecase.data.GetAveragePriceByShopByVariantByProducerByDayForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetItemsForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForProductUseCase
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
data class DisplayProductUiState(
    val listState: LazyListState = LazyListState(),
    val chartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val productPriceByTime: ImmutableList<ProductPriceByShopByVariantByProducerByTime> =
        emptyImmutableList(),
    val spentByTime: ImmutableList<ChartSource> = emptyImmutableList(),
    val spentByTimePeriod: SpendingSummaryPeriod = SpendingSummaryPeriod.Month,
    val productName: String = String(),
    val items: Flow<PagingData<Item>> = emptyFlow(),
    val totalChartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val totalSpentValue: Float = 0f,
    val averageChartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val averageSpentValue: Float = 0f,
    val medianChartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val medianSpentValue: Float = 0f,
)

@Immutable
sealed class DisplayProductEvent {
    data object NavigateBack : DisplayProductEvent()

    data class NavigateDisplayProductCategory(val productCategoryId: Long) : DisplayProductEvent()

    data class NavigateEditProductCategory(val productCategoryId: Long) : DisplayProductEvent()

    data class NavigateDisplayProductProducer(val productProducerId: Long) : DisplayProductEvent()

    data class NavigateEditProductProducer(val productProducerId: Long) : DisplayProductEvent()

    data class NavigateDisplayShop(val shopId: Long) : DisplayProductEvent()

    data class NavigateEditShop(val shopId: Long) : DisplayProductEvent()

    data class NavigateEditItem(val itemId: Long) : DisplayProductEvent()

    data object NavigateEditProduct : DisplayProductEvent()

    data class SetSpentByTimePeriod(val newPeriod: SpendingSummaryPeriod) : DisplayProductEvent()
}

@HiltViewModel
class DisplayProductViewModel
@Inject
constructor(
    private val getProductEntityUseCase: GetProductEntityUseCase,
    private val getTotalSpentForProductUseCase: GetTotalSpentForProductUseCase,
    private val getItemsForProductUseCase: GetItemsForProductUseCase,
    private val getTotalSpentByDayForProductUseCase: GetTotalSpentByDayForProductUseCase,
    private val getTotalSpentByWeekForProductUseCase: GetTotalSpentByWeekForProductUseCase,
    private val getTotalSpentByMonthForProductUseCase: GetTotalSpentByMonthForProductUseCase,
    private val getTotalSpentByYearForProductUseCase: GetTotalSpentByYearForProductUseCase,
    private val getAveragePriceByShopByVariantByProducerByDayForProductUseCase:
        GetAveragePriceByShopByVariantByProducerByDayForProductUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DisplayProductUiState())
    val uiState = _uiState.asStateFlow()

    private var job: Job? = null
    private var chartJob: Job? = null

    private var _productId: Long? = null

    suspend fun checkExists(id: Long?) =
        viewModelScope
            .async {
                return@async id?.let { getProductEntityUseCase(it).first() } != null
            }
            .await()

    fun updateState(productId: Long?) =
        viewModelScope.launch {
            if (productId == null || _productId == productId) return@launch
            val product = getProductEntityUseCase(productId).first() ?: return@launch
            _productId = productId

            job?.cancel()
            job =
                viewModelScope.launch {
                    viewModelScope.launch {
                        _uiState.update { currentState ->
                            currentState.copy(
                                productName = product.name,
                                items = getItemsForProductUseCase(productId).cachedIn(this),
                            )
                        }
                    }

                    viewModelScope.launch {
                        getTotalSpentForProductUseCase(productId).collectLatest {
                            _uiState.update { currentState ->
                                currentState.copy(totalSpentValue = it ?: 0f)
                            }
                        }
                    }

                    viewModelScope.launch {
                        getAveragePriceByShopByVariantByProducerByDayForProductUseCase(productId)
                            .collectLatest {
                                _uiState.update { currentState ->
                                    currentState.copy(productPriceByTime = it)
                                }
                            }
                    }
                }

            updateChartJob(_uiState.value.spentByTimePeriod, productId)
        }

    fun handleEvent(event: DisplayProductEvent) {
        when (event) {
            is DisplayProductEvent.NavigateBack -> {}
            is DisplayProductEvent.NavigateDisplayProductCategory -> {}
            is DisplayProductEvent.NavigateEditProductCategory -> {}
            is DisplayProductEvent.NavigateDisplayProductProducer -> {}
            is DisplayProductEvent.NavigateEditProductProducer -> {}
            is DisplayProductEvent.NavigateDisplayShop -> {}
            is DisplayProductEvent.NavigateEditShop -> {}
            is DisplayProductEvent.NavigateEditItem -> {}
            is DisplayProductEvent.NavigateEditProduct -> {}
            is DisplayProductEvent.SetSpentByTimePeriod -> setSpentByTimePeriod(event.newPeriod)
        }
    }

    private fun setSpentByTimePeriod(newPeriod: SpendingSummaryPeriod) {
        _uiState.update { currentState -> currentState.copy(spentByTimePeriod = newPeriod) }

        _productId?.let { updateChartJob(newPeriod, it) }
    }

    private fun updateChartJob(period: SpendingSummaryPeriod, productId: Long) {
        chartJob?.cancel()
        chartJob =
            viewModelScope.launch {
                when (period) {
                    SpendingSummaryPeriod.Day -> {
                        getTotalSpentByDayForProductUseCase(productId).collectLatest {
                            updateChartUiState(it)
                        }
                    }

                    SpendingSummaryPeriod.Week -> {
                        getTotalSpentByWeekForProductUseCase(productId).collectLatest {
                            updateChartUiState(it)
                        }
                    }

                    SpendingSummaryPeriod.Month -> {
                        getTotalSpentByMonthForProductUseCase(productId).collectLatest {
                            updateChartUiState(it)
                        }
                    }

                    SpendingSummaryPeriod.Year -> {
                        getTotalSpentByYearForProductUseCase(productId).collectLatest {
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
