package com.kssidll.arrugarq.ui.screen.modify.product.editproduct


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.modify.product.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditProductRoute(
    productId: Long,
    onBack: () -> Unit,
    onBackDelete: () -> Unit,
    onProducerAdd: () -> Unit,
    onCategoryAdd: () -> Unit,
    onProducerEdit: (producerId: Long) -> Unit,
    onCategoryEdit: (categoryId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditProductViewModel = hiltViewModel()

    LaunchedEffect(productId) {
        if (!viewModel.updateState(productId)) {
            onBack()
        }
    }

    ModifyProductScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        categories = viewModel.allCategories()
            .collectAsState(initial = emptyList()).value,
        producers = viewModel.allProducers()
            .collectAsState(initial = emptyList()).value,
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
        onCategoryAdd = onCategoryAdd,
        onProducerEdit = onProducerEdit,
        onCategoryEdit = onCategoryEdit,
        submitButtonText = stringResource(id = R.string.item_product_edit),
    )
}
