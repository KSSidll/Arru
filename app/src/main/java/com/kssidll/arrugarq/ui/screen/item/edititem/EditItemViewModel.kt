package com.kssidll.arrugarq.ui.screen.item.edititem


import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.item.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditItemViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
    private val productRepository: IProductRepository,
    private val variantsRepository: IVariantRepository,
    private val shopRepository: IShopRepository,
): ViewModel() {
    internal val screenState: ModifyItemScreenState = ModifyItemScreenState()

    init {
        fillStateShops()
        fillStateProductsWithAltNames()
    }

    /**
     * Tries to update item with provided [itemId] with current screen state data
     */
    fun updateItem(itemId: Long) = viewModelScope.launch {
        screenState.attemptedToSubmit.value = true
        val item = screenState.extractItemOrNull(itemId) ?: return@launch

        itemRepository.update(item)
    }

    /**
     * Tries to delete item with provided [itemId]
     * @return True if operation started, false otherwise
     */
    suspend fun deleteItem(itemId: Long) = viewModelScope.async {
        // return true if no such item exists
        val item = itemRepository.get(itemId) ?: return@async true

        itemRepository.delete(item)
        return@async true
    }
        .await()

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

    /**
     * Fetches data related to a product to state, should be called after a state representation of product changes
     */
    fun onProductChange() = viewModelScope.launch {
        screenState.loadingPrice.value = true
        screenState.loadingQuantity.value = true

        fillStateProductVariants()

        val lastItemByProduct =
            itemRepository.getLastByProductId(screenState.selectedProduct.value!!.id)

        if (lastItemByProduct == null) {
            screenState.price.value = String()
            screenState.quantity.value = String()
            screenState.selectedVariant.value = null

            return@launch
        }

        screenState.selectedVariant.value = lastItemByProduct.variantId?.let {
            variantsRepository.get(it)
        }

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

            screenState.validateQuantity()
            screenState.validatePrice()
        }

    private var fillStateShopsJob: Job? = null

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillStateShops() {
        fillStateShopsJob?.cancel()
        fillStateShopsJob = viewModelScope.launch {
            screenState.shops.value = shopRepository.getAllFlow()
        }
    }

    private var fillStateProductsWithAltNamesJob: Job? = null

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillStateProductsWithAltNames() {
        fillStateProductsWithAltNamesJob?.cancel()
        fillStateProductsWithAltNamesJob = viewModelScope.launch {
            screenState.productsWithAltNames.value = productRepository.getAllWithAltNamesFlow()
        }
    }

    private var fillStateProductVariantsJob: Job? = null

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillStateProductVariants() {
        fillStateProductVariantsJob?.cancel()
        fillStateProductVariantsJob = viewModelScope.launch {
            with(screenState.selectedProduct) {
                if (value != null) {
                    screenState.variants.value = variantsRepository.getByProductIdFlow(value!!.id)
                }
            }
        }
    }
}
