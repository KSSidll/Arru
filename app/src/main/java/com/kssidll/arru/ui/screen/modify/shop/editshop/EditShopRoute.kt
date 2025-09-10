package com.kssidll.arru.ui.screen.modify.shop.editshop

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopEvent
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopEventResult
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun EditShopRoute(
    shopId: Long,
    provideBack: (shopId: Long?) -> Unit,
    navigateBack: () -> Unit,
    viewModel: EditShopViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val navigateBackLock = remember { Mutex() }

    BackHandler {
        if (!navigateBackLock.isLocked) {
            navigateBackLock.tryLock()
            provideBack(shopId)
            navigateBack()
        }
    }

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(shopId) && !navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                provideBack(null)
                navigateBack()
            }
        }
    }

    LaunchedEffect(shopId) { viewModel.updateState(shopId) }

    ModifyShopScreenImpl(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyShopEvent.NavigateBack -> {
                        if (!navigateBackLock.isLocked) {
                            navigateBackLock.tryLock()
                            provideBack(shopId)
                            navigateBack()
                        }
                    }
                    is ModifyShopEvent.DeleteShop -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyShopEventResult.SuccessDelete &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            provideBack(null)
                            navigateBack()
                        }
                    }
                    is ModifyShopEvent.MergeShop -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyShopEventResult.SuccessMerge &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            provideBack(result.id)
                            navigateBack()
                        }
                    }
                    is ModifyShopEvent.SelectMergeCandidate -> viewModel.handleEvent(event)
                    is ModifyShopEvent.SetDangerousDeleteDialogConfirmation ->
                        viewModel.handleEvent(event)
                    is ModifyShopEvent.SetDangerousDeleteDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyShopEvent.SetMergeConfirmationDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyShopEvent.SetMergeSearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyShopEvent.SetName -> viewModel.handleEvent(event)
                    is ModifyShopEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyShopEventResult.SuccessUpdate &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            provideBack(shopId)
                            navigateBack()
                        }
                    }
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_shop_edit),
    )
}
