package com.kssidll.arru.ui.screen.modify.productvariant.editproductvariant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProductVariantRoute(
    variantId: Long,
    navigateBack: () -> Unit,
    viewModel: EditProductVariantViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(variantId)) {
                navigateBack()
            }
        }
    }

    LaunchedEffect(variantId) { viewModel.updateState(variantId) }

    ModifyProductVariantScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                if (viewModel.updateVariant(variantId).isNotError()) {
                    navigateBack()
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteVariant(variantId).isNotError()) {
                    navigateBack()
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_product_variant_edit),
    )
}
