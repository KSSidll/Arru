package com.kssidll.arrugarq.ui.screen.modify.item.additem

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.modify.item.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddItemRoute(
    onBack: () -> Unit,
    onProductAdd: () -> Unit,
    onVariantAdd: (productId: Long) -> Unit,
    onShopAdd: () -> Unit,
    onProductEdit: (productId: Long) -> Unit,
    onVariantEdit: (variantId: Long) -> Unit,
    onShopEdit: (shopId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddItemViewModel = hiltViewModel()

    ModifyItemScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        shops = viewModel.allShops()
            .collectAsState(initial = emptyList()).value,
        products = viewModel.allProducts()
            .collectAsState(initial = emptyList()).value,
        variants = viewModel.productVariants.collectAsState(initial = emptyList()).value,
        onSubmit = {
            scope.launch {
                val result = viewModel.addItem()
                if (result != null) onBack()
            }
        },
        onProductAdd = onProductAdd,
        onShopAdd = onShopAdd,
        onVariantAdd = {
            with(viewModel.screenState.selectedProduct) {
                if (value != null) {
                    onVariantAdd(value!!.id)
                }
            }
        },
        onProductEdit = onProductEdit,
        onShopEdit = onShopEdit,
        onVariantEdit = onVariantEdit,
        onProductChange = {
            viewModel.onProductChange()
        }
    )
}