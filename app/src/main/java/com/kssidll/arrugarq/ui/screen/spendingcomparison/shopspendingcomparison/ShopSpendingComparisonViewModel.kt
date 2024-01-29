package com.kssidll.arrugarq.ui.screen.spendingcomparison.shopspendingcomparison

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class ShopSpendingComparisonViewModel @Inject constructor(
    private val shopRepository: ShopRepositorySource
): ViewModel() {

    /**
     * @return List of data points representing category spending in [year] and [month]
     */
    fun shopTotalSpentCurrentMonth(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByShop>> {
        return shopRepository.totalSpentByShopByMonthFlow(
            year,
            month
        )
    }

    /**
     * @return List of data points representing category spending in previous month for [year] and [month]
     */
    fun shopTotalSpentPreviousMonth(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByShop>> {
        var localYear: Int = year
        var localMonth: Int = month

        if (localMonth == 1) {
            localYear -= 1
            localMonth = 12
        } else {
            localMonth -= 1
        }

        return shopRepository.totalSpentByShopByMonthFlow(
            localYear,
            localMonth
        )
    }
}
