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
        data object Test: DisplayCategoryEvent()
}

@HiltViewModel
class DisplayCategoryViewModel @Inject constructor(
    private val getProductCategoryEntityUseCase: GetProductCategoryEntityUseCase,
    private val getTotalSpentForProductCategoryUseCase: GetTotalSpentForProductCategoryUseCase,
    private val getItemsForProductCategoryUseCase: GetItemsForProductCategoryUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(DisplayCategoryUiState())
    val uiState = _uiState.asStateFlow()

    private var job: Job? = null

    /**
     * @return true if provided [categoryId] was valid, false otherwise
     */
    suspend fun performDataUpdate(categoryId: Long) = viewModelScope.async {
        val category = getProductCategoryEntityUseCase(categoryId).first() ?: return@async false

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

        return@async true
    }.await()
}
