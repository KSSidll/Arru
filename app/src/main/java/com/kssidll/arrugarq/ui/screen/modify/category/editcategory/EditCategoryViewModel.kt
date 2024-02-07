package com.kssidll.arrugarq.ui.screen.modify.category.editcategory


import android.util.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.data.repository.CategoryRepositorySource.Companion.DeleteResult
import com.kssidll.arrugarq.data.repository.CategoryRepositorySource.Companion.MergeResult
import com.kssidll.arrugarq.data.repository.CategoryRepositorySource.Companion.UpdateResult
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.category.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    override val categoryRepository: CategoryRepositorySource,
): ModifyCategoryViewModel() {
    private val mMergeMessageCategoryName: MutableState<String> = mutableStateOf(String())
    val mergeMessageCategoryName get() = mMergeMessageCategoryName.value

    val chosenMergeCandidate: MutableState<ProductCategory?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    override suspend fun updateState(categoryId: Long): Boolean {
        return super.updateState(categoryId)
            .also {
                mMergeMessageCategoryName.value = mCategory?.name.orEmpty()
            }
    }

    /**
     * Tries to update product with provided [categoryId] with current screen state data
     * @return Whether the update was successful
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
     * @return True if operation started, false otherwise
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
     * @return True if operation succeded, false otherwise
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
