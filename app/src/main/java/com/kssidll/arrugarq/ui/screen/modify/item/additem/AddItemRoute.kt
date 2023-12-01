package com.kssidll.arrugarq.ui.screen.modify.item.additem

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.modify.item.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddItemRoute(
    navigateBack: () -> Unit,
    navigateProductAdd: () -> Unit,
    navigateVariantAdd: (productId: Long) -> Unit,
    navigateShopAdd: () -> Unit,
    navigateProductEdit: (productId: Long) -> Unit,
    navigateVariantEdit: (variantId: Long) -> Unit,
    navigateShopEdit: (shopId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddItemViewModel = hiltViewModel()

    ModifyItemScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        shops = viewModel.allShops()
            .collectAsState(initial = emptyList()).value,
        products = viewModel.allProducts()
            .collectAsState(initial = emptyList()).value,
        variants = viewModel.productVariants.collectAsState(initial = emptyList()).value,
        onSubmit = {
            scope.launch {
                if (viewModel.addItem() != null) {
                    navigateBack()
                }
            }
        },
        onProductChange = {
            viewModel.onProductChange()
        },
        onProductAddButtonClick = navigateProductAdd,
        onVariantAddButtonClick = {
            with(viewModel.screenState.selectedProduct.value) {
                if (data != null) {
                    navigateVariantAdd(data.id)
                }
            }
        },
        onShopAddButtonClick = navigateShopAdd,
        onItemLongClick = navigateProductEdit,
        onItemVariantLongClick = navigateVariantEdit,
        onItemShopLongClick = navigateShopEdit,
    )
}