package com.kssidll.arrugarq.presentation.screen.addproductcategory

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun AddProductCategoryRoute(
    onBack: () -> Unit,
) {
    val addProductCategoryViewModel: AddProductCategoryViewModel = hiltViewModel()

    AddProductCategoryScreen(
        onBack = onBack,
        onCategoryAdd = {
            addProductCategoryViewModel.addProductCategory(it)
        },
    )
}

