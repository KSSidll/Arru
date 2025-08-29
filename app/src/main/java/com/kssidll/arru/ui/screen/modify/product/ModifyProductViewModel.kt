package com.kssidll.arru.ui.screen.modify.product

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetAllProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetAllProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetAllProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductProducerEntityUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ModifyProductUiState(
    val currentProduct: ProductEntity? = null,
    val allProducts: ImmutableList<ProductEntity> = emptyImmutableList(),
    val allProductCategories: ImmutableList<ProductCategoryEntity> = emptyImmutableList(),
    val allProductProducers: ImmutableList<ProductProducerEntity> = emptyImmutableList(),
    val name: Field<String> = Field.Loaded(),
    val selectedProductCategory: Field<ProductCategoryEntity> = Field.Loaded(),
    val selectedProductProducer: Field<ProductProducerEntity?> = Field.Loaded(),
    val selectedMergeCandidate: ProductEntity? = null,
    val isProductCategorySearchDialogExpanded: Boolean = false,
    val isProductProducerSearchDialogExpanded: Boolean = false,
    val isDeleteVisible: Boolean = false,
    val isDangerousDeleteDialogVisible: Boolean = false,
    val isDangerousDeleteDialogConfirmed: Boolean = false,
    val isMergeVisible: Boolean = false,
    val isMergeSearchDialogVisible: Boolean = false,
    val isMergeConfirmationDialogVisible: Boolean = false,
)

@Immutable
sealed class ModifyProductEvent {
    data object NavigateBack : ModifyProductEvent()

    data object Submit : ModifyProductEvent()

    data object DeleteProduct : ModifyProductEvent()

    data class SetDangerousDeleteDialogVisibility(val visible: Boolean) : ModifyProductEvent()

    data class SetDangerousDeleteDialogConfirmation(val confirmed: Boolean) : ModifyProductEvent()

    data class SetName(val name: String?) : ModifyProductEvent()

    data class SelectProductCategory(val productCategoryId: Long?) : ModifyProductEvent()

    data class SelectProductProducer(val productProducerId: Long?) : ModifyProductEvent()

    data class SetProductCategorySearchDialogVisibility(val visible: Boolean) :
        ModifyProductEvent()

    data class SetProductProducerSearchDialogVisibility(val visible: Boolean) :
        ModifyProductEvent()

    data class NavigateEditProductCategory(val productCategoryId: Long) : ModifyProductEvent()

    data class NavigateEditProductProducer(val productProducerId: Long) : ModifyProductEvent()

    data class NavigateAddProductCategory(val name: String?) : ModifyProductEvent()

    data class NavigateAddProductProducer(val name: String?) : ModifyProductEvent()

    data class MergeProduct(val mergeInto: ProductEntity?) : ModifyProductEvent()

    data class SetMergeConfirmationDialogVisibility(val visible: Boolean) : ModifyProductEvent()

    data class SetMergeSearchDialogVisibility(val visible: Boolean) : ModifyProductEvent()

    data class SelectMergeCandidate(val mergeInto: ProductEntity?) : ModifyProductEvent()
}

sealed class ModifyProductEventResult {
    data object Success : ModifyProductEventResult()

    data object Failure : ModifyProductEventResult()

    data class SuccessInsert(val id: Long) : ModifyProductEventResult()

    data class SuccessMerge(val id: Long) : ModifyProductEventResult()
}

abstract class ModifyProductViewModel : ViewModel() {
    @Suppress("PropertyName") protected val _uiState = MutableStateFlow(ModifyProductUiState())
    val uiState = _uiState.asStateFlow()

    protected abstract val getAllProductEntityUseCase: GetAllProductEntityUseCase
    protected abstract val getAllProductCategoryEntityUseCase: GetAllProductCategoryEntityUseCase
    protected abstract val getAllProductProducerEntityUseCase: GetAllProductProducerEntityUseCase
    protected abstract val getProductCategoryEntityUseCase: GetProductCategoryEntityUseCase
    protected abstract val getProductProducerEntityUseCase: GetProductProducerEntityUseCase

