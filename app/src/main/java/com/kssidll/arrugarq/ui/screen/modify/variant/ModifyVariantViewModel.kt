package com.kssidll.arrugarq.ui.screen.modify.variant

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*

/**
 * Base [ViewModel] class for Variant modification view models
 * @property screenState A [ModifyVariantScreenState] instance to use as screen state representation
 */
abstract class ModifyVariantViewModel: ViewModel() {
    protected abstract val variantRepository: VariantRepositorySource
    internal val screenState: ModifyVariantScreenState = ModifyVariantScreenState()
}

/**
 * Data representing [ModifyVariantScreenImpl] screen state
 */
data class ModifyVariantScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
): ModifyScreenState()
