package com.kssidll.arru.ui.screen.modify.productproducer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.ModifyScreenState

/**
 * Base [ViewModel] class for Producer modification view models
 *
 * @property screenState A [ModifyProductProducerScreenState] instance to use as screen state
 *   representation
 */
abstract class ModifyProductProducerViewModel : ViewModel() {
    protected abstract val producerRepository: ProductProducerRepositorySource
    internal val screenState: ModifyProductProducerScreenState = ModifyProductProducerScreenState()
}

/** Data representing [ModifyProductProducerScreenImpl] screen state */
data class ModifyProductProducerScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded())
) : ModifyScreenState()
