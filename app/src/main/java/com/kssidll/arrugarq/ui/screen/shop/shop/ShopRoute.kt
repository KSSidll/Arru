package com.kssidll.arrugarq.ui.screen.shop.shop


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ShopRoute(
    shopId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onCategoryClick: (categoryId: Long) -> Unit,
    onProducerClick: (producerId: Long) -> Unit,
) {
    val viewModel: ShopViewModel = hiltViewModel()

    LaunchedEffect(shopId) {
        viewModel.performDataUpdate(shopId)
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
        onCategoryClick = {
            onCategoryClick(it.id)
        },
        onProducerClick = {
            onProducerClick(it.id)
        },
    )
}