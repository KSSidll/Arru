package com.kssidll.arrugarq.ui.screen.home

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun HomeRoute(
    onAddItem: () -> Unit,
    onDashboardCategoryCardClick: () -> Unit,
    onDashboardShopCardClick: () -> Unit,
    onTransactionItemClick: (productId: Long) -> Unit,
    onTransactionCategoryClick: (categoryId: Long) -> Unit,
    onTransactionProducerClick: (producerId: Long) -> Unit,
    onTransactionShopClick: (shopId: Long) -> Unit,
) {
    val homeViewModel: HomeViewModel = hiltViewModel()

    HomeScreen(
        onAddItem = onAddItem,
        onDashboardCategoryCardClick = onDashboardCategoryCardClick,
        onDashboardShopCardClick = onDashboardShopCardClick,
        requestFullItems = {
            homeViewModel.queryFullItems(it)
        },
        fullItems = homeViewModel.fullItemsData,
        totalSpentData = homeViewModel.getTotalSpent(),
        spentByShopData = homeViewModel.getSpentByShop(),
        spentByCategoryData = homeViewModel.getSpentByCategory(),
        spentByTimeData = homeViewModel.spentByTimeData,
        spentByTimePeriod = homeViewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            homeViewModel.switchToSpentByTimePeriod(it)
        },
        onTransactionItemClick = {
            onTransactionItemClick(it.embeddedItem.item.productId)
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