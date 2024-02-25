package com.kssidll.arru.domain

import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arru.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Defines available time periods and handles flow managment when changing time periods
 */
class TimePeriodFlowHandler<T>(
    private val scope: CoroutineScope,
    private val dayFlow: () -> Flow<T>,
    private val weekFlow: () -> Flow<T>,
    private val monthFlow: () -> Flow<T>,
    private val yearFlow: () -> Flow<T>,
    startPeriod: Periods = Periods.Month,
) {
    private var mCurrentPeriod: MutableState<Periods>
    val currentPeriod get() = mCurrentPeriod.value

    private var mSpentByTimeQuery: Job? = null
    private var mSpentByTimeData: MutableState<Flow<T>> =
        mutableStateOf(flowOf())
    val spentByTimeData by mSpentByTimeData

    init {
        mCurrentPeriod = mutableStateOf(startPeriod)
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
                Periods.Day -> mSpentByTimeData.value = dayFlow().distinctUntilChanged()
                    .cancellable()

                Periods.Week -> mSpentByTimeData.value = weekFlow().distinctUntilChanged()
                    .cancellable()

                Periods.Month -> mSpentByTimeData.value = monthFlow().distinctUntilChanged()
                    .cancellable()

                Periods.Year -> mSpentByTimeData.value = yearFlow().distinctUntilChanged()
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
