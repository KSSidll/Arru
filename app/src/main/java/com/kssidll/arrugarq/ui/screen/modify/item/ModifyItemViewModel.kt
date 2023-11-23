package com.kssidll.arrugarq.ui.screen.modify.item

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Base [ViewModel] class for Item modification view models
 * @property computeStartState Initializes start state, should be called as child in init of inheriting view model
 * @property screenState A [ModifyItemScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Item matching provided id, only changes representation data and loading state
 * @property onProductChange Updates the screen state representation property values related to product to represent the product that is currently selected, should be called after the selected product changes
 */
abstract class ModifyItemViewModel: ViewModel() {
    protected abstract val itemRepository: IItemRepository
    protected abstract val productRepository: IProductRepository
    protected abstract val variantsRepository: IVariantRepository
    protected abstract val shopRepository: IShopRepository

    internal val screenState: ModifyItemScreenState = ModifyItemScreenState()

    /**
     * Fetches start data to state
     */
    protected fun computeStartState() = viewModelScope.launch {
        screenState.loadingShop.value = true
        screenState.loadingDate.value = true

        val lastItem: Item? = itemRepository.getLast()

        if (screenState.selectedShop.value == null) {
            screenState.selectedShop.value = lastItem?.shopId?.let { shopRepository.get(it) }
        }

        if (screenState.date.value == null) {
            screenState.date.value = lastItem?.date
        }

        screenState.loadingDate.value = false
        screenState.loadingShop.value = false
    }

    /**
     * Updates the screen state representation property values related to product to represent the product that is currently selected, should be called after the selected product changes
     */
    fun onProductChange() = viewModelScope.launch {
        screenState.loadingPrice.value = true
        screenState.loadingQuantity.value = true
        screenState.loadingShop.value = true

        updateProductVariants()

        val lastItemByProduct =
            itemRepository.getLastByProductId(screenState.selectedProduct.value!!.id)

        if (lastItemByProduct == null) {
            screenState.price.value = String()
            screenState.quantity.value = String()
            screenState.selectedVariant.value = null

            return@launch
        }

        val shop = shopRepository.get(lastItemByProduct.shopId!!)

        screenState.selectedShop.value = shop

        val variant = lastItemByProduct.variantId?.let {
            variantsRepository.get(it)
        }

        screenState.selectedVariant.value = variant

        screenState.price.value = String.format(
            "%.2f",
            lastItemByProduct.price / 100f
        )

        screenState.quantity.value = String.format(
            "%.3f",
            lastItemByProduct.quantity / 1000f
        )
    }
        .invokeOnCompletion {
            screenState.loadingQuantity.value = false
            screenState.loadingPrice.value = false
            screenState.loadingShop.value = false

            screenState.validateQuantity()
            screenState.validatePrice()
        }

    /**
     * @return List of all shops
     */
    fun allShops(): Flow<List<Shop>> {
        return shopRepository.getAllFlow()
    }

    /**
     * @return List of all products
     */
    fun allProducts(): Flow<List<ProductWithAltNames>> {
        return productRepository.getAllWithAltNamesFlow()
    }

    private val mProductVariants: MutableState<Flow<List<ProductVariant>>> =
        mutableStateOf(flowOf())
    val productVariants: Flow<List<ProductVariant>> by mProductVariants
    private var fillStateProductVariantsJob: Job? = null

    /**
     * Updates [productVariants] to represent available variants for currently set product
     */
    private fun updateProductVariants() {
        fillStateProductVariantsJob?.cancel()
        fillStateProductVariantsJob = viewModelScope.launch {
            with(screenState.selectedProduct) {
                if (value != null) {
                    mProductVariants.value = variantsRepository.getByProductIdFlow(value!!.id)
                }
            }
        }
    }

    /**
     * Updates data in the screen state
     * @return true if provided [itemId] was valid, false otherwise
     */
    suspend fun updateState(itemId: Long) = viewModelScope.async {
        screenState.loadingProduct.value = true
        screenState.loadingVariant.value = true
        screenState.loadingShop.value = true
        screenState.loadingQuantity.value = true
        screenState.loadingPrice.value = true
        screenState.loadingDate.value = true

        val dispose = {
            screenState.loadingProduct.value = false
            screenState.loadingVariant.value = false
            screenState.loadingShop.value = false
            screenState.loadingQuantity.value = false
            screenState.loadingPrice.value = false
            screenState.loadingDate.value = false
        }

        val item = itemRepository.get(itemId)
        if (item == null) {
            dispose()
            return@async false
        }

        val product = productRepository.get(item.productId)
        if (product == null) {
            dispose()
            return@async false
        }

        val variant =
            if (item.variantId != null)
                variantsRepository.get(item.variantId)
            else null

        val shop =
            if (item.shopId != null)
                shopRepository.get(item.shopId)
            else null

        screenState.selectedProduct.value = product
        screenState.selectedVariant.value = variant
        screenState.selectedShop.value = shop
        screenState.quantity.value = item.actualQuantity()
            .toString()
        screenState.price.value = item.actualPrice()
            .toString()
        screenState.date.value = item.date

        onProductChange()

        dispose()
        return@async true
    }
        .await()
}
