package com.kssidll.arru.ui.screen.home.dashboard


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionSpentByTime
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.repository.CategoryRepositorySource
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.kssidll.arru.domain.data.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepository: TransactionBasketRepositorySource,
    private val categoryRepository: CategoryRepositorySource,
    private val shopRepository: ShopRepositorySource,
): ViewModel() {
    private val mTimePeriodFlowHandler: TimePeriodFlowHandler<Data<List<TransactionSpentByTime>>> =
        TimePeriodFlowHandler(
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
    fun getTotalSpent(): Flow<Data<Float?>> {
        return transactionRepository.totalSpentFlow()
    }

    /**
     * @return List of items representing [Shop] spending in time as flow
     */
    fun getSpentByShop(): Flow<Data<List<TransactionTotalSpentByShop>>> {
        return shopRepository.totalSpentByShopFlow()
    }

    /**
     * @return List of items representing [ProductCategory] spending in time as flow
     */
    fun getSpentByCategory(): Flow<Data<List<ItemSpentByCategory>>> {
        return categoryRepository.totalSpentByCategoryFlow()
    }
}
