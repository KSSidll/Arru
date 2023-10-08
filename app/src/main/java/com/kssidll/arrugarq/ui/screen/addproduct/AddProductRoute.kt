package com.kssidll.arrugarq.ui.screen.addproduct

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProductRoute(
    onBack: () -> Unit,
    onProductCategoryAdd: () -> Unit,
    onProductProducerAdd: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val addProductViewModel: AddProductViewModel = hiltViewModel()

    AddProductScreen(
        onBack = onBack,
        state = addProductViewModel.addProductScreenState,
        onCategoryAdd = onProductCategoryAdd,
        onProducerAdd = onProductProducerAdd,
        onProductAdd = {
            scope.launch {
                val result = addProductViewModel.addProduct()
                if (result != null) onBack()
            }
        },
        categoriesWithAltNames = addProductViewModel.getProductCategoriesWithAltNamesFlow(),
        producers = addProductViewModel.getProductProducersFlow(),
    )
}
