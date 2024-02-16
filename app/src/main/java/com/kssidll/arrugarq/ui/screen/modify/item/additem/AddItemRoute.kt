package com.kssidll.arrugarq.ui.screen.modify.item.additem

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.modify.item.*
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
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddItemViewModel = hiltViewModel()

    ModifyItemScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        products = viewModel.allProducts()
            .collectAsState(initial = emptyList()).value,
        variants = viewModel.productVariants.collectAsState(initial = emptyList()).value,
        onSubmit = {
            scope.launch {
                if (viewModel.addItem(transactionId)
                        .isNotError()
                ) {
                    navigateBack()
                }
            }
        },
        onProductChange = {
            viewModel.onProductChange()
        },
        onProductAddButtonClick = navigateProductAdd,
        onVariantAddButtonClick = navigateVariantAdd,
        onItemLongClick = navigateProductEdit,
        onItemVariantLongClick = navigateVariantEdit,
    )
}