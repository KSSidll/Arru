package com.kssidll.arrugarq.ui.screen.home.transactions


import androidx.compose.runtime.*
import com.kssidll.arrugarq.data.data.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun TransactionsRoute(
    onItemClick: (item: FullItem) -> Unit,
    onItemLongClick: (item: FullItem) -> Unit,
    onCategoryClick: (category: ProductCategory) -> Unit,
    onProducerClick: (producer: ProductProducer) -> Unit,
    onShopClick: (shop: Shop) -> Unit,
) {
    val viewModel: TransactionsViewModel = hiltViewModel()

    TransactionsScreen(
        requestMoreItems = {
            viewModel.queryMoreFullItems()
        },
        items = viewModel.fullItemsData,
        onItemClick = onItemClick,
        onItemLongClick = onItemLongClick,
        onProducerClick = onProducerClick,
        onCategoryClick = onCategoryClick,
        onShopClick = onShopClick,
    )
}
