package com.kssidll.arrugarq.ui.screen.variant.addvariant

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.variant.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddVariantRoute(
    productId: Long,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddVariantViewModel = hiltViewModel()

    ModifyVariantScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addVariant(productId)
                if (result != null) onBack()
            }
        }
    )
}