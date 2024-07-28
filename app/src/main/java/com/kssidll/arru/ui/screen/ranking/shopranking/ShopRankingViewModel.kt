package com.kssidll.arru.ui.screen.ranking.shopranking

import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.data.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ShopRankingViewModel @Inject constructor(
    private val shopRepository: ShopRepositorySource
): ViewModel() {

    /**
     * @return List of data points representing shop spending in time as flow
     */
    fun shopTotalSpentFlow(): Flow<Data<List<TransactionTotalSpentByShop>>> {
        return shopRepository.totalSpentByShopFlow()
    }
}