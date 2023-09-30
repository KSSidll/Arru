package com.kssidll.arrugarq.ui.screen.additem

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.component.other.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddItemRoute(
    onBack: () -> Unit,
    onProductAdd: () -> Unit,
    onVariantAdd: (Long) -> Unit,
    onShopAdd: () -> Unit,
) {
    val scope = rememberCoroutineScope()

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
        AddItemScreen(
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
                scope.launch {
                    isLoading = true
                    addItemViewModel.fillStateWithSelectedProductLatestData()
                    isLoading = false
                }
            },
        )
    }
}