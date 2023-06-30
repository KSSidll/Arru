package com.kssidll.arrugarq.ui.additem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.kssidll.arrugarq.ui.shared.Loading

@Composable
fun AddItemRoute(
    onBack: () -> Unit,
    onProductAdd: () -> Unit,
    onVariantAdd: (Long) -> Unit,
    onShopAdd: () -> Unit,
) {
    val addItemViewModel: AddItemViewModel = hiltViewModel()

    var isLoading: Boolean by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        isLoading = true
        addItemViewModel.fetch()
        isLoading = false
    }

    if (isLoading) {
        Loading()
    } else {
        AddItemScreen (
            onBack = onBack,
            onItemAdd = {
                addItemViewModel.addItem(it)
            },
            onProductAdd = onProductAdd,
            onShopAdd = onShopAdd,
            onVariantAdd = onVariantAdd,
            productsWithAltNames = addItemViewModel.getProductsWithAltNamesFlow(),
            variants = addItemViewModel.variants.value,
            shops = addItemViewModel.getShopsFlow(),
            state = addItemViewModel.addItemState,
            onSelectProduct = {
                addItemViewModel.queryProductVariants(it.id)
            },
        )
    }
}