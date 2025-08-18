package com.kssidll.arru.ui.screen.modify.productvariant

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.ModifyScreenState

/**
 * Base [ViewModel] class for Variant modification view models
 *
 * @property screenState A [ModifyProductVariantScreenState] instance to use as screen state
 *   representation
 */
abstract class ModifyProductVariantViewModel : ViewModel() {
    protected abstract val variantRepository: ProductVariantRepositorySource
    internal val screenState: ModifyProductVariantScreenState = ModifyProductVariantScreenState()
}

/** Data representing [ModifyProductVariantScreenImpl] screen state */
data class ModifyProductVariantScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
    val isVariantGlobal: MutableState<Field<Boolean>> = mutableStateOf(Field.Loaded()),
) : ModifyScreenState()
