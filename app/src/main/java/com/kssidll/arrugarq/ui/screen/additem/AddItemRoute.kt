package com.kssidll.arrugarq.ui.screen.additem

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.shared.*
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

    EditItemScreenImpl(
        onBack = onBack,
        state = addItemViewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = addItemViewModel.addItem()
                if (result != null) onBack()
            }
        },
        onProductAdd = onProductAdd,
        onShopAdd = onShopAdd,
        onVariantAdd = {
            with(addItemViewModel.screenState.selectedProduct) {
                if (value != null) {
                    onVariantAdd(value!!.id)
                }
            }
        },
        onProductChange = {
            addItemViewModel.onProductChange()
        }
    )
}