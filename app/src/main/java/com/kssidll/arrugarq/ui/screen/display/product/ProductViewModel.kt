package com.kssidll.arrugarq.ui.screen.display.product


import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.paging.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.data.*
import com.patrykandpatrick.vico.core.entry.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepositorySource,
): ViewModel() {
    private val mProduct: MutableState<Product?> = mutableStateOf(null)
    val product: Product? by mProduct

    val chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler? = null
    val spentByTimePeriod: TimePeriodFlowHandler.Periods? get() = mTimePeriodFlowHandler?.currentPeriod
    val spentByTimeData: Flow<List<ChartSource>>? get() = mTimePeriodFlowHandler?.spentByTimeData

    fun productTotalSpent(): Flow<Float>? {
        if (product == null) return null

        return productRepository.totalSpentFlow(product!!)
            .map {
                it.toFloat()
                    .div(Item.PRICE_DIVISOR * Item.QUANTITY_DIVISOR)
            }
    }

    fun productPriceByShop(): Flow<List<ProductPriceByShopByTime>>? {
        if (product == null) return null

        return productRepository.averagePriceByVariantByShopByMonthFlow(product!!)
    }

    /**
     * @return paging data of full item for current product as flow
     */
    fun transactions(): Flow<PagingData<FullItem>> {
        if (product == null) return emptyFlow()
        return productRepository.fullItemsPagedFlow(product!!)
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

        return@async true
    }
        .await()
}
