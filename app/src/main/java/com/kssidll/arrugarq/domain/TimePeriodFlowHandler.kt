package com.kssidll.arrugarq.domain

import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Defines available time periods and handles flow managment when changing time periods
 */
class TimePeriodFlowHandler(
    private val scope: CoroutineScope,
    private val dayFlow: () -> Flow<List<ItemSpentByTime>>,
    private val weekFlow: () -> Flow<List<ItemSpentByTime>>,
    private val monthFlow: () -> Flow<List<ItemSpentByTime>>,
    private val yearFlow: () -> Flow<List<ItemSpentByTime>>,
    startPeriod: TimePeriodFlowHandler.Periods = Periods.Month,
) {
    private var mCurrentPeriod: MutableState<Periods>
    val currentPeriod get() = mCurrentPeriod.value

    private var spentByTimeQuery: Job? = null
    private var _spentByTimeData: MutableState<Flow<List<ItemSpentByTime>>> =
        mutableStateOf(flowOf())
    val spentByTimeData by _spentByTimeData

    init {
        mCurrentPeriod = mutableStateOf(startPeriod)
        handlePeriodSwitch()
    }

    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        mCurrentPeriod.value = newPeriod
        handlePeriodSwitch()
    }

    private fun handlePeriodSwitch() {
        spentByTimeQuery?.cancel()

        spentByTimeQuery = scope.launch {
            when (currentPeriod) {
                Periods.Day -> _spentByTimeData.value = dayFlow().distinctUntilChanged()
                    .cancellable()

                Periods.Week -> _spentByTimeData.value = weekFlow().distinctUntilChanged()
                    .cancellable()

                Periods.Month -> _spentByTimeData.value = monthFlow().distinctUntilChanged()
                    .cancellable()

                Periods.Year -> _spentByTimeData.value = yearFlow().distinctUntilChanged()
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
