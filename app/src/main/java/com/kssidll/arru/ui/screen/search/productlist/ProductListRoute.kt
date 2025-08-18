package com.kssidll.arru.ui.screen.search.productlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.search.shared.SearchList
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
internal fun ProductListRoute(
    onProductClick: (productId: Long) -> Unit,
    onProductLongClick: (productId: Long) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel(),
) {
    SearchList(
        filter = viewModel.filter,
        onFilterChange = { viewModel.filter = it },
        items = viewModel.items().collectAsState(initial = emptyImmutableList()).value,
        onItemClick = { onProductClick(it.id) },
        onItemLongClick = { onProductLongClick(it.id) },
    )
}
