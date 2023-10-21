package com.kssidll.arrugarq.ui.screen.category.category


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun CategoryRoute(
    categoryId: Long,
    onBack: () -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onProducerClick: (producerId: Long) -> Unit,
    onShopClick: (shopId: Long) -> Unit,
) {
    val viewModel: CategoryViewModel = hiltViewModel()

    LaunchedEffect(categoryId) {
        viewModel.performDataUpdate(categoryId)
    }

    CategoryScreen(
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
        onProducerClick = {
            onProducerClick(it.id)
        },
        onShopClick = {
            onShopClick(it.id)
        },
    )
}