package com.kssidll.arrugarq.ui.screen.shop.shop


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ShopRoute(
    shopId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onCategoryClick: (categoryId: Long) -> Unit,
    onProducerClick: (producerId: Long) -> Unit,
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
        onCategoryClick = {
            onCategoryClick(it.id)
        },
        onProducerClick = {
            onProducerClick(it.id)
        },
    )
}