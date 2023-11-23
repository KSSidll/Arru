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
    onBack: () -> Unit,
    onBackDelete: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditVariantViewModel = hiltViewModel()

    LaunchedEffect(variantId) {
        if (!viewModel.updateState(variantId)) {
            onBack()
        }
    }

    ModifyVariantScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        onSubmit = {
            viewModel.updateVariant(variantId)
            onBack()
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteVariant(variantId)) {
                    onBackDelete()
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_product_variant_edit),
    )
}
