package com.kssidll.arru.ui.screen.modify.category.addcategory

import androidx.lifecycle.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.data.repository.CategoryRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.modify.category.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    override val categoryRepository: CategoryRepositorySource,
): ModifyCategoryViewModel() {

    /**
     * Tries to add a product category to the repository
     * @return resulting [InsertResult]
     */
    suspend fun addCategory(): InsertResult = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = categoryRepository.insert(screenState.name.value.data.orEmpty())

        if (result.isError()) {
            when (result.error!!) {
                is InsertResult.InvalidName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                is InsertResult.DuplicateName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.DuplicateValueError)
                    }
                }
            }
        }

        return@async result
    }
        .await()
}