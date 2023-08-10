package com.kssidll.arrugarq.ui.additem

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import kotlin.jvm.optionals.*

@HiltViewModel
class AddItemViewModel @Inject constructor(
    itemRepository: IItemRepository,
    productRepository: IProductRepository,
    variantsRepository: IProductVariantRepository,
    shopRepository: IShopRepository,
): ViewModel() {
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

        if (addItemState.selectedShop.value == null) {
            addItemState.selectedShop.value = lastItem?.shopId?.let { shopRepository.get(it) }
        }

        if (addItemState.date.value == null) {
            addItemState.date.value = lastItem?.date
        }
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
            variants.value = variantsRepository.getByProductFlow(productId)
                .cancellable()
        }
    }

    suspend fun fillStateWithSelectedProductLatestData() {
        val lastItemByProduct =
            itemRepository.getLastByProductId(addItemState.selectedProduct.value!!.id)

        if (lastItemByProduct == null) return

        addItemState.selectedVariant.value = lastItemByProduct.variantId?.let {
            variantsRepository.get(it)
        }

        addItemState.price.value = String.format(
            "%.2f",
            lastItemByProduct.price / 100f
        )
    }

}