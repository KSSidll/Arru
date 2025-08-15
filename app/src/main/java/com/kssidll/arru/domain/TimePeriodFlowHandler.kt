package com.kssidll.arru.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * Defines available time periods and handles flow managment when changing time periods
 */
class TimePeriodFlowHandler<T>(
    private val scope: CoroutineScope,
    private val day: () -> Flow<T>,
    private val week: () -> Flow<T>,
    private val month: () -> Flow<T>,
    private val year: () -> Flow<T>,
    startPeriod: Periods = Periods.Month,
) {
    private var mCurrentPeriod: MutableState<Periods> = mutableStateOf(startPeriod)
    val currentPeriod get() = mCurrentPeriod.value

    private var mSpentByTimeQuery: Job? = null
    private var mSpentByTimeData: MutableState<Flow<T>> = mutableStateOf(flowOf())
    val spentByTimeData by mSpentByTimeData

    init {
        handlePeriodSwitch()
    }

    fun switchPeriod(newPeriod: Periods) {
        mCurrentPeriod.value = newPeriod
        handlePeriodSwitch()
    }

    private fun handlePeriodSwitch() {
        mSpentByTimeQuery?.cancel()

        mSpentByTimeQuery = scope.launch {
            when (currentPeriod) {
                Periods.Day -> mSpentByTimeData.value = day().distinctUntilChanged()
                    .cancellable()

                Periods.Week -> mSpentByTimeData.value = week().distinctUntilChanged()
                    .cancellable()

                Periods.Month -> mSpentByTimeData.value = month().distinctUntilChanged()
                    .cancellable()

                Periods.Year -> mSpentByTimeData.value = year().distinctUntilChanged()
                    .cancellable()
            }
        }
    }

    /**
     * Ordinal signifies which order they appear in on the UI
     */
    enum class Periods {
        Day,
        Week,
        Month,
        Year,
    }
}

@Composable
@ReadOnlyComposable
fun TimePeriodFlowHandler.Periods.getTranslation(): String {
    return when (this) {
        TimePeriodFlowHandler.Periods.Day -> stringResource(R.string.day)
        TimePeriodFlowHandler.Periods.Week -> stringResource(R.string.week)
        TimePeriodFlowHandler.Periods.Month -> stringResource(R.string.month)
        TimePeriodFlowHandler.Periods.Year -> stringResource(R.string.year)
    }
}
