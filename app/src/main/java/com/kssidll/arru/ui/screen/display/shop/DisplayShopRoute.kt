package com.kssidll.arru.ui.screen.display.shop


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.orEmpty
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun DisplayShopRoute(
    shopId: Long,
    navigateBack: () -> Unit,
    navigateDisplayProduct: (productId: Long) -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayProductProducer: (producerId: Long) -> Unit,
    navigateEditItem: (itemId: Long) -> Unit,
    navigateEditShop: () -> Unit,
    viewModel: DisplayShopViewModel = hiltViewModel()
) {
    LaunchedEffect(shopId) {
        if (!viewModel.performDataUpdate(shopId)) {
            navigateBack()
        }
    }

    DisplayShopScreen(
        onBack = navigateBack,
        shop = viewModel.shop,
        transactionItems = viewModel.transactions().collectAsLazyPagingItems(),
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = emptyImmutableList())?.value.orEmpty(),
        totalSpentData = viewModel.shopTotalSpent()?.collectAsState(initial = null)?.value ?: 0f,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onItemClick = navigateDisplayProduct,
        onItemCategoryClick = navigateDisplayProductCategory,
        onItemProducerClick = navigateDisplayProductProducer,
        onItemLongClick = navigateEditItem,
        onEditAction = navigateEditShop,
    )
}