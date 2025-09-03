package com.kssidll.arru.ui.screen.modify.productcategory

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetAllProductCategoryEntityUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ModifyProductCategoryUiState(
    val currentProductCategory: ProductCategoryEntity? = null,
    val allProductCategories: ImmutableList<ProductCategoryEntity> = emptyImmutableList(),
    val name: Field<String> = Field.Loaded(String()),
    val selectedMergeCandidate: ProductCategoryEntity? = null,
    val isDeleteEnabled: Boolean = false,
    val isDangerousDeleteDialogVisible: Boolean = false,
    val isDangerousDeleteDialogConfirmed: Boolean = false,
    val isMergeEnabled: Boolean = false,
    val isMergeSearchDialogVisible: Boolean = false,
    val isMergeConfirmationDialogVisible: Boolean = false,
)

@Immutable
sealed class ModifyProductCategoryEvent {
    data object NavigateBack : ModifyProductCategoryEvent()

    data object Submit : ModifyProductCategoryEvent()

    data object DeleteProductCategory : ModifyProductCategoryEvent()

    data class SetDangerousDeleteDialogVisibility(val visible: Boolean) :
        ModifyProductCategoryEvent()

    data class SetDangerousDeleteDialogConfirmation(val confirmed: Boolean) :
        ModifyProductCategoryEvent()

    data class SetName(val name: String) : ModifyProductCategoryEvent()

    data class MergeProductCategory(val mergeInto: ProductCategoryEntity?) :
        ModifyProductCategoryEvent()

    data class SetMergeConfirmationDialogVisibility(val visible: Boolean) :
        ModifyProductCategoryEvent()

    data class SetMergeSearchDialogVisibility(val visible: Boolean) : ModifyProductCategoryEvent()

    data class SelectMergeCandidate(val mergeInto: ProductCategoryEntity?) :
        ModifyProductCategoryEvent()
}

sealed class ModifyProductCategoryEventResult {
    data object Success : ModifyProductCategoryEventResult()

    data object Failure : ModifyProductCategoryEventResult()

    data class SuccessInsert(val id: Long) : ModifyProductCategoryEventResult()

    data object SuccessUpdate : ModifyProductCategoryEventResult()

    data object SuccessDelete : ModifyProductCategoryEventResult()

    data class SuccessMerge(val id: Long) : ModifyProductCategoryEventResult()
}

abstract class ModifyProductCategoryViewModel : ViewModel() {
    @Suppress("PropertyName")
    protected val _uiState = MutableStateFlow(ModifyProductCategoryUiState())
    val uiState = _uiState.asStateFlow()

    protected abstract val getAllProductCategoryEntityUseCase: GetAllProductCategoryEntityUseCase

    fun init() {
        viewModelScope.launch {
            getAllProductCategoryEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allProductCategories = it) }
            }
        }
    }

    open suspend fun handleEvent(
        event: ModifyProductCategoryEvent
    ): ModifyProductCategoryEventResult {
        when (event) {
            is ModifyProductCategoryEvent.DeleteProductCategory -> {}
            is ModifyProductCategoryEvent.MergeProductCategory -> {}
            is ModifyProductCategoryEvent.NavigateBack -> {}
            is ModifyProductCategoryEvent.SelectMergeCandidate -> {}
            is ModifyProductCategoryEvent.SetDangerousDeleteDialogConfirmation -> {}
            is ModifyProductCategoryEvent.SetDangerousDeleteDialogVisibility -> {}
            is ModifyProductCategoryEvent.SetMergeConfirmationDialogVisibility -> {}
            is ModifyProductCategoryEvent.SetMergeSearchDialogVisibility -> {}
            is ModifyProductCategoryEvent.Submit -> {}
            is ModifyProductCategoryEvent.SetName -> {
                _uiState.update { currentState ->
                    currentState.copy(name = Field.Loaded(event.name))
                }
            }
        }

        return ModifyProductCategoryEventResult.Success
    }
}
