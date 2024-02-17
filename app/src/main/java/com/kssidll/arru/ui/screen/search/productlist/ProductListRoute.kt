package com.kssidll.arru.ui.screen.search.productlist


import androidx.compose.runtime.*
import com.kssidll.arru.ui.screen.search.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun ProductListRoute(
    onProductClick: (productId: Long) -> Unit,
    onProductLongClick: (productId: Long) -> Unit,
) {
    val viewModel: ProductListViewModel = hiltViewModel()

    ListScreen(
        state = viewModel.screenState,
        onItemClick = {
            onProductClick(it.product.id)
        },
        onItemLongClick = {
            onProductLongClick(it.product.id)
        },
    )
}
