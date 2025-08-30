package com.kssidll.arru.ui.screen.modify.productvariant

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetAllGlobalProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityByProductUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ModifyProductVariantUiState(
    val currentProductVariant: ProductVariantEntity? = null,
    val allProductVariants: ImmutableList<ProductVariantEntity> = emptyImmutableList(),
    val name: Field<String> = Field.Loaded(),
    val isVariantGlobal: Field<Boolean> = Field.Loaded(false),
    val isVariantGlobalChangeEnabled: Boolean = false,
    val selectedMergeCandidate: ProductVariantEntity? = null,
    val isDeleteVisible: Boolean = false,
    val isDangerousDeleteDialogVisible: Boolean = false,
    val isDangerousDeleteDialogConfirmed: Boolean = false,
    val isMergeVisible: Boolean = false,
    val isMergeSearchDialogVisible: Boolean = false,
    val isMergeConfirmationDialogVisible: Boolean = false,
)

@Immutable
sealed class ModifyProductVariantEvent {
    data object NavigateBack : ModifyProductVariantEvent()

    data object Submit : ModifyProductVariantEvent()

    data object DeleteProductVariant : ModifyProductVariantEvent()

    data class SetDangerousDeleteDialogVisibility(val visible: Boolean) :
        ModifyProductVariantEvent()

    data class SetDangerousDeleteDialogConfirmation(val confirmed: Boolean) :
        ModifyProductVariantEvent()

    data class SetName(val name: String?) : ModifyProductVariantEvent()

    data class SetIsVariantGlobal(val isGlobal: Boolean) : ModifyProductVariantEvent()

    data class MergeProductVariant(val mergeInto: ProductVariantEntity?) :
        ModifyProductVariantEvent()

    data class SetMergeConfirmationDialogVisibility(val visible: Boolean) :
        ModifyProductVariantEvent()

    data class SetMergeSearchDialogVisibility(val visible: Boolean) : ModifyProductVariantEvent()

    data class SelectMergeCandidate(val mergeInto: ProductVariantEntity?) :
        ModifyProductVariantEvent()
}

sealed class ModifyProductVariantEventResult {
    data object Success : ModifyProductVariantEventResult()

    data object Failure : ModifyProductVariantEventResult()

    data class SuccessInsert(val id: Long) : ModifyProductVariantEventResult()

    data object SuccessUpdate : ModifyProductVariantEventResult()

    data object SuccessDelete : ModifyProductVariantEventResult()

    data class SuccessMerge(val id: Long) : ModifyProductVariantEventResult()
}

abstract class ModifyProductVariantViewModel : ViewModel() {
    @Suppress("PropertyName")
    protected val _uiState = MutableStateFlow(ModifyProductVariantUiState())
    val uiState = _uiState.asStateFlow()

    protected var mProduct: ProductEntity? = null
    private var _allProductVariantsJob: Job? = null

    protected abstract val getProductEntityUseCase: GetProductEntityUseCase
    protected abstract val getProductVariantEntityByProductUseCase:
        GetProductVariantEntityByProductUseCase
    protected abstract val getAllGlobalProductVariantEntityUseCase:
        GetAllGlobalProductVariantEntityUseCase

    suspend fun setAndCheckProduct(productId: Long?): Boolean {
        mProduct = productId?.let { getProductEntityUseCase(it).first() }

        // refresh variants for the product if it changed
        if (productId == mProduct?.id) {
            _allProductVariantsJob?.cancel()

            if (productId != null) {
                _allProductVariantsJob =
                    viewModelScope.launch {
                        getProductVariantEntityByProductUseCase(productId, true).collectLatest {
                            _uiState.update { currentState ->
                                currentState.copy(allProductVariants = it)
                            }
                        }
                    }
            } else {
                _allProductVariantsJob =
                    viewModelScope.launch {
                        getAllGlobalProductVariantEntityUseCase().collectLatest {
                            _uiState.update { currentState ->
                                currentState.copy(allProductVariants = it)
                            }
                        }
                    }
            }
        }

        return mProduct != null
    }

    open suspend fun handleEvent(
        event: ModifyProductVariantEvent
    ): ModifyProductVariantEventResult {
        when (event) {
            is ModifyProductVariantEvent.DeleteProductVariant -> {}
            is ModifyProductVariantEvent.MergeProductVariant -> {}
            is ModifyProductVariantEvent.NavigateBack -> {}
            is ModifyProductVariantEvent.SelectMergeCandidate -> {}
            is ModifyProductVariantEvent.SetDangerousDeleteDialogConfirmation -> {}
            is ModifyProductVariantEvent.SetDangerousDeleteDialogVisibility -> {}
            is ModifyProductVariantEvent.SetMergeConfirmationDialogVisibility -> {}
            is ModifyProductVariantEvent.SetMergeSearchDialogVisibility -> {}
            is ModifyProductVariantEvent.Submit -> {}
            is ModifyProductVariantEvent.SetName -> {
                _uiState.update { currentState ->
                    currentState.copy(name = Field.Loaded(event.name))
                }
            }
            is ModifyProductVariantEvent.SetIsVariantGlobal -> {
                _uiState.update { currentState ->
                    currentState.copy(isVariantGlobal = Field.Loaded(event.isGlobal))
                }
            }
        }

        return ModifyProductVariantEventResult.Success
    }
}
