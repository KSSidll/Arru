package com.kssidll.arru.ui.screen.search.productlist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetAllProductEntityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ProductListSearchUiState(
    val allProducts: ImmutableList<ProductEntity> = emptyImmutableList(),
    val filter: String = String(),
)

@Immutable
sealed class ProductListSearchEvent {
    data class SetFilter(val filter: String) : ProductListSearchEvent()
}

@HiltViewModel
class ProductListViewModel
@Inject
constructor(private val getAllProductEntityUseCase: GetAllProductEntityUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductListSearchUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAllProductEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allProducts = it) }
            }
        }
    }

    fun handleEvent(event: ProductListSearchEvent) {
        when (event) {
            is ProductListSearchEvent.SetFilter -> {
                _uiState.update { currentState -> currentState.copy(filter = event.filter) }
            }
        }
    }
}
