package com.kssidll.arru.ui.screen.modify.productcategory

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.ModifyScreenState

/**
 * Base [ViewModel] class for Category modification view models
 * @property screenState A [ModifyProductCategoryScreenState] instance to use as screen state representation
 */
abstract class ModifyProductCategoryViewModel: ViewModel() {
    protected abstract val categoryRepository: ProductCategoryRepositorySource
    internal val screenState: ModifyProductCategoryScreenState = ModifyProductCategoryScreenState()
}

/**
 * Data representing [ModifyProductCategoryScreenImpl] screen state
 */
data class ModifyProductCategoryScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded(String())),
): ModifyScreenState()