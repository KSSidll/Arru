package com.kssidll.arru.ui.screen.spendingcomparison.shopspendingcomparison

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.TotalSpentByShop
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByShopByMonthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ShopSpendingComparisonUiState(
    val currentSpent: ImmutableList<TotalSpentByShop> = emptyImmutableList(),
    val previousSpent: ImmutableList<TotalSpentByShop> = emptyImmutableList(),
    val title: String = String(),
)

@Immutable
sealed class ShopSpendingComparisonEvent {
    data class SetYear(val year: Int) : ShopSpendingComparisonEvent()

    data class SetMonth(val month: Int) : ShopSpendingComparisonEvent()
}

@HiltViewModel
class ShopSpendingComparisonViewModel
@Inject
constructor(private val getTotalSpentByShopByMonthUseCase: GetTotalSpentByShopByMonthUseCase) :
    ViewModel() {
    private val _uiState = MutableStateFlow(ShopSpendingComparisonUiState())
    val uiState = _uiState.asStateFlow()

    private var year: Int? = null
    private var month: Int? = null

    private var _currentSpentJob: Job? = null
    private var _previousSpentJob: Job? = null

    fun handleEvent(event: ShopSpendingComparisonEvent) {
        when (event) {
            is ShopSpendingComparisonEvent.SetMonth -> {
                month = event.month
                updateData()
            }
            is ShopSpendingComparisonEvent.SetYear -> {
                year = event.year
                updateData()
            }
        }
    }

    private fun updateData() {
        updateCurrentSpentJob()
        updatePreviousSpentJob()
        updateTitle()
    }

    private fun updateCurrentSpentJob() {
        _currentSpentJob?.cancel()
        year?.let { year ->
            month?.let { month ->
                _currentSpentJob =
                    viewModelScope.launch {
                        getTotalSpentByShopByMonthUseCase(year, month).collectLatest {
                            _uiState.update { currentState -> currentState.copy(currentSpent = it) }
                        }
                    }
            }
        }
    }

    private fun updatePreviousSpentJob() {
        _previousSpentJob?.cancel()
        year?.let { year ->
            month?.let { month ->
                _previousSpentJob =
                    viewModelScope.launch {
                        var previousDateYear: Int = year
                        var previousDateMonth: Int = month

                        if (previousDateMonth == 1) {
                            previousDateYear -= 1
                            previousDateMonth = 12
                        } else {
                            previousDateMonth -= 1
                        }

                        getTotalSpentByShopByMonthUseCase(previousDateYear, previousDateMonth)
                            .collectLatest {
                                _uiState.update { currentState ->
                                    currentState.copy(previousSpent = it)
                                }
                            }
                    }
            }
        }
    }

    private fun updateTitle() {
        year?.let { year ->
            month?.let { month ->
                val calendar = Calendar.getInstance()
                calendar.clear()
                calendar.set(Calendar.MONTH, month - 1) // calendar has 0 - 11 month indexes

                val formatter = SimpleDateFormat("LLLL", Locale.getDefault())

                _uiState.update { currentState ->
                    currentState.copy(
                        title =
                            "${formatter.format(calendar.time).replaceFirstChar { it.titlecase() }} $year"
                    )
                }
            }
        }
    }
}
