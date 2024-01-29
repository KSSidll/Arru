package com.kssidll.arrugarq.ui.screen.ranking.shopranking

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class ShopRankingViewModel @Inject constructor(
    private val shopRepository: ShopRepositorySource
): ViewModel() {

    /**
     * @return List of data points representing shop spending in time as flow
     */
    fun shopTotalSpentFlow(): Flow<List<ItemSpentByShop>> {
        return shopRepository.totalSpentByShopFlow()
    }
}