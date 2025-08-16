package com.kssidll.arru.ui.screen.display.product


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductPriceByShopByTime
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.kssidll.arru.domain.usecase.data.GetItemsForProductUseCase
import com.kssidll.arru.ui.component.SpendingSummaryPeriod
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepositorySource,
    private val getItemsForProductUseCase: GetItemsForProductUseCase
): ViewModel() {
    private val mProduct: MutableState<ProductEntity?> = mutableStateOf(null)
    val product: ProductEntity? by mProduct

    private var mProductListener: Job? = null

    val chartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler<ImmutableList<ItemSpentByTime>>? = null
    val spentByTimePeriod: SpendingSummaryPeriod? get() = mTimePeriodFlowHandler?.currentPeriod?.let { SpendingSummaryPeriod.valueOf(it.name) }
    val spentByTimeData: Flow<ImmutableList<ItemSpentByTime>>? get() = mTimePeriodFlowHandler?.spentByTimeData

    fun productTotalSpent(): Flow<Float?>? {
        return product?.let { productRepository.totalSpent(it) }
    }

    fun productPriceByShop(): Flow<ImmutableList<ProductPriceByShopByTime>>? {
        return product?.let { productRepository.averagePriceByVariantByShopByMonth(it) }
    }

    /**
     * @return paging data of full item for current product as flow
     */
    fun transactions(): Flow<PagingData<Item>> {
        return product?.let { getItemsForProductUseCase(it.id) } ?: emptyFlow()
    }

    /**
     * Switches the state period to [newPeriod]
     * @param newPeriod Period to switch the state to
     */
    fun switchPeriod(newPeriod: SpendingSummaryPeriod) {
        val nPeriod = TimePeriodFlowHandler.Periods.valueOf(newPeriod.name)
        mTimePeriodFlowHandler?.switchPeriod(nPeriod)
    }

    /**
     * @return True if provided [productId] was valid, false otherwise
     */
    suspend fun performDataUpdate(productId: Long) = viewModelScope.async {
        val product = productRepository.get(productId).first() ?: return@async false

        // We ignore the possiblity of changing category while one is already loaded
        // as not doing that would increase complexity too much
        // and if it happens somehow, it would be considered a bug
        if (mProduct.value != null || productId == mProduct.value?.id) return@async true

        mProductListener?.cancel()
        mProductListener = viewModelScope.launch {
            productRepository.get(productId)
                .collectLatest {
                    mProduct.value = it
                }
        }

        mProduct.value = product

        mTimePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            day = {
                productRepository.totalSpentByDay(product)
            },
            week = {
                productRepository.totalSpentByWeek(product)
            },
            month = {
                productRepository.totalSpentByMonth(product)
            },
            year = {
                productRepository.totalSpentByYear(product)
            },
        )

        return@async true
    }
        .await()
}
