package com.kssidll.arrugarq.ui.screen.category

import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import kotlinx.coroutines.*

/**
 * Base [ViewModel] class for Category modification view models
 * @property screenState A [ModifyCategoryScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Category matching provided id, only changes representation data and loading state
 */
abstract class ModifyCategoryViewModel: ViewModel() {
    protected abstract val categoryRepository: ICategoryRepository

    internal val screenState: ModifyCategoryScreenState = ModifyCategoryScreenState()


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
