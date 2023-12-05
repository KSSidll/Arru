package com.kssidll.arrugarq.ui.screen.modify.product.editproduct


import android.database.sqlite.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.product.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditProductViewModel @Inject constructor(
    override val productRepository: ProductRepositorySource,
    override val producerRepository: ProducerRepositorySource,
    override val categoryRepository: CategoryRepositorySource,
    private val variantRepository: VariantRepositorySource,
    private val itemRepository: ItemRepositorySource,
): ModifyProductViewModel() {

    /**
     * Tries to update product with provided [productId] with current screen state data
     * @return Whether the update was successful
     */
    suspend fun updateProduct(productId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        screenState.validate()

        val product = screenState.extractDataOrNull(productId) ?: return@async false

        try {
            productRepository.update(product)
        } catch (_: SQLiteConstraintException) {
            screenState.name.apply { value = value.toError(FieldError.DuplicateValueError) }
            return@async false
        }

        return@async true
    }
        .await()

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
