package com.kssidll.arru.ui.screen.display.producer


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.orEmpty
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun ProducerRoute(
    producerId: Long,
    navigateBack: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateProducerEdit: () -> Unit,
    viewModel: ProducerViewModel = hiltViewModel()
) {
    LaunchedEffect(producerId) {
        if (!viewModel.performDataUpdate(producerId)) {
            navigateBack()
        }
    }

    ProducerScreen(
        onBack = navigateBack,
        producer = viewModel.producer,
        transactionItems = viewModel.transactions().collectAsLazyPagingItems(),
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = emptyImmutableList())?.value.orEmpty(),
        totalSpentData = viewModel.producerTotalSpent()?.collectAsState(initial = null)?.value ?: 0f,
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