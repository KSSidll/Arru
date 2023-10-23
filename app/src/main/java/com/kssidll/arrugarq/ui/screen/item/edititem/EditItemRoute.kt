package com.kssidll.arrugarq.ui.screen.item.edititem


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.ui.screen.category.*
import com.kssidll.arrugarq.ui.screen.item.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditItemRoute(
    itemId: Long,
    onBack: () -> Unit,
    onBackDelete: () -> Unit,
    onShopAdd: () -> Unit,
    onShopEdit: (shopId: Long) -> Unit,
    onProductAdd: () -> Unit,
    onProductEdit: (productId: Long) -> Unit,
    onVariantAdd: (productId: Long) -> Unit,
    onVariantEdit: (variantId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditItemViewModel = hiltViewModel()

    LaunchedEffect(itemId) {
        if (!viewModel.updateState(itemId)) {
            onBack()
        }
    }

    EditItemScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
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
        onShopEdit = {
            onShopEdit(it.id)
        },
        onProductAdd = onProductAdd,
        onProductEdit = {
            onProductEdit(it.id)
        },
        onVariantAdd = {
            onVariantAdd(it.id)
        },
        onVariantEdit = {
            onVariantEdit(it.id)
        },
        onProductChange = {
            viewModel.onProductChange()
        },
        submitButtonText = stringResource(id = R.string.item_edit),
    )
}
