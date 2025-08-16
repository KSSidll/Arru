package com.kssidll.arru.ui.screen.modify.variant

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.ModifyScreenState

/**
 * Base [ViewModel] class for Variant modification view models
 * @property screenState A [ModifyVariantScreenState] instance to use as screen state representation
 */
abstract class ModifyVariantViewModel: ViewModel() {
    protected abstract val variantRepository: ProductVariantRepositorySource
    internal val screenState: ModifyVariantScreenState = ModifyVariantScreenState()
}

/**
 * Data representing [ModifyVariantScreenImpl] screen state
 */
data class ModifyVariantScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
    val isVariantGlobal: MutableState<Field<Boolean>> = mutableStateOf(Field.Loaded()),
): ModifyScreenState()
