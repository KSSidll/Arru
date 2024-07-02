package com.kssidll.arru.ui.screen.display.shop


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionTotalSpentByTime
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.kssidll.arru.domain.data.Data
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopRepository: ShopRepositorySource,
): ViewModel() {
    private val mShop: MutableState<Shop?> = mutableStateOf(null)
    val shop: Shop? by mShop

    private var mShopListener: Job? = null

    val chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler<Data<List<TransactionTotalSpentByTime>>>? =
        null
    val spentByTimePeriod: TimePeriodFlowHandler.Periods? get() = mTimePeriodFlowHandler?.currentPeriod
    val spentByTimeData: Flow<Data<List<TransactionTotalSpentByTime>>>? get() = mTimePeriodFlowHandler?.spentByTimeData

    fun shopTotalSpent(): Flow<Data<Float?>>? {
        if (shop == null) return null

        return shopRepository.totalSpentFlow(shop!!)
    }

    /**
     * @return paging data of full item for current shop as flow
     */
    fun transactions(): Flow<PagingData<Item>> {
        if (shop == null) return emptyFlow()
        return shopRepository.fullItemsPagedFlow(shop!!)
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
        val shop = shopRepository.get(shopId) ?: return@async false

        // We ignore the possiblity of changing shop while one is already loaded
        // as not doing that would increase complexity too much
        // and if it happens somehow, it would be considered a bug
        if (mShop.value != null || shopId == mShop.value?.id) return@async true

        mShopListener?.cancel()
        mShopListener = viewModelScope.launch {
            shopRepository.getFlow(shopId)
                .collectLatest {
                    if (it is Data.Loaded) {
                        mShop.value = it.data
                    } else {
                        mShop.value = null
                    }
                }
        }

        mShop.value = shop

        mTimePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            dayFlow = {
                shopRepository.totalSpentByDayFlow(shop)
            },
            weekFlow = {
                shopRepository.totalSpentByWeekFlow(shop)
            },
            monthFlow = {
                shopRepository.totalSpentByMonthFlow(shop)
            },
            yearFlow = {
                shopRepository.totalSpentByYearFlow(shop)
            },
        )

        return@async true
    }
        .await()
}
