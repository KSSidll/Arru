package com.kssidll.arru.ui.screen.modify.productproducer

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetAllProductProducerEntityUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ModifyProductProducerUiState(
    val currentProductProducer: ProductProducerEntity? = null,
    val allProductProducers: ImmutableList<ProductProducerEntity> = emptyImmutableList(),
    val name: Field<String> = Field.Loaded(String()),
    val selectedMergeCandidate: ProductProducerEntity? = null,
    val isDeleteEnabled: Boolean = false,
    val isDangerousDeleteDialogVisible: Boolean = false,
    val isDangerousDeleteDialogConfirmed: Boolean = false,
    val isMergeEnabled: Boolean = false,
    val isMergeSearchDialogVisible: Boolean = false,
    val isMergeConfirmationDialogVisible: Boolean = false,
)

@Immutable
sealed class ModifyProductProducerEvent {
    data object NavigateBack : ModifyProductProducerEvent()

    data object Submit : ModifyProductProducerEvent()

    data object DeleteProductProducer : ModifyProductProducerEvent()

    data class SetDangerousDeleteDialogVisibility(val visible: Boolean) :
        ModifyProductProducerEvent()

    data class SetDangerousDeleteDialogConfirmation(val confirmed: Boolean) :
        ModifyProductProducerEvent()

    data class SetName(val name: String) : ModifyProductProducerEvent()

    data class MergeProductProducer(val mergeInto: ProductProducerEntity?) :
        ModifyProductProducerEvent()

    data class SetMergeConfirmationDialogVisibility(val visible: Boolean) :
        ModifyProductProducerEvent()

    data class SetMergeSearchDialogVisibility(val visible: Boolean) : ModifyProductProducerEvent()

    data class SelectMergeCandidate(val mergeInto: ProductProducerEntity?) :
        ModifyProductProducerEvent()
}

sealed class ModifyProductProducerEventResult {
    data object Success : ModifyProductProducerEventResult()

    data object Failure : ModifyProductProducerEventResult()

    data class SuccessInsert(val id: Long) : ModifyProductProducerEventResult()

    data object SuccessUpdate : ModifyProductProducerEventResult()

    data object SuccessDelete : ModifyProductProducerEventResult()

    data class SuccessMerge(val id: Long) : ModifyProductProducerEventResult()
}

abstract class ModifyProductProducerViewModel : ViewModel() {
    @Suppress("PropertyName")
    protected val _uiState = MutableStateFlow(ModifyProductProducerUiState())
    val uiState = _uiState.asStateFlow()

    protected abstract val getAllProductProducerEntityUseCase: GetAllProductProducerEntityUseCase

    fun init() {
        viewModelScope.launch {
            getAllProductProducerEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allProductProducers = it) }
            }
        }
    }

    open suspend fun handleEvent(
        event: ModifyProductProducerEvent
    ): ModifyProductProducerEventResult {
        when (event) {
            is ModifyProductProducerEvent.DeleteProductProducer -> {}
            is ModifyProductProducerEvent.MergeProductProducer -> {}
            is ModifyProductProducerEvent.NavigateBack -> {}
            is ModifyProductProducerEvent.SelectMergeCandidate -> {}
            is ModifyProductProducerEvent.SetDangerousDeleteDialogConfirmation -> {}
            is ModifyProductProducerEvent.SetDangerousDeleteDialogVisibility -> {}
            is ModifyProductProducerEvent.SetMergeConfirmationDialogVisibility -> {}
            is ModifyProductProducerEvent.SetMergeSearchDialogVisibility -> {}
            is ModifyProductProducerEvent.Submit -> {}
            is ModifyProductProducerEvent.SetName -> {
                _uiState.update { currentState ->
                    currentState.copy(name = Field.Loaded(event.name))
                }
            }
        }

        return ModifyProductProducerEventResult.Success
    }
}
