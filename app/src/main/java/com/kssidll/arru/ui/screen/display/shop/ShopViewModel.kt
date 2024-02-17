package com.kssidll.arru.ui.screen.display.shop


import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.paging.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.domain.*
import com.kssidll.arru.domain.data.*
import com.patrykandpatrick.vico.core.entry.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopRepository: ShopRepositorySource,
): ViewModel() {
    private val mShop: MutableState<Shop?> = mutableStateOf(null)
    val shop: Shop? by mShop

    private var mShopListener: Job? = null

    val chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler? = null
    val spentByTimePeriod: TimePeriodFlowHandler.Periods? get() = mTimePeriodFlowHandler?.currentPeriod
    val spentByTimeData: Flow<List<ChartSource>>? get() = mTimePeriodFlowHandler?.spentByTimeData

    fun shopTotalSpent(): Flow<Float>? {
        if (shop == null) return null

        return shopRepository.totalSpentFlow(shop!!)
            .map {
                it.toFloat()
                    .div(TransactionBasket.COST_DIVISOR)
            }
    }

    /**
     * @return paging data of full item for current shop as flow
     */
    fun transactions(): Flow<PagingData<FullItem>> {
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
                    mShop.value = it
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
