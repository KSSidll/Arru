package com.kssidll.arru.ui.screen.modify.product.editproduct


import android.util.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.modify.product.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class EditProductViewModel @Inject constructor(
    override val productRepository: ProductRepositorySource,
    override val producerRepository: ProducerRepositorySource,
    override val categoryRepository: CategoryRepositorySource,
): ModifyProductViewModel() {
    private var mProduct: Product? = null

    private val mMergeMessageProductName: MutableState<String> = mutableStateOf(String())
    val mergeMessageProductName get() = mMergeMessageProductName.value

    val chosenMergeCandidate: MutableState<Product?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Updates data in the screen state
     * @return true if provided [productId] was valid, false otherwise
     */
    suspend fun updateState(productId: Long) = viewModelScope.async {
        // skip state update for repeating productId
        if (productId == mProduct?.id) return@async true

        screenState.name.apply { value = value.toLoading() }
        screenState.selectedProductProducer.apply { value = value.toLoading() }
        screenState.selectedProductCategory.apply { value = value.toLoading() }

        mProduct = productRepository.get(productId)
        mMergeMessageProductName.value = mProduct?.name.orEmpty()

        val producer: ProductProducer? = mProduct?.producerId?.let { producerRepository.get(it) }
        val category = mProduct?.categoryId?.let { categoryRepository.get(it) }

        screenState.name.apply {
            value = mProduct?.name?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        screenState.selectedProductProducer.apply {
            value = producer?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        screenState.selectedProductCategory.apply {
            value = category?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        return@async mProduct != null
    }
        .await()

    /**
     * @return list of merge candidates as flow
     */
    fun allMergeCandidates(productId: Long): Flow<Data<List<Product>>> {
        return productRepository.allFlow()
            .onEach {
                if (it is Data.Loaded) {
                    it.data.filter { item -> item.id != productId }
                }
            }
    }

    /**
     * Tries to update product with provided [productId] with current screen state data
     * @return resulting [UpdateResult]
     */
    suspend fun updateProduct(productId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = productRepository.update(
            productId = productId,
            name = screenState.name.value.data.orEmpty(),
            categoryId = screenState.selectedProductCategory.value.data?.id
                ?: Product.INVALID_CATEGORY_ID,
            producerId = screenState.selectedProductProducer.value.data?.id
        )

        if (result.isError()) {
            when (result.error!!) {
                UpdateResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to update product with invalid product id in EditProductViewModel"
                    )
                    return@async UpdateResult.Success
                }

                UpdateResult.InvalidName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                UpdateResult.DuplicateName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.DuplicateValueError)
                    }
                }

                UpdateResult.InvalidCategoryId -> {
                    screenState.selectedProductCategory.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                UpdateResult.InvalidProducerId -> {
                    screenState.selectedProductProducer.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }
            }
        }

        return@async result
    }
        .await()

    /**
     * Tries to delete product with provided [productId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return resulting [DeleteResult]
     */
    suspend fun deleteProduct(productId: Long) = viewModelScope.async {
        val result = productRepository.delete(
            productId,
            screenState.deleteWarningConfirmed.value
        )

        if (result.isError()) {
            when (result.error!!) {
                DeleteResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to delete product with invalid product id in EditProductViewModel"
                    )
                    return@async DeleteResult.Success
                }

                DeleteResult.DangerousDelete -> {
                    screenState.showDeleteWarning.value = true
                }
            }
        }

        return@async result
    }
        .await()

    /**
     * Tries to delete merge product into provided [mergeCandidate]
     * @return resulting [MergeResult]
     */
    suspend fun mergeWith(mergeCandidate: Product) = viewModelScope.async {
        if (mProduct == null) {
            Log.e(
                "InvalidId",
                "Tried to merge product without the product being set in EditProductViewModel"
            )
            return@async MergeResult.Success
        }

        val result = productRepository.merge(
            mProduct!!,
            mergeCandidate
        )

        if (result.isError()) {
            when (result.error!!) {
                MergeResult.InvalidProduct -> {
                    Log.e(
                        "InvalidId",
                        "Tried to merge product without the product being set in EditProductViewModel"
                    )
                    return@async MergeResult.Success
                }

                MergeResult.InvalidMergingInto -> {
                    Log.e(
                        "InvalidId",
                        "Tried to merge product without the product being set in EditProductViewModel"
                    )
                    return@async MergeResult.Success
                }
            }
        }

        return@async result
    }
        .await()
}
