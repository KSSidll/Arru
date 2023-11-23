package com.kssidll.arrugarq.ui.screen.display.producer


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProducerRoute(
    producerId: Long,
    onBack: () -> Unit,
    onProducerEdit: () -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
) {
    val viewModel: ProducerViewModel = hiltViewModel()

    LaunchedEffect(producerId) {
        if (!viewModel.performDataUpdate(producerId)) {
            onBack()
        }
    }

    ProducerScreen(
        onBack = onBack,
        producer = viewModel.producer,
        transactionItems = viewModel.transactionItems,
        requestMoreTransactionItems = {
            viewModel.queryMoreFullItems()
        },
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = emptyList())?.value
            ?: emptyList(),
        totalSpentData = viewModel.producerTotalSpent()
            ?.collectAsState(initial = 0F)?.value ?: 0F,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onProductSelect = onProductSelect,
        onCategorySelect = onCategorySelect,
        onShopSelect = onShopSelect,
        onItemEdit = onItemEdit,
        onProducerEdit = onProducerEdit,
    )
}