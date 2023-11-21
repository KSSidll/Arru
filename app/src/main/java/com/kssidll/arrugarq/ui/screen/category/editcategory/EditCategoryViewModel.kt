package com.kssidll.arrugarq.ui.screen.category.editcategory


import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.category.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val categoryRepository: ICategoryRepository,
    private val itemRepository: IItemRepository,
    private val productRepository: IProductRepository,
    private val variantRepository: IVariantRepository,
): ViewModel() {
    internal val screenState: ModifyCategoryScreenState = ModifyCategoryScreenState()

    /**
     * Tries to update product with provided [categoryId] with current screen state data
     */
    fun updateCategory(categoryId: Long) = viewModelScope.launch {
        screenState.attemptedToSubmit.value = true
        val category = screenState.extractCategoryOrNull(categoryId) ?: return@launch

        categoryRepository.update(category)
    }

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
     * Updates data in the screen state
     * @return true if provided [categoryId] was valid, false otherwise
     */
    suspend fun updateState(categoryId: Long) = viewModelScope.async {
        screenState.loadingName.value = true

        val category = categoryRepository.get(categoryId)
        if (category == null) {
            screenState.loadingName.value = false
            return@async false
        }

        screenState.name.value = category.name

        screenState.loadingName.value = false
        return@async true
    }
        .await()
}
