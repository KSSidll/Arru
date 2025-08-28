package com.kssidll.arru.ui.screen.modify.productproducer.editproductproducer

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteProductProducerEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.MergeProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductProducerEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.UpdateProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateProductProducerEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// TODO refactor uiState Event UseCase

@HiltViewModel
class EditProductProducerViewModel
@Inject
constructor(
    override val producerRepository: ProductProducerRepositorySource,
    private val updateProductProducerEntityUseCase: UpdateProductProducerEntityUseCase,
    private val mergeProductProducerEntityUseCase: MergeProductProducerEntityUseCase,
    private val deleteProductProducerEntityUseCase: DeleteProductProducerEntityUseCase,
) : ModifyProductProducerViewModel() {
    private var mProducer: ProductProducerEntity? = null

    private val mMergeMessageProducerName: MutableState<String> = mutableStateOf(String())
    val mergeMessageProducerName
        get() = mMergeMessageProducerName.value

    val chosenMergeCandidate: MutableState<ProductProducerEntity?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    suspend fun checkExists(id: Long): Boolean {
        return producerRepository.get(id).first() != null
    }

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

    suspend fun updateProducer(producerId: Long): Boolean {
        screenState.attemptedToSubmit.value = true

        val result =
            updateProductProducerEntityUseCase(id = producerId, name = screenState.name.value.data)

        return when (result) {
            is UpdateProductProducerEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        UpdateProductProducerEntityUseCaseResult.ProductProducerIdInvalid -> {
                            Log.e(
                                "ModifyProductProducer",
                                "Insert invalid product producer `${producerId}`",
                            )
                        }
                        UpdateProductProducerEntityUseCaseResult.NameDuplicateValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.DuplicateValueError)
                            }
                        }
                        UpdateProductProducerEntityUseCaseResult.NameNoValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                    }
                }

                false
            }
            is UpdateProductProducerEntityUseCaseResult.Success -> {
                true
            }
        }
    }

    suspend fun deleteProducer(producerId: Long): Boolean {
        val result =
            deleteProductProducerEntityUseCase(producerId, screenState.deleteWarningConfirmed.value)

        return when (result) {
            is DeleteProductProducerEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        DeleteProductProducerEntityUseCaseResult.DangerousDelete -> {
                            screenState.showDeleteWarning.value = true
                        }
                        DeleteProductProducerEntityUseCaseResult.ProductProducerIdInvalid -> {
                            Log.e(
                                "ModifyProductProducer",
                                "Tried to delete product producer with invalid id",
                            )
                        }
                    }
                }

                false
            }
            is DeleteProductProducerEntityUseCaseResult.Success -> {
                true
            }
        }
    }

    suspend fun mergeWith(mergeCandidate: ProductProducerEntity): ProductProducerEntity? {
        if (mProducer == null) {
            Log.e("ModifyProductProducer", "Tried to merge product producer without being set")
            return null
        }

        val result = mergeProductProducerEntityUseCase(mProducer!!.id, mergeCandidate.id)

        return when (result) {
            is MergeProductProducerEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        MergeProductProducerEntityUseCaseResult.MergeIntoIdInvalid -> {
                            Log.e(
                                "ModifyProductProducer",
                                "Tried to merge product producer but merge id was invalid",
                            )
                        }
                        MergeProductProducerEntityUseCaseResult.ProductProducerIdInvalid -> {
                            Log.e(
                                "ModifyProductProducer",
                                "Tried to merge product producer but id was invalid",
                            )
                        }
                    }
                }

                null
            }
            is MergeProductProducerEntityUseCaseResult.Success -> {
                result.mergedEntity
            }
        }
    }
}
