package com.kssidll.arrugarq.ui.screen.modify.product.editproduct


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
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
    private val mMergeMessageProductName: MutableState<String> = mutableStateOf(String())
    val mergeMessageProductName get() = mMergeMessageProductName.value

    val chosenMergeCandidate: MutableState<Product?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    override suspend fun updateState(productId: Long): Boolean {
        return super.updateState(productId)
            .also {
                mMergeMessageProductName.value = mProduct?.name.orEmpty()
            }
    }

    /**
     * Tries to update product with provided [productId] with current screen state data
     * @return Whether the update was successful
     */
    suspend fun updateProduct(productId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        screenState.validate()

        val product = screenState.extractDataOrNull(productId) ?: return@async false
        val other = productRepository.getByNameAndProducerId(
            product.name,
            product.producerId
        )

        if (other != null) {
            if (other.id == productId) return@async true

            screenState.name.apply {
                value = value.toError(FieldError.DuplicateValueError)
            }

            screenState.selectedProductProducer.apply {
                value = value.toError(FieldError.DuplicateValueError)
            }

            chosenMergeCandidate.value = other
            showMergeConfirmDialog.value = true

            return@async false
        } else {
            productRepository.update(product)
            return@async true
        }
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

    /**
     * Tries to delete merge category into provided [mergeCandidate]
     * @return True if operation succeded, false otherwise
     */
    suspend fun mergeWith(mergeCandidate: Product) = viewModelScope.async {
        val items = mProduct?.let { itemRepository.getByProductId(it.id) } ?: return@async false
        val variants =
            mProduct?.let { variantRepository.getByProductId(it.id) } ?: return@async false

        if (items.isNotEmpty()) {
            items.forEach { it.productId = mergeCandidate.id }
            itemRepository.update(items)
        }

        if (variants.isNotEmpty()) {
            variants.forEach { it.productId = mergeCandidate.id }
            variantRepository.update(variants)
        }

        mProduct?.let { productRepository.delete(it) }

        return@async true
    }
        .await()
}
