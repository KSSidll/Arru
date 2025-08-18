package com.kssidll.arru.ui.screen.display.product


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.orEmpty
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun DisplayProductRoute(
    productId: Long,
    navigateBack: () -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayProductProducer: (producerId: Long) -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    navigateEditItem: (itemId: Long) -> Unit,
    navigateEditProduct: () -> Unit,
    viewModel: DisplayProductViewModel = hiltViewModel()
) {
    LaunchedEffect(productId) {
        if (!viewModel.performDataUpdate(productId)) {
            navigateBack()
        }
    }

    DisplayProductScreen(
        onBack = navigateBack,
        product = viewModel.product,
        transactionItems = viewModel.transactions().collectAsLazyPagingItems(),
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = emptyImmutableList())?.value.orEmpty(),
        productPriceByShopByTimeData = viewModel.productPriceByShop()?.collectAsState(initial = emptyImmutableList())?.value.orEmpty(),
        totalSpentData = viewModel.productTotalSpent()?.collectAsState(initial = null)?.value ?: 0f,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onItemCategoryClick = navigateDisplayProductCategory,
        onItemProducerClick = navigateDisplayProductProducer,
        onItemShopClick = navigateDisplayShop,
        onItemLongClick = navigateEditItem,
        onEditAction = navigateEditProduct,
    )
}