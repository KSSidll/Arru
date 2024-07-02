package com.kssidll.arru.ui.screen.spendingcomparison.shopspendingcomparison

import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.data.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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
