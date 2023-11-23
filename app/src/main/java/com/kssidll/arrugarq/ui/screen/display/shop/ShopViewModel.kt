package com.kssidll.arrugarq.ui.screen.display.shop


import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.repository.*
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
class ShopViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
    private val shopRepository: IShopRepository,
): ViewModel() {
    private val mShop: MutableState<Shop?> = mutableStateOf(null)
    val shop: Shop? by mShop

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

    fun shopTotalSpent(): Flow<Float>? {
        if (shop == null) return null

        return itemRepository.getTotalSpentByShopFlow(shop!!.id)
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
     * @return True if provided [shopId] was valid, false otherwise
     */
    suspend fun performDataUpdate(shopId: Long) = viewModelScope.async {
        val shop = shopRepository.get(shopId) ?: return@async false

        // We ignore the possiblity of changing category while one is already loaded
        // as not doing that would increase complexity too much
        // and if it happens somehow, it would be considered a bug
        if (mShop.value != null || shopId == mShop.value?.id) return@async true

        mShop.value = shop

        mTimePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            dayFlow = {
                itemRepository.getTotalSpentByShopByDayFlow(shopId)
            },
            weekFlow = {
                itemRepository.getTotalSpentByShopByWeekFlow(shopId)
            },
            monthFlow = {
                itemRepository.getTotalSpentByShopByMonthFlow(shopId)
            },
            yearFlow = {
                itemRepository.getTotalSpentByShopByYearFlow(shopId)
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
        if (mFullItemsDataQuery == null) return

        if (mFullItemsDataQuery!!.isCompleted) {
            mFullItemsDataQuery = performFullItemsQuery(mFullItemOffset)
            mFullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Requires shop to be a non null value
     *
     * Doesn't check it itself as it doesn't update the offset
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        mTransactionItems.addAll(
            itemRepository.getFullItemsByShop(
                offset = queryOffset,
                count = fullItemFetchCount,
                shopId = shop!!.id,
            )
        )
    }

}
