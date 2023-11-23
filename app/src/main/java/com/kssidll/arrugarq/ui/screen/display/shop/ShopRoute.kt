package com.kssidll.arrugarq.ui.screen.display.shop


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ShopRoute(
    shopId: Long,
    onBack: () -> Unit,
    onShopEdit: () -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
) {
    val viewModel: ShopViewModel = hiltViewModel()

    LaunchedEffect(shopId) {
        if (!viewModel.performDataUpdate(shopId)) {
            onBack()
        }
    }

    ShopScreen(
        onBack = onBack,
        state = viewModel.screenState,
        onShopEdit = onShopEdit,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        requestMoreItems = {
            viewModel.queryMoreFullItems()
        },
        onProductSelect = onProductSelect,
        onItemEdit = onItemEdit,
        onCategorySelect = onCategorySelect,
        onProducerSelect = onProducerSelect,
    )
}