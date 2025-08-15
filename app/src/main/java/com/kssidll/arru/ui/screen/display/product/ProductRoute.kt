package com.kssidll.arru.ui.screen.display.product


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.orEmpty
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun ProductRoute(
    productId: Long,
    navigateBack: () -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateProductEdit: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    LaunchedEffect(productId) {
        if (!viewModel.performDataUpdate(productId)) {
            navigateBack()
        }
    }

    ProductScreen(
        onBack = navigateBack,
        product = viewModel.product,
        transactionItems = viewModel.transactions()
            .collectAsLazyPagingItems(),
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = emptyImmutableList())?.value.orEmpty(),
        productPriceByShopByTimeData = viewModel.productPriceByShop()?.collectAsState(initial = emptyImmutableList())?.value.orEmpty(),
        totalSpentData = viewModel.productTotalSpent()?.collectAsState(initial = null)?.value ?: 0f,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onItemCategoryClick = navigateCategory,
        onItemProducerClick = navigateProducer,
        onItemShopClick = navigateShop,
        onItemLongClick = navigateItemEdit,
        onEditAction = navigateProductEdit,
    )
}