package com.kssidll.arrugarq.ui.screen.home.transactions


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun TransactionsRoute(
    onItemEdit: (itemId: Long) -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
) {
    val viewModel: TransactionsViewModel = hiltViewModel()

    TransactionsScreen(
        requestMoreItems = {
            viewModel.queryMoreFullItems()
        },
        items = viewModel.fullItemsData,
        onItemEdit = onItemEdit,
        onProductSelect = onProductSelect,
        onCategorySelect = onCategorySelect,
        onProducerSelect = onProducerSelect,
        onShopSelect = onShopSelect,
    )
}
