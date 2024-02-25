package com.kssidll.arru.ui.screen.modify.item.additem

import androidx.compose.runtime.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.modify.item.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddItemRoute(
    transactionId: Long,
    navigateBack: () -> Unit,
    navigateProductAdd: (query: String?) -> Unit,
    navigateVariantAdd: (productId: Long, query: String?) -> Unit,
    navigateProductEdit: (productId: Long) -> Unit,
    navigateVariantEdit: (variantId: Long) -> Unit,
    providedProductId: Long?,
    providedVariantId: Long?,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddItemViewModel = hiltViewModel()

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
                if (viewModel.addItem(transactionId)
                        .isNotError()
                ) {
                    navigateBack()
                }
            }
        },
        onProductAddButtonClick = navigateProductAdd,
        onVariantAddButtonClick = navigateVariantAdd,
        onItemLongClick = navigateProductEdit,
        onItemVariantLongClick = navigateVariantEdit,
    )
}