package com.kssidll.arru.ui.screen.display.productproducer


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.orEmpty
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun DisplayProductProducerRoute(
    producerId: Long,
    navigateBack: () -> Unit,
    navigateDisplayProduct: (productId: Long) -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    navigateEditItem: (itemId: Long) -> Unit,
    navigateEditProductProducer: () -> Unit,
    viewModel: DisplayProductProducerViewModel = hiltViewModel()
) {
    LaunchedEffect(producerId) {
        if (!viewModel.performDataUpdate(producerId)) {
            navigateBack()
        }
    }

    DisplayProductProducerScreen(
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
        onItemClick = navigateDisplayProduct,
        onItemCategoryClick = navigateDisplayProductCategory,
        onItemShopClick = navigateDisplayShop,
        onItemLongClick = navigateEditItem,
        onEditAction = navigateEditProductProducer,
    )
}