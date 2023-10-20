package com.kssidll.arrugarq.ui.screen.addproductcategory

import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.shared.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddProductCategoryViewModel @Inject constructor(
    private val categoryRepository: IProductCategoryRepository,
): ViewModel() {
    internal val screenState: EditProductCategoryScreenState = EditProductCategoryScreenState()

    /**
     * Tries to add a product category to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addCategory(): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        val category = screenState.extractCategoryOrNull() ?: return@async null

        return@async categoryRepository.insert(category)
    }
        .await()
}