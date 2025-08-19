package com.kssidll.arru.ui.screen.modify.shop

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.ModifyScreenState

// TODO refactor uiState Event UseCase

/**
 * Base [ViewModel] class for Shop modification view models
 *
 * @property screenState A [ModifyShopScreenState] instance to use as screen state representation
 */
abstract class ModifyShopViewModel : ViewModel() {
    protected abstract val shopRepository: ShopRepositorySource
    internal val screenState: ModifyShopScreenState = ModifyShopScreenState()
}

/** Data representing [ModifyShopScreenImpl] screen state */
data class ModifyShopScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded())
) : ModifyScreenState()
