package com.kssidll.arrugarq.ui.screen.addproductcategory

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.shared.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProductCategoryRoute(
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val addProductCategoryViewModel: AddProductCategoryViewModel = hiltViewModel()

    EditProductCategoryScreen(
        onBack = onBack,
        state = addProductCategoryViewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = addProductCategoryViewModel.addCategory()
                if (result != null) onBack()
            }
        },
    )
}

