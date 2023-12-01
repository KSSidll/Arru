package com.kssidll.arrugarq.ui.screen.modify.category.editcategory


import android.database.sqlite.*
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

    /**
     * Tries to update product with provided [categoryId] with current screen state data
     * @return Whether the update was successful
     */
    suspend fun updateCategory(categoryId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        screenState.validate()

        val category: ProductCategory =
            screenState.extractDataOrNull(categoryId) ?: return@async false

        try {
            categoryRepository.update(category)
        } catch (_: SQLiteConstraintException) {
            screenState.name.let {
                it.value = it.value.toError(FieldError.DuplicateValueError)
            }
            return@async false
        }

        return@async true
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
}
