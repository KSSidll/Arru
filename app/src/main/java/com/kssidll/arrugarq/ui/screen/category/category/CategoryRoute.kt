package com.kssidll.arrugarq.ui.screen.category.category


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun CategoryRoute(
    categoryId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onProducerClick: (producerId: Long) -> Unit,
    onShopClick: (shopId: Long) -> Unit,
) {
    val viewModel: CategoryViewModel = hiltViewModel()

    LaunchedEffect(categoryId) {
        if (!viewModel.performDataUpdate(categoryId)) {
            onBack()
        }
    }

    CategoryScreen(
        onBack = onBack,
        state = viewModel.screenState,
        onEdit = onEdit,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        requestMoreItems = {
            viewModel.queryMoreFullItems()
        },
        onItemClick = {
            onItemClick(it.embeddedItem.item.productId)
        },
        onItemLongClick = {
            onItemLongClick(it.embeddedItem.item.id)
        },
        onProducerClick = {
            onProducerClick(it.id)
        },
        onShopClick = {
            onShopClick(it.id)
        },
    )
}