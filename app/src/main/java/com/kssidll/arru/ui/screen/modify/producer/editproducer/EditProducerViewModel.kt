package com.kssidll.arru.ui.screen.modify.producer.editproducer


import android.util.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.data.repository.ProducerRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ProducerRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ProducerRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.modify.producer.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class EditProducerViewModel @Inject constructor(
    override val producerRepository: ProducerRepositorySource,
): ModifyProducerViewModel() {
    private var mProducer: ProductProducer? = null

    private val mMergeMessageProducerName: MutableState<String> = mutableStateOf(String())
    val mergeMessageProducerName get() = mMergeMessageProducerName.value

    val chosenMergeCandidate: MutableState<ProductProducer?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Updates data in the screen state
     * @return true if provided [producerId] was valid, false otherwise
     */
    suspend fun updateState(producerId: Long) = viewModelScope.async {
        // skip state update for repeating producerId
        if (producerId == mProducer?.id) return@async true

        screenState.name.apply { value = value.toLoading() }

        mProducer = producerRepository.get(producerId)
        mMergeMessageProducerName.value = mProducer?.name.orEmpty()

        screenState.name.apply {
            value = mProducer?.name?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        return@async mProducer != null
    }
        .await()

    /**
     * @return list of merge candidates as flow
     */
    fun allMergeCandidates(producerId: Long): Flow<Data<List<ProductProducer>>> {
        return producerRepository.allFlow()
            .onEach {
                if (it is Data.Loaded) {
                    it.data.filter { item -> item.id != producerId }
                }
            }
    }

    /**
     * Tries to update producer with provided [producerId] with current screen state data
     * @return resulting [UpdateResult]
     */
    suspend fun updateProducer(producerId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = producerRepository.update(
            producerId,
            screenState.name.value.data.orEmpty()
        )

        if (result.isError()) {
            when (result.error!!) {
                UpdateResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to update producer with invalid producer id in EditProducerViewModel"
                    )
                    return@async UpdateResult.Success
                }

                UpdateResult.InvalidName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                UpdateResult.DuplicateName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.DuplicateValueError)
                    }
                }
            }
        }

        return@async result
    }
        .await()

    /**
     * Tries to delete product producer with provided [producerId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return resulting [DeleteResult]
     */
    suspend fun deleteProducer(producerId: Long) = viewModelScope.async {
        val result = producerRepository.delete(
            producerId,
            screenState.deleteWarningConfirmed.value
        )

        if (result.isError()) {
            when (result.error!!) {
                DeleteResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to delete producer with invalid producer id in EditProducerViewModel"
                    )
                    return@async DeleteResult.Success
                }

                DeleteResult.DangerousDelete -> {
                    screenState.showDeleteWarning.value = true
                }
            }
        }

        return@async result
    }
        .await()

    /**
     * Tries to delete merge producer into provided [mergeCandidate]
     * @return resulting [MergeResult]
     */
    suspend fun mergeWith(mergeCandidate: ProductProducer) = viewModelScope.async {
        if (mProducer == null) {
            Log.e(
                "InvalidId",
                "Tried to merge producer without the producer being set in EditProducerViewModel"
            )
            return@async MergeResult.Success
        }

        val result = producerRepository.merge(
            mProducer!!,
            mergeCandidate
        )

        if (result.isError()) {
            when (result.error!!) {
                MergeResult.InvalidProducer -> {
                    Log.e(
                        "InvalidId",
                        "Tried to merge producer without the producer being set in EditProducerViewModel"
                    )
                    return@async MergeResult.Success
                }

                MergeResult.InvalidMergingInto -> {
                    Log.e(
                        "InvalidId",
                        "Tried to merge producer without the producer being set in EditProducerViewModel"
                    )
                    return@async MergeResult.Success
                }
            }
        }

        return@async result
    }
        .await()
}
