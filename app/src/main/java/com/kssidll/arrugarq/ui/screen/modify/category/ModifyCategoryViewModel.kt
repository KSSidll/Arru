package com.kssidll.arrugarq.ui.screen.modify.category

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import kotlinx.coroutines.*

/**
 * Base [ViewModel] class for Category modification view models
 * @property screenState A [ModifyCategoryScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Category matching provided id, only changes representation data and loading state
 */
abstract class ModifyCategoryViewModel: ViewModel() {
    protected abstract val categoryRepository: CategoryRepositorySource

    internal val screenState: ModifyCategoryScreenState = ModifyCategoryScreenState()


    /**
     * Updates data in the screen state
     * @return true if provided [categoryId] was valid, false otherwise
     */
    suspend fun updateState(categoryId: Long) = viewModelScope.async {
        screenState.name.value = screenState.name.value.toLoading()

        val category = categoryRepository.get(categoryId)

        if (category != null) {
            screenState.name.apply {
                value = Field.Loaded(category.name)
            }
            return@async true
        } else {
            screenState.name.apply {
                value = value.toLoadedOrError()
            }
            return@async false
        }

    }
        .await()
}
