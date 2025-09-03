package com.kssidll.arru.ui.screen.search.productlist

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.ui.screen.search.shared.SearchList
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
internal fun ProductListRoute(
    onProductClick: (productId: Long) -> Unit,
    onProductLongClick: (productId: Long) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    SearchList(
        filter = uiState.filter,
        onFilterChange = { viewModel.handleEvent(ProductListSearchEvent.SetFilter(it)) },
        items = uiState.allProducts,
        onItemClick = { onProductClick(it.id) },
        onItemLongClick = { onProductLongClick(it.id) },
    )
}
