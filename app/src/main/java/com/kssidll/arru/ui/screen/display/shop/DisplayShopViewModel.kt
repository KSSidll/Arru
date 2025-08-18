package com.kssidll.arru.ui.screen.display.shop

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.domain.usecase.data.GetItemsForShopUseCase
import com.kssidll.arru.ui.component.SpendingSummaryPeriod
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class DisplayShopViewModel
@Inject
constructor(
    private val shopRepository: ShopRepositorySource,
    private val getItemsForShopUseCase: GetItemsForShopUseCase,
) : ViewModel() {
    private val mShop: MutableState<ShopEntity?> = mutableStateOf(null)
    val shop: ShopEntity? by mShop

    private var mShopListener: Job? = null

    val chartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler<ImmutableList<ChartSource>>? = null
    val spentByTimePeriod: SpendingSummaryPeriod?
        get() =
            mTimePeriodFlowHandler?.currentPeriod?.let { SpendingSummaryPeriod.valueOf(it.name) }

    val spentByTimeData: Flow<ImmutableList<ChartSource>>?
        get() = mTimePeriodFlowHandler?.spentByTimeData

    fun shopTotalSpent(): Flow<Float?>? {
        return shop?.let { shopRepository.totalSpent(it.id) }
    }

    /** @return paging data of full item for current shop as flow */
    fun transactions(): Flow<PagingData<Item>> {
        return shop?.let { getItemsForShopUseCase(it.id) } ?: emptyFlow()
    }

    /**
     * Switches the state period to [newPeriod]
     *
     * @param newPeriod Period to switch the state to
     */
    fun switchPeriod(newPeriod: SpendingSummaryPeriod) {
        val nPeriod = TimePeriodFlowHandler.Periods.valueOf(newPeriod.name)
        mTimePeriodFlowHandler?.switchPeriod(nPeriod)
    }

    /** @return True if provided [shopId] was valid, false otherwise */
    suspend fun performDataUpdate(shopId: Long) =
        viewModelScope
            .async {
                val shop = shopRepository.get(shopId).first() ?: return@async false

                // We ignore the possiblity of changing shop while one is already loaded
                // as not doing that would increase complexity too much
                // and if it happens somehow, it would be considered a bug
                if (mShop.value != null || shopId == mShop.value?.id) return@async true

                mShopListener?.cancel()
                mShopListener =
                    viewModelScope.launch {
                        shopRepository.get(shopId).collectLatest { mShop.value = it }
                    }

                mShop.value = shop

                mTimePeriodFlowHandler =
                    TimePeriodFlowHandler(
                        scope = viewModelScope,
                        day = { shopRepository.totalSpentByDay(shopId) },
                        week = { shopRepository.totalSpentByWeek(shopId) },
                        month = { shopRepository.totalSpentByMonth(shopId) },
                        year = { shopRepository.totalSpentByYear(shopId) },
                    )

                return@async true
            }
            .await()
}
