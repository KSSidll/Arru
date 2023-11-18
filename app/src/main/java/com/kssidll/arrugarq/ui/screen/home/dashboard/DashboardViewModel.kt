package com.kssidll.arrugarq.ui.screen.home.dashboard


import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
): ViewModel() {
    private val timePeriodFlowHandler: TimePeriodFlowHandler = TimePeriodFlowHandler(
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

    val spentByTimeData get() = timePeriodFlowHandler.spentByTimeData
    val spentByTimePeriod get() = timePeriodFlowHandler.currentPeriod

    fun switchToSpentByTimePeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        timePeriodFlowHandler.switchPeriod(newPeriod)
    }

    fun getTotalSpent(): Flow<Float> {
        return itemRepository.getTotalSpentFlow()
            .map {
                it.div(100000F)
            }
            .distinctUntilChanged()
    }

    fun getSpentByShop(): Flow<List<ItemSpentByShop>> {
        return itemRepository.getShopTotalSpentFlow()
    }

    fun getSpentByCategory(): Flow<List<ItemSpentByCategory>> {
        return itemRepository.getCategoryTotalSpentFlow()
    }
}
