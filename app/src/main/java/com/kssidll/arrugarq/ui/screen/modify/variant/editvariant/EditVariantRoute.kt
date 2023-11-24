package com.kssidll.arrugarq.ui.screen.modify.variant.editvariant


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.modify.variant.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditVariantRoute(
    variantId: Long,
    navigateBack: () -> Unit,
    navigateBackDelete: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditVariantViewModel = hiltViewModel()

    LaunchedEffect(variantId) {
        if (!viewModel.updateState(variantId)) {
            navigateBack()
        }
    }

    ModifyVariantScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        onSubmit = {
            viewModel.updateVariant(variantId)
            navigateBack()
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteVariant(variantId)) {
                    navigateBackDelete()
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_product_variant_edit),
    )
}
