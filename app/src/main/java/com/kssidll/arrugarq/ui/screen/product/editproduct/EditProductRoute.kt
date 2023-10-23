package com.kssidll.arrugarq.ui.screen.product.editproduct


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.product.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditProductRoute(
    productId: Long,
    onBack: () -> Unit,
    onBackDelete: () -> Unit,
    onProducerAdd: () -> Unit,
    onProducerEdit: (producerId: Long) -> Unit,
    onCategoryAdd: () -> Unit,
    onCategoryEdit: (categoryId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditProductViewModel = hiltViewModel()

    LaunchedEffect(productId) {
        if (!viewModel.updateState(productId)) {
            onBack()
        }
    }

    EditProductScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        onSubmit = {
            viewModel.updateProduct(productId)
            onBack()
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteProduct(productId)) {
                    onBackDelete()
                }
            }
        },
        onProducerAdd = onProducerAdd,
        onProducerEdit = {
            onProducerEdit(it.id)
        },
        onCategoryAdd = onCategoryAdd,
        onCategoryEdit = {
            onCategoryEdit(it.id)
        },
        submitButtonText = stringResource(id = R.string.item_product_edit),
    )
}
