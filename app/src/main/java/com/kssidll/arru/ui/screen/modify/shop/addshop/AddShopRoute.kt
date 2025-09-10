package com.kssidll.arru.ui.screen.modify.shop.addshop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopEvent
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopEventResult
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddShopRoute(
    defaultName: String?,
    provideBack: (shopId: Long?) -> Unit,
    navigateBack: () -> Unit,
    viewModel: AddShopViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.handleEvent(ModifyShopEvent.SetName(defaultName ?: String())) }

    ModifyShopScreenImpl(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyShopEvent.NavigateBack -> navigateBack()
                    is ModifyShopEvent.DeleteShop -> {}
                    is ModifyShopEvent.MergeShop -> {}
                    is ModifyShopEvent.SelectMergeCandidate -> {}
                    is ModifyShopEvent.SetDangerousDeleteDialogConfirmation -> {}
                    is ModifyShopEvent.SetDangerousDeleteDialogVisibility -> {}
                    is ModifyShopEvent.SetMergeConfirmationDialogVisibility -> {}
                    is ModifyShopEvent.SetMergeSearchDialogVisibility -> {}
                    is ModifyShopEvent.SetName -> viewModel.handleEvent(event)
                    is ModifyShopEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (result is ModifyShopEventResult.SuccessInsert) {
                            provideBack(result.id)
                            navigateBack()
                        }
                    }
                }
            }
        },
    )
}
