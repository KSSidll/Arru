package com.kssidll.arrugarq.ui.screen.modify.category.editcategory


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.category.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    override val categoryRepository: CategoryRepositorySource,
    private val itemRepository: ItemRepositorySource,
    private val productRepository: ProductRepositorySource,
    private val variantRepository: VariantRepositorySource,
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
        screenState.validate()

        val category: ProductCategory =
            screenState.extractDataOrNull(categoryId) ?: return@async false

        val other = categoryRepository.getByName(category.name)
        if (other != null) {
            screenState.name.apply {
                value = value.toError(FieldError.DuplicateValueError)
            }

            chosenMergeCandidate.value = other
            showMergeConfirmDialog.value = true

            return@async false
        } else {
            categoryRepository.update(category)
            return@async true
        }
    }
        .await()

    /**
     * Tries to delete category with provided [categoryId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return True if operation started, false otherwise
     */
    suspend fun deleteCategory(categoryId: Long) = viewModelScope.async {
        // return true if no such category exists
        val category = categoryRepository.get(categoryId) ?: return@async true

        val products = productRepository.getByCategoryId(categoryId)

        val items = buildList {
            products.forEach {
                addAll(itemRepository.getByProductId(it.id))
            }
        }.toList()

        val variants = buildList {
            products.forEach {
                addAll(variantRepository.getByProductId(it.id))
            }
        }.toList()

        if ((products.isNotEmpty() || items.isNotEmpty() || variants.isNotEmpty()) && !screenState.deleteWarningConfirmed.value) {
            screenState.showDeleteWarning.value = true
            return@async false
        } else {
            itemRepository.delete(items)
            variantRepository.delete(variants)
            productRepository.delete(products)
            categoryRepository.delete(category)
            return@async true
        }
    }
        .await()

    /**
     * Tries to delete merge category into provided [mergeCandidate]
     * @return True if operation succeded, false otherwise
     */
    suspend fun mergeWith(mergeCandidate: ProductCategory) = viewModelScope.async {
        val products =
            mCategory?.let { productRepository.getByCategoryId(it.id) } ?: return@async false

        if (products.isNotEmpty()) {
            products.forEach { it.categoryId = mergeCandidate.id }
            productRepository.update(products)
        }

        mCategory?.let { categoryRepository.delete(it) }

        return@async true
    }
        .await()
}
