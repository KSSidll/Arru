package com.kssidll.arrugarq.ui.screen.home

import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class HomeViewModel @Inject constructor(
    itemRepository: IItemRepository,
    shopRepository: IShopRepository,
): ViewModel() {
    private val shopRepository: IShopRepository
    private val itemRepository: IItemRepository

    private var spentByTimeQuery: Job? = null
    private var _spentByTimeData: MutableState<Flow<List<Chartable>>> = mutableStateOf(flowOf())
    val spentByTimeData by _spentByTimeData

    private var _spentByTimePeriod: MutableState<SpentByTimePeriod> =
        mutableStateOf(SpentByTimePeriod.Month)
    val spentByTimePeriod by _spentByTimePeriod

    init {
        this.shopRepository = shopRepository
        this.itemRepository = itemRepository

        switchToSpentByTimePeriod(spentByTimePeriod)
    }

    fun getTotalSpent(): Flow<Float> {
        return itemRepository.getTotalSpentFlow()
            .map {
                it.div(100F)
            }
    }

    fun getSpentByShop(): Flow<List<ItemSpentByShop>> {
        return itemRepository.getShopTotalSpentFlow()
    }

    fun getSpentByCategory(): Flow<List<ItemSpentByCategory>> {
        return itemRepository.getCategoryTotalSpentFlow()
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

enum class SpentByTimePeriod {
    Day,
    Week,
    Month,
    Year,
}

@Composable
@ReadOnlyComposable
fun SpentByTimePeriod.getTranslation(): String {
    return when (this) {
        SpentByTimePeriod.Day -> stringResource(R.string.day)
        SpentByTimePeriod.Week -> stringResource(R.string.week)
        SpentByTimePeriod.Month -> stringResource(R.string.month)
        SpentByTimePeriod.Year -> stringResource(R.string.year)
    }
}

// Important, the order of items in the enum determines the order that the locations appear in
// on the bottom navigation bar
enum class HomeScreenLocations(
    val initial: Boolean = false,
) {
    Dashboard(initial = true),
    FakeLocation(),
    AnotherFakeLocation(),
    ;

    val description: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Dashboard -> stringResource(R.string.navigate_to_dashboard_description)
            FakeLocation -> "Test"
            AnotherFakeLocation -> "Test"
        }

    val imageVector: ImageVector?
        @Composable
        get() = when (this) {
            Dashboard -> Icons.Rounded.Home
            FakeLocation -> Icons.Rounded.AccountTree
            AnotherFakeLocation -> Icons.Rounded.Airlines
        }

    val painter: Painter?
        @Composable
        get() = when (this) {
            Dashboard -> null
            FakeLocation -> null
            AnotherFakeLocation -> null
        }

    companion object {
        private val idMap = entries.associateBy { it.ordinal }
        fun getByOrdinal(ordinal: Int) = idMap[ordinal]

    }
}

@Composable
@ReadOnlyComposable
fun HomeScreenLocations.getTranslation(): String {
    return when (this) {
        HomeScreenLocations.Dashboard -> stringResource(R.string.dashboard_nav_label)
        HomeScreenLocations.FakeLocation -> "Test"
        HomeScreenLocations.AnotherFakeLocation -> "Test"
    }
}