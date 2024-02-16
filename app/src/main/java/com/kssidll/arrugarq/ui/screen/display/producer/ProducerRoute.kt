package com.kssidll.arrugarq.ui.screen.display.producer


import androidx.compose.runtime.*
import androidx.paging.compose.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProducerRoute(
    producerId: Long,
    navigateBack: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateProducerEdit: () -> Unit,
) {
    val viewModel: ProducerViewModel = hiltViewModel()

    LaunchedEffect(producerId) {
        if (!viewModel.performDataUpdate(producerId)) {
            navigateBack()
        }
    }

    ProducerScreen(
        onBack = navigateBack,
        producer = viewModel.producer,
        transactionItems = viewModel.transactions()
            .collectAsLazyPagingItems(),
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = emptyList())?.value
            ?: emptyList(),
        totalSpentData = viewModel.producerTotalSpent()
            ?.collectAsState(initial = 0F)?.value ?: 0F,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onItemClick = navigateProduct,
        onItemCategoryClick = navigateCategory,
        onItemShopClick = navigateShop,
        onItemLongClick = navigateItemEdit,
        onEditAction = navigateProducerEdit,
    )
}