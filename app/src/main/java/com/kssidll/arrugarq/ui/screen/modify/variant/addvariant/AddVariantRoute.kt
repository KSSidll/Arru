package com.kssidll.arrugarq.ui.screen.modify.variant.addvariant

import androidx.compose.runtime.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.variant.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddVariantRoute(
    productId: Long,
    defaultName: String?,
    navigateBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddVariantViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.screenState.name.value = Field.Loaded(defaultName)
    }

    ModifyVariantScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addVariant(productId)
                if (result != null) navigateBack()
            }
        }
    )
}