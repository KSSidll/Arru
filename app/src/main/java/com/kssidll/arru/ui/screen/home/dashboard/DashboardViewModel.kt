package com.kssidll.arru.ui.screen.home.dashboard


import androidx.lifecycle.ViewModel
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.kssidll.arru.domain.data.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(

): ViewModel() {
    /**
     * Currently set spending period
     */
    val spentByTimePeriod get() = TimePeriodFlowHandler.Periods.Month

    /**
     * Switches the spending period to [newPeriod]
     * @param newPeriod Period to switch the state to
     */
    fun switchToSpentByTimePeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        // TODO
    }

    /**
     * @return Number representing total transaction spending as flow
     */
    fun getTotalSpent(): Flow<Data<Float?>> {
        return flowOf(Data.Loading())
    }
}
