package com.kssidll.arrugarq.ui.screen.home.dashboard


import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val itemRepository: ItemRepositorySource,
): ViewModel() {
    private val mTimePeriodFlowHandler: TimePeriodFlowHandler = TimePeriodFlowHandler(
        scope = viewModelScope,
        dayFlow = {
            itemRepository.getTotalSpentByDayFlow()
        },
        weekFlow = {
            itemRepository.getTotalSpentByWeekFlow()
        },
        monthFlow = {
            itemRepository.getTotalSpentByMonthFlow()
        },
        yearFlow = {
            itemRepository.getTotalSpentByYearFlow()
        },
    )

    /**
     * List of items representing [Item] spending in time as flow
     */
    val spentByTimeData get() = mTimePeriodFlowHandler.spentByTimeData

    /**
     * Currently set spending period
     */
    val spentByTimePeriod get() = mTimePeriodFlowHandler.currentPeriod

    /**
     * Switches the spending period to [newPeriod]
     * @param newPeriod Period to switch the state to
     */
    fun switchToSpentByTimePeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        mTimePeriodFlowHandler.switchPeriod(newPeriod)
    }

    /**
     * @return Number representing total [Item] spending as flow
     */
    fun getTotalSpent(): Flow<Float> {
        return itemRepository.getTotalSpentFlow()
            .map {
                it.toFloat()
                    .div(Item.PRICE_DIVISOR * Item.QUANTITY_DIVISOR)
            }
            .distinctUntilChanged()
    }

    /**
     * @return List of items representing [Shop] spending in time as flow
     */
    fun getSpentByShop(): Flow<List<ItemSpentByShop>> {
        return itemRepository.getShopTotalSpentFlow()
            .distinctUntilChanged()
    }

    /**
     * @return List of items representing [ProductCategory] spending in time as flow
     */
    fun getSpentByCategory(): Flow<List<ItemSpentByCategory>> {
        return itemRepository.getCategoryTotalSpentFlow()
            .distinctUntilChanged()
    }
}
