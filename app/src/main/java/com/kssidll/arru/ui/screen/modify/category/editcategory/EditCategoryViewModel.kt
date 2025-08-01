package com.kssidll.arru.ui.screen.modify.category.editcategory


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.repository.CategoryRepositorySource
import com.kssidll.arru.data.repository.CategoryRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.CategoryRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.CategoryRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.category.ModifyCategoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    override val categoryRepository: CategoryRepositorySource,
): ModifyCategoryViewModel() {
    private var mCategory: ProductCategory? = null

    private val mMergeMessageCategoryName: MutableState<String> = mutableStateOf(String())
    val mergeMessageCategoryName get() = mMergeMessageCategoryName.value

    val chosenMergeCandidate: MutableState<ProductCategory?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Updates data in the screen state
     * @return true if provided [categoryId] was valid, false otherwise
     */
    suspend fun updateState(categoryId: Long) = viewModelScope.async {
        // skip state update for repeating categoryId
        if (categoryId == mCategory?.id) return@async true

        screenState.name.value = screenState.name.value.toLoading()

        mCategory = categoryRepository.get(categoryId)
        mMergeMessageCategoryName.value = mCategory?.name.orEmpty()

        screenState.name.apply {
            value = mCategory?.let { Field.Loaded(it.name) } ?: value.toLoadedOrError()
        }

        return@async mCategory != null
    }
        .await()

    /**
     * @return list of merge candidates as flow
     */
    fun allMergeCandidates(categoryId: Long): Flow<Data<ImmutableList<ProductCategory>>> {
        return categoryRepository.allFlow()
            .map {
                if (it is Data.Loaded) {
                    Data.Loaded(it.data.filter { item -> item.id != categoryId }.toImmutableList())
                } else it
            }
    }

    /**
     * Tries to update category with provided [categoryId] with current screen state data
     * @return resulting [UpdateResult]
     */
    suspend fun updateCategory(categoryId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = categoryRepository.update(
            categoryId,
            screenState.name.value.data.orEmpty()
        )

        if (result.isError()) {
            when (result.error!!) {
                UpdateResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to update category with invalid category id in EditCategoryViewModel"
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
            }
        }

        return@async result
    }
        .await()

    /**
     * Tries to delete category with provided [categoryId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return resulting [DeleteResult]
     */
    suspend fun deleteCategory(categoryId: Long) = viewModelScope.async {
        val result = categoryRepository.delete(
            categoryId,
            screenState.deleteWarningConfirmed.value
        )

        if (result.isError()) {
            when (result.error!!) {
                DeleteResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to delete category with invalid category id in EditCategoryViewModel"
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
     * Tries to delete merge category into provided [mergeCandidate]
     * @return resulting [MergeResult]
     */
    suspend fun mergeWith(mergeCandidate: ProductCategory) = viewModelScope.async {
        if (mCategory == null) {
            Log.e(
                "InvalidId",
                "Tried to merge category without the category being set in EditCategoryViewModel"
            )
            return@async MergeResult.Success
        }

        val result = categoryRepository.merge(
            mCategory!!,
            mergeCandidate
        )

        if (result.isError()) {
            when (result.error!!) {
                MergeResult.InvalidCategory -> {
                    Log.e(
                        "InvalidId",
                        "Tried to merge category without the category being set in EditCategoryViewModel"
                    )
                    return@async MergeResult.Success
                }

                MergeResult.InvalidMergingInto -> {
                    Log.e(
                        "InvalidId",
                        "Tried to merge category without the category being set in EditCategoryViewModel"
                    )
                    return@async MergeResult.Success
                }
            }
        }

        return@async result
    }
        .await()
}
