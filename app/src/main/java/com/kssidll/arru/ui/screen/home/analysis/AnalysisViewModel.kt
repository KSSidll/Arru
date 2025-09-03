package com.kssidll.arru.ui.screen.home.analysis

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.TotalSpentByCategory
import com.kssidll.arru.data.data.TotalSpentByShop
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByProductCategoryByMonthUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByShopByMonthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class AnalysisUiState(
    val currentDateYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val currentDateMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val currentDateCategoryData: ImmutableList<TotalSpentByCategory> = emptyImmutableList(),
    val currentDateShopData: ImmutableList<TotalSpentByShop> = emptyImmutableList(),
    val previousDateCategoryData: ImmutableList<TotalSpentByCategory> = emptyImmutableList(),
    val previousDateShopData: ImmutableList<TotalSpentByShop> = emptyImmutableList(),
) {
    val nothingToDisplayVisible: Boolean =
        currentDateCategoryData.isEmpty() &&
            currentDateShopData.isEmpty() &&
            previousDateCategoryData.isEmpty() &&
            previousDateShopData.isEmpty()
    val categoryCardVisible: Boolean =
        currentDateCategoryData.isNotEmpty() || previousDateCategoryData.isNotEmpty()
    val shopCardVisible: Boolean =
        currentDateShopData.isNotEmpty() || previousDateShopData.isNotEmpty()
}

@Immutable
sealed class AnalysisEvent {
    data object IncrementCurrentDate : AnalysisEvent()

    data object DecrementCurrentDate : AnalysisEvent()

    data class NavigateCategorySpendingComparison(val year: Int, val month: Int) : AnalysisEvent()

    data class NavigateShopSpendingComparison(val year: Int, val month: Int) : AnalysisEvent()
}

@HiltViewModel
class AnalysisViewModel
@Inject
constructor(
    private val getTotalSpentByProductCategoryByMonthUseCase:
        GetTotalSpentByProductCategoryByMonthUseCase,
    private val getTotalSpentByShopByMonthUseCase: GetTotalSpentByShopByMonthUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState = _uiState.asStateFlow()

    private var collectionJob: Job? = null

    init {
        updateDataCollectionJobs()
    }

    fun handleEvent(event: AnalysisEvent) {
        when (event) {
            is AnalysisEvent.IncrementCurrentDate -> incrementCurrentDate()

            is AnalysisEvent.DecrementCurrentDate -> decrementCurrentDate()
            is AnalysisEvent.NavigateCategorySpendingComparison -> {}
            is AnalysisEvent.NavigateShopSpendingComparison -> {}
        }
    }

    private fun incrementCurrentDate() {
        val localUiState = uiState.value
        val newMonth: Int
        val newYear: Int

        if (localUiState.currentDateMonth == 12) {
            newYear = localUiState.currentDateYear + 1
            newMonth = 1
        } else {
            newYear = localUiState.currentDateYear
            newMonth = localUiState.currentDateMonth + 1
        }

        _uiState.update { currentState ->
            currentState.copy(currentDateYear = newYear, currentDateMonth = newMonth)
        }

        updateDataCollectionJobs()
    }

    private fun decrementCurrentDate() {
        val localUiState = uiState.value
        val newMonth: Int
        val newYear: Int

        if (localUiState.currentDateMonth == 1) {
            newYear = localUiState.currentDateYear - 1
            newMonth = 12
        } else {
            newYear = localUiState.currentDateYear
            newMonth = localUiState.currentDateMonth - 1
        }

        _uiState.update { currentState ->
            currentState.copy(currentDateYear = newYear, currentDateMonth = newMonth)
        }

        updateDataCollectionJobs()
    }

    private fun updateDataCollectionJobs() {
        val localUiState = uiState.value

        var previousDateYear: Int = localUiState.currentDateYear
        var previousDateMonth: Int = localUiState.currentDateMonth

        if (previousDateMonth == 1) {
            previousDateYear -= 1
            previousDateMonth = 12
        } else {
            previousDateMonth -= 1
        }

        collectionJob?.cancel()
        collectionJob =
            viewModelScope.launch {
                // current date category
                viewModelScope.launch {
                    getTotalSpentByProductCategoryByMonthUseCase(
                            localUiState.currentDateYear,
                            localUiState.currentDateMonth,
                        )
                        .collectLatest {
                            _uiState.update { currentState ->
                                currentState.copy(currentDateCategoryData = it)
                            }
                        }
                }

                // current date shop
                viewModelScope.launch {
                    getTotalSpentByShopByMonthUseCase(
                            localUiState.currentDateYear,
                            localUiState.currentDateMonth,
                        )
                        .collectLatest {
                            _uiState.update { currentState ->
                                currentState.copy(currentDateShopData = it)
                            }
                        }
                }

                // previous date category
                viewModelScope.launch {
                    getTotalSpentByProductCategoryByMonthUseCase(
                            previousDateYear,
                            previousDateMonth,
                        )
                        .collectLatest {
                            _uiState.update { currentState ->
                                currentState.copy(previousDateCategoryData = it)
                            }
                        }
                }

                // previous date shop
                viewModelScope.launch {
                    getTotalSpentByShopByMonthUseCase(previousDateYear, previousDateMonth)
                        .collectLatest {
                            _uiState.update { currentState ->
                                currentState.copy(previousDateShopData = it)
                            }
                        }
                }
            }
    }
}
