package com.kssidll.arrugarq.ui.screen.modify.item.edititem


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.modify.item.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditItemRoute(
    itemId: Long,
    navigateBack: () -> Unit,
    navigateBackDelete: () -> Unit,
    navigateProductAdd: (query: String?) -> Unit,
    navigateVariantAdd: (productId: Long, query: String?) -> Unit,
    navigateShopAdd: (query: String?) -> Unit,
    navigateProductEdit: (productId: Long) -> Unit,
    navigateVariantEdit: (variantId: Long) -> Unit,
    navigateShopEdit: (shopId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditItemViewModel = hiltViewModel()

    LaunchedEffect(itemId) {
        if (!viewModel.updateState(itemId)) {
            navigateBack()
        }
    }

    ModifyItemScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        shops = viewModel.allShops()
            .collectAsState(initial = emptyList()).value,
        products = viewModel.allProducts()
            .collectAsState(initial = emptyList()).value,
        variants = viewModel.productVariants.collectAsState(initial = emptyList()).value,
        onSubmit = {
            scope.launch {
                if (viewModel.updateItem(itemId)) {
                    navigateBack()
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteItem(itemId)) {
                    navigateBackDelete()
                }
            }
        },
        onProductChange = {
            viewModel.onProductChange()
        },
        submitButtonText = stringResource(id = R.string.item_edit),
        onProductAddButtonClick = navigateProductAdd,
        onVariantAddButtonClick = navigateVariantAdd,
        onShopAddButtonClick = navigateShopAdd,
        onItemLongClick = navigateProductEdit,
        onItemVariantLongClick = navigateVariantEdit,
        onItemShopLongClick = navigateShopEdit,
    )
}
