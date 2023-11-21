package com.kssidll.arrugarq.ui.screen.product.editproduct


import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.product.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val productRepository: IProductRepository,
    private val producerRepository: IProducerRepository,
    private val categoryRepository: ICategoryRepository,
    private val variantRepository: IVariantRepository,
    private val itemRepository: IItemRepository,
): ViewModel() {
    internal val screenState: ModifyProductScreenState = ModifyProductScreenState()

    init {
        fillStateCategoriesWithAltNames()
        fillStateProducers()
    }

    /**
     * Tries to update product with provided [productId] with current screen state data
     */
    fun updateProduct(productId: Long) = viewModelScope.launch {
        screenState.attemptedToSubmit.value = true
        val product = screenState.extractProductOrNull(productId) ?: return@launch

        productRepository.update(product)
    }

    /**
     * Tries to delete product with provided [productId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return True if operation started, false otherwise
     */
    suspend fun deleteProduct(productId: Long) = viewModelScope.async {
        // return true if no such product exists
        val product = productRepository.get(productId) ?: return@async true

        val items = itemRepository.getByProductId(productId)
        val variants = variantRepository.getByProductId(productId)

        if ((items.isNotEmpty() || variants.isNotEmpty()) && !screenState.deleteWarningConfirmed.value) {
            screenState.showDeleteWarning.value = true
            return@async false
        } else {
            itemRepository.delete(items)
            variantRepository.delete(variants)
            productRepository.delete(product)
            return@async true
        }
    }
        .await()

    /**
     * Updates data in the screen state
     * @return true if provided [productId] was valid, false otherwise
     */
    suspend fun updateState(productId: Long) = viewModelScope.async {
        screenState.loadingName.value = true
        screenState.loadingProductProducer.value = true
        screenState.loadingProductCategory.value = true

        val dispose = {
            screenState.loadingName.value = false
            screenState.loadingProductProducer.value = false
            screenState.loadingProductCategory.value = false
        }

        val product = productRepository.get(productId)
        if (product == null) {
            dispose()
            return@async false
        }

        val producer =
            if (product.producerId != null) {
                producerRepository.get(product.producerId)
            } else {
                null
            }

        val category = categoryRepository.get(product.categoryId)
        if (category == null) {
            dispose()
            return@async false
        }


        screenState.name.value = product.name
        screenState.selectedProductProducer.value = producer
        screenState.selectedProductCategory.value = category

        dispose()
        return@async true
    }
        .await()

    private var fillStateCategoriesWithAltNamesJob: Job? = null

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillStateCategoriesWithAltNames() {
        fillStateCategoriesWithAltNamesJob?.cancel()
        fillStateCategoriesWithAltNamesJob = viewModelScope.launch {
            screenState.categoriesWithAltNames.value =
                categoryRepository.getAllWithAltNamesFlow()
        }
    }

    private var fillStateProducers: Job? = null

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillStateProducers() {
        fillStateProducers?.cancel()
        fillStateProducers = viewModelScope.launch {
            screenState.producers.value = producerRepository.getAllFlow()
        }
    }
}
