package com.kssidll.arrugarq.ui.screen.display.category


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun CategoryRoute(
    categoryId: Long,
    onBack: () -> Unit,
    onCategoryEdit: () -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
) {
    val viewModel: CategoryViewModel = hiltViewModel()

    LaunchedEffect(categoryId) {
        if (!viewModel.performDataUpdate(categoryId)) {
            onBack()
        }
    }

    CategoryScreen(
        onBack = onBack,
        category = viewModel.category,
        transactionItems = viewModel.transactionItems,
        requestMoreTransactionItems = {
            viewModel.queryMoreFullItems()
        },
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = emptyList())?.value
            ?: emptyList(),
        totalSpentData = viewModel.categoryTotalSpent()
            ?.collectAsState(initial = 0F)?.value ?: 0F,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onCategoryEdit = onCategoryEdit,
        onProductSelect = onProductSelect,
        onItemEdit = onItemEdit,
        onProducerSelect = onProducerSelect,
        onShopSelect = onShopSelect,
    )
}