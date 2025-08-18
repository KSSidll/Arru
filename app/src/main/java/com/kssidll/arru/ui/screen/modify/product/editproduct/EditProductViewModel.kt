package com.kssidll.arru.ui.screen.modify.product.editproduct

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.product.ModifyProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@HiltViewModel
class EditProductViewModel
@Inject
constructor(
    override val productRepository: ProductRepositorySource,
    override val producerRepository: ProductProducerRepositorySource,
    override val categoryRepository: ProductCategoryRepositorySource,
) : ModifyProductViewModel() {
    private var mProduct: ProductEntity? = null

    private val mMergeMessageProductName: MutableState<String> = mutableStateOf(String())
    val mergeMessageProductName
        get() = mMergeMessageProductName.value

    val chosenMergeCandidate: MutableState<ProductEntity?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Updates data in the screen state
     *
     * @return true if provided [productId] was valid, false otherwise
     */
    suspend fun updateState(productId: Long) =
        viewModelScope
            .async {
                // skip state update for repeating productId
                if (productId == mProduct?.id) return@async true

                screenState.name.apply { value = value.toLoading() }
                screenState.selectedProductProducer.apply { value = value.toLoading() }
                screenState.selectedProductCategory.apply { value = value.toLoading() }

                mProduct = productRepository.get(productId).first()
                mMergeMessageProductName.value = mProduct?.name.orEmpty()

                val producer: ProductProducerEntity? =
                    mProduct?.productProducerEntityId?.let { producerRepository.get(it).first() }
                val category =
                    mProduct?.productCategoryEntityId?.let { categoryRepository.get(it).first() }

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

    /** @return list of merge candidates as flow */
    fun allMergeCandidates(productId: Long): Flow<ImmutableList<ProductEntity>> {
        return productRepository.all().map {
            it.filter { item -> item.id != productId }.toImmutableList()
        }
    }

    /**
     * Tries to update product with provided [productId] with current screen state data
     *
     * @return resulting [UpdateResult]
     */
    suspend fun updateProduct(productId: Long) =
        viewModelScope
            .async {
                screenState.attemptedToSubmit.value = true

                val result =
                    productRepository.update(
                        id = productId,
                        name = screenState.name.value.data.orEmpty(),
                        categoryId =
                            screenState.selectedProductCategory.value.data?.id
                                ?: ProductEntity.INVALID_CATEGORY_ID,
                        producerId = screenState.selectedProductProducer.value.data?.id,
                    )

                if (result.isError()) {
                    when (result.error!!) {
                        UpdateResult.InvalidId -> {
                            Log.e(
                                "InvalidId",
                                "Tried to update product with invalid product id in EditProductViewModel",
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
     * Tries to delete product with provided [productId], sets showDeleteWarning flag in state if
     * operation would require deleting foreign constrained data, state deleteWarningConfirmed flag
     * needs to be set to start foreign constrained data deletion
     *
     * @return resulting [DeleteResult]
     */
    suspend fun deleteProduct(productId: Long) =
        viewModelScope
            .async {
                val result =
                    productRepository.delete(productId, screenState.deleteWarningConfirmed.value)

                if (result.isError()) {
                    when (result.error!!) {
                        DeleteResult.InvalidId -> {
                            Log.e(
                                "InvalidId",
                                "Tried to delete product with invalid product id in EditProductViewModel",
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
     *
     * @return resulting [MergeResult]
     */
    suspend fun mergeWith(mergeCandidate: ProductEntity) =
        viewModelScope
            .async {
                if (mProduct == null) {
                    Log.e(
                        "InvalidId",
                        "Tried to merge product without the product being set in EditProductViewModel",
                    )
                    return@async MergeResult.Success
                }

                val result = productRepository.merge(mProduct!!, mergeCandidate)

                if (result.isError()) {
                    when (result.error!!) {
                        MergeResult.InvalidProduct -> {
                            Log.e(
                                "InvalidId",
                                "Tried to merge product without the product being set in EditProductViewModel",
                            )
                            return@async MergeResult.Success
                        }

                        MergeResult.InvalidMergingInto -> {
                            Log.e(
                                "InvalidId",
                                "Tried to merge product without the product being set in EditProductViewModel",
                            )
                            return@async MergeResult.Success
                        }
                    }
                }

                return@async result
            }
            .await()
}
