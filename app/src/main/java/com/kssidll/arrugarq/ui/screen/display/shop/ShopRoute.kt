package com.kssidll.arrugarq.ui.screen.display.shop


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ShopRoute(
    shopId: Long,
    onBack: () -> Unit,
    onShopEdit: () -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
) {
    val viewModel: ShopViewModel = hiltViewModel()

    LaunchedEffect(shopId) {
        if (!viewModel.performDataUpdate(shopId)) {
            onBack()
        }
    }

    ShopScreen(
        onBack = onBack,
        shop = viewModel.shop,
        transactionItems = viewModel.transactionItems,
        requestMoreTransactionItems = {
            viewModel.queryMoreFullItems()
        },
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = emptyList())?.value
            ?: emptyList(),
        totalSpentData = viewModel.shopTotalSpent()
            ?.collectAsState(initial = 0F)?.value ?: 0F,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onProductSelect = onProductSelect,
        onCategorySelect = onCategorySelect,
        onProducerSelect = onProducerSelect,
        onItemEdit = onItemEdit,
        onShopEdit = onShopEdit,
    )
}