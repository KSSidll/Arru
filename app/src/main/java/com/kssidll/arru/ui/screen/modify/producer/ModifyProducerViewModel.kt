package com.kssidll.arru.ui.screen.modify.producer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.ModifyScreenState

/**
 * Base [ViewModel] class for Producer modification view models
 * @property screenState A [ModifyProducerScreenState] instance to use as screen state representation
 */
abstract class ModifyProducerViewModel: ViewModel() {
    protected abstract val producerRepository: ProductProducerRepositorySource
    internal val screenState: ModifyProducerScreenState = ModifyProducerScreenState()
}

/**
 * Data representing [ModifyProducerScreenImpl] screen state
 */
data class ModifyProducerScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded())
): ModifyScreenState()