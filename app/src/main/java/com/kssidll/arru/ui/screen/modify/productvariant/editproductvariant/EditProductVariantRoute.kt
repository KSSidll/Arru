package com.kssidll.arru.ui.screen.modify.productvariant.editproductvariant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun EditProductVariantRoute(
    variantId: Long,
    navigateBack: (variantId: Long?) -> Unit,
    viewModel: EditProductVariantViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val navigateBackLock = remember { Mutex() }

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(variantId) && !navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                navigateBack(null)
            }
        }
    }

    LaunchedEffect(variantId) { viewModel.updateState(variantId) }

    ModifyProductVariantScreenImpl(
        onBack = {
            if (!navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                navigateBack(variantId)
            }
        },
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                // if (viewModel.updateVariant(variantId).isNotError() && !navigateBackLock.isLocked) {
                // navigateBackLock.tryLock()
                //     navigateBack(variantId)
                // }
            }
        },
        onDelete = {
            scope.launch {
                // if (viewModel.deleteVariant(variantId).isNotError() && !navigateBackLock.isLocked) {
                // navigateBackLock.tryLock()
                //     navigateBack(null)
                // }
            }
        },
        submitButtonText = stringResource(id = R.string.item_product_variant_edit),
    )
}
