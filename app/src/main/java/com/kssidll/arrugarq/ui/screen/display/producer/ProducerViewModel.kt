package com.kssidll.arrugarq.ui.screen.display.producer


import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.display.shop.fullItemFetchCount
import com.patrykandpatrick.vico.core.entry.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

/**
 * Page fetch size
 */
internal const val fullItemFetchCount = 8

/**
 * Maximum prefetched items
 */
internal const val fullItemMaxPrefetchCount = fullItemFetchCount * 6

@HiltViewModel
class ProducerViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
    private val producerRepository: IProducerRepository,
): ViewModel() {
    private val mProducer: MutableState<ProductProducer?> = mutableStateOf(null)
    val producer: ProductProducer? by mProducer

    val chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()

    private val mTransactionItems: SnapshotStateList<FullItem> = mutableStateListOf()
    val transactionItems get() = mTransactionItems.toList()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler? = null
    val spentByTimePeriod: TimePeriodFlowHandler.Periods? get() = mTimePeriodFlowHandler?.currentPeriod
    val spentByTimeData: Flow<List<ItemSpentByTime>>? get() = mTimePeriodFlowHandler?.spentByTimeData

    private var mFullItemsDataQuery: Job? = null
    private var mFullItemOffset: Int = 0

    private var mNewFullItemFlowJob: Job? = null
    private var mNewFullItemFlow: (Flow<Item>)? = null

    fun producerTotalSpent(): Flow<Float>? {
        if (producer == null) return null

        return itemRepository.getTotalSpentByProducerFlow(producer!!.id)
            .map {
                it.toFloat()
                    .div(Item.QUANTITY_DIVISOR * Item.PRICE_DIVISOR)
            }
            .distinctUntilChanged()
    }

    /**
     * Switches the state period to [newPeriod]
     * @param newPeriod Period to switch the state to
     */
    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        mTimePeriodFlowHandler?.switchPeriod(newPeriod)
    }

    /**
     * @return true if provided [producerId] was valid, false otherwise
     */
    suspend fun performDataUpdate(producerId: Long) = viewModelScope.async {
        val producer = producerRepository.get(producerId) ?: return@async false

        // We ignore the possiblity of changing category while one is already loaded
        // as not doing that would increase complexity too much
        // and if it happens somehow, it would be considered a bug
        if (mProducer.value != null || producerId == mProducer.value?.id) return@async true

        mProducer.value = producer

        mTimePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            dayFlow = {
                itemRepository.getTotalSpentByProducerByDayFlow(producerId)
            },
            weekFlow = {
                itemRepository.getTotalSpentByProducerByWeekFlow(producerId)
            },
            monthFlow = {
                itemRepository.getTotalSpentByProducerByMonthFlow(producerId)
            },
            yearFlow = {
                itemRepository.getTotalSpentByProducerByYearFlow(producerId)
            },
        )

        mNewFullItemFlowJob?.cancel()
        mNewFullItemFlowJob = launch {
            mNewFullItemFlow = itemRepository.getLastFlow()
                .cancellable()

            mNewFullItemFlow?.collect {
                mFullItemOffset = 0
                mTransactionItems.clear()
                mFullItemsDataQuery?.cancel()
                mFullItemsDataQuery = performFullItemsQuery()
                mFullItemOffset += fullItemFetchCount
            }
        }

        return@async true
    }
        .await()

    /**
     * Requests a query of [fullItemFetchCount] items to be appended to transactions list
     */
    fun queryMoreFullItems() {
        if (producer == null || mFullItemsDataQuery == null) return

        if (mFullItemsDataQuery!!.isCompleted) {
            mFullItemsDataQuery = performFullItemsQuery(mFullItemOffset)
            mFullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Requires producer to be a non null value
     *
     * Doesn't check it itself as it doesn't update the offset
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        mTransactionItems.addAll(
            itemRepository.getFullItemsByProducer(
                offset = queryOffset,
                count = fullItemFetchCount,
                producerId = producer!!.id,
            )
        )
    }
}
