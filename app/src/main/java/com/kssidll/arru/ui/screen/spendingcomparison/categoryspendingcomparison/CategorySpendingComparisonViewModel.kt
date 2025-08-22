package com.kssidll.arru.ui.screen.spendingcomparison.categoryspendingcomparison

import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.data.TotalSpentByCategory
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

// TODO refactor uiState Event UseCase

@HiltViewModel
class CategorySpendingComparisonViewModel
@Inject
constructor(private val categoryRepository: ProductCategoryRepositorySource) : ViewModel() {

    /** @return List of data points representing category spending in [year] and [month] */
    fun categoryTotalSpentCurrentMonth(
        year: Int,
        month: Int,
    ): Flow<ImmutableList<TotalSpentByCategory>> {
        return categoryRepository.totalSpentByCategoryByMonth(year, month)
    }

    /**
     * @return List of data points representing category spending in previous month for [year] and
     *   [month]
     */
    fun categoryTotalSpentPreviousMonth(
        year: Int,
        month: Int,
    ): Flow<ImmutableList<TotalSpentByCategory>> {
        var localYear: Int = year
        var localMonth: Int = month

        if (localMonth == 1) {
            localYear -= 1
            localMonth = 12
        } else {
            localMonth -= 1
        }

        return categoryRepository.totalSpentByCategoryByMonth(localYear, localMonth)
    }
}
