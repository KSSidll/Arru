package com.kssidll.arrugarq.ui.screen.home

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.chart.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

enum class SpentByTimePeriod {
    Day,
    Week,
    Month,
    Year,
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    itemRepository: IItemRepository,
    shopRepository: IShopRepository,
): ViewModel() {
    private val shopRepository: IShopRepository
    private val itemRepository: IItemRepository

    private var spentByTimeQuery: Job? = null
    private var _spentByTimeData: MutableState<Flow<List<IChartable>>> = mutableStateOf(flowOf())
    val spentByTimeData by _spentByTimeData

    private var _spentByTimePeriod: MutableState<SpentByTimePeriod> =
        mutableStateOf(SpentByTimePeriod.Month)
    val spentByTimePeriod by _spentByTimePeriod

    init {
        this.shopRepository = shopRepository
        this.itemRepository = itemRepository

        switchToSpentByTimePeriod(spentByTimePeriod)
    }

    fun switchToSpentByTimePeriod(newPeriod: SpentByTimePeriod) {
        _spentByTimePeriod.value = newPeriod
        spentByTimeQuery?.cancel()

        spentByTimeQuery = viewModelScope.launch {
            with(itemRepository) {
                when (newPeriod) {
                    SpentByTimePeriod.Day -> _spentByTimeData.value =
                        getTotalSpentByDayFlow().cancellable()

                    SpentByTimePeriod.Week -> _spentByTimeData.value =
                        getTotalSpentByWeekFlow().cancellable()

                    SpentByTimePeriod.Month -> _spentByTimeData.value =
                        getTotalSpentByMonthFlow().cancellable()

                    SpentByTimePeriod.Year -> _spentByTimeData.value =
                        getTotalSpentByYearFlow().cancellable()
                }
            }
        }
    }
}
