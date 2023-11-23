package com.kssidll.arrugarq.ui.screen.display.producer


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProducerRoute(
    producerId: Long,
    onBack: () -> Unit,
    onProducerEdit: () -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
) {
    val viewModel: ProducerViewModel = hiltViewModel()

    LaunchedEffect(producerId) {
        if (!viewModel.performDataUpdate(producerId)) {
            onBack()
        }
    }

    ProducerScreen(
        onBack = onBack,
        state = viewModel.screenState,
        onProducerEdit = onProducerEdit,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        requestMoreItems = {
            viewModel.queryMoreFullItems()
        },
        onProductSelect = onProductSelect,
        onItemEdit = onItemEdit,
        onCategorySelect = onCategorySelect,
        onShopSelect = onShopSelect,
    )
}