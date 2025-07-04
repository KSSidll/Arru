package com.kssidll.arru.ui.screen.ranking.shopranking

import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.repository.ShopRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ShopRankingViewModel @Inject constructor(
    private val shopRepository: ShopRepositorySource
): ViewModel() {

    /**
     * @return List of data points representing shop spending in time as flow
     */
    fun shopTotalSpentFlow(): Flow<ImmutableList<TransactionTotalSpentByShop>> {
        return shopRepository.totalSpentByShopFlow()
    }
}