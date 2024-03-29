package com.kssidll.arru.ui.screen.modify.item.edititem


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.modify.item.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditItemRoute(
    itemId: Long,
    navigateBack: () -> Unit,
    navigateBackDelete: () -> Unit,
    navigateProductAdd: (query: String?) -> Unit,
    navigateVariantAdd: (productId: Long, query: String?) -> Unit,
    navigateProductEdit: (productId: Long) -> Unit,
    navigateVariantEdit: (variantId: Long) -> Unit,
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
        products = viewModel.allProducts()
            .collectAsState(initial = Data.Loading()).value,
        variants = viewModel.productVariants.collectAsState(initial = Data.Loading()).value,
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
        onProductAddButtonClick = navigateProductAdd,
        onVariantAddButtonClick = navigateVariantAdd,
        onItemLongClick = navigateProductEdit,
        onItemVariantLongClick = navigateVariantEdit,
    )
}
