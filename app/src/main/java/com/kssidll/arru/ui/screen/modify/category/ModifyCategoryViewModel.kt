package com.kssidll.arru.ui.screen.modify.category

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.modify.*

/**
 * Base [ViewModel] class for Category modification view models
 * @property screenState A [ModifyCategoryScreenState] instance to use as screen state representation
 */
abstract class ModifyCategoryViewModel: ViewModel() {
    protected abstract val categoryRepository: CategoryRepositorySource
    internal val screenState: ModifyCategoryScreenState = ModifyCategoryScreenState()
}

/**
 * Data representing [ModifyCategoryScreenImpl] screen state
 */
data class ModifyCategoryScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded(String())),
): ModifyScreenState()