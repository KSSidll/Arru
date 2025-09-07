package com.kssidll.arru.ui.screen.display.transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun DisplayTransactionRoute(
    transactionId: Long,
    navigateBack: () -> Unit,
    navigateEditTransaction: () -> Unit,
    navigateAddItem: () -> Unit,
    navigateDisplayProduct: (productId: Long) -> Unit,
    navigateEditItem: (itemId: Long) -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateEditProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayProductProducer: (producerId: Long) -> Unit,
    navigateEditProductProducer: (producerId: Long) -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    navigateEditShop: (shopId: Long) -> Unit,
    viewModel: DisplayTransactionViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(transactionId)) {
                navigateBack()
            }
        }
    }

    LaunchedEffect(transactionId) { viewModel.updateState(transactionId) }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    DisplayTransactionScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                is DisplayTransactionEvent.NavigateBack -> navigateBack()
                is DisplayTransactionEvent.NavigateDisplayProduct ->
                    navigateDisplayProduct(event.productId)
                is DisplayTransactionEvent.NavigateDisplayProductCategory ->
                    navigateDisplayProductCategory(event.productCategoryId)
                is DisplayTransactionEvent.NavigateEditProductCategory ->
                    navigateEditProductCategory(event.productCategoryId)
                is DisplayTransactionEvent.NavigateDisplayProductProducer ->
                    navigateDisplayProductProducer(event.productProducerId)
                is DisplayTransactionEvent.NavigateEditProductProducer ->
                    navigateEditProductProducer(event.productProducerId)
                is DisplayTransactionEvent.NavigateDisplayShop ->
                    navigateDisplayShop(uiState.transaction?.shopId!!)
                is DisplayTransactionEvent.NavigateEditShop ->
                    navigateEditShop(uiState.transaction?.shopId!!)
                is DisplayTransactionEvent.NavigateEditItem -> navigateEditItem(event.itemId)
                is DisplayTransactionEvent.NavigateEditTransaction -> navigateEditTransaction()
                is DisplayTransactionEvent.NavigateAddItem -> navigateAddItem()
            }
        },
    )
}
