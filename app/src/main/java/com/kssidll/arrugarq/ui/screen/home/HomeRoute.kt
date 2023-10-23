package com.kssidll.arrugarq.ui.screen.home

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun HomeRoute(
    onAddItem: () -> Unit,
    onDashboardCategoryCardClick: () -> Unit,
    onDashboardShopCardClick: () -> Unit,
    onTransactionItemClick: (productId: Long) -> Unit,
    onTransactionItemLongClick: (itemId: Long) -> Unit,
    onTransactionCategoryClick: (categoryId: Long) -> Unit,
    onTransactionProducerClick: (producerId: Long) -> Unit,
    onTransactionShopClick: (shopId: Long) -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()

    HomeScreen(
        onAddItem = onAddItem,
        onDashboardCategoryCardClick = onDashboardCategoryCardClick,
        onDashboardShopCardClick = onDashboardShopCardClick,
        requestMoreFullItems = {
            viewModel.queryMoreFullItems()
        },
        fullItems = viewModel.fullItemsData,
        totalSpentData = viewModel.getTotalSpent(),
        spentByShopData = viewModel.getSpentByShop(),
        spentByCategoryData = viewModel.getSpentByCategory(),
        spentByTimeData = viewModel.spentByTimeData,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchToSpentByTimePeriod(it)
        },
        onTransactionItemClick = {
            onTransactionItemClick(it.embeddedItem.item.productId)
        },
        onTransactionItemLongClick = {
            onTransactionItemLongClick(it.embeddedItem.item.id)
        },
        onTransactionCategoryClick = {
            onTransactionCategoryClick(it.id)
        },
        onTransactionProducerClick = {
            onTransactionProducerClick(it.id)
        },
        onTransactionShopClick = {
            onTransactionShopClick(it.id)
        },
    )
}