package com.kssidll.arrugarq.ui.screen.modify.product.editproduct


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.ui.screen.modify.product.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditProductViewModel @Inject constructor(
    override val productRepository: ProductRepositorySource,
    override val producerRepository: ProducerRepositorySource,
    override val categoryRepository: CategoryRepositorySource,
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
     * @return resulting [UpdateResult]
     */
    suspend fun updateProduct(productId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        return@async true
        // TODO add use case
    }
        .await()

    /**
     * Tries to delete product with provided [productId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return resulting [DeleteResult]
     */
    suspend fun deleteProduct(productId: Long) = viewModelScope.async {
        return@async true
        // TODO add use case
    }
        .await()

    /**
     * Tries to delete merge product into provided [mergeCandidate]
     * @return resulting [MergeResult]
     */
    suspend fun mergeWith(mergeCandidate: Product) = viewModelScope.async {
        return@async true
        // TODO add use case
    }
        .await()
}
