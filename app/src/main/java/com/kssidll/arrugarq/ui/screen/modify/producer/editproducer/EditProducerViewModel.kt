package com.kssidll.arrugarq.ui.screen.modify.producer.editproducer


import android.util.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.data.repository.ProducerRepositorySource.Companion.DeleteResult
import com.kssidll.arrugarq.data.repository.ProducerRepositorySource.Companion.MergeResult
import com.kssidll.arrugarq.data.repository.ProducerRepositorySource.Companion.UpdateResult
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.producer.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditProducerViewModel @Inject constructor(
    override val producerRepository: ProducerRepositorySource,
): ModifyProducerViewModel() {
    private val mMergeMessageProducerName: MutableState<String> = mutableStateOf(String())
    val mergeMessageProducerName get() = mMergeMessageProducerName.value

    val chosenMergeCandidate: MutableState<ProductProducer?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    override suspend fun updateState(producerId: Long): Boolean {
        return super.updateState(producerId)
            .also {
                mMergeMessageProducerName.value = mProducer?.name.orEmpty()
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
