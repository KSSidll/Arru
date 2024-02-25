package com.kssidll.arru.ui.screen.display.shop


import androidx.compose.runtime.*
import androidx.paging.compose.*
import com.kssidll.arru.domain.data.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ShopRoute(
    shopId: Long,
    navigateBack: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateShopEdit: () -> Unit,
) {
    val viewModel: ShopViewModel = hiltViewModel()

    LaunchedEffect(shopId) {
        if (!viewModel.performDataUpdate(shopId)) {
            navigateBack()
        }
    }

    ShopScreen(
        onBack = navigateBack,
        shop = viewModel.shop,
        transactionItems = viewModel.transactions()
            .collectAsLazyPagingItems(),
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = Data.Loading())?.value
            ?: Data.Loaded(emptyList()),
        totalSpentData = viewModel.shopTotalSpent()
            ?.collectAsState(initial = Data.Loading())?.value ?: Data.Loaded(0F),
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