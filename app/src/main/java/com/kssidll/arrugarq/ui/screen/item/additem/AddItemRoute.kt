package com.kssidll.arrugarq.ui.screen.item.additem

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.item.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddItemRoute(
    onBack: () -> Unit,
    onProductAdd: () -> Unit,
    onProductEdit: (productId: Long) -> Unit,
    onVariantAdd: (productId: Long) -> Unit,
    onVariantEdit: (variantId: Long) -> Unit,
    onShopAdd: () -> Unit,
    onShopEdit: (shopId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddItemViewModel = hiltViewModel()

    EditItemScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addItem()
                if (result != null) onBack()
            }
        },
        onProductAdd = onProductAdd,
        onProductEdit = {
            onProductEdit(it.id)
        },
        onShopAdd = onShopAdd,
        onShopEdit = {
            onShopEdit(it.id)
        },
        onVariantAdd = {
            with(viewModel.screenState.selectedProduct) {
                if (value != null) {
                    onVariantAdd(value!!.id)
                }
            }
        },
        onVariantEdit = {
            onVariantEdit(it.id)
        },
        onProductChange = {
            viewModel.onProductChange()
        }
    )
}