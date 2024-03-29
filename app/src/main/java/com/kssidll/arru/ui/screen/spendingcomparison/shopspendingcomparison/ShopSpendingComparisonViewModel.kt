package com.kssidll.arru.ui.screen.spendingcomparison.shopspendingcomparison

import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.domain.data.*
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
    ): Flow<Data<List<TransactionTotalSpentByShop>>> {
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
    ): Flow<Data<List<TransactionTotalSpentByShop>>> {
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
