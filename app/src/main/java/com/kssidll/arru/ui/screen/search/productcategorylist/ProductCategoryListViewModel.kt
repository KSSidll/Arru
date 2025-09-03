package com.kssidll.arru.ui.screen.search.productcategorylist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetAllProductCategoryEntityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ProductCategoryListSearchUiState(
    val allProductCategories: ImmutableList<ProductCategoryEntity> = emptyImmutableList(),
    val filter: String = String(),
)

@Immutable
sealed class ProductCategoryListSearchEvent {
    data class SetFilter(val filter: String) : ProductCategoryListSearchEvent()
}

@HiltViewModel
class ProductCategoryListViewModel
@Inject
constructor(private val getAllProductCategoryEntityUseCase: GetAllProductCategoryEntityUseCase) :
    ViewModel() {
    private val _uiState = MutableStateFlow(ProductCategoryListSearchUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAllProductCategoryEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allProductCategories = it) }
            }
        }
    }

    fun handleEvent(event: ProductCategoryListSearchEvent) {
        when (event) {
            is ProductCategoryListSearchEvent.SetFilter -> {
                _uiState.update { currentState -> currentState.copy(filter = event.filter) }
            }
        }
    }
}
