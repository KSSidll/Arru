package com.kssidll.arrugarq.ui.screen.modify.shop

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*

/**
 * Base [ViewModel] class for Shop modification view models
 * @property screenState A [ModifyShopScreenState] instance to use as screen state representation
 */
abstract class ModifyShopViewModel: ViewModel() {
    protected abstract val shopRepository: ShopRepositorySource
    internal val screenState: ModifyShopScreenState = ModifyShopScreenState()
}

/**
 * Data representing [ModifyShopScreenImpl] screen state
 */
data class ModifyShopScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
): ModifyScreenState()