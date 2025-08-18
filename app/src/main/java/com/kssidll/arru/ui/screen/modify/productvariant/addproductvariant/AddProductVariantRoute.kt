package com.kssidll.arru.ui.screen.modify.productvariant.addproductvariant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddProductVariantRoute(
    productId: Long,
    defaultName: String?,
    navigateBack: (variantId: Long?) -> Unit,
    viewModel: AddProductVariantViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.screenState.name.value = Field.Loaded(defaultName) }

    ModifyProductVariantScreenImpl(
        onBack = { navigateBack(null) },
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addVariant(productId)
                if (result.isNotError()) {
                    navigateBack(result.id)
                }
            }
        },
    )
}
