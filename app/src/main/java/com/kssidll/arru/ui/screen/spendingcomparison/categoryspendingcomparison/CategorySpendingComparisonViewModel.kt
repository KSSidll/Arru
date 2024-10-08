package com.kssidll.arru.ui.screen.spendingcomparison.categoryspendingcomparison

import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.repository.CategoryRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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