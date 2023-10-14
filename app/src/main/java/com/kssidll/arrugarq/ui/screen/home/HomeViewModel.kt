package com.kssidll.arrugarq.ui.screen.home

import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.graphics.painter.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

internal const val fullItemFetchCount = 8
internal const val fullItemMaxPrefetchCount = 50

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
): ViewModel() {
    private val timePeriodFlowHandler: TimePeriodFlowHandler = TimePeriodFlowHandler(
        scope = viewModelScope,
        cancellableDayFlow = {
            itemRepository.getTotalSpentByDayFlow()
                .cancellable()
        },
        cancellableWeekFlow = {
            itemRepository.getTotalSpentByWeekFlow()
                .cancellable()
        },
        cancellableMonthFlow = {
            itemRepository.getTotalSpentByMonthFlow()
                .cancellable()
        },
        cancellableYearFlow = {
            itemRepository.getTotalSpentByYearFlow()
                .cancellable()
        },
    )

    val spentByTimeData get() = timePeriodFlowHandler.spentByTimeData
    val spentByTimePeriod get() = timePeriodFlowHandler.currentPeriod

    private var fullItemsDataQuery: Job = Job()
    var fullItemsData: SnapshotStateList<FullItem> = mutableStateListOf()
    private val newFullItemFlow: Flow<Item> = itemRepository.getLastFlow()
    private var fullItemOffset: Int = 0

    init {
        viewModelScope.launch {
            newFullItemFlow.collect {
                fullItemOffset = 0
                fullItemsDataQuery.cancel()
                fullItemsData.clear()
                fullItemsDataQuery = performFullItemsQuery()
                fullItemOffset += fullItemFetchCount
            }
        }
    }

    fun switchToSpentByTimePeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        timePeriodFlowHandler.switchPeriod(newPeriod)
    }

    fun queryMoreFullItems() {
        if (fullItemsDataQuery.isCompleted) {
            fullItemsDataQuery = performFullItemsQuery(fullItemOffset)
            fullItemOffset += fullItemFetchCount
        }
    }

    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        fullItemsData.addAll(
            itemRepository.getFullItems(
                offset = queryOffset,
                count = fullItemFetchCount
            )
        )
    }

    fun getTotalSpent(): Flow<Float> {
        return itemRepository.getTotalSpentFlow()
            .map {
                it.div(100000F)
            }
    }

    fun getSpentByShop(): Flow<List<ItemSpentByShop>> {
        return itemRepository.getShopTotalSpentFlow()
    }

    fun getSpentByCategory(): Flow<List<ItemSpentByCategory>> {
        return itemRepository.getCategoryTotalSpentFlow()
    }
}

// Important, the order of items in the enum determines the order that the locations appear in
// on the bottom navigation bar
enum class HomeScreenLocations(
    val initial: Boolean = false,
) {
    Dashboard(initial = true),
    Predictions(),
    Transactions(),
    ;

    val description: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Dashboard -> stringResource(R.string.navigate_to_dashboard_description)
            Predictions -> stringResource(R.string.navigate_to_predictions_description)
            Transactions -> stringResource(R.string.navigate_to_transactions_description)
        }

    val imageVector: ImageVector?
        @Composable
        get() = when (this) {
            Dashboard -> Icons.Rounded.Home
            Predictions -> Icons.Rounded.Upcoming
            Transactions -> Icons.AutoMirrored.Rounded.Notes
        }

    val painter: Painter?
        @Composable
        get() = when (this) {
            Dashboard -> null
            Predictions -> null
            Transactions -> null
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
        HomeScreenLocations.Predictions -> stringResource(R.string.predictions_nav_label)
        HomeScreenLocations.Transactions -> stringResource(R.string.transactions_nav_label)
    }
}