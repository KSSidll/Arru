package com.kssidll.arru.ui.screen.display.shop


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionTotalSpentByTime
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopRepository: ShopRepositorySource,
): ViewModel() {
    private val mShop: MutableState<ShopEntity?> = mutableStateOf(null)
    val shop: ShopEntity? by mShop

    private var mShopListener: Job? = null

    val chartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler<ImmutableList<TransactionTotalSpentByTime>>? =
        null
    val spentByTimePeriod: TimePeriodFlowHandler.Periods? get() = mTimePeriodFlowHandler?.currentPeriod
    val spentByTimeData: Flow<ImmutableList<TransactionTotalSpentByTime>>? get() = mTimePeriodFlowHandler?.spentByTimeData

    fun shopTotalSpent(): Flow<Float?>? {
        return shop?.let { shopRepository.totalSpent(it) }
    }

    /**
     * @return paging data of full item for current shop as flow
     */
    fun transactions(): Flow<PagingData<FullItem>> {
        return shop?.let { shopRepository.fullItemsPaged(it) } ?: emptyFlow()
    }

    /**
     * Switches the state period to [newPeriod]
     * @param newPeriod Period to switch the state to
     */
    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        mTimePeriodFlowHandler?.switchPeriod(newPeriod)
    }

    /**
     * @return True if provided [shopId] was valid, false otherwise
     */
    suspend fun performDataUpdate(shopId: Long) = viewModelScope.async {
        val shop = shopRepository.get(shopId).first() ?: return@async false

        // We ignore the possiblity of changing shop while one is already loaded
        // as not doing that would increase complexity too much
        // and if it happens somehow, it would be considered a bug
        if (mShop.value != null || shopId == mShop.value?.id) return@async true

        mShopListener?.cancel()
        mShopListener = viewModelScope.launch {
            shopRepository.get(shopId)
                .collectLatest {
                    mShop.value = it
                }
        }

        mShop.value = shop

        mTimePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            day = {
                shopRepository.totalSpentByDay(shop)
            },
            week = {
                shopRepository.totalSpentByWeek(shop)
            },
            month = {
                shopRepository.totalSpentByMonth(shop)
            },
            year = {
                shopRepository.totalSpentByYear(shop)
            },
        )

        return@async true
    }
        .await()
}
