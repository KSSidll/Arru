package com.kssidll.arru.ui.screen.modify.item.edititem


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.modify.item.ModifyItemScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun EditItemRoute(
    itemId: Long,
    navigateBack: () -> Unit,
    navigateBackDelete: () -> Unit,
    navigateAddProduct: (query: String?) -> Unit,
    navigateAddProductVariant: (productId: Long, query: String?) -> Unit,
    navigateEditProduct: (productId: Long) -> Unit,
    navigateEditProductVariant: (variantId: Long) -> Unit,
    providedProductId: Long?,
    providedVariantId: Long?,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditItemViewModel = hiltViewModel()

    LaunchedEffect(itemId) {
        if (!viewModel.updateState(itemId)) {
            navigateBack()
        }
    }

    LaunchedEffect(
        providedProductId,
        providedVariantId
    ) {
        viewModel.setSelectedProductToProvided(
            providedProductId,
            providedVariantId
        )
    }

    ModifyItemScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        products = viewModel.allProducts().collectAsState(initial = emptyImmutableList()).value,
        variants = viewModel.productVariants.collectAsState(initial = emptyImmutableList()).value,
        onNewProductSelected = {
            scope.launch {
                viewModel.onNewProductSelected(it)
            }
        },
        onNewVariantSelected = {
            viewModel.onNewVariantSelected(it)
        },
        onSubmit = {
            scope.launch {
                if (viewModel.updateItem(itemId)
                        .isNotError()
                ) {
                    navigateBack()
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteItem(itemId)
                        .isNotError()
                ) {
                    navigateBackDelete()
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_edit),
        onProductAddButtonClick = navigateAddProduct,
        onVariantAddButtonClick = navigateAddProductVariant,
        onItemLongClick = navigateEditProduct,
        onItemVariantLongClick = navigateEditProductVariant,
    )
}
