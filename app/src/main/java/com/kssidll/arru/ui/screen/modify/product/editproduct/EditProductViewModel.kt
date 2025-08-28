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
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteProductEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.MergeProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.UpdateProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateProductEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.product.ModifyProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// TODO refactor uiState Event UseCase

@HiltViewModel
class EditProductViewModel
@Inject
constructor(
    override val productRepository: ProductRepositorySource,
    override val producerRepository: ProductProducerRepositorySource,
    override val categoryRepository: ProductCategoryRepositorySource,
    private val updateProductEntityUseCase: UpdateProductEntityUseCase,
    private val deleteProductEntityUseCase: DeleteProductEntityUseCase,
    private val mergeProductEntityUseCase: MergeProductEntityUseCase,
) : ModifyProductViewModel() {
    private var mProduct: ProductEntity? = null

    private val mMergeMessageProductName: MutableState<String> = mutableStateOf(String())
    val mergeMessageProductName
        get() = mMergeMessageProductName.value

    val chosenMergeCandidate: MutableState<ProductEntity?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    suspend fun checkExists(id: Long): Boolean {
        return productRepository.get(id).first() != null
    }

    fun updateState(productId: Long) =
        viewModelScope.launch {
            // skip state update for repeating productId
            if (productId == mProduct?.id) return@launch

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
        }

    /** @return list of merge candidates as flow */
    fun allMergeCandidates(productId: Long): Flow<ImmutableList<ProductEntity>> {
        return productRepository.all().map {
            it.filter { item -> item.id != productId }.toImmutableList()
        }
    }

    suspend fun updateProduct(productId: Long): Boolean {
        screenState.attemptedToSubmit.value = true

        val result =
            updateProductEntityUseCase(
                id = productId,
                name = screenState.name.value.data,
                productProducerEntityId = screenState.selectedProductProducer.value.data?.id,
                productCategoryEntityId = screenState.selectedProductCategory.value.data?.id,
            )

        return when (result) {
            is UpdateProductEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        UpdateProductEntityUseCaseResult.ProductIdInvalid -> {
                            Log.e("ModifyProduct", "Insert invalid product `${productId}`")
                        }
                        UpdateProductEntityUseCaseResult.NameDuplicateValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.DuplicateValueError)
                            }
                        }
                        UpdateProductEntityUseCaseResult.NameNoValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                        UpdateProductEntityUseCaseResult.ProductCategoryIdInvalid -> {
                            screenState.selectedProductCategory.apply {
                                value = value.toError(FieldError.InvalidValueError)
                            }
                        }
                        UpdateProductEntityUseCaseResult.ProductCategoryNoValue -> {
                            screenState.selectedProductCategory.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                        UpdateProductEntityUseCaseResult.ProductProducerIdInvalid -> {
                            screenState.selectedProductProducer.apply {
                                value = value.toError(FieldError.InvalidValueError)
                            }
                        }
                    }
                }

                false
            }
            is UpdateProductEntityUseCaseResult.Success -> {
                true
            }
        }
    }

    suspend fun deleteProduct(productId: Long): Boolean {
        val result = deleteProductEntityUseCase(productId, screenState.deleteWarningConfirmed.value)

        return when (result) {
            is DeleteProductEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        DeleteProductEntityUseCaseResult.DangerousDelete -> {
                            screenState.showDeleteWarning.value = true
                        }
                        DeleteProductEntityUseCaseResult.ProductIdInvalid -> {
                            Log.e("ModifyProduct", "Tried to delete product with invalid id")
                        }
                    }
                }

                false
            }
            is DeleteProductEntityUseCaseResult.Success -> {
                true
            }
        }
    }

    suspend fun mergeWith(mergeCandidate: ProductEntity): ProductEntity? {
        if (mProduct == null) {
            Log.e("ModifyProduct", "Tried to merge product without being set")
            return null
        }

        val result = mergeProductEntityUseCase(mProduct!!.id, mergeCandidate.id)

        return when (result) {
            is MergeProductEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        MergeProductEntityUseCaseResult.MergeIntoIdInvalid -> {
                            Log.e(
                                "ModifyProduct",
                                "Tried to merge product but merge id was invalid",
                            )
                        }
                        MergeProductEntityUseCaseResult.ProductIdInvalid -> {
                            Log.e("ModifyProduct", "Tried to merge product but id was invalid")
                        }
                    }
                }

                null
            }
            is MergeProductEntityUseCaseResult.Success -> {
                result.mergedEntity
            }
        }
    }
}
