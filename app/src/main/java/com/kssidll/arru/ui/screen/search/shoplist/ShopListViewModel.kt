package com.kssidll.arru.ui.screen.search.shoplist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetAllShopEntityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ShopListSearchUiState(
    val allShops: ImmutableList<ShopEntity> = emptyImmutableList(),
    val filter: String = String(),
)

@Immutable
sealed class ShopListSearchEvent {
    data class SetFilter(val filter: String) : ShopListSearchEvent()
}

@HiltViewModel
class ShopListViewModel
@Inject
constructor(private val getAllShopEntityUseCase: GetAllShopEntityUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(ShopListSearchUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAllShopEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allShops = it) }
            }
        }
    }

    fun handleEvent(event: ShopListSearchEvent) {
        when (event) {
            is ShopListSearchEvent.SetFilter -> {
                _uiState.update { currentState -> currentState.copy(filter = event.filter) }
            }
        }
    }
}
