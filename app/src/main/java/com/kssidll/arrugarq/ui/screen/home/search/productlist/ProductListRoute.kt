package com.kssidll.arrugarq.ui.screen.home.search.productlist


import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.home.search.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun ProductListRoute(
    onProductSelect: (productId: Long) -> Unit,
    onProductEdit: (productId: Long) -> Unit,
) {
    val viewModel: ProductListViewModel = hiltViewModel()

    ListScreen(
        state = viewModel.screenState,
        onItemSelect = {
            onProductSelect(it.product.id)
        },
        onItemEdit = {
            onProductEdit(it.product.id)
        },
    )
}
