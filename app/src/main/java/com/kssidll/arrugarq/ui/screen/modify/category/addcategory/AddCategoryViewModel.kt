package com.kssidll.arrugarq.ui.screen.modify.category.addcategory

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.ui.screen.modify.category.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    override val categoryRepository: CategoryRepositorySource,
): ModifyCategoryViewModel() {

    /**
     * Tries to add a product category to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addCategory(): Long? = viewModelScope.async {
        //        screenState.attemptedToSubmit.value = true
        //        screenState.validate()
        //
        //        val category: ProductCategory = screenState.extractDataOrNull() ?: return@async null
        //        val other = categoryRepository.byName(category.name)
        //
        //        if (other != null) {
        //            screenState.name.apply {
        //                value = value.toError(FieldError.DuplicateValueError)
        //            }
        //
        //            return@async null
        //        } else {
        //            return@async categoryRepository.insert(category)
        //        }
        return@async 1L
        // TODO add use case
    }
        .await()
}