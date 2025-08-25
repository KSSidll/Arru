package com.kssidll.arru.ui.screen.modify.productproducer.editproductproducer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// TODO refactor uiState Event UseCase

@HiltViewModel
class EditProducerViewModel
@Inject
constructor(override val producerRepository: ProductProducerRepositorySource) :
    ModifyProductProducerViewModel() {
    private var mProducer: ProductProducerEntity? = null

    private val mMergeMessageProducerName: MutableState<String> = mutableStateOf(String())
    val mergeMessageProducerName
        get() = mMergeMessageProducerName.value

    val chosenMergeCandidate: MutableState<ProductProducerEntity?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    suspend fun checkExists(id: Long) =
        viewModelScope
            .async {
                return@async producerRepository.get(id).first() != null
            }
            .await()

    fun updateState(producerId: Long) =
        viewModelScope.launch {
            // skip state update for repeating producerId
            if (producerId == mProducer?.id) return@launch

            screenState.name.apply { value = value.toLoading() }

            mProducer = producerRepository.get(producerId).first()
            mMergeMessageProducerName.value = mProducer?.name.orEmpty()

            screenState.name.apply {
                value = mProducer?.name?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
            }
        }

    /** @return list of merge candidates as flow */
    fun allMergeCandidates(producerId: Long): Flow<ImmutableList<ProductProducerEntity>> {
        return producerRepository.all().map {
            it.filter { item -> item.id != producerId }.toImmutableList()
        }
    }

    /**
     * Tries to update producer with provided [producerId] with current screen state data
     *
     * @return resulting [UpdateResult]
     */
    // suspend fun updateProducer(producerId: Long) =
    //     viewModelScope
    //         .async {
    //             screenState.attemptedToSubmit.value = true
    //
    //             val result =
    //                 producerRepository.update(producerId, screenState.name.value.data.orEmpty())
    //
    //             if (result.isError()) {
    //                 when (result.error!!) {
    //                     UpdateResult.InvalidId -> {
    //                         Log.e(
    //                             "InvalidId",
    //                             "Tried to update producer with invalid producer id in
    // EditProducerViewModel",
    //                         )
    //                         return@async UpdateResult.Success
    //                     }
    //
    //                     UpdateResult.InvalidName -> {
    //                         screenState.name.apply {
    //                             value = value.toError(FieldError.InvalidValueError)
    //                         }
    //                     }
    //
    //                     UpdateResult.DuplicateName -> {
    //                         screenState.name.apply {
    //                             value = value.toError(FieldError.DuplicateValueError)
    //                         }
    //                     }
    //                 }
    //             }
    //
    //             return@async result
    //         }
    //         .await()
    //
    // /**
    //  * Tries to delete product producer with provided [producerId], sets showDeleteWarning flag
    // in
    //  * state if operation would require deleting foreign constrained data, state
    //  * deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
    //  *
    //  * @return resulting [DeleteResult]
    //  */
    // suspend fun deleteProducer(producerId: Long) =
    //     viewModelScope
    //         .async {
    //             val result =
    //                 producerRepository.delete(producerId,
    // screenState.deleteWarningConfirmed.value)
    //
    //             if (result.isError()) {
    //                 when (result.error!!) {
    //                     DeleteResult.InvalidId -> {
    //                         Log.e(
    //                             "InvalidId",
    //                             "Tried to delete producer with invalid producer id in
    // EditProducerViewModel",
    //                         )
    //                         return@async DeleteResult.Success
    //                     }
    //
    //                     DeleteResult.DangerousDelete -> {
    //                         screenState.showDeleteWarning.value = true
    //                     }
    //                 }
    //             }
    //
    //             return@async result
    //         }
    //         .await()
    //
    // /**
    //  * Tries to delete merge producer into provided [mergeCandidate]
    //  *
    //  * @return resulting [MergeResult]
    //  */
    // suspend fun mergeWith(mergeCandidate: ProductProducerEntity) =
    //     viewModelScope
    //         .async {
    //             if (mProducer == null) {
    //                 Log.e(
    //                     "InvalidId",
    //                     "Tried to merge producer without the producer being set in
    // EditProducerViewModel",
    //                 )
    //                 return@async MergeResult.Success
    //             }
    //
    //             val result = producerRepository.merge(mProducer!!, mergeCandidate)
    //
    //             if (result.isError()) {
    //                 when (result.error!!) {
    //                     MergeResult.InvalidProducer -> {
    //                         Log.e(
    //                             "InvalidId",
    //                             "Tried to merge producer without the producer being set in
    // EditProducerViewModel",
    //                         )
    //                         return@async MergeResult.Success
    //                     }
    //
    //                     MergeResult.InvalidMergingInto -> {
    //                         Log.e(
    //                             "InvalidId",
    //                             "Tried to merge producer without the producer being set in
    // EditProducerViewModel",
    //                         )
    //                         return@async MergeResult.Success
    //                     }
    //                 }
    //             }
    //
    //             return@async result
    //         }
    //         .await()
}
