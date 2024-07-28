package com.kssidll.arru.ui.screen.modify.variant.addvariant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.variant.ModifyVariantScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddVariantRoute(
    productId: Long,
    defaultName: String?,
    navigateBack: (variantId: Long?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddVariantViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.screenState.name.value = Field.Loaded(defaultName)
    }

    ModifyVariantScreenImpl(
        onBack = {
            navigateBack(null)
        },
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addVariant(productId)
                if (result.isNotError()) {
                    navigateBack(result.id)
                }
            }
        }
    )
}