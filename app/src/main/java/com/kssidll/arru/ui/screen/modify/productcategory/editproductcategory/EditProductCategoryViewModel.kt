package com.kssidll.arru.ui.screen.modify.productcategory.editproductcategory

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteProductCategoryEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.MergeProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductCategoryEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.UpdateProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateProductCategoryEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryViewModel
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
class EditProductCategoryViewModel
@Inject
constructor(
    override val categoryRepository: ProductCategoryRepositorySource,
    private val updateProductCategoryEntityUseCase: UpdateProductCategoryEntityUseCase,
    private val mergeProductCategoryEntityUseCase: MergeProductCategoryEntityUseCase,
    private val deleteProductCategoryEntityUseCase: DeleteProductCategoryEntityUseCase,
) : ModifyProductCategoryViewModel() {
    private var mCategory: ProductCategoryEntity? = null

    private val mMergeMessageCategoryName: MutableState<String> = mutableStateOf(String())
    val mergeMessageCategoryName
        get() = mMergeMessageCategoryName.value

    val chosenMergeCandidate: MutableState<ProductCategoryEntity?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    suspend fun checkExists(id: Long): Boolean {
        return categoryRepository.get(id).first() != null
    }

    fun updateState(categoryId: Long) =
        viewModelScope.launch {
            // skip state update for repeating categoryId
            if (categoryId == mCategory?.id) return@launch

            screenState.name.value = screenState.name.value.toLoading()

            mCategory = categoryRepository.get(categoryId).first()
            mMergeMessageCategoryName.value = mCategory?.name.orEmpty()

            screenState.name.apply {
                value = mCategory?.let { Field.Loaded(it.name) } ?: value.toLoadedOrError()
            }
        }

    /** @return list of merge candidates as flow */
    fun allMergeCandidates(categoryId: Long): Flow<ImmutableList<ProductCategoryEntity>> {
        return categoryRepository.all().map {
            it.filter { item -> item.id != categoryId }.toImmutableList()
        }
    }

    suspend fun updateCategory(categoryId: Long): Boolean {
        screenState.attemptedToSubmit.value = true

        val result =
            updateProductCategoryEntityUseCase(id = categoryId, name = screenState.name.value.data)

        return when (result) {
            is UpdateProductCategoryEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        UpdateProductCategoryEntityUseCaseResult.ProductCategoryIdInvalid -> {
                            Log.e(
                                "ModifyProductCategory",
                                "Insert invalid product category `${categoryId}`",
                            )
                        }
                        UpdateProductCategoryEntityUseCaseResult.NameDuplicateValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.DuplicateValueError)
                            }
                        }
                        UpdateProductCategoryEntityUseCaseResult.NameNoValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                    }
                }

                false
            }
            is UpdateProductCategoryEntityUseCaseResult.Success -> {
                true
            }
        }
    }

    suspend fun deleteCategory(categoryId: Long): Boolean {
        val result =
            deleteProductCategoryEntityUseCase(categoryId, screenState.deleteWarningConfirmed.value)

        return when (result) {
            is DeleteProductCategoryEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        DeleteProductCategoryEntityUseCaseResult.DangerousDelete -> {
                            screenState.showDeleteWarning.value = true
                        }
                        DeleteProductCategoryEntityUseCaseResult.ProductCategoryIdInvalid -> {
                            Log.e(
                                "ModifyProductCategory",
                                "Tried to delete product category with invalid id",
                            )
                        }
                    }
                }

                false
            }
            is DeleteProductCategoryEntityUseCaseResult.Success -> {
                true
            }
        }
    }

    suspend fun mergeWith(mergeCandidate: ProductCategoryEntity): ProductCategoryEntity? {
        if (mCategory == null) {
            Log.e("ModifyProductCategory", "Tried to merge product category without being set")
            return null
        }

        val result = mergeProductCategoryEntityUseCase(mCategory!!.id, mergeCandidate.id)

        return when (result) {
            is MergeProductCategoryEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        MergeProductCategoryEntityUseCaseResult.MergeIntoIdInvalid -> {
                            Log.e(
                                "ModifyProductCategory",
                                "Tried to merge product category but merge id was invalid",
                            )
                        }
                        MergeProductCategoryEntityUseCaseResult.ProductCategoryIdInvalid -> {
                            Log.e(
                                "ModifyProductCategory",
                                "Tried to merge product category but id was invalid",
                            )
                        }
                    }
                }

                null
            }
            is MergeProductCategoryEntityUseCaseResult.Success -> {
                result.mergedEntity
            }
        }
    }
}
