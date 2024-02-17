package com.kssidll.arru.ui.screen.spendingcomparison.categoryspendingcomparison

import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class CategorySpendingComparisonViewModel @Inject constructor(
    private val categoryRepository: CategoryRepositorySource
): ViewModel() {

    /**
     * @return List of data points representing category spending in [year] and [month]
     */
    fun categoryTotalSpentCurrentMonth(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByCategory>> {
        return categoryRepository.totalSpentByCategoryByMonthFlow(
            year,
            month
        )
    }

    /**
     * @return List of data points representing category spending in previous month for [year] and [month]
     */
    fun categoryTotalSpentPreviousMonth(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByCategory>> {
        var localYear: Int = year
        var localMonth: Int = month

        if (localMonth == 1) {
            localYear -= 1
            localMonth = 12
        } else {
            localMonth -= 1
        }

        return categoryRepository.totalSpentByCategoryByMonthFlow(
            localYear,
            localMonth
        )
    }
}