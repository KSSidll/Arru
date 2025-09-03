package com.kssidll.arru.ui.screen.ranking.shopranking

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.TotalSpentByShop
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByShopUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ShopRankingUiState(
    val totalSpentByShop: ImmutableList<TotalSpentByShop> = emptyImmutableList()
)

@HiltViewModel
class ShopRankingViewModel
@Inject
constructor(private val getTotalSpentByShopUseCase: GetTotalSpentByShopUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(ShopRankingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTotalSpentByShopUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(totalSpentByShop = it) }
            }
        }
    }
}
