package com.kssidll.arru.ui.screen.display.category


import androidx.compose.runtime.*
import androidx.paging.compose.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun CategoryRoute(
    categoryId: Long,
    navigateBack: () -> Unit,
    navigateCategoryEdit: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
) {
    val viewModel: CategoryViewModel = hiltViewModel()

    LaunchedEffect(categoryId) {
        if (!viewModel.performDataUpdate(categoryId)) {
            navigateBack()
        }
    }

    CategoryScreen(
        onBack = navigateBack,
        category = viewModel.category,
        transactionItems = viewModel.transactions()
            .collectAsLazyPagingItems(),
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = emptyList())?.value
            ?: emptyList(),
        totalSpentData = viewModel.categoryTotalSpent()
            ?.collectAsState(initial = 0F)?.value ?: 0F,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onItemClick = navigateProduct,
        onItemProducerClick = navigateProducer,
        onItemShopClick = navigateShop,
        onItemLongClick = navigateItemEdit,
        onEditAction = navigateCategoryEdit,
    )
}