package com.kssidll.arrugarq.ui.screen.producer


import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.shop.fullItemFetchCount
import com.patrykandpatrick.vico.core.entry.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

internal data class ProducerScreenState(
    val producer: MutableState<ProductProducer?> = mutableStateOf(null),
    val items: SnapshotStateList<FullItem> = mutableStateListOf(),
    val chartData: MutableState<Flow<List<ItemSpentByTime>>> = mutableStateOf(flowOf()),
    val totalSpentData: MutableFloatState = mutableFloatStateOf(0F),

    val spentByTimePeriod: MutableState<TimePeriodFlowHandler.Periods?> = mutableStateOf(null),

    val columnChartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer(),
    var finishedChartAnimation: Boolean = false,
)

internal const val fullItemFetchCount = 8
internal const val fullItemMaxPrefetchCount = 50

@HiltViewModel
class ProducerViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
    private val productProducerRepository: IProductProducerRepository,
): ViewModel() {
    internal val screenState: ProducerScreenState = ProducerScreenState()

    private var timePeriodFlowHandler: TimePeriodFlowHandler? = null

    private var fullItemsDataQuery: Job? = null
    private var fullItemOffset: Int = 0

    private var newFullItemFlowJob: Job? = null
    private var newFullItemFlow: (Flow<Item>)? = null

    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        timePeriodFlowHandler?.switchPeriod(newPeriod)
        screenState.spentByTimePeriod.value = newPeriod

        screenState.chartData.value = timePeriodFlowHandler!!.spentByTimeData
    }

    fun performDataUpdate(producerId: Long) = viewModelScope.launch {
        if (producerId == screenState.producer.value?.id) return@launch

        screenState.producer.value = productProducerRepository.get(producerId)

        viewModelScope.launch {
            screenState.totalSpentData.floatValue =
                itemRepository.getTotalSpentByProducer(producerId)
                    .toFloat()
                    .div(100000)
        }

        timePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            cancellableDayFlow = {
                itemRepository.getTotalSpentByProducerByDayFlow(producerId)
                    .cancellable()
            },
            cancellableWeekFlow = {
                itemRepository.getTotalSpentByProducerByWeekFlow(producerId)
                    .cancellable()
            },
            cancellableMonthFlow = {
                itemRepository.getTotalSpentByProducerByMonthFlow(producerId)
                    .cancellable()
            },
            cancellableYearFlow = {
                itemRepository.getTotalSpentByProducerByYearFlow(producerId)
                    .cancellable()
            },
        )

        screenState.spentByTimePeriod.value = timePeriodFlowHandler?.currentPeriod

        screenState.chartData.value = timePeriodFlowHandler!!.spentByTimeData

        newFullItemFlowJob?.cancel()
        newFullItemFlowJob = viewModelScope.launch {
            newFullItemFlow = itemRepository.getLastFlow()
                .cancellable()

            newFullItemFlow?.collect {
                fullItemOffset = 0
                screenState.items.clear()
                fullItemsDataQuery?.cancel()
                fullItemsDataQuery = performFullItemsQuery()
                fullItemOffset += fullItemFetchCount
            }
        }

    }

    fun queryMoreFullItems() {
        if (fullItemsDataQuery == null) return

        if (fullItemsDataQuery!!.isCompleted && screenState.producer.value != null) {
            fullItemsDataQuery = performFullItemsQuery(fullItemOffset)
            fullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Requires producer value of producerScreenState to be a non null.
     * Doesn't check it itself as it doesn't update the offset
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        screenState.items.addAll(
            itemRepository.getFullItemsByProducer(
                offset = queryOffset,
                count = fullItemFetchCount,
                producerId = screenState.producer.value!!.id,
            )
        )
    }
}
