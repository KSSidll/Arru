package com.kssidll.arrugarq.ui.additem

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arrugarq.data.data.Item
import com.kssidll.arrugarq.data.data.ProductVariant
import com.kssidll.arrugarq.data.data.ProductWithAltNames
import com.kssidll.arrugarq.data.data.Shop
import com.kssidll.arrugarq.data.repository.IItemRepository
import com.kssidll.arrugarq.data.repository.IProductRepository
import com.kssidll.arrugarq.data.repository.IProductVariantRepository
import com.kssidll.arrugarq.data.repository.IShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class AddItemViewModel @Inject constructor(
    itemRepository: IItemRepository,
    productRepository: IProductRepository,
    variantsRepository: IProductVariantRepository,
    shopRepository: IShopRepository,
) : ViewModel() {
    private val itemRepository: IItemRepository
    private val productRepository: IProductRepository
    private val variantsRepository: IProductVariantRepository
    private val shopRepository: IShopRepository

    var addItemState: AddItemState = AddItemState()

    private var variantsJob: Job? = null
    var variants: MutableState<Flow<List<ProductVariant>>> = mutableStateOf(flowOf())

    init {
        this.itemRepository = itemRepository
        this.productRepository = productRepository
        this.variantsRepository = variantsRepository
        this.shopRepository = shopRepository
    }

    suspend fun fetch() {
        val lastItem: Item? = itemRepository.getLast()

        addItemState.selectedShop.value = lastItem?.shopId?.let { shopRepository.get(it) }
        addItemState.date.value = lastItem?.date
    }

    /**
     * Doesn't ensure validity of non optional values as they should be validated on Screen level
     * to allow for UI changes depending on data validity
     */
    fun addItem(itemData: AddItemData) = viewModelScope.launch {
        itemRepository.insert(
            Item(
                productId = itemData.productId,
                variantId = itemData.variantId.getOrNull(),
                shopId = itemData.shopId.getOrNull(),
                quantity = itemData.quantity,
                price = (itemData.price * 100).toLong(),
                date = itemData.date,
            )
        )
    }

    fun getShopsFlow(): Flow<List<Shop>> {
        return shopRepository.getAllFlow()
    }

    fun getProductsWithAltNamesFlow(): Flow<List<ProductWithAltNames>> {
        return productRepository.getAllWithAltNamesFlow()
    }

    fun queryProductVariants(productId: Long) {
        variantsJob?.cancel()

        variantsJob = viewModelScope.launch {
            variants.value = variantsRepository.getByProductFlow(productId).cancellable()
        }
    }
}