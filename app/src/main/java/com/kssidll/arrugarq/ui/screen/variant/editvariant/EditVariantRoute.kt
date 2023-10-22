package com.kssidll.arrugarq.ui.screen.variant.editvariant


import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.variant.*
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
        viewModel.updateState(variantId)
    }

    EditVariantScreenImpl(
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
        }
    )
}
