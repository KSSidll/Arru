package com.kssidll.arrugarq.ui.screen.product.addproduct

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.product.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProductRoute(
    onBack: () -> Unit,
    onCategoryAdd: () -> Unit,
    onProducerAdd: () -> Unit,
    onCategoryEdit: (categoryId: Long) -> Unit,
    onProducerEdit: (producerId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddProductViewModel = hiltViewModel()

    EditProductScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addProduct()
                if (result != null) onBack()
            }
        },
        onProducerAdd = onProducerAdd,
        onCategoryAdd = onCategoryAdd,
        onCategoryEdit = onCategoryEdit,
        onProducerEdit = onProducerEdit,
    )
}
