package com.kssidll.arrugarq.ui.screen.modify.producer

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Base [ViewModel] class for Producer modification view models
 * @property screenState A [ModifyProducerScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Producer matching provided id, only changes representation data and loading state
 */
abstract class ModifyProducerViewModel: ViewModel() {
    protected abstract val producerRepository: ProducerRepositorySource
    protected var mProducer: ProductProducer? = null
    internal val screenState: ModifyProducerScreenState = ModifyProducerScreenState()

    /**
     * Updates data in the screen state
     * @return true if provided [producerId] was valid, false otherwise
     */
    open suspend fun updateState(producerId: Long) = viewModelScope.async {
        screenState.name.apply { value = value.toLoading() }

        mProducer = producerRepository.get(producerId)

        screenState.name.apply {
            value = mProducer?.name?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        return@async mProducer != null
    }
        .await()

    /**
     * @return list of merge candidates as flow
     */
    fun allMergeCandidates(producerId: Long): Flow<List<ProductProducer>> {
        return producerRepository.allFlow()
            .onEach { it.filter { item -> item.id != producerId } }
            .distinctUntilChanged()
    }
}

/**
 * Data representing [ModifyProducerScreenImpl] screen state
 */
data class ModifyProducerScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded())
): ModifyScreenState()