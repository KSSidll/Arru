package com.kssidll.arrugarq.ui.screen.display.product


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProductRoute(
    productId: Long,
    onBack: () -> Unit,
    onProductEdit: () -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
) {
    val viewModel: ProductViewModel = hiltViewModel()

    LaunchedEffect(productId) {
        if (!viewModel.performDataUpdate(productId)) {
            onBack()
        }
    }

    ProductScreen(
        onBack = onBack,
        state = viewModel.screenState,
        onProductEdit = onProductEdit,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        requestMoreItems = {
            viewModel.queryMoreFullItems()
        },
        onCategorySelect = onCategorySelect,
        onProducerSelect = onProducerSelect,
        onShopSelect = onShopSelect,
        onItemEdit = onItemEdit,
    )
}