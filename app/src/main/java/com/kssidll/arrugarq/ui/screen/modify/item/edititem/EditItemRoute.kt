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
    onBack: () -> Unit,
    onBackDelete: () -> Unit,
    onShopAdd: () -> Unit,
    onProductAdd: () -> Unit,
    onVariantAdd: (productId: Long) -> Unit,
    onShopEdit: (shopId: Long) -> Unit,
    onProductEdit: (productId: Long) -> Unit,
    onVariantEdit: (variantId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditItemViewModel = hiltViewModel()

    LaunchedEffect(itemId) {
        if (!viewModel.updateState(itemId)) {
            onBack()
        }
    }

    ModifyItemScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        shops = viewModel.allShops()
            .collectAsState(initial = emptyList()).value,
        products = viewModel.allProducts()
            .collectAsState(initial = emptyList()).value,
        variants = viewModel.productVariants.collectAsState(initial = emptyList()).value,
        onSubmit = {
            viewModel.updateItem(itemId)
            onBack()
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteItem(itemId)) {
                    onBackDelete()
                }
            }
        },
        onShopAdd = onShopAdd,
        onProductAdd = onProductAdd,
        onVariantAdd = onVariantAdd,
        onShopEdit = onShopEdit,
        onProductEdit = onProductEdit,
        onVariantEdit = onVariantEdit,
        onProductChange = {
            viewModel.onProductChange()
        },
        submitButtonText = stringResource(id = R.string.item_edit),
    )
}
