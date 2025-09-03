package com.kssidll.arru.ui.screen.ranking.productcategory

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.TotalSpentByCategory
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByProductCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ProductCategoryRankingUiState(
    val totalSpentByProductCategory: ImmutableList<TotalSpentByCategory> = emptyImmutableList()
)

@HiltViewModel
class ProductCategoryRankingViewModel
@Inject
constructor(
    private val getTotalSpentByProductCategoryUseCase: GetTotalSpentByProductCategoryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductCategoryRankingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTotalSpentByProductCategoryUseCase().collectLatest {
                _uiState.update { currentState ->
                    currentState.copy(totalSpentByProductCategory = it)
                }
            }
        }
    }
}
