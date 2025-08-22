package com.kssidll.arru.ui.screen.spendingcomparison.shopspendingcomparison

import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.data.TotalSpentByShop
import com.kssidll.arru.data.repository.ShopRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

// TODO refactor uiState Event UseCase

@HiltViewModel
class ShopSpendingComparisonViewModel
@Inject
constructor(private val shopRepository: ShopRepositorySource) : ViewModel() {

    /** @return List of data points representing category spending in [year] and [month] */
    fun shopTotalSpentCurrentMonth(year: Int, month: Int): Flow<ImmutableList<TotalSpentByShop>> {
        return shopRepository.totalSpentByShopByMonth(year, month)
    }

    /**
     * @return List of data points representing category spending in previous month for [year] and
     *   [month]
     */
    fun shopTotalSpentPreviousMonth(year: Int, month: Int): Flow<ImmutableList<TotalSpentByShop>> {
        var localYear: Int = year
        var localMonth: Int = month

        if (localMonth == 1) {
            localYear -= 1
            localMonth = 12
        } else {
            localMonth -= 1
        }

        return shopRepository.totalSpentByShopByMonth(localYear, localMonth)
    }
}
