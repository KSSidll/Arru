package com.kssidll.arrugarq.ui.screen.producer.producer


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProducerRoute(
    producerId: Long,
    onBack: () -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onCategoryClick: (categoryId: Long) -> Unit,
    onShopClick: (shopId: Long) -> Unit,
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
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        requestMoreItems = {
            viewModel.queryMoreFullItems()
        },
        onItemClick = {
            onItemClick(it.embeddedItem.item.productId)
        },
        onCategoryClick = {
            onCategoryClick(it.id)
        },
        onShopClick = {
            onShopClick(it.id)
        },
    )
}