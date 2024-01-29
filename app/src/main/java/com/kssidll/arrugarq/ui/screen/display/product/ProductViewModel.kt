package com.kssidll.arrugarq.ui.screen.display.product


import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.*
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
class ProductViewModel @Inject constructor(
    private val itemRepository: ItemRepositorySource,
    private val productRepository: ProductRepositorySource,
): ViewModel() {
    private val mProduct: MutableState<Product?> = mutableStateOf(null)
    val product: Product? by mProduct

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

    fun productTotalSpent(): Flow<Float>? {
        if (product == null) return null

        return productRepository.totalSpentFlow(product!!)
    }

    fun productPriceByShop(): Flow<List<ProductPriceByShopByTime>>? {
        if (product == null) return null

        return productRepository.averagePriceByVariantByShopByMonthFlow(product!!)
    }

    /**
     * Switches the state period to [newPeriod]
     * @param newPeriod Period to switch the state to
     */
    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        mTimePeriodFlowHandler?.switchPeriod(newPeriod)
    }

    /**
     * @return True if provided [productId] was valid, false otherwise
     */
    suspend fun performDataUpdate(productId: Long) = viewModelScope.async {
        val product = productRepository.get(productId) ?: return@async false

        // We ignore the possiblity of changing category while one is already loaded
        // as not doing that would increase complexity too much
        // and if it happens somehow, it would be considered a bug
        if (mProduct.value != null || productId == mProduct.value?.id) return@async true

        mProduct.value = product

        mTimePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            dayFlow = {
                productRepository.totalSpentByDayFlow(product)
            },
            weekFlow = {
                productRepository.totalSpentByWeekFlow(product)
            },
            monthFlow = {
                productRepository.totalSpentByMonthFlow(product)
            },
            yearFlow = {
                productRepository.totalSpentByYearFlow(product)
            },
        )

        mNewFullItemFlowJob?.cancel()
        mNewFullItemFlowJob = viewModelScope.launch {
            mNewFullItemFlow = itemRepository.newestFlow()

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
        if (product == null || mFullItemsDataQuery == null) return

        if (mFullItemsDataQuery!!.isCompleted) {
            mFullItemsDataQuery = performFullItemsQuery(mFullItemOffset)
            mFullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Requires product to be a non null value
     *
     * Doesn't check it itself as it doesn't update the offset
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        mTransactionItems.addAll(
            productRepository.fullItems(
                product = product!!,
                count = fullItemFetchCount,
                offset = queryOffset,
            )
        )
    }
}
