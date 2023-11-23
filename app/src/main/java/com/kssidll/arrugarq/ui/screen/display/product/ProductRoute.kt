package com.kssidll.arrugarq.ui.screen.display.product


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProductRoute(
    productId: Long,
    onBack: () -> Unit,
    onProductEdit: () -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
) {
    val viewModel: ProductViewModel = hiltViewModel()

    LaunchedEffect(productId) {
        if (!viewModel.performDataUpdate(productId)) {
            onBack()
        }
    }

    ProductScreen(
        onBack = onBack,
        product = viewModel.product,
        transactionItems = viewModel.transactionItems,
        requestMoreTransactionItems = {
            viewModel.queryMoreFullItems()
        },
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = emptyList())?.value
            ?: emptyList(),
        productPriceByShopByTimeData = viewModel.productPriceByShop()
            ?.collectAsState(initial = emptyList())?.value ?: emptyList(),
        totalSpentData = viewModel.productTotalSpent()
            ?.collectAsState(initial = 0F)?.value ?: 0F,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onCategorySelect = onCategorySelect,
        onProducerSelect = onProducerSelect,
        onShopSelect = onShopSelect,
        onItemEdit = onItemEdit,
        onProductEdit = onProductEdit,
    )
}