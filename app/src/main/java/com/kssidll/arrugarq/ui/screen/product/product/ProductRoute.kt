package com.kssidll.arrugarq.ui.screen.product.product


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProductRoute(
    productId: Long,
    onBack: () -> Unit,
    onCategoryClick: (categoryId: Long) -> Unit,
    onProducerClick: (producerId: Long) -> Unit,
    onShopClick: (shopId: Long) -> Unit,
) {
    val viewModel: ProductViewModel = hiltViewModel()

    LaunchedEffect(productId) {
        viewModel.performDataUpdate(productId)
    }

    ProductScreen(
        onBack = onBack,
        state = viewModel.screenState,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        requestMoreItems = {
            viewModel.queryMoreFullItems()
        },
        onCategoryClick = {
            onCategoryClick(it.id)
        },
        onProducerClick = {
            onProducerClick(it.id)
        },
        onShopClick = {
            onShopClick(it.id)
        },
    )
}