    private var _productProducerListener: Job? = null
    private var _productCategoryListener: Job? = null

    fun init() {
        viewModelScope.launch {
            getAllProductEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allProducts = it) }
            }
        }

        viewModelScope.launch {
            getAllProductCategoryEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allProductCategories = it) }
            }
        }

        viewModelScope.launch {
            getAllProductProducerEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allProductProducers = it) }
            }
        }
    }

    open suspend fun handleEvent(event: ModifyProductEvent): ModifyProductEventResult {
        when (event) {
            is ModifyProductEvent.NavigateAddProductCategory -> {}
            is ModifyProductEvent.NavigateAddProductProducer -> {}
            is ModifyProductEvent.NavigateBack -> {}
            is ModifyProductEvent.NavigateEditProductCategory -> {}
            is ModifyProductEvent.NavigateEditProductProducer -> {}
            is ModifyProductEvent.Submit -> {}
            is ModifyProductEvent.DeleteProduct -> {}
            is ModifyProductEvent.SetDangerousDeleteDialogVisibility -> {}
            is ModifyProductEvent.SetDangerousDeleteDialogConfirmation -> {}
            is ModifyProductEvent.MergeProduct -> {}
            is ModifyProductEvent.SetMergeConfirmationDialogVisibility -> {}
            is ModifyProductEvent.SetMergeSearchDialogVisibility -> {}
            is ModifyProductEvent.SelectMergeCandidate -> {}
            is ModifyProductEvent.SetProductCategorySearchDialogVisibility -> {
                _uiState.update { currenState ->
                    currenState.copy(isProductCategorySearchDialogExpanded = event.visible)
                }
            }
            is ModifyProductEvent.SetProductProducerSearchDialogVisibility -> {
                _uiState.update { currenState ->
                    currenState.copy(isProductProducerSearchDialogExpanded = event.visible)
                }
            }
            is ModifyProductEvent.SelectProductCategory ->
                handleSelectProductCategory(event.productCategoryId)
            is ModifyProductEvent.SelectProductProducer ->
                handleSelectProductProducer(event.productProducerId)
            is ModifyProductEvent.SetName -> {
                _uiState.update { currenState -> currenState.copy(name = Field.Loaded(event.name)) }
            }
        }

        return ModifyProductEventResult.Success
    }

    protected fun handleSelectProductCategory(productCategoryId: Long?) {
        setProductCategoryListener(productCategoryId)
    }

    protected fun handleSelectProductProducer(productProducerId: Long?) {
        setProductProducerListener(productProducerId)
    }

    protected fun cancelProductCategoryListener() {
        _productCategoryListener?.cancel()
    }

    protected fun setProductCategoryListener(productCategoryId: Long?) {
        cancelProductCategoryListener()
        if (productCategoryId == null) {
            _uiState.update { currentState ->
                currentState.copy(selectedProductCategory = Field.Loaded())
            }
            return
        }

        _productCategoryListener =
            viewModelScope.launch {
                getProductCategoryEntityUseCase(productCategoryId).collectLatest {
                    _uiState.update { currentState ->
                        currentState.copy(selectedProductCategory = Field.Loaded(it))
                    }
                }
            }
    }

    protected fun cancelProductProducerListener() {
        _productProducerListener?.cancel()
    }

    protected fun setProductProducerListener(productProducerId: Long?) {
        cancelProductProducerListener()
        if (productProducerId == null) {
            _uiState.update { currentState ->
                currentState.copy(selectedProductProducer = Field.Loaded())
            }
            return
        }

        _productProducerListener =
            viewModelScope.launch {
                getProductProducerEntityUseCase(productProducerId).collectLatest {
                    _uiState.update { currentState ->
                        currentState.copy(selectedProductProducer = Field.Loaded(it))
                    }
                }
            }
    }
}
