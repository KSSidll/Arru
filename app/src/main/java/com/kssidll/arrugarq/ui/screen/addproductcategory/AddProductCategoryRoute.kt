package com.kssidll.arrugarq.ui.screen.addproductcategory

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProductCategoryRoute(
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val addProductCategoryViewModel: AddProductCategoryViewModel = hiltViewModel()

    AddProductCategoryScreen(
        onBack = onBack,
        state = addProductCategoryViewModel.addProductCategoryScreenState,
        onCategoryAdd = {
            scope.launch {
                val result = addProductCategoryViewModel.addCategory()
                if (result != null) onBack()
            }
        },
    )
}

