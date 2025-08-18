package com.kssidll.arru.ui.screen.modify.item.additem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.modify.item.ModifyItemScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddItemRoute(
    transactionId: Long,
    navigateBack: () -> Unit,
    navigateAddProduct: (query: String?) -> Unit,
    navigateAddProductVariant: (productId: Long, query: String?) -> Unit,
    navigateEditProduct: (productId: Long) -> Unit,
    navigateEditProductVariant: (variantId: Long) -> Unit,
    providedProductId: Long?,
    providedVariantId: Long?,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddItemViewModel = hiltViewModel()

    LaunchedEffect(providedProductId, providedVariantId) {
        viewModel.setSelectedProductToProvided(providedProductId, providedVariantId)
    }

    ModifyItemScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        products = viewModel.allProducts().collectAsState(initial = emptyImmutableList()).value,
        variants = viewModel.productVariants.collectAsState(initial = emptyImmutableList()).value,
        onNewProductSelected = { scope.launch { viewModel.onNewProductSelected(it) } },
        onNewVariantSelected = { viewModel.onNewVariantSelected(it) },
        onSubmit = {
            scope.launch {
                if (viewModel.addItem(transactionId).isNotError()) {
                    navigateBack()
                }
            }
        },
        onProductAddButtonClick = navigateAddProduct,
        onVariantAddButtonClick = navigateAddProductVariant,
        onItemLongClick = navigateEditProduct,
        onItemVariantLongClick = navigateEditProductVariant,
    )
}
