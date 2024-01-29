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
    private val transactionRepository: TransactionBasketRepositorySource,
    private val categoryRepository: CategoryRepositorySource,
    private val shopRepository: ShopRepositorySource,
): ViewModel() {
    private val mTimePeriodFlowHandler: TimePeriodFlowHandler = TimePeriodFlowHandler(
        scope = viewModelScope,
        dayFlow = {
            transactionRepository.totalSpentByDayFlow()
        },
        weekFlow = {
            transactionRepository.totalSpentByWeekFlow()
        },
        monthFlow = {
            transactionRepository.totalSpentByMonthFlow()
        },
        yearFlow = {
            transactionRepository.totalSpentByYearFlow()
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
     * @return Number representing total transaction spending as flow
     */
    fun getTotalSpent(): Flow<Float> {
        return transactionRepository.totalSpentFlow()
    }

    /**
     * @return List of items representing [Shop] spending in time as flow
     */
    fun getSpentByShop(): Flow<List<ItemSpentByShop>> {
        return shopRepository.totalSpentByShopFlow()
    }

    /**
     * @return List of items representing [ProductCategory] spending in time as flow
     */
    fun getSpentByCategory(): Flow<List<ItemSpentByCategory>> {
        return categoryRepository.totalSpentByCategoryFlow()
    }
}
