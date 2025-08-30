package com.kssidll.arru.ui.screen.modify.shop

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetAllShopEntityUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ModifyShopUiState(
    val currentShop: ShopEntity? = null,
    val allShops: ImmutableList<ShopEntity> = emptyImmutableList(),
    val name: Field<String> = Field.Loaded(String()),
    val selectedMergeCandidate: ShopEntity? = null,
    val isDeleteEnabled: Boolean = false,
    val isDangerousDeleteDialogVisible: Boolean = false,
    val isDangerousDeleteDialogConfirmed: Boolean = false,
    val isMergeEnabled: Boolean = false,
    val isMergeSearchDialogVisible: Boolean = false,
    val isMergeConfirmationDialogVisible: Boolean = false,
)

@Immutable
sealed class ModifyShopEvent {
    data object NavigateBack : ModifyShopEvent()

    data object Submit : ModifyShopEvent()

    data object DeleteShop : ModifyShopEvent()

    data class SetDangerousDeleteDialogVisibility(val visible: Boolean) : ModifyShopEvent()

    data class SetDangerousDeleteDialogConfirmation(val confirmed: Boolean) : ModifyShopEvent()

    data class SetName(val name: String) : ModifyShopEvent()

    data class MergeShop(val mergeInto: ShopEntity?) : ModifyShopEvent()

    data class SetMergeConfirmationDialogVisibility(val visible: Boolean) : ModifyShopEvent()

    data class SetMergeSearchDialogVisibility(val visible: Boolean) : ModifyShopEvent()

    data class SelectMergeCandidate(val mergeInto: ShopEntity?) : ModifyShopEvent()
}

sealed class ModifyShopEventResult {
    data object Success : ModifyShopEventResult()

    data object Failure : ModifyShopEventResult()

    data class SuccessInsert(val id: Long) : ModifyShopEventResult()

    data object SuccessUpdate : ModifyShopEventResult()

    data object SuccessDelete : ModifyShopEventResult()

    data class SuccessMerge(val id: Long) : ModifyShopEventResult()
}

abstract class ModifyShopViewModel : ViewModel() {
    @Suppress("PropertyName") protected val _uiState = MutableStateFlow(ModifyShopUiState())
    val uiState = _uiState.asStateFlow()

    protected abstract val getAllShopEntityUseCase: GetAllShopEntityUseCase

    fun init() {
        viewModelScope.launch {
            getAllShopEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allShops = it) }
            }
        }
    }

    open suspend fun handleEvent(event: ModifyShopEvent): ModifyShopEventResult {
        when (event) {
            is ModifyShopEvent.DeleteShop -> {}
            is ModifyShopEvent.MergeShop -> {}
            is ModifyShopEvent.NavigateBack -> {}
            is ModifyShopEvent.SelectMergeCandidate -> {}
            is ModifyShopEvent.SetDangerousDeleteDialogConfirmation -> {}
            is ModifyShopEvent.SetDangerousDeleteDialogVisibility -> {}
            is ModifyShopEvent.SetMergeConfirmationDialogVisibility -> {}
            is ModifyShopEvent.SetMergeSearchDialogVisibility -> {}
            is ModifyShopEvent.Submit -> {}
            is ModifyShopEvent.SetName -> {
                _uiState.update { currentState ->
                    currentState.copy(name = Field.Loaded(event.name))
                }
            }
        }

        return ModifyShopEventResult.Success
    }
}
