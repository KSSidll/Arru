package com.kssidll.arrugarq.ui.screen.modify.product.editproduct


import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.modify.product.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditProductViewModel @Inject constructor(
    override val productRepository: IProductRepository,
    override val producerRepository: IProducerRepository,
    override val categoryRepository: ICategoryRepository,
    private val variantRepository: IVariantRepository,
    private val itemRepository: IItemRepository,
): ModifyProductViewModel() {

    init {
        initialize()
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
}
