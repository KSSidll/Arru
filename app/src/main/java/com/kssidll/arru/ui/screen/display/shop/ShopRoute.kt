package com.kssidll.arru.ui.screen.display.shop


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.orEmpty
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun ShopRoute(
    shopId: Long,
    navigateBack: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateShopEdit: () -> Unit,
    viewModel: ShopViewModel = hiltViewModel()
) {
    LaunchedEffect(shopId) {
        if (!viewModel.performDataUpdate(shopId)) {
            navigateBack()
        }
    }

    ShopScreen(
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
        onItemClick = navigateProduct,
        onItemCategoryClick = navigateCategory,
        onItemProducerClick = navigateProducer,
        onItemLongClick = navigateItemEdit,
        onEditAction = navigateShopEdit,
    )
}