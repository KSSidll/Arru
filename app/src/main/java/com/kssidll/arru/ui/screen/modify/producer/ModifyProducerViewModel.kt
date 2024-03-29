package com.kssidll.arru.ui.screen.modify.producer

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.modify.*

/**
 * Base [ViewModel] class for Producer modification view models
 * @property screenState A [ModifyProducerScreenState] instance to use as screen state representation
 */
abstract class ModifyProducerViewModel: ViewModel() {
    protected abstract val producerRepository: ProducerRepositorySource
    internal val screenState: ModifyProducerScreenState = ModifyProducerScreenState()
}

/**
 * Data representing [ModifyProducerScreenImpl] screen state
 */
data class ModifyProducerScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded())
): ModifyScreenState()