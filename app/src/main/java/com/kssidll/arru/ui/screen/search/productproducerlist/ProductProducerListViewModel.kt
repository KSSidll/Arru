package com.kssidll.arru.ui.screen.search.productproducerlist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetAllProductProducerEntityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ProductProducerListSearchUiState(
    val allProductProducers: ImmutableList<ProductProducerEntity> = emptyImmutableList(),
    val filter: String = String(),
)

@Immutable
sealed class ProductProducerListSearchEvent {
    data class SetFilter(val filter: String) : ProductProducerListSearchEvent()
}

@HiltViewModel
class ProductProducerListViewModel
@Inject
constructor(private val getAllProductProducerEntityUseCase: GetAllProductProducerEntityUseCase) :
    ViewModel() {
    private val _uiState = MutableStateFlow(ProductProducerListSearchUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAllProductProducerEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allProductProducers = it) }
            }
        }
    }

    fun handleEvent(event: ProductProducerListSearchEvent) {
        when (event) {
            is ProductProducerListSearchEvent.SetFilter -> {
                _uiState.update { currentState -> currentState.copy(filter = event.filter) }
            }
        }
    }
}
