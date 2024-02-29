package com.kssidll.arru.ui.screen.search.productlist


import androidx.compose.runtime.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.search.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun ProductListRoute(
    onProductClick: (productId: Long) -> Unit,
    onProductLongClick: (productId: Long) -> Unit,
) {
    val viewModel: ProductListViewModel = hiltViewModel()

    SearchList(
        filter = viewModel.filter,
        onFilterChange = {
            viewModel.filter = it
        },
        items = viewModel.items()
            .collectAsState(initial = Data.Loading()).value,
        onItemClick = {
            onProductClick(it.product.id)
        },
        onItemLongClick = {
            onProductLongClick(it.product.id)
        },
    )
}
