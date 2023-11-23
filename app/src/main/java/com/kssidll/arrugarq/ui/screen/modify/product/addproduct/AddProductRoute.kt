package com.kssidll.arrugarq.ui.screen.modify.product.addproduct

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.modify.product.*
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

    ModifyProductScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        categories = viewModel.allCategories()
            .collectAsState(initial = emptyList()).value,
        producers = viewModel.allProducers()
            .collectAsState(initial = emptyList()).value,
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